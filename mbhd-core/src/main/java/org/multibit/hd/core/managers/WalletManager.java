package org.multibit.hd.core.managers;

import com.google.bitcoin.core.*;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.bitcoinj.wallet.Protos;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletSaveException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.multibit.hd.core.dto.WalletId.WALLET_ID_SEPARATOR;
import static org.multibit.hd.core.dto.WalletId.parseWalletFilename;

/**
 *  <p>Manager to provide the following to core users:</p>
 *  <ul>
 *  <li>create wallet</li>
 *  <li>save wallet wallet</li>
 *  <li>load wallet wallet</li>
 * <li>tracks the current wallet and the list of wallet directories</li>
 *  </ul>
 *
 * TODO (GR) Consider renaming/restructuring this to Wallets since it provides tools for multiple wallets
 */
public enum WalletManager implements WalletEventListener {
  INSTANCE {
    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
      // Emit an event so that GUI elements can update as required
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx));
    }

    @Override
    public void onCoinsSent(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
      // Emit an event so that GUI elements can update as required
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx));
    }

    @Override
    public void onReorganize(Wallet wallet) {

    }

    @Override
    public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
      // Emit an event so that GUI elements can update as required
      CoreEvents.fireTransactionSeenEvent(new TransactionSeenEvent(tx));
    }

    @Override
    public void onWalletChanged(Wallet wallet) {

    }

    @Override
    public void onKeysAdded(Wallet wallet, List<ECKey> keys) {

    }

    @Override
    public void onScriptsAdded(Wallet wallet, List<Script> scripts) {

    }
  };

  private static final int AUTO_SAVE_DELAY = 20; // Seconds

  private static WalletProtobufSerializer walletProtobufSerializer;

  static {
    walletProtobufSerializer = new WalletProtobufSerializer();
    // TODO was originally multibit protobuf serializer - ok ?
  }

  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  public static final String WALLET_DIRECTORY_PREFIX = "mbhd";

  // The format of the wallet directories is WALLET_DIRECTORY_PREFIX + a wallet id.
  // A walletid is 5 groups of 4 bytes in lowercase hex, with a "-' separator e.g. mbhd-11111111-22222222-33333333-44444444-55555555
  public static final String REGEX_FOR_WALLET_DIRECTORY = "^"
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

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit
   */
  public static final int ENCRYPTED_WALLET_VERSION = 3; // TODO - need a new version when the wallet HD format is created

  public static final String MBHD_WALLET_PREFIX = "mbhd";
  public static final String MBHD_WALLET_SUFFIX = ".wallet";
  public static final String MBHD_WALLET_NAME = MBHD_WALLET_PREFIX + MBHD_WALLET_SUFFIX;

  private File applicationDataDirectory;

  private Optional<WalletSummary> currentWalletSummary = Optional.absent();

  /**
   * Initialise enum, load up the available wallets and find the current wallet
   *
   * @param applicationDataDirectory The application data directory
   * @param walletId                 The wallet ID to locate the wallet
   * @param password                 The password to use to decrypt the wallet
   */
  public Optional<WalletSummary> open(File applicationDataDirectory, WalletId walletId, CharSequence password) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");
    Preconditions.checkNotNull(password, "'password' must be present");

    this.applicationDataDirectory = applicationDataDirectory;
    this.currentWalletSummary = Optional.absent();

    // Work out the list of available wallets in the application data directory
    List<File> walletDirectories = findWalletDirectories(applicationDataDirectory);

    // If a wallet directory is present try to load the wallet
    if (!walletDirectories.isEmpty()) {

      String walletIdPath = walletId.toFormattedString();

      // Match the wallet directory to the wallet data
      for (File walletDirectory : walletDirectories) {

        String walletDirectoryPath = walletDirectory.getAbsolutePath();
        if (walletDirectoryPath.contains(walletIdPath)) {
          // Found the required wallet directory - attempt to open the wallet
          String walletFilename = walletDirectory + File.separator + MBHD_WALLET_NAME;
          WalletSummary walletSummary = loadFromFile(new File(walletFilename), password);
          currentWalletSummary = Optional.of(walletSummary);
        }

      }

    } else {
      currentWalletSummary = Optional.absent();
    }

    return currentWalletSummary;
  }

  /**
   * Create a wallet that contains only a single, random private key.
   * This is stored in the MultiBitHD application data directory
   * The name of the wallet file is derived from the seed.
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   *
   * @param seed     the seed used to initialise the wallet
   * @param password to use to encrypt the wallet
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
   */
  public WalletSummary createWallet(byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {

    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    return getOrCreateWalletSummary(applicationDataDirectory, seed, password);

  }

  /**
   * Create a wallet that contains only a single, random private key.
   * This is stored in the specified directory.
   * The name of the wallet file is derived from the seed.
   * <p/>
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   * <p/>
   * Auto-save is hooked up so that the wallet is changed on modification
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param seed                     The seed phrase to initialise the wallet
   * @param password                 The password to use to encrypt the wallet
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletSummary getOrCreateWalletSummary(File applicationDataDirectory, byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {

    final WalletSummary walletSummaryToReturn;

    // Create a wallet id from the seed to work out the wallet root directory
    final WalletId walletId = new WalletId(seed);
    String walletRoot = createWalletRoot(walletId);

    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);
    final File walletFile = new File(walletDirectory, MBHD_WALLET_NAME);

    if (walletFile.exists()) {

      // There is already a wallet created with this root - if so load it and return that
      walletSummaryToReturn = loadFromFile(walletFile, password);
      if (Configurations.currentConfiguration != null) {
        Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);
      }
      setCurrentWalletSummary(walletSummaryToReturn);

      return walletSummaryToReturn;

    }

    // Wallet file does not exist so create it

    // Create the containing directory if it does not exist
    if (!walletDirectory.exists()) {
      if (!walletDirectory.mkdir()) {
        throw new IllegalStateException("The directory for the wallet '" + walletDirectory.getAbsoluteFile() + "' could not be created");
      }
    }
    // Create a wallet with a single private key using the seed (modulo-ed), encrypted with the password
    KeyCrypter keyCrypter = new KeyCrypterScrypt();

    Wallet walletToReturn = new Wallet(BitcoinNetworkService.NETWORK_PARAMETERS, keyCrypter);
    walletToReturn.setVersion(ENCRYPTED_WALLET_VERSION);

    // Add the 'zero index' key into the wallet
    // Ensure that the seed is within the Bitcoin EC group.
    BigInteger privateKeyToUse = moduloSeedByECGroupSize(new BigInteger(1, seed));

    ECKey newKey = new ECKey(privateKeyToUse);
    newKey = newKey.encrypt(walletToReturn.getKeyCrypter(), walletToReturn.getKeyCrypter().deriveKey(password));
    walletToReturn.addKey(newKey);

    // Set up auto-save on the wallet.
    // This ensures the wallet is saved on modification
    // The listener has a 'after save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
    walletToReturn.autosaveToFile(walletFile, AUTO_SAVE_DELAY, TimeUnit.SECONDS, new WalletAutoSaveListener());

    // Save it now to ensure it is on the disk
    saveWallet(walletToReturn, walletFile.getAbsolutePath());
    if (Configurations.currentConfiguration != null) {
      Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);
    }
    walletSummaryToReturn = new WalletSummary(walletId, walletToReturn);
    walletSummaryToReturn.setPassword(password);
    setCurrentWalletSummary(walletSummaryToReturn);

    // See if there is a checkpoints file - if not then get the InstallationManager to copy one in
    String checkpointsFilename = walletDirectory.getAbsolutePath() + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX;
    InstallationManager.copyCheckpointsTo(checkpointsFilename);

    // Create an initial rolling backup and zip backup
    BackupManager.INSTANCE.createRollingBackup(currentWalletSummary.get());
    BackupManager.INSTANCE.createLocalAndCloudBackup(currentWalletSummary.get().getWalletId());

    return walletSummaryToReturn;
  }

  /**
   * Create a new key for the wallet using the seed of the wallet and an index 0, 1,2,3,4 etc.
   * <p/>
   * TODO this will be replaced by the proper HD wallet algorithm when it is added to bitcoinj
   * Note that the "last used index"needs to be persisted - this is currently stored in the top level of the payments db
   * but it would be better in the wallet itself
   */
  public ECKey createAndAddNewWalletKey(Wallet wallet, CharSequence walletPassword, int indexToCreate) {

    Preconditions.checkState(wallet.getKeychainSize() > 0, "There is no 'first key' to derive subsequent keys from");

    // Get the private key from the first private key in the wallet - subsequent keys are derived from this.
    ECKey firstKey = wallet.getKeys().get(0);
    KeyParameter aesKey = wallet.getKeyCrypter().deriveKey(walletPassword);
    ECKey decryptedFirstKey = firstKey.decrypt(wallet.getKeyCrypter(), aesKey);

    // Ensure that the seed combined with the index is within the Bitcoin EC group.
    BigInteger privateKeyToUse = moduloSeedByECGroupSize(new BigInteger(1, decryptedFirstKey.getPrivKeyBytes()).add(BigInteger.valueOf(indexToCreate)));

    ECKey newKey = new ECKey(privateKeyToUse);
    newKey = newKey.encrypt(wallet.getKeyCrypter(), aesKey);
    wallet.addKey(newKey);

    return newKey;
  }

  /**
   * Ensure that the seed is within the range of the bitcoin EC group
   *
   * @param seedAsBigInteger the seed - converted to a BigInteger
   *
   * @return the seed, guaranteed to be within the Bitcoin EC group range
   */
  private BigInteger moduloSeedByECGroupSize(BigInteger seedAsBigInteger) {

    X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    BigInteger sizeOfGroup = params.getN();

    return seedAsBigInteger.mod(sizeOfGroup);
  }

  /**
   * <p>Load up a Wallet from a specified wallet file.</p>
   * <p>Reduced visibility for testing</p>
   *
   * @param walletFile The file containing the wallet to load
   * @param password   The password to use to decrypt the wallet
   *
   * @return Wallet - the loaded wallet
   *
   * @throws WalletLoadException    If the wallet could not be loaded
   * @throws WalletVersionException If the wallet has an unsupported version number
   */
  WalletSummary loadFromFile(File walletFile, CharSequence password) throws WalletLoadException, WalletVersionException {

    Preconditions.checkNotNull(walletFile, "'walletFile' must be present");
    Preconditions.checkNotNull(password, "'password' must be present");

    String walletFilename = walletFile.getAbsolutePath();

    WalletId walletId = parseWalletFilename(walletFilename);

    try {
      if (isWalletSerialised(walletFile)) {
        // Serialised wallets are no longer supported.
        throw new WalletLoadException(
          "Could not load wallet '"
            + walletFilename
            + "'. Serialized wallets are no longer supported."
        );
      }

      final Wallet wallet;

      try (
        FileInputStream fileInputStream = new FileInputStream(walletFile);
        InputStream stream = new BufferedInputStream(fileInputStream)
      ) {

        Protos.Wallet walletProto = WalletProtobufSerializer.parseToProto(stream);

        wallet = new Wallet(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
        wallet.addExtension(new SendFeeDtoWalletExtension());
        wallet.addExtension(new MatcherResponseWalletExtension());
        new WalletProtobufSerializer().readWallet(walletProto, wallet);

        log.debug("Wallet at read in from file:\n" + wallet.toString());

      } catch (WalletVersionException wve) {
        // We want this exception to propagate out.
        throw wve;
      } catch (Exception e) {
        log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
        throw new WalletLoadException(e.getMessage(), e);
      }

      WalletSummary walletSummary = new WalletSummary(walletId, wallet);
      setCurrentWalletSummary(walletSummary);

      // Set up auto-save on the wallet.
      // This ensures the wallet is saved on modification
      // The listener has a 'post save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
      wallet.autosaveToFile(walletFile, AUTO_SAVE_DELAY, TimeUnit.SECONDS, new WalletAutoSaveListener());

      return walletSummary;

    } catch (WalletVersionException wve) {
      // We want this to propagate out as is
      throw wve;
    } catch (Exception e) {
      // TODO Consider the ExceptionHandler
      log.error(e.getMessage(), e);
      throw new WalletLoadException(e.getMessage(), e);
    }
  }

  /**
   * Save the wallet
   * TODO would be nice to remove this and just use the vanilla wallet .saveFile
   *
   * @param wallet         wallet to save
   * @param walletFilename location to save the wallet
   */
  public void saveWallet(Wallet wallet, String walletFilename) {

    File walletFile = new File(walletFilename);

    // Save the wallet file
    try (FileOutputStream fis = new FileOutputStream(walletFile)) {
      if (wallet != null) {
        log.debug("Saving wallet file '{}' ...", walletFile.getAbsolutePath());
        if (3 == wallet.getVersion()) { // 3 = PROTOBUF_ENCRYPTED

          // Save as a Wallet message with a mandatory extension
          // to prevent loading by older versions of multibit.
          walletProtobufSerializer.writeWallet(wallet, fis);
        } else {
          throw new WalletVersionException("Cannot save wallet '" + walletFilename
            + "'. Its wallet version is '" + wallet.getVersion()
            + "' but this version of MultiBit HD does not understand that format.");
        }
      }

      // Must be OK to be here
      log.debug("Wallet file saved.");

    } catch (IOException | UnsupportedOperationException e) {
      throw new WalletSaveException("Cannot save wallet '" + walletFilename, e);
    }
  }

  /**
   * @param walletFile the wallet to test serialisation for
   *
   * @return true if the wallet file specified is serialised (this format is no longer supported)
   */
  private boolean isWalletSerialised(File walletFile) {

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
   * @param walletId The wallet id to use
   *
   * @return directoryName in which the wallet is stored.
   */
  public static String createWalletRoot(WalletId walletId) {
    return WALLET_DIRECTORY_PREFIX + WALLET_ID_SEPARATOR + walletId.toFormattedString();
  }

  /**
   * Create a directory composed of the root directory and a subdirectory.
   * The subdirectory is created if it does not exist
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param walletId                 The name of the wallet which will be used to create a subdirectory (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   *
   * @return The directory composed of parentDirectory plus the walletRoot
   *
   * @throws IllegalStateException if wallet could not be created
   */
  public static File getOrCreateWalletDirectory(File applicationDataDirectory, String walletId) {

    File walletDirectory = new File(applicationDataDirectory, walletId);

    if (!walletDirectory.exists()) {
      // Create the wallet directory
      if (!walletDirectory.mkdir()) {
        throw new IllegalStateException("Could not create missing wallet directory '" + walletId + "'");
      }
    }

    if (!walletDirectory.isDirectory()) {
      throw new IllegalStateException("Wallet directory '" + walletId + "' is not actually a directory");
    }

    return walletDirectory;
  }

  /**
   * <p>Work out what wallets are available in a directory (typically the user data directory).
   * This is achieved by looking for directories with a name like <code>"mbhd-walletId"</code>
   *
   * @param directoryToSearch The directory to search
   *
   * @return A list of files of wallet directories
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
   * @return A list of wallet summaries
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
          WalletSummary walletSummary = new WalletSummary();
          walletSummary.setWalletId(walletId);

          // TODO (GR) Read these from a per-wallet config file
          walletSummary.setName("Name: " + directoryName);
          walletSummary.setDescription("Wallet Description (temp):" + directoryName);

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
   * TODO (GR) Consider moving this to the same model as Configurations and Themes
   *
   * @return The current wallet data
   */
  public Optional<WalletSummary> getCurrentWalletSummary() {
    return currentWalletSummary;
  }

  /**
   * @param currentWalletSummary The current wallet data
   */
  public void setCurrentWalletSummary(WalletSummary currentWalletSummary) {

    if (currentWalletSummary.getWallet() != null) {

      // Remove the previous WalletEventListener
      currentWalletSummary.getWallet().removeEventListener(this);

      // Add the wallet event listener
      currentWalletSummary.getWallet().addEventListener(this);
    }

    this.currentWalletSummary = Optional.of(currentWalletSummary);
  }

  /**
   * @return The current wallet file
   */
  public Optional<File> getCurrentWalletFile() {

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
   * @return The current wallet root as defined in the configuration, or absent
   */
  public Optional<String> getCurrentWalletRoot() {
    return Optional.fromNullable(Configurations.currentConfiguration.getWallet().getCurrentWalletRoot());
  }
}
