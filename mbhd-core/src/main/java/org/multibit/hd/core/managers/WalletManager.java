package org.multibit.hd.core.managers;

import com.google.bitcoin.core.*;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.store.UnreadableWalletException;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.bitcoin.wallet.DeterministicSeed;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import org.bitcoinj.wallet.Protos;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.extensions.SendFeeDtoWalletExtension;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.files.SecureFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
 * <p/>
 * TODO (GR) Consider renaming/restructuring this to Wallets since it provides tools for multiple wallets and allow for BitcoinNetwork injection
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
    public void onScriptsAdded(Wallet wallet, List<Script> scripts) {

    }

    @Override
    public void onKeysAdded(List<ECKey> keys) {

    }
  };

  private static final int AUTO_SAVE_DELAY = 30; // Seconds

  // TODO (GR) Refactor this to be injected
  private static final NetworkParameters networkParameters = BitcoinNetwork.current().get();

  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  public static final String WALLET_DIRECTORY_PREFIX = "mbhd";
  // The format of the wallet directories is WALLET_DIRECTORY_PREFIX + a wallet id.
  // A walletid is 5 groups of 4 bytes in lowercase hex, with a "-' separator e.g. mbhd-11111111-22222222-33333333-44444444-55555555
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
  public static final int MBHD_WALLET_VERSION = 1; // TODO - check compatibility - this is the same as the old serialised MB classic wallets
  public static final String MBHD_WALLET_PREFIX = "mbhd";
  public static final String MBHD_WALLET_SUFFIX = ".wallet";
  public static final String MBHD_AES_SUFFIX = ".aes";
  public static final String MBHD_SUMMARY_SUFFIX = ".yaml";
  public static final String MBHD_WALLET_NAME = MBHD_WALLET_PREFIX + MBHD_WALLET_SUFFIX;

  public static final String MBHD_SUMMARY_NAME = MBHD_WALLET_PREFIX + MBHD_SUMMARY_SUFFIX;

  public static final int LOOK_AHEAD_SIZE = 50; // A smaller look ahead size than the bitcoinj default of 100 (speeds up syncing as te bloom filters are smaller)

  private Optional<WalletSummary> currentWalletSummary = Optional.absent();

  private static final SecureRandom random = new SecureRandom();

  /**
   * The initialisation vector to use for AES encryption of output files (such as wallets)
   * There is no particular significance to the value of these bytes
   */
  public static final byte[] AES_INITIALISATION_VECTOR = new byte[]{(byte) 0xa3, (byte) 0x44, (byte) 0x39, (byte) 0x1f, (byte) 0x53, (byte) 0x83, (byte) 0x11,
    (byte) 0xb3, (byte) 0x29, (byte) 0x54, (byte) 0x86, (byte) 0x16, (byte) 0xc4, (byte) 0x89, (byte) 0x72, (byte) 0x3e};

  /**
   * The salt used for deriving the KeyParameter from the password in AES encryption for wallets
   */
  public static final byte[] SCRYPT_SALT = new byte[]{(byte) 0x35, (byte) 0x51, (byte) 0x03, (byte) 0x80, (byte) 0x75, (byte) 0xa3, (byte) 0xb0, (byte) 0xc5};

  /**
   * Open the given wallet
   *
   * @param applicationDataDirectory The application data directory
   * @param walletId                 The wallet ID to locate the wallet
   * @param password                 The password to use to decrypt the wallet
   */
  public Optional<WalletSummary> open(File applicationDataDirectory, WalletId walletId, CharSequence password) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");
    Preconditions.checkNotNull(password, "'password' must be present");

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

        checkWalletDirectory(walletDirectory);

        String walletDirectoryPath = walletDirectory.getAbsolutePath();
        if (walletDirectoryPath.contains(walletIdPath)) {
          // Found the required wallet directory - attempt to open the wallet
          WalletSummary walletSummary = loadFromWalletDirectory(walletDirectory, password);
          currentWalletSummary = Optional.of(walletSummary);
        }

      }

    } else {
      currentWalletSummary = Optional.absent();
    }

    return currentWalletSummary;
  }

  /**
   * @param shutdownEvent The shutdown event
   */
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    currentWalletSummary = Optional.absent();

  }

  /**
   * Create a wallet
   * This is stored in the MultiBitHD application data directory
   * The name of the wallet file is derived from the seed.
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   *
   * @param seed                  the seed used to initialise the wallet
   * @param creationTimeInSeconds The creation time of the wallet, in seconds since epoch
   * @param password              to use to encrypt the wallet
   * @param name                  The wallet name
   * @param notes                 Public notes associated with the wallet
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
   */
  public WalletSummary createWalletSummary(
    byte[] seed,
    long creationTimeInSeconds,
    String password,
    String name,
    String notes

  ) throws WalletLoadException, WalletVersionException, IOException {

    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    return getOrCreateWalletSummary(applicationDataDirectory, seed, creationTimeInSeconds, password, name, notes);

  }

  /**
   * Create a wallet.
   * This is stored in the specified directory.
   * The name of the wallet file is derived from the seed.
   * <p/>
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   * <p/>
   * Auto-save is hooked up so that the wallet is changed on modification
   *
   * @param applicationDataDirectory The application data directory containing the wallet
   * @param seed                     The seed phrase to initialise the wallet
   * @param creationTimeInSeconds    The creation time of the wallet, in seconds since epoch
   * @param password                 The password to use to encrypt the wallet - if mull then the wallet is not loaded
   * @param name                     The wallet name
   * @param notes                    Public notes associated with the wallet
   *
   * @return Wallet summary containing the wallet object and the walletId (used in storage etc)
   *
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletSummary getOrCreateWalletSummary(
    File applicationDataDirectory,
    byte[] seed,
    long creationTimeInSeconds,
    String password,
    String name,
    String notes
  ) throws WalletLoadException, WalletVersionException, IOException {

    final WalletSummary walletSummary;

    // Create a wallet id from the seed to work out the wallet root directory
    final WalletId walletId = new WalletId(seed);
    String walletRoot = createWalletRoot(walletId);

    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, walletRoot);

    checkWalletDirectory(walletDirectory);

    final File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    final File walletFileWithAES = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME + MBHD_AES_SUFFIX);
    if (walletFileWithAES.exists()) {

      // There is already a wallet created with this root - if so load it and return that
      walletSummary = loadFromWalletDirectory(walletDirectory, password);
      if (Configurations.currentConfiguration != null) {
        Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);
      }
      setCurrentWalletSummary(walletSummary);

      return walletSummary;
    }

    // Wallet file does not exist so create it

    // Create the containing directory if it does not exist
    if (!walletDirectory.exists()) {
      if (!walletDirectory.mkdir()) {
        throw new IllegalStateException("The directory for the wallet '" + walletDirectory.getAbsoluteFile() + "' could not be created");
      }
    }

    // Create a wallet using the seed and password
    DeterministicSeed deterministicSeed = new DeterministicSeed(seed, "", creationTimeInSeconds);
    Wallet walletToReturn = Wallet.fromSeed(networkParameters, deterministicSeed);
    walletToReturn.setKeychainLookaheadSize(LOOK_AHEAD_SIZE);
    walletToReturn.encrypt(password);
    walletToReturn.setVersion(MBHD_WALLET_VERSION);

    // Set up auto-save on the wallet.
    // This ensures the wallet is saved on modification
    // The listener has a 'after save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
    walletToReturn.autosaveToFile(walletFile, AUTO_SAVE_DELAY, TimeUnit.SECONDS, new WalletAutoSaveListener());

    // Save it now to ensure it is on the disk
    walletToReturn.saveToFile(walletFile);
    EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, password);

    if (Configurations.currentConfiguration != null) {
      Configurations.currentConfiguration.getWallet().setCurrentWalletRoot(walletRoot);
    }

    // Create a new wallet summary
    walletSummary = new WalletSummary(walletId, walletToReturn);
    walletSummary.setName(name);
    walletSummary.setNotes(notes);
    walletSummary.setPassword(password);
    setCurrentWalletSummary(walletSummary);

    try {
      WalletManager.writeEncryptedPasswordAndBackupKey(walletSummary, seed, (String) password);
      File walletSummaryFile = WalletManager.getOrCreateWalletSummaryFile(walletDirectory);
      WalletManager.updateWalletSummary(walletSummaryFile, walletSummary);
    } catch (NoSuchAlgorithmException e) {
      throw new WalletLoadException("could not store encrypted password and backup AES key", e);
    }

    // See if there is a checkpoints file - if not then get the InstallationManager to copy one in
    File checkpointsFile = new File(walletDirectory.getAbsolutePath() + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);
    InstallationManager.copyCheckpointsTo(checkpointsFile);

    // Do not create an initial rolling backup and zip backup (the backup location may not exist)

    return walletSummary;
  }

  public static Wallet loadWalletFromFile(File walletFile, CharSequence password) throws IOException, UnreadableWalletException {
    // Read the encrypted file in and decrypt it.
    byte[] encryptedWalletBytes = org.multibit.hd.brit.utils.FileUtils.readFile(walletFile);
    log.trace("Encrypted wallet bytes after load:\n" + Utils.HEX.encode(encryptedWalletBytes));

    KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(EncryptedFileReaderWriter.makeScryptParameters(WalletManager.SCRYPT_SALT));
    KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);

    // Decrypt the wallet bytes
    byte[] decryptedBytes = AESUtils.decrypt(encryptedWalletBytes, keyParameter, WalletManager.AES_INITIALISATION_VECTOR);

    InputStream inputStream = new ByteArrayInputStream(decryptedBytes);

    Protos.Wallet walletProto = WalletProtobufSerializer.parseToProto(inputStream);

    WalletExtension[] walletExtensions = new WalletExtension[]{new SendFeeDtoWalletExtension(), new MatcherResponseWalletExtension()};
    Wallet wallet = new WalletProtobufSerializer().readWallet(BitcoinNetwork.current().get(), walletExtensions, walletProto);
    wallet.setKeychainLookaheadSize(WalletManager.LOOK_AHEAD_SIZE);

    log.debug("Just loaded wallet:\n" + wallet.toString());
    return wallet;
  }

  /**
   * <p>Load up an encrypted Wallet from a specified wallet directory.</p>
   * <p>Reduced visibility for testing</p>
   *
   * @param walletDirectory The wallet directory containing the various wallet files to load
   * @param password        The password to use to decrypt the wallet
   *
   * @return Wallet - the loaded wallet
   *
   * @throws WalletLoadException    If the wallet could not be loaded
   * @throws WalletVersionException If the wallet has an unsupported version number
   */
  WalletSummary loadFromWalletDirectory(File walletDirectory, CharSequence password) throws WalletLoadException, WalletVersionException {

    Preconditions.checkNotNull(walletDirectory, "'walletDirectory' must be present");
    Preconditions.checkNotNull(password, "'password' must be present");
    checkWalletDirectory(walletDirectory);

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
        log.error(e.getClass().getCanonicalName() + " " + e.getMessage());

        // Try loading one of the rolling backups - this will send a BackupWalletLoadedEvent containing the initial error
        // If the rolling backups don't load then loadRollingBackup will throw a WalletLoadException which will propagate out
        wallet = BackupManager.INSTANCE.loadRollingBackup(walletId, password);
      }

      // Create the wallet summary with its wallet
      WalletSummary walletSummary = getOrCreateWalletSummary(walletDirectory, walletId);
      walletSummary.setWallet(wallet);
      walletSummary.setPassword(password);
      setCurrentWalletSummary(walletSummary);

      // Set up autosave on the wallet.
      // This ensures the wallet is saved on modification
      // The listener has a 'post save' callback which:
      // + encrypts the wallet
      // + ensures rolling backups
      // + local/ cloud backups are also saved where necessary
      wallet.autosaveToFile(new File(walletFilenameNoAESSuffix), AUTO_SAVE_DELAY, TimeUnit.SECONDS, new WalletAutoSaveListener());

      return walletSummary;

    } catch (WalletVersionException wve) {
      // We want this to propagate out as is
      throw wve;
    } catch (Exception e) {
      throw new WalletLoadException(e.getMessage(), e);
    }
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
  // TODO (GR) Refactor this to take a WalletId and infer the prefix to avoid info leak
  public static File getOrCreateWalletDirectory(File applicationDataDirectory, String walletRoot) {

    File walletDirectory = SecureFiles.verifyOrCreateDirectory(applicationDataDirectory, walletRoot);

    checkWalletDirectory(walletDirectory);

    if (!walletDirectory.exists()) {
      // Create the wallet directory
      Preconditions.checkState(walletDirectory.mkdir(), "Could not create missing wallet directory '" + walletRoot + "'");
    }

    Preconditions.checkState(walletDirectory.isDirectory(), "'walletDirectory' must be a directory");

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
    return Optional.fromNullable(Configurations.currentConfiguration.getWallet().getCurrentWalletRoot());
  }

  /**
   * @param walletSummary The wallet summary to write
   */
  public static void updateWalletSummary(File walletSummaryFile, WalletSummary walletSummary) {

    // Persist the new configuration
    try (FileOutputStream fos = new FileOutputStream(walletSummaryFile)) {

      Configurations.writeYaml(fos, walletSummary);

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

    checkWalletDirectory(walletDirectory);

    Optional<WalletSummary> walletSummaryOptional = Optional.absent();

    File walletSummaryFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_SUMMARY_NAME);
    if (walletSummaryFile.exists()) {
      try (InputStream is = new FileInputStream(walletSummaryFile)) {
        // Load configuration (providing a default if none exists)
        walletSummaryOptional = Configurations.readYaml(is, WalletSummary.class);
      } catch (IOException e) {
        log.warn("Could not read wallet summary in '{}': {}. Creating default.", walletDirectory.getAbsolutePath(), e.getMessage());
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
   * Write the encrypted wallet password and backup AES key to the wallet configuration.
   * You probably want to save it afterwards with an updateSummary
   */
  public static void writeEncryptedPasswordAndBackupKey(WalletSummary walletSummary, byte[] seed, String password) throws NoSuchAlgorithmException {
    // Save the wallet password, AES encrypted with a key derived from the wallet seed
    KeyParameter seedDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(seed, SCRYPT_SALT);
    byte[] passwordBytes = password.getBytes(Charsets.UTF_8);

    byte[] paddedPasswordBytes = padPasswordBytes(passwordBytes);
    byte[] encryptedPaddedPassword = org.multibit.hd.brit.crypto.AESUtils.encrypt(paddedPasswordBytes, seedDerivedAESKey, AES_INITIALISATION_VECTOR);
    walletSummary.setEncryptedPassword(encryptedPaddedPassword);

    // Save the backupAESKey, AES encrypted with a key generated from the wallet password
    KeyParameter walletPasswordDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(passwordBytes, SCRYPT_SALT);
    byte[] encryptedBackupAESKey = org.multibit.hd.brit.crypto.AESUtils.encrypt(seedDerivedAESKey.getKey(), walletPasswordDerivedAESKey, AES_INITIALISATION_VECTOR);
    walletSummary.setEncryptedBackupKey(encryptedBackupAESKey);
  }

  /**
   * @param walletDirectory The candidate wallet directory (e.g. "/User/example/Application Support/MultiBitHD/mbhd-11111111-22222222-33333333-44444444-55555555")
   *
   * @throws IllegalStateException If the wallet directory is malformed
   */
  private static void checkWalletDirectory(File walletDirectory) {

    Preconditions.checkState(walletDirectory.isDirectory(), "'walletDirectory' must be a directory: '" + walletDirectory.getAbsolutePath() + "'");

    // Use the pre-compiled regex
    boolean result = walletDirectoryPattern.matcher(walletDirectory.getName()).matches();

    Preconditions.checkState(result, "'walletDirectory' is not named correctly: '" + walletDirectory.getAbsolutePath() + "'");

  }


  /**
   * <p>Method to sign a message</p>
   *
   * @param addressText    Text address to use to sign (makes UI Address conversion code DRY)
   * @param messageText    The message to sign
   * @param walletPassword The wallet password
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

        if (signingKey == null) {
          // No signing key found.
          return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_NO_SIGNING_KEY, new Object[]{addressText});
        } else {
          if (signingKey.getKeyCrypter() != null) {
            KeyParameter aesKey = signingKey.getKeyCrypter().deriveKey(walletPassword);
            ECKey decryptedSigingKey = signingKey.decrypt(aesKey);

            String signatureBase64 = decryptedSigingKey.signMessage(messageText);
            return new SignMessageResult(Optional.of(signatureBase64), true, CoreMessageKey.SIGN_MESSAGE_SUCCESS, null);
          } else {
            // The signing key is not encrypted but it should be
            return new SignMessageResult(Optional.<String>absent(), false, CoreMessageKey.SIGN_MESSAGE_SIGNING_KEY_NOT_ENCRYPTED, null);
          }
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
   * the length of the encrypted password (which is always a multiple of the AES block size (16 bytes).
   *
   * @param passwordBytes the password bytes to pad
   *
   * @return paddedPasswordBytes - this is guaranteed to be longer than 48 bytes. Byte 0 indicates the number of padding bytes,
   * which are random bytes stored from byte 1 to byte <number of padding bytes). The real password is stored int he remaining bytes
   */
  public static byte[] padPasswordBytes(byte[] passwordBytes) {
    if (passwordBytes.length > AESUtils.BLOCK_LENGTH * 3) {
      // No padding required - add a zero to the beginning of the password bytes (to indicate no padding bytes)
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
   * Unpad passowrd bytes, removing the random prefix bytes length marker byte and te random bytes themselves
   */
  public static byte[] unpadPasswordBytes(byte[] paddedPasswordBytes) {
    Preconditions.checkNotNull(paddedPasswordBytes);
    Preconditions.checkState(paddedPasswordBytes.length > 0);

    // Get the length of the pad
    int lengthOfPad = (int) paddedPasswordBytes[0];

    if (lengthOfPad > paddedPasswordBytes.length - 1) {
      throw new IllegalStateException("Stored encrypted password is not in the correct format");
    }
    return Arrays.copyOfRange(paddedPasswordBytes, 1 + lengthOfPad, paddedPasswordBytes.length);
  }
}
