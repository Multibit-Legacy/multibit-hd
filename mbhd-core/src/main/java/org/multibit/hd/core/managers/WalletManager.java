package org.multibit.hd.core.managers;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.store.WalletProtobufSerializer;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Protos;
import org.joda.time.DateTime;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.brit.services.TransactionSentBySelfProvider;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.Yaml;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.events.WalletLoadEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.services.BackupService;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import javax.annotation.Nullable;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.multibit.hd.core.dto.WalletId.*;

/**
 * <p>Manager to provide the following to core users:</p>
 * <ul>
 * <li>create wallet</li>
 * <li>save wallet wallet</li>
 * <li>load wallet wallet</li>
 * <li>tracks the current wallet and the list of wallet directories</li>
 * </ul>
 * <p/>
 */
public enum WalletManager implements WalletEventListener {

  INSTANCE {
    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
      // Emit an event so that GUI elements can update as required
      Coin value = tx.getValue(wallet);
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx, value));
    }

    @Override
    public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
      // Emit an event so that GUI elements can update as required
      Coin value = tx.getValue(wallet);
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx, value));
    }

    @Override
    public void onReorganize(Wallet wallet) {

    }

    @Override
    public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
      // Emit an event so that GUI elements can update as required
      Coin value = tx.getValue(wallet);
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx, value));
    }

    @Override
    public void onWalletChanged(Wallet wallet) {

    }

    @Override
    public void onKeysAdded(List<ECKey> keys) {

    }

    @Override
    public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {

    }
  };

  private static final int AUTO_SAVE_DELAY = 30000; // milliseconds

  // TODO (GR) Refactor this to be injected
  private static final NetworkParameters networkParameters = BitcoinNetwork.current().get();

  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  public static final String EARLIEST_HD_WALLET_DATE = "2014-10-01"; // TODO refine this

  /**
   * How much a block time might drift
   */
  public static final int DRIFT_TIME = 3600; // seconds

  public static final String WALLET_DIRECTORY_PREFIX = "mbhd";
  // The format of the wallet directories is WALLET_DIRECTORY_PREFIX + a wallet id.
  // A wallet id is 5 groups of 4 bytes in lowercase hex, with a "-' separator e.g. mbhd-11111111-22222222-33333333-44444444-55555555
  private static final String REGEX_FOR_WALLET_DIRECTORY = "^"
    + WALLET_DIRECTORY_PREFIX
    + WALLET_ID_SEPARATOR
    + "[0-9a-f]{8}"
    + WALLET_ID_SEPARATOR
    + "[0-9a-f]{8}"
    + WALLET_ID_SEPARATOR
    + "[0-9a-f]{8}"
    + WALLET_ID_SEPARATOR
    + "[0-9a-f]{8}"
    + WALLET_ID_SEPARATOR
    + "[0-9a-f]{8}$";

  private static final Pattern walletDirectoryPattern = Pattern.compile(REGEX_FOR_WALLET_DIRECTORY);

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit Classic
   */
  public static final int MBHD_WALLET_VERSION = 1;
  public static final String MBHD_WALLET_PREFIX = "mbhd";
  public static final String MBHD_WALLET_SUFFIX = ".wallet";
  public static final String MBHD_AES_SUFFIX = ".aes";
  public static final String MBHD_SUMMARY_SUFFIX = ".yaml";
  public static final String MBHD_WALLET_NAME = MBHD_WALLET_PREFIX + MBHD_WALLET_SUFFIX;

  public static final String MBHD_SUMMARY_NAME = MBHD_WALLET_PREFIX + MBHD_SUMMARY_SUFFIX;

  public static final int LOOK_AHEAD_SIZE = 50; // A smaller look ahead size than the bitcoinj default of 100 (speeds up syncing as te bloom filters are smaller)
  public static final long MAXIMUM_WALLET_CREATION_DELTA = 180 * 1000; // 3 minutes in millis

  private Optional<WalletSummary> currentWalletSummary = Optional.absent();

  private static final SecureRandom random = new SecureRandom();

  /**
   * The initialisation vector to use for AES encryption of output files (such as wallets)
   * There is no particular significance to the value of these bytes
   */
  private static final byte[] AES_INITIALISATION_VECTOR = new byte[]{(byte) 0xa3, (byte) 0x44, (byte) 0x39, (byte) 0x1f, (byte) 0x53, (byte) 0x83, (byte) 0x11,
    (byte) 0xb3, (byte) 0x29, (byte) 0x54, (byte) 0x86, (byte) 0x16, (byte) 0xc4, (byte) 0x89, (byte) 0x72, (byte) 0x3e};

  /**
   * The salt used for deriving the KeyParameter from the credentials in AES encryption for wallets
   */
  private static final byte[] SCRYPT_SALT = new byte[]{(byte) 0x35, (byte) 0x51, (byte) 0x03, (byte) 0x80, (byte) 0x75, (byte) 0xa3, (byte) 0xb0, (byte) 0xc5};

  private FeeService feeService;

  private ListeningExecutorService walletExecutorService = null;

  /**
   * @return A copy of the AES initialisation vector
   */
  public static byte[] aesInitialisationVector() {
    return Arrays.copyOf(AES_INITIALISATION_VECTOR, AES_INITIALISATION_VECTOR.length);
  }

  /**
   * @return A copy of the Scrypt salt
   */
  public static byte[] scryptSalt() {
    return Arrays.copyOf(SCRYPT_SALT, SCRYPT_SALT.length);
  }

  /**
   * Open the given wallet and hook it up to the blockchain and peergroup so that it receives notifications
   *
   * @param applicationDataDirectory The application data directory
   * @param walletId                 The wallet ID to locate the wallet
   * @param password                 The credentials to use to decrypt the wallet
   *
   * @return The wallet summary if found
   */
  public Optional<WalletSummary> openWalletFromWalletId(File applicationDataDirectory, WalletId walletId, CharSequence password) throws WalletLoadException {
    log.debug("openWalletFromWalletId called");
    Preconditions.checkNotNull(walletId, "'walletId' must be present");
    Preconditions.checkNotNull(password, "'credentials' must be present");

    this.currentWalletSummary = Optional.absent();

    // Ensure BackupManager knows where the wallets are
    BackupManager.INSTANCE.setApplicationDataDirectory(applicationDataDirectory);

    // Work out the list of available wallets in the application data directory
    List<File> walletDirectories = findWalletDirectories(applicationDataDirectory);

    // If a wallet directory is present try to load the wallet
    if (!walletDirectories.isEmpty()) {
      String walletIdPath = walletId.toFormattedString();
      // Match the wallet directory to the wallet data
      for (File walletDirectory : walletDirectories) {

        verifyWalletDirectory(walletDirectory);

        String walletDirectoryPath = walletDirectory.getAbsolutePath();
        if (walletDirectoryPath.contains(walletIdPath)) {
          // Found the required wallet directory - attempt to present the wallet
          WalletSummary walletSummary = loadFromWalletDirectory(walletDirectory, password);
          setCurrentWalletSummary(walletSummary);

          try {
            // Wallet is now created - finish off other configuration
            updateConfigurationAndCheckSync(createWalletRoot(walletId), walletDirectory, walletSummary, false, true);
          } catch (IOException ioe) {
            throw new WalletLoadException("Cannot load wallet with id: " + walletId, ioe);
          }

          break;
        }
      }
    } else {
      currentWalletSummary = Optional.absent();
    }

    return currentWalletSummary;
  }

  /**
   * <p>Create a MBHD soft wallet from a seed.</p>
   * <p>This is stored in the specified directory.</p>
   * <p>The name of the wallet directory is derived from the seed.</p>
   * <p>If the wallet file already exists it is loaded and returned</p>
   * <p>Auto-save is hooked up so that the wallet is saved on modification</p>
   * <p>Synchronization is begun if required</p>
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param seed                     The seed phrase to initialise the wallet
   * @param creationTimeInSeconds    The creation time of the wallet, in seconds since epoch
   * @param password                 The credentials to use to encrypt the wallet - if null then the wallet is not loaded
   * @param name                     The wallet name
   * @param notes                    Public notes associated with the wallet
   * @param performSynch
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletSummary getOrCreateMBHDSoftWalletSummaryFromSeed(
    File applicationDataDirectory,
    byte[] seed,
    long creationTimeInSeconds,
    String password,
    String name,
    String notes,
    boolean performSynch) throws WalletLoadException, WalletVersionException, IOException {
    log.debug("getOrCreateMBHDSoftWalletSummaryFromSeed called");
    final WalletSummary walletSummary;

    // Create a wallet id from the seed to work out the wallet root directory
    final WalletId walletId = new WalletId(seed);
    String walletRoot = createWalletRoot(walletId);

    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);
    log.debug("Wallet directory:\n'{}'", walletDirectory.getAbsolutePath());

    final File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    final File walletFileWithAES = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME + MBHD_AES_SUFFIX);

    boolean saveWalletYaml = false;
    if (walletFileWithAES.exists()) {
      log.debug("Discovered AES encrypted wallet file. Loading...");

      // There is already a wallet created with this root - if so load it and return that
      walletSummary = loadFromWalletDirectory(walletDirectory, password);

      walletSummary.setWalletType(WalletType.MBHD_SOFT_WALLET);
      setCurrentWalletSummary(walletSummary);
    } else {
      // Wallet file does not exist so create it below the known good wallet directory
      log.debug("Creating new wallet file...");

      // Create a wallet using the seed (no salt passphrase)
      DeterministicSeed deterministicSeed = new DeterministicSeed(seed, "", creationTimeInSeconds);
      Wallet walletToReturn = Wallet.fromSeed(networkParameters, deterministicSeed);
      walletToReturn.setKeychainLookaheadSize(LOOK_AHEAD_SIZE);
      walletToReturn.encrypt(password);
      walletToReturn.setVersion(MBHD_WALLET_VERSION);

      // Save it now to ensure it is on the disk
      walletToReturn.saveToFile(walletFile);
      EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, password);

      // Create a new wallet summary
      walletSummary = new WalletSummary(walletId, walletToReturn);
      walletSummary.setName(name);
      walletSummary.setNotes(notes);
      walletSummary.setPassword(password);
      walletSummary.setWalletType(WalletType.MBHD_SOFT_WALLET);
      walletSummary.setWalletFile(walletFile);
      setCurrentWalletSummary(walletSummary);

      // Save the wallet YAML
      saveWalletYaml = true;

      try {
        WalletManager.writeEncryptedPasswordAndBackupKey(walletSummary, seed, password);
      } catch (NoSuchAlgorithmException e) {
        throw new WalletLoadException("Could not store encrypted credentials and backup AES key", e);
      }
    }

    // Wallet is now created - finish off other configuration and check if wallet needs syncing
    updateConfigurationAndCheckSync(walletRoot, walletDirectory, walletSummary, saveWalletYaml, performSynch);

    return walletSummary;
  }

  /**
   * Create a Trezor hard wallet from an HD root node.
   * <p/>
   * This is stored in the specified application directory.
   * The name of the wallet directory is derived from the rootNode.
   * <p/>
   * If the wallet file already exists it is loaded and returned
   * <p/>
   * Auto-save is hooked up so that the wallet is saved on modification
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param rootNode                 The root node that will be used to initialise the wallet (e.g. a BIP44 node)
   * @param creationTimeInSeconds    The creation time of the wallet, in seconds since epoch
   * @param password                 The credentials to use to encrypt the wallet - if null then the wallet is not loaded
   * @param name                     The wallet name
   * @param notes                    Public notes associated with the wallet
   * @param performSync              True if the wallet should immediately being synchronization
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletSummary getOrCreateTrezorHardWalletSummaryFromRootNode(
    File applicationDataDirectory,
    DeterministicKey rootNode,
    long creationTimeInSeconds,
    String password,
    String name,
    String notes,
    boolean performSync) throws WalletLoadException, WalletVersionException, IOException {

    log.debug("getOrCreateTrezorHardWalletSummaryFromRootNode called");

    // Create a wallet id from the rootNode to work out the wallet root directory
    final WalletId walletId = new WalletId(rootNode.getIdentifier());
    String walletRoot = createWalletRoot(walletId);

    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);
    final File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    final File walletFileWithAES = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME + MBHD_AES_SUFFIX);

    final WalletSummary walletSummary;

    if (walletFileWithAES.exists()) {
      try {
        // There is already a wallet created with this root - if so load it and return that
        log.debug("Opening AES wallet:\n'{}'", walletFileWithAES.getAbsolutePath());
        walletSummary = loadFromWalletDirectory(walletDirectory, password);
      } catch (WalletLoadException e) {
        // Failed to decrypt the existing wallet/backups or something else went wrong
        log.error("Failed to load from wallet directory.");
        CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletId), false, CoreMessageKey.WALLET_FAILED_TO_LOAD, e));
        throw e;
      }
    } else {
      log.debug("Wallet file does not exist. Creating...");

      // Create the containing directory if it does not exist
      if (!walletDirectory.exists()) {
        if (!walletDirectory.mkdir()) {
          IllegalStateException error = new IllegalStateException("The directory for the wallet '" + walletDirectory.getAbsoluteFile() + "' could not be created");
          CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletId), false, CoreMessageKey.WALLET_FAILED_TO_LOAD, error));
          throw error;
        }
      }

      // Create a wallet using the root node
      DeterministicKey rootNodePubOnly = rootNode.getPubOnly();
      log.debug("Watching wallet based on: {}", rootNodePubOnly);

      Wallet walletToReturn = Wallet.fromWatchingKey(networkParameters, rootNodePubOnly, creationTimeInSeconds, rootNodePubOnly.getPath());
      walletToReturn.setKeychainLookaheadSize(LOOK_AHEAD_SIZE);
      walletToReturn.setVersion(MBHD_WALLET_VERSION);

      // Save it now to ensure it is on the disk
      walletToReturn.saveToFile(walletFile);
      EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, password);

      // Create a new wallet summary
      walletSummary = new WalletSummary(walletId, walletToReturn);
    }

    // Wallet summary cannot be null at this point
    walletSummary.setWalletType(WalletType.TREZOR_HARD_WALLET);
    walletSummary.setWalletFile(walletFile);
    walletSummary.setName(name);
    walletSummary.setNotes(notes);
    walletSummary.setPassword(password);

    setCurrentWalletSummary(walletSummary);

    try {
      // The entropy based password from the Trezor is used for both the wallet password and for the backup's password
      WalletManager.writeEncryptedPasswordAndBackupKey(walletSummary, password.getBytes(Charsets.UTF_8), password);
    } catch (NoSuchAlgorithmException e) {
      WalletLoadException error = new WalletLoadException("Could not store encrypted credentials and backup AES key", e);
      CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletId), false, CoreMessageKey.WALLET_FAILED_TO_LOAD, error));
      throw error;
    }
    // Wallet is now created - finish off other configuration and check if wallet needs syncing
    // (Always save the wallet yaml as there was a bug in early Trezor wallets where it was not written out)
    updateConfigurationAndCheckSync(walletRoot, walletDirectory, walletSummary, true, performSync);

    return walletSummary;
  }

  /**
   * Create a Trezor soft wallet from a seed phrase
   * <p/>
   * This is stored in the specified application directory.
   * The name of the wallet directory is derived from the rootNode.
   * <p/>
   * If the wallet file already exists it is loaded and returned
   * <p/>
   * Auto-save is hooked up so that the wallet is saved on modification
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param seedPhrase               The BIP39 seed phrase to use to initialise the walelt
   * @param creationTimeInSeconds    The creation time of the wallet, in seconds since epoch
   * @param password                 The credentials to use to encrypt the wallet - if null then the wallet is not loaded
   * @param name                     The wallet name
   * @param notes                    Public notes associated with the wallet
   * @param performSync              True if the wallet should immediately begin synchronizing
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletSummary getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
    File applicationDataDirectory,
    String seedPhrase,
    long creationTimeInSeconds,
    String password,
    String name,
    String notes,
    boolean performSync) throws UnreadableWalletException, WalletLoadException, WalletVersionException, IOException {

    log.debug("getOrCreateTrezorSoftWalletSummaryFromSeedPhrase called");

    // Create a wallet id from the seed to work out the wallet root directory
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    List<String> seedPhraseList = Bip39SeedPhraseGenerator.split(seedPhrase);
    byte[] seed = seedGenerator.convertToSeed(seedPhraseList);

    final WalletId walletId = new WalletId(seed, getWalletIdSaltUsedInScryptForTrezorSoftWallets());
    String walletRoot = createWalletRoot(walletId);

    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);
    final File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    final File walletFileWithAES = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME + MBHD_AES_SUFFIX);

    final WalletSummary walletSummary;

    if (walletFileWithAES.exists()) {
      try {
        // There is already a wallet created with this root - if so load it and return that
        log.debug("A wallet with name {} exists. Opening...", walletFileWithAES.getAbsolutePath());
        walletSummary = loadFromWalletDirectory(walletDirectory, password);
      } catch (WalletLoadException e) {
        // Failed to decrypt the existing wallet/backups
        log.error("Failed to load from wallet directory.");
        IllegalStateException error = new IllegalStateException("The wallet could not be opened");
        CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletId), false, CoreMessageKey.WALLET_FAILED_TO_LOAD, error));
        throw error;

      }
    } else {
      log.debug("Wallet file does not exist. Creating...");

      // Create the containing directory if it does not exist
      if (!walletDirectory.exists()) {
        if (!walletDirectory.mkdir()) {
          throw new IllegalStateException("The directory for the wallet '" + walletDirectory.getAbsoluteFile() + "' could not be created");
        }
      }

      // Trezor uses BIP-44
      // BIP-44 starts from M/44h/0h/0h for soft wallets
      List<ChildNumber> trezorRootNodePathList = new ArrayList<>();
      trezorRootNodePathList.add(new ChildNumber(44 | ChildNumber.HARDENED_BIT));
      trezorRootNodePathList.add(new ChildNumber(ChildNumber.HARDENED_BIT));

      DeterministicKey trezorRootNode = HDKeyDerivation.createRootNodeWithPrivateKey(ImmutableList.copyOf(trezorRootNodePathList), seed);
      log.debug("Creating Trezor soft wallet with root node with path {}", trezorRootNode.getPath());

      // Create a KeyCrypter to encrypt the waller
      KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(EncryptedFileReaderWriter.makeScryptParameters(WalletManager.SCRYPT_SALT));

      // Create a wallet using the seed phrase and Trezor root node
      DeterministicSeed deterministicSeed = new DeterministicSeed(seed, seedPhraseList, creationTimeInSeconds);

      Wallet walletToReturn = Wallet.fromSeed(networkParameters, deterministicSeed, trezorRootNode.getPath(), password, keyCrypterScrypt);
      walletToReturn.setKeychainLookaheadSize(LOOK_AHEAD_SIZE);
      walletToReturn.setVersion(MBHD_WALLET_VERSION);

      // Save it now to ensure it is on the disk
      walletToReturn.saveToFile(walletFile);
      EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, password);

      // Create a new wallet summary
      walletSummary = new WalletSummary(walletId, walletToReturn);
    }

    // Wallet summary cannot be null
    walletSummary.setWalletType(WalletType.TREZOR_SOFT_WALLET);
    walletSummary.setWalletFile(walletFile);
    walletSummary.setName(name);
    walletSummary.setNotes(notes);
    walletSummary.setPassword(password);

    setCurrentWalletSummary(walletSummary);

    try {
      WalletManager.writeEncryptedPasswordAndBackupKey(walletSummary, seed, password);
    } catch (NoSuchAlgorithmException e) {
      throw new WalletLoadException("Could not store encrypted credentials and backup AES key", e);
    }

    // Wallet is now created - finish off other configuration and check if wallet needs syncing
    // Always save the wallet YAML as there was a bug in early Trezor wallets where it was not written out
    updateConfigurationAndCheckSync(walletRoot, walletDirectory, walletSummary, true, performSync);

    return walletSummary;
  }


  /**
   * Update configuration with new wallet information
   */
  private void updateConfigurationAndCheckSync(String walletRoot, File walletDirectory, WalletSummary walletSummary, boolean saveWalletYaml, boolean performSync) throws IOException {

    Preconditions.checkNotNull(walletRoot, "'walletRoot' must be present");
    Preconditions.checkNotNull(walletDirectory, "'walletDirectory' must be present");
    Preconditions.checkNotNull(walletSummary, "'walletSummary' must be present");

    if (saveWalletYaml) {
      File walletSummaryFile = WalletManager.getOrCreateWalletSummaryFile(walletDirectory);
      log.debug("Writing wallet YAML to file:\n'{}'", walletSummaryFile.getAbsolutePath());
      WalletManager.updateWalletSummary(walletSummaryFile, walletSummary);
    }

    // Remember the current soft wallet root
    if (WalletType.TREZOR_HARD_WALLET != walletSummary.getWalletType()) {
      if (Configurations.currentConfiguration != null) {
        Configurations.currentConfiguration.getWallet().setLastSoftWalletRoot(walletRoot);
      }
    }

    // See if there is a checkpoints file - if not then get the InstallationManager to copy one in
    File checkpointsFile = new File(walletDirectory.getAbsolutePath() + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);
    InstallationManager.copyCheckpointsTo(checkpointsFile);

    // Set up auto-save on the wallet.
    addAutoSaveListener(walletSummary.getWallet(), walletSummary.getWalletFile());

    // Wallet loaded successfully
    CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletSummary.getWalletId()), true, CoreMessageKey.WALLET_LOADED_OK, null));

    // Check if the wallet needs to synch (not required during FEST tests)
    if (performSync) {
      log.info("Wallet configured - performing synchronization");
      checkIfWalletNeedsToSync(walletSummary);
    } else {
      log.warn("Wallet configured - synchronization not selected - expect this during testing");
    }
  }

  /**
   * Check if the wallet needs to sync and, if so, work out the sync date and fire off the synchronise
   *
   * @param walletSummary The wallet summary containing the wallet that may need syncing
   */
  private void checkIfWalletNeedsToSync(WalletSummary walletSummary) {
    // See if the wallet and blockstore are at the same height - in which case perform a regular download blockchain
    // Else perform a sync from the last seen block date to ensure all tx are seen
    log.debug("Seeing if wallet needs to sync");
    if (walletSummary != null) {
      Wallet walletBeingReturned = walletSummary.getWallet();

      if (walletBeingReturned == null) {
        log.debug("There is no wallet to examine");
      } else {

        boolean performRegularSync = false;
        BlockStore blockStore = null;
        try {
          // Get the bitcoin network service
          BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
          log.debug("bitcoinNetworkService: {}", bitcoinNetworkService);

          int walletBlockHeight = walletBeingReturned.getLastBlockSeenHeight();
          Date walletLastSeenBlockTime = walletBeingReturned.getLastBlockSeenTime();

          log.debug("Wallet lastBlockSeenHeight: {}, lastSeenBlockTime: {}, earliestKeyCreationTime: {}", walletBlockHeight, walletLastSeenBlockTime, walletBeingReturned.getEarliestKeyCreationTime());

          // See if the bitcoinNetworkService already has an open blockstore
          blockStore = bitcoinNetworkService.getBlockStore();

          if (blockStore == null) {
            // Open the blockstore with no checkpointing (this is to get the chain height)
            blockStore = bitcoinNetworkService.openBlockStore(InstallationManager.getOrCreateApplicationDataDirectory(), Optional.<Date>absent());
          }
          log.debug("blockStore = {}", blockStore);

          int blockStoreBlockHeight = -2;  // -2 is just a dummy value
          if (blockStore != null) {
            StoredBlock chainHead = blockStore.getChainHead();
            blockStoreBlockHeight = chainHead == null ? -2 : chainHead.getHeight();

          }
          log.debug("The blockStore is at height {}", blockStoreBlockHeight);

          long earliestKeyCreationTimeInSeconds = walletBeingReturned.getEarliestKeyCreationTime();
          if (walletBlockHeight > 0 && walletBlockHeight == blockStoreBlockHeight) {
            if (walletLastSeenBlockTime == null ||
              earliestKeyCreationTimeInSeconds == -1 ||
              (walletLastSeenBlockTime.getTime() / 1000 > earliestKeyCreationTimeInSeconds - DRIFT_TIME)) {
              // Regular sync is ok - no need to use checkpoints
              log.debug("Will perform a regular sync");
              performRegularSync = true;
            }
          }
        } catch (BlockStoreException bse) {
          // Carry on - it's just logging
          bse.printStackTrace();
        } finally {
          // Close the blockstore - it will get opened again later but may or may not be checkpointed
          if (blockStore != null) {
            try {
              blockStore.close();
            } catch (BlockStoreException bse) {
              bse.printStackTrace();
            }
          }
        }

        if (performRegularSync) {
          synchroniseWallet(Optional.<Date>absent());
        } else {
          // Work out the date to sync from from the last block seen, the earliest key creation date and the earliest HD wallet date
          DateTime syncDate = null;

          if (walletBeingReturned.getLastBlockSeenTime() != null) {
            syncDate = new DateTime(walletBeingReturned.getLastBlockSeenTime());
          }
          if (walletBeingReturned.getEarliestKeyCreationTime() != -1) {
            DateTime keyCreationTime = new DateTime(walletBeingReturned.getEarliestKeyCreationTime() * 1000);
            if (syncDate == null) {
              syncDate = keyCreationTime;
            } else {
              syncDate = keyCreationTime.isBefore(syncDate) ? syncDate : keyCreationTime;
            }
          }

          DateTime earliestHDWalletDate = DateTime.parse(EARLIEST_HD_WALLET_DATE);

          if (syncDate == null || syncDate.isBefore(earliestHDWalletDate)) {
            syncDate = earliestHDWalletDate;
          }

          log.debug("Syncing wallet from date {}", syncDate);
          if (syncDate != null) {
            synchroniseWallet(Optional.of(syncDate.toDate()));
          }
        }
      }
    }
  }

  /**
   * Load a wallet from a file and decrypt it
   * (but don't hook it up to the Bitcoin network or sync it)
   *
   * @param walletFile wallet file to load
   * @param password   password to use to decrypt the wallet
   *
   * @return the loaded wallet
   *
   * @throws IOException
   * @throws UnreadableWalletException
   */
  public Wallet loadWalletFromFile(File walletFile, CharSequence password) throws IOException, UnreadableWalletException {

    // Read the encrypted file in and decrypt it.
    byte[] encryptedWalletBytes = Files.toByteArray(walletFile);

    Preconditions.checkNotNull(encryptedWalletBytes, "'encryptedWalletBytes' must be present");

    log.trace("Encrypted wallet bytes after load:\n{}", Utils.HEX.encode(encryptedWalletBytes));
    log.debug("Loaded the encrypted wallet bytes with length: {}", encryptedWalletBytes.length);

    KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(EncryptedFileReaderWriter.makeScryptParameters(SCRYPT_SALT));
    KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);

    // Decrypt the wallet bytes
    byte[] decryptedBytes = AESUtils.decrypt(encryptedWalletBytes, keyParameter, AES_INITIALISATION_VECTOR);

    log.debug("Successfully decrypted wallet bytes, length is now: {}", decryptedBytes.length);

    InputStream inputStream = new ByteArrayInputStream(decryptedBytes);

    Protos.Wallet walletProto = WalletProtobufSerializer.parseToProto(inputStream);

    WalletExtension[] walletExtensions = new WalletExtension[]{new SendFeeDtoWalletExtension(), new MatcherResponseWalletExtension()};
    Wallet wallet = new WalletProtobufSerializer().readWallet(BitcoinNetwork.current().get(), walletExtensions, walletProto);
    wallet.setKeychainLookaheadSize(LOOK_AHEAD_SIZE);

    return wallet;
  }

  /**
   * <p>Load up an encrypted Wallet from a specified wallet directory.</p>
   * <p>Reduced visibility for testing</p>
   *
   * @param walletDirectory The wallet directory containing the various wallet files to load
   * @param password        The credentials to use to decrypt the wallet
   *
   * @return Wallet - the loaded wallet
   *
   * @throws WalletLoadException    If the wallet could not be loaded
   * @throws WalletVersionException If the wallet has an unsupported version number
   */
  WalletSummary loadFromWalletDirectory(File walletDirectory, CharSequence password) throws WalletLoadException, WalletVersionException {

    Preconditions.checkNotNull(walletDirectory, "'walletDirectory' must be present");
    Preconditions.checkNotNull(password, "'credentials' must be present");
    verifyWalletDirectory(walletDirectory);

    try {
      String walletFilenameNoAESSuffix = walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME;
      File walletFile = new File(walletFilenameNoAESSuffix + MBHD_AES_SUFFIX);
      WalletId walletId = parseWalletFilename(walletFile.getAbsolutePath());

      if (walletFile.exists() && isWalletSerialised(walletFile)) {
        // Serialised wallets are no longer supported.
        throw new WalletLoadException(
          "Could not load wallet '"
            + walletFile
            + "'. Serialized wallets are no longer supported."
        );
      }

      Wallet wallet;

      try {
        wallet = loadWalletFromFile(walletFile, password);
      } catch (WalletVersionException wve) {
        // We want this exception to propagate out.
        // Don't bother trying to load the rolling backups as they will most likely be an unreadable version too.
        throw wve;
      } catch (Exception e) {
        // Log the initial error
        log.error("WalletManager error: " + e.getClass().getCanonicalName() + " " + e.getMessage(), e);

        // Try loading one of the rolling backups - this will send a BackupWalletLoadedEvent containing the initial error
        // If the rolling backups don't load then loadRollingBackup will throw a WalletLoadException which will propagate out
        wallet = BackupManager.INSTANCE.loadRollingBackup(walletId, password);
      }

      // Create the wallet summary with its wallet
      WalletSummary walletSummary = getOrCreateWalletSummary(walletDirectory, walletId);
      walletSummary.setWallet(wallet);
      walletSummary.setWalletFile(new File(walletFilenameNoAESSuffix));
      walletSummary.setPassword(password);

      log.debug("Loaded the wallet successfully from \n{}", walletDirectory);
      log.debug("Wallet:{}", wallet);

      return walletSummary;

    } catch (WalletVersionException wve) {
      // We want this to propagate out as is
      throw wve;
    } catch (Exception e) {
      throw new WalletLoadException(e.getMessage(), e);
    }
  }

  /**
   * Set up auto-save on the wallet.
   * This ensures the wallet is saved on modification
   * The listener has a 'after save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
   *
   * @param wallet The wallet to add the autosave listener to
   * @param file   The file to add the autoSaveListener to - this should be WITHOUT the AES suffix
   */
  private void addAutoSaveListener(Wallet wallet, File file) {
    if (file != null) {
      WalletAutoSaveListener walletAutoSaveListener = new WalletAutoSaveListener();
      wallet.autosaveToFile(file, AUTO_SAVE_DELAY, TimeUnit.MILLISECONDS, walletAutoSaveListener);
      log.debug("WalletAutoSaveListener {} on file {} just added to wallet {}", System.identityHashCode(this), file.getAbsolutePath(), System.identityHashCode(wallet));
    } else {
      log.debug("Not adding autoSaveListener to wallet {} as no wallet file is specified", System.identityHashCode(wallet));
    }
  }

  private void synchroniseWallet(final Optional<Date> syncDateOptional) {
    log.debug("Synchronise wallet called with syncDate {}", syncDateOptional);

    if (walletExecutorService == null) {
      walletExecutorService = SafeExecutors.newSingleThreadExecutor("sync-wallet");
    }

    // Start the Bitcoin network synchronization operation
    ListenableFuture future = walletExecutorService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() throws Exception {
          log.debug("synchroniseWallet  called with replay date {}", syncDateOptional);

          // Replay wallet - this also bounces the Bitcoin network connection
          CoreServices.getOrCreateBitcoinNetworkService().replayWallet(InstallationManager.getOrCreateApplicationDataDirectory(), syncDateOptional);
          return true;

        }

      });
    Futures.addCallback(
      future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {
          // Do nothing this just means that the block chain download has begun
          log.debug("Sync has begun");

        }

        @Override
        public void onFailure(Throwable t) {
          // Have a failure
          log.debug("Sync failed, error was " + t.getClass().getCanonicalName() + " " + t.getMessage());

        }
      });
  }

  /**
   * @param walletFile the wallet to test serialisation for
   *
   * @return true if the wallet file specified is serialised (this format is no longer supported)
   */
  private boolean isWalletSerialised(File walletFile) {

    Preconditions.checkNotNull(walletFile, "'walletFile' must be present");
    Preconditions.checkState(walletFile.isFile(), "'walletFile' must be a file");

    boolean isWalletSerialised = false;
    InputStream stream = null;
    try {
      // Determine what kind of wallet stream this is: Java serialization or protobuf format
      stream = new BufferedInputStream(new FileInputStream(walletFile));
      isWalletSerialised = stream.read() == 0xac && stream.read() == 0xed;
    } catch (IOException e) {
      log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
        }
      }
    }
    return isWalletSerialised;
  }

  /**
   * Create the name of the directory in which the wallet is stored
   *
   * @param walletId The wallet id to use (e.g. "11111111-22222222-33333333-44444444-55555555")
   *
   * @return A wallet root
   */
  public static String createWalletRoot(WalletId walletId) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    return WALLET_DIRECTORY_PREFIX + WALLET_ID_SEPARATOR + walletId.toFormattedString();
  }

  /**
   * <p>Get or create the sub-directory of the given application directory with the given wallet root</p>
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param walletRoot               The wallet root from which to make a sub-directory (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   *
   * @return The directory composed of parent directory plus the wallet root
   *
   * @throws IllegalStateException if wallet could not be created
   */
  public static File getOrCreateWalletDirectory(File applicationDataDirectory, String walletRoot) {

    // Create wallet directory under application directory
    File walletDirectory = SecureFiles.verifyOrCreateDirectory(applicationDataDirectory, walletRoot);

    // Sanity check the wallet directory name and existence
    verifyWalletDirectory(walletDirectory);

    return walletDirectory;
  }

  /**
   * @return A list of wallet summaries based on the current application directory contents (never null)
   */
  public static List<WalletSummary> getWalletSummaries() {

    List<File> walletDirectories = findWalletDirectories(InstallationManager.getOrCreateApplicationDataDirectory());
    Optional<String> walletRoot = INSTANCE.getCurrentWalletRoot();
    return findWalletSummaries(walletDirectories, walletRoot);

  }

  /**
   * @return A list of non Tezor wallet summaries based on the current application directory contents (never null)
   */
  public static List<WalletSummary> getNonHardTrezorWalletSummaries() {

    List<File> walletDirectories = findWalletDirectories(InstallationManager.getOrCreateApplicationDataDirectory());
    Optional<String> walletRoot = INSTANCE.getCurrentWalletRoot();
    List<WalletSummary> allWalletSummaries = findWalletSummaries(walletDirectories, walletRoot);
    List<WalletSummary> nonHardTrezorWalletSummaries = Lists.newArrayList();

    for (WalletSummary walletSummary : allWalletSummaries) {
      if (!WalletType.TREZOR_HARD_WALLET.equals(walletSummary.getWalletType())) {
        nonHardTrezorWalletSummaries.add(walletSummary);
      }
    }

    return nonHardTrezorWalletSummaries;
  }

  /**
   * <p>Work out what wallets are available in a directory (typically the user data directory).
   * This is achieved by looking for directories with a name like <code>"mbhd-walletId"</code>
   *
   * @param directoryToSearch The directory to search
   *
   * @return A list of files of wallet directories (never null)
   */
  public static List<File> findWalletDirectories(File directoryToSearch) {

    Preconditions.checkNotNull(directoryToSearch);

    File[] files = directoryToSearch.listFiles();
    List<File> walletDirectories = Lists.newArrayList();

    // Look for file names with format "mbhd"-"walletId" and are not empty
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          String filename = file.getName();
          if (filename.matches(REGEX_FOR_WALLET_DIRECTORY)) {
            // The name matches so add it
            walletDirectories.add(file);
          }
        }
      }
    }

    return walletDirectories;
  }

  /**
   * <p>Find Wallet summaries for all the wallet directories provided</p>
   *
   * @param walletDirectories The candidate wallet directory references
   * @param walletRoot        The wallet root of the first entry
   *
   * @return A list of wallet summaries (never null)
   */
  public static List<WalletSummary> findWalletSummaries(List<File> walletDirectories, Optional walletRoot) {

    Preconditions.checkNotNull(walletDirectories, "'walletDirectories' must be present");

    List<WalletSummary> walletList = Lists.newArrayList();
    for (File walletDirectory : walletDirectories) {
      if (walletDirectory.isDirectory()) {
        String directoryName = walletDirectory.getName();
        if (directoryName.matches(REGEX_FOR_WALLET_DIRECTORY)) {

          // The name matches so process it
          WalletId walletId = new WalletId(directoryName.substring(MBHD_WALLET_PREFIX.length() + 1));
          WalletSummary walletSummary = getOrCreateWalletSummary(walletDirectory, walletId);

          // Check if the wallet root is present and matches the file name
          if (walletRoot.isPresent() && directoryName.equals(walletRoot.get())) {
            walletList.add(0, walletSummary);
          } else {
            walletList.add(walletSummary);
          }
        }
      }
    }

    return walletList;
  }


  /**
   * Get the balance of the current wallet (this does not include a decrement due to the BRIT fees)
   * This is Optional.absent() if there is no wallet
   */
  public Optional<Coin> getCurrentWalletBalance() {
    Optional<WalletSummary> currentWalletSummary = getCurrentWalletSummary();
    if (currentWalletSummary.isPresent()) {
      // Use the real wallet data
      return Optional.of(currentWalletSummary.get().getWallet().getBalance());
    } else {
      // Unknown at this time
      return Optional.absent();
    }
  }

  /**
   * @param includeOneExtraFee include an extra fee to include a tx currently being constructed that isn't in the wallet yet
   *
   * @return The BRIT fee state for the current wallet - this includes things like how much is
   * currently owed to BRIT
   */
  public Optional<FeeState> calculateBRITFeeState(boolean includeOneExtraFee) {

    if (feeService == null) {
      feeService = CoreServices.createFeeService();
    }

    if (getCurrentWalletSummary() != null && getCurrentWalletSummary().isPresent()) {
      Wallet wallet = getCurrentWalletSummary().get().getWallet();

      // Set the transaction sent by self provider to use TransactionInfos
      TransactionSentBySelfProvider transactionSentBySelfProvider = new TransactionInfoSentBySelfProvider(getCurrentWalletSummary().get().getWalletId());
      feeService.setTransactionSentBySelfProvider(transactionSentBySelfProvider);

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      Optional<File> walletFileOptional = getCurrentWalletFile(applicationDataDirectory);
      if (walletFileOptional.isPresent()) {
        log.debug("Wallet file prior to calculateFeeState is " + walletFileOptional.get().length() + " bytes");
      }
      FeeState feeState = feeService.calculateFeeState(wallet, false);
      if (includeOneExtraFee) {
        feeState.setFeeOwed(feeState.getFeeOwed().add(FeeService.FEE_PER_SEND));
      }
      Optional<FeeState> feeStateOptional = Optional.of(feeState);
      if (walletFileOptional.isPresent()) {
        log.debug("Wallet file after to calculateFeeState is " + walletFileOptional.get().length() + " bytes");
      }

      return feeStateOptional;
    } else {
      return Optional.absent();
    }
  }

  /**
   * @return The current wallet summary (present only if a wallet has been unlocked)
   */
  public Optional<WalletSummary> getCurrentWalletSummary() {
    return currentWalletSummary;
  }

  /**
   * @param walletSummary The current wallet summary (null if a reset is required)
   */
  public void setCurrentWalletSummary(WalletSummary walletSummary) {

    if (walletSummary != null && walletSummary.getWallet() != null) {

      // Remove the previous WalletEventListener
      walletSummary.getWallet().removeEventListener(this);

      // Add the wallet event listener
      walletSummary.getWallet().addEventListener(this);
    }

    this.currentWalletSummary = Optional.fromNullable(walletSummary);

  }

  /**
   * @return The current wallet file (e.g. "/User/example/Application Support/MultiBitHD/mbhd-1111-2222-3333-4444/mbhd.wallet")
   */
  public Optional<File> getCurrentWalletFile(File applicationDataDirectory) {

    if (applicationDataDirectory != null && currentWalletSummary.isPresent()) {

      String walletFilename =
        applicationDataDirectory
          + File.separator
          + WALLET_DIRECTORY_PREFIX
          + WALLET_ID_SEPARATOR
          + currentWalletSummary.get().getWalletId().toFormattedString()
          + File.separator
          + MBHD_WALLET_NAME;
      return Optional.of(new File(walletFilename));

    } else {
      return Optional.absent();
    }

  }

  /**
   * @return The current wallet summary file (e.g. "/User/example/Application Support/MultiBitHD/mbhd-1111-2222-3333-4444/mbhd.yaml")
   */
  public Optional<File> getCurrentWalletSummaryFile(File applicationDataDirectory) {

    if (applicationDataDirectory != null && currentWalletSummary.isPresent()) {

      String walletFilename =
        applicationDataDirectory
          + File.separator
          + WALLET_DIRECTORY_PREFIX
          + WALLET_ID_SEPARATOR
          + currentWalletSummary.get().getWalletId().toFormattedString()
          + File.separator
          + MBHD_SUMMARY_NAME;
      return Optional.of(new File(walletFilename));

    } else {
      return Optional.absent();
    }

  }

  /**
   * @param walletDirectory The wallet directory containing the various wallet files
   *
   * @return A wallet summary file
   */
  public static File getOrCreateWalletSummaryFile(File walletDirectory) {
    return SecureFiles.verifyOrCreateFile(walletDirectory, MBHD_SUMMARY_NAME);
  }

  /**
   * @return The current wallet root as defined in the configuration, or absent
   */
  public Optional<String> getCurrentWalletRoot() {
    return Optional.fromNullable(Configurations.currentConfiguration.getWallet().getLastSoftWalletRoot());
  }

  /**
   * @param walletSummary The wallet summary to write
   */
  public static void updateWalletSummary(File walletSummaryFile, WalletSummary walletSummary) {

    if (walletSummary == null) {
      log.warn("WalletSummary is missing. The wallet configuration file is NOT being overwritten.");
      return;
    }

    // Persist the new configuration
    try (FileOutputStream fos = new FileOutputStream(walletSummaryFile)) {

      Yaml.writeYaml(fos, walletSummary);

    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }
  }

  /**
   * @param walletDirectory The wallet directory to read
   *
   * @return The wallet summary if present, or a default if not
   */
  public static WalletSummary getOrCreateWalletSummary(File walletDirectory, WalletId walletId) {

    log.debug("getOrCreateWalletSummary called");
    verifyWalletDirectory(walletDirectory);

    Optional<WalletSummary> walletSummaryOptional = Optional.absent();

    File walletSummaryFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_SUMMARY_NAME);
    if (walletSummaryFile.exists()) {
      try (InputStream is = new FileInputStream(walletSummaryFile)) {
        // Load configuration (providing a default if none exists)
        walletSummaryOptional = Yaml.readYaml(is, WalletSummary.class);
      } catch (IOException e) {
        // A full stack trace is too much here
        log.warn("Could not read wallet summary:\n'{}'\nException: {}", walletDirectory.getAbsolutePath(), e.getMessage());
      }
    }

    final WalletSummary walletSummary;
    if (walletSummaryOptional.isPresent()) {
      walletSummary = walletSummaryOptional.get();
    } else {
      walletSummary = new WalletSummary();
      // TODO No localiser available in core to localise core_default_wallet_name.
      String shortWalletDirectory = walletDirectory.getName().substring(0, 13); // The mbhd and the first group of digits
      walletSummary.setName("Wallet (" + shortWalletDirectory + "...)");
      walletSummary.setNotes("");
    }
    walletSummary.setWalletId(walletId);

    return walletSummary;

  }

  /**
   * Write the encrypted wallet credentials and backup AES key to the wallet configuration.
   * You probably want to save it afterwards with an updateSummary
   */
  public static void writeEncryptedPasswordAndBackupKey(WalletSummary walletSummary, byte[] entropy, String password) throws NoSuchAlgorithmException {

    Preconditions.checkNotNull(walletSummary, "'walletSummary' must be present");
    Preconditions.checkNotNull(entropy, "'entropy' must be present");
    Preconditions.checkNotNull(password, "'password' must be present");

    // Save the wallet credentials, AES encrypted with a key derived from the wallet seed/ entropy
    KeyParameter entropyDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(entropy, SCRYPT_SALT);
    byte[] passwordBytes = password.getBytes(Charsets.UTF_8);

    byte[] paddedPasswordBytes = padPasswordBytes(passwordBytes);
    byte[] encryptedPaddedPassword = org.multibit.hd.brit.crypto.AESUtils.encrypt(paddedPasswordBytes, entropyDerivedAESKey, AES_INITIALISATION_VECTOR);
    walletSummary.setEncryptedPassword(encryptedPaddedPassword);

    // Save the backupAESKey, AES encrypted with a key generated from the wallet password
    KeyParameter walletPasswordDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(passwordBytes, SCRYPT_SALT);
    byte[] encryptedBackupAESKey = org.multibit.hd.brit.crypto.AESUtils.encrypt(entropyDerivedAESKey.getKey(), walletPasswordDerivedAESKey, AES_INITIALISATION_VECTOR);
    walletSummary.setEncryptedBackupKey(encryptedBackupAESKey);
  }

  /**
   * @param walletDirectory The candidate wallet directory (e.g. "/User/example/Application Support/MultiBitHD/mbhd-11111111-22222222-33333333-44444444-55555555")
   *
   * @throws IllegalStateException If the wallet directory is malformed
   */
  private static void verifyWalletDirectory(File walletDirectory) {

    log.trace("Verifying wallet directory: '{}'", walletDirectory.getAbsolutePath());

    Preconditions.checkState(walletDirectory.isDirectory(), "'walletDirectory' must be a directory: '" + walletDirectory.getAbsolutePath() + "'");

    // Use the pre-compiled regex
    boolean result = walletDirectoryPattern.matcher(walletDirectory.getName()).matches();

    Preconditions.checkState(result, "'walletDirectory' is not named correctly: '" + walletDirectory.getAbsolutePath() + "'");

    log.trace("Wallet directory verified ok");

  }

  /**
   * Method to determine whether a message is 'mine', meaning an existing address in the current wallet
   *
   * @param address The address to test for wallet inclusion
   *
   * @return true if address is in current wallet, false otherwise
   */
  public boolean isAddressMine(Address address) {
    try {

      Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (walletSummaryOptional.isPresent()) {
        WalletSummary walletSummary = walletSummaryOptional.get();

        Wallet wallet = walletSummary.getWallet();
        ECKey signingKey = wallet.findKeyFromPubHash(address.getHash160());

        return signingKey != null;
      } else {
        // No wallet present
        return false;
      }
    } catch (Exception e) {
      // Some other problem
      return false;
    }
  }

  /**
   * @return True if current wallet is unlocked and represents a Trezor "hard" wallet
   */
  public boolean isUnlockedTrezorHardWallet() {
    try {

      Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (walletSummaryOptional.isPresent()) {
        WalletSummary walletSummary = walletSummaryOptional.get();

        return WalletType.TREZOR_HARD_WALLET.equals(walletSummary.getWalletType());

      } else {
        // No wallet present
        return false;
      }
    } catch (Exception e) {
      // Some other problem
      return false;
    }
  }

  /**
   * <p>Method to sign a message</p>
   *
   * @param addressText    Text address to use to sign (makes UI Address conversion code DRY)
   * @param messageText    The message to sign
   * @param walletPassword The wallet credentials
   *
   * @return A "sign message result" describing the outcome
   */
  public SignMessageResult signMessage(String addressText, String messageText, String walletPassword) {
    if (Strings.isNullOrEmpty(addressText)) {
      return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_ENTER_ADDRESS, null);
    }

    if (Strings.isNullOrEmpty(messageText)) {
      return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_ENTER_MESSAGE, null);
    }

    if (Strings.isNullOrEmpty(walletPassword)) {
      return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_ENTER_PASSWORD, null);
    }

    try {
      Address signingAddress = new Address(BitcoinNetwork.current().get(), addressText);

      Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

      if (walletSummaryOptional.isPresent()) {
        WalletSummary walletSummary = walletSummaryOptional.get();

        Wallet wallet = walletSummary.getWallet();

        ECKey signingKey = wallet.findKeyFromPubHash(signingAddress.getHash160());
        if (signingKey != null) {
          if (signingKey.getKeyCrypter() != null) {
            KeyParameter aesKey = signingKey.getKeyCrypter().deriveKey(walletPassword);
            ECKey decryptedSigningKey = signingKey.decrypt(aesKey);

            String signatureBase64 = decryptedSigningKey.signMessage(messageText);
            return new SignMessageResult(Optional.of(signatureBase64), true, CoreMessageKey.SIGN_MESSAGE_SUCCESS, null);
          } else {
            // The signing key is not encrypted but it should be
            return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_SIGNING_KEY_NOT_ENCRYPTED, null);
          }
        } else {
          // No signing key found.
          return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_NO_SIGNING_KEY, new Object[]{addressText});
        }
      } else {
        return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_NO_WALLET, null);
      }
    } catch (KeyCrypterException e) {
      return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_NO_PASSWORD, null);
    } catch (Exception e) {
      e.printStackTrace();
      return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_FAILURE, null);
    }
  }

  /**
   * <p>Method to verify a message</p>
   *
   * @param addressText   Text address to use to sign (makes UI Address conversion code DRY)
   * @param messageText   The message to sign
   * @param signatureText The signature text (can include CRLF characters which will be stripped)
   *
   * @return A "verify message result" describing the outcome
   */
  public VerifyMessageResult verifyMessage(String addressText, String messageText, String signatureText) {
    if (Strings.isNullOrEmpty(addressText)) {
      return new VerifyMessageResult(false, CoreMessageKey.VERIFY_MESSAGE_ENTER_ADDRESS, null);
    }

    if (Strings.isNullOrEmpty(messageText)) {
      return new VerifyMessageResult(false, CoreMessageKey.VERIFY_MESSAGE_ENTER_MESSAGE, null);
    }

    if (Strings.isNullOrEmpty(signatureText)) {
      return new VerifyMessageResult(false, CoreMessageKey.VERIFY_MESSAGE_ENTER_SIGNATURE, null);
    }

    try {
      Address signingAddress = new Address(BitcoinNetwork.current().get(), addressText);

      // Strip CRLF from signature text
      signatureText = signatureText.replaceAll("\n", "").replaceAll("\r", "");

      ECKey key = ECKey.signedMessageToKey(messageText, signatureText);
      Address gotAddress = key.toAddress(BitcoinNetwork.current().get());
      if (signingAddress.equals(gotAddress)) {
        return new VerifyMessageResult(true, CoreMessageKey.VERIFY_MESSAGE_VERIFY_SUCCESS, null);
      } else {
        return new VerifyMessageResult(false, CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE, null);
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new VerifyMessageResult(false, CoreMessageKey.VERIFY_MESSAGE_FAILURE, null);
    }
  }

  /**
   * Password short passwords with extra bytes - this is done so that the existence of short passwords is not leaked by
   * the length of the encrypted credentials (which is always a multiple of the AES block size (16 bytes).
   *
   * @param passwordBytes the credentials bytes to pad
   *
   * @return paddedPasswordBytes - this is guaranteed to be longer than 48 bytes. Byte 0 indicates the number of padding bytes,
   * which are random bytes stored from byte 1 to byte <number of padding bytes). The real credentials is stored int he remaining bytes
   */
  public static byte[] padPasswordBytes(byte[] passwordBytes) {
    if (passwordBytes.length > AESUtils.BLOCK_LENGTH * 3) {
      // No padding required - add a zero to the beginning of the credentials bytes (to indicate no padding bytes)
      return Bytes.concat(new byte[]{(byte) 0x0}, passwordBytes);
    } else {
      if (passwordBytes.length > AESUtils.BLOCK_LENGTH * 2) {
        // Pad with 16 random bytes
        byte[] paddingBytes = new byte[16];
        random.nextBytes(paddingBytes);
        return Bytes.concat(new byte[]{(byte) 0x10}, paddingBytes, passwordBytes);
      } else {
        if (passwordBytes.length > AESUtils.BLOCK_LENGTH) {
          // Pad with 32 random bytes
          byte[] paddingBytes = new byte[32];
          random.nextBytes(paddingBytes);
          return Bytes.concat(new byte[]{(byte) 0x20}, paddingBytes, passwordBytes);
        } else {
          // Pad with 48 random bytes
          byte[] paddingBytes = new byte[48];
          random.nextBytes(paddingBytes);
          return Bytes.concat(new byte[]{(byte) 0x30}, paddingBytes, passwordBytes);
        }
      }
    }
  }

  /**
   * Unpad credentials bytes, removing the random prefix bytes length marker byte and te random bytes themselves
   */
  public static byte[] unpadPasswordBytes(byte[] paddedPasswordBytes) {
    Preconditions.checkNotNull(paddedPasswordBytes);
    Preconditions.checkState(paddedPasswordBytes.length > 0);

    // Get the length of the pad
    int lengthOfPad = (int) paddedPasswordBytes[0];

    if (lengthOfPad > paddedPasswordBytes.length - 1) {
      throw new IllegalStateException("Stored encrypted credentials is not in the correct format");
    }
    return Arrays.copyOfRange(paddedPasswordBytes, 1 + lengthOfPad, paddedPasswordBytes.length);
  }

  /**
   * Generate the DeterministicKey from the private master key for a Trezor  wallet
   * <p/>
   * For a real Trezor device this will be the result of a GetPublicKey of the M/44'/0'/0' path, received as an xpub and then converted to a DeterministicKey
   *
   * @param privateMasterKey the private master key derived from the wallet seed
   *
   * @return the public only DeterministicSeed corresponding to the root Trezor wallet node e.g. M/44'/0'/0'
   */
  public static DeterministicKey generateTrezorWalletRootNode(DeterministicKey privateMasterKey) {
    DeterministicKey key_m_44h = HDKeyDerivation.deriveChildKey(privateMasterKey, new ChildNumber(44 | ChildNumber.HARDENED_BIT));
    log.debug("key_m_44h deterministic key = " + key_m_44h);

    DeterministicKey key_m_44h_0h = HDKeyDerivation.deriveChildKey(key_m_44h, ChildNumber.ZERO_HARDENED);
    log.debug("key_m_44h_0h deterministic key = " + key_m_44h_0h);

    DeterministicKey key_m_44h_0h_0h = HDKeyDerivation.deriveChildKey(key_m_44h_0h, ChildNumber.ZERO_HARDENED);
    log.debug("key_m_44h_0h_0h = " + key_m_44h_0h_0h);

    return key_m_44h_0h_0h;
  }

  /**
   * @param shutdownType The shutdown type
   */
  public void shutdownNow(ShutdownEvent.ShutdownType shutdownType) {

    log.debug("Received shutdown: {}", shutdownType.name());
    currentWalletSummary = Optional.absent();

  }

  /**
   * <p>Save the current wallet to application directory, create a rolling backup and a cloud backup</p>
   */
  public void saveWallet() {

    // Save the current wallet immediately
    if (getCurrentWalletSummary().isPresent()) {

      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      WalletId walletId = walletSummary.getWalletId();
      log.debug("Saving wallet with id : {}, height : {}", walletId, walletSummary.getWallet().getLastBlockSeenHeight());

      try {
        File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
        File currentWalletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();

        walletSummary.getWallet().saveToFile(currentWalletFile);

        File encryptedAESCopy = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(currentWalletFile, walletSummary.getPassword());
        log.debug("Created AES encrypted wallet as file:\n'{}'\nSize: {} bytes", encryptedAESCopy.getAbsolutePath(), encryptedAESCopy.length());

        BackupService backupService = CoreServices.getOrCreateBackupService();
        backupService.rememberWalletSummaryAndPasswordForRollingBackup(walletSummary, walletSummary.getPassword());
        backupService.rememberWalletIdAndPasswordForLocalZipBackup(walletSummary.getWalletId(), walletSummary.getPassword());
        backupService.rememberWalletIdAndPasswordForCloudZipBackup(walletSummary.getWalletId(), walletSummary.getPassword());

      } catch (IOException ioe) {
        log.error("Could not write wallet and backups for wallet with id '" + walletId + "' successfully. The error was '" + ioe.getMessage() + "'");
      }
    }

  }

  /**
   * Closes the wallet
   */
  public void closeWallet() {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      try {
        Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
        log.debug("Shutdown wallet autosave at height: {} ", wallet.getLastBlockSeenHeight());
        wallet.shutdownAutosaveAndWait();
      } catch (IllegalStateException ise) {
        // If there is no autosaving set up yet then that is ok
        if (!ise.getMessage().contains("Auto saving not enabled.")) {
          throw ise;
        }
      }
    } else {
      log.info("No current wallet summary to provide wallet");
    }

  }


}
