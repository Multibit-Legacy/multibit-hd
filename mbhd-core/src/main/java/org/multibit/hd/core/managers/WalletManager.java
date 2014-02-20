package org.multibit.hd.core.managers;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletData;
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

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  <p>Manager to provide the following to core users:<br>
 *  <ul>
 *  <li>create wallet</li>
 *  <li>save wallet wallet</li>
 *  <li>load wallet wallet</li>
 *  <li>tracks the current wallet and the list of wallet directories</li>
 *  </ul>
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


  // TODO remove the storage of the seed in memory
  private byte[] seed;

  // TODO remove the storage of the password in memory
  private CharSequence password;

  private static final int AUTOSAVE_DELAY = 20000; // millisecond

  private static WalletProtobufSerializer walletProtobufSerializer;

  static {
    walletProtobufSerializer = new WalletProtobufSerializer();
    // TODO was originally multibit protobuf serializer - ok ?
  }

  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  public static final String WALLET_DIRECTORY_PREFIX = "mbhd";

  public static final String SEPARATOR = "-";

  // The format of the wallet directories is WALLET_DIRECTORY_PREFIX + a wallet id.
  // A walletid is 5 groups of 4 bytes in lowercase hex, with a "-' separator e.g. mbhd-11111111-22222222-33333333-44444444-55555555
  public static final String REGEX_FOR_WALLET_DIRECTORY = "^" + WALLET_DIRECTORY_PREFIX + SEPARATOR + "[0-9a-f]{8}"
          + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}$";

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit
   */
  public static final int ENCRYPTED_WALLET_VERSION = 3; // TODO - need a new version when the wallet HD format is created

  public static final String MBHD_WALLET_PREFIX = "mbhd";
  public static final String MBHD_WALLET_SUFFIX = ".wallet";
  public static final String MBHD_WALLET_NAME = MBHD_WALLET_PREFIX + MBHD_WALLET_SUFFIX;

  private File applicationDataDirectory;

  private List<File> walletDirectories = Lists.newArrayList();

  private Optional<WalletData> currentWalletData = Optional.absent();

  /**
   * Initialise enum, loadContacts up the available wallets and find the current wallet
   *
   * @param applicationDataDirectory The directory in which to writeContacts and read wallets.
   */
  public void initialise(File applicationDataDirectory) {
    this.applicationDataDirectory = applicationDataDirectory;

    // Work out the list of available wallets in the application data directory
    walletDirectories = findWalletDirectories(applicationDataDirectory);

    // TODO enable user to switch wallets - currently using the first

    // If a wallet directory is present try to load the wallet
    if (!walletDirectories.isEmpty()) {
      String walletFilename = walletDirectories.get(0) + File.separator + MBHD_WALLET_NAME;
      WalletData walletData = loadFromFile(new File(walletFilename));
      currentWalletData = Optional.of(walletData);
    } else {
      currentWalletData = Optional.absent();
    }
  }

  /**
   * Create a wallet that contains only a single, random private key.
   * This is stored in the MultiBitHD application data directory
   * The name of the wallet file is derived from the seed.
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   *
   * @param seed     the seed used to initialise the wallet
   * @param password to use to encrypt the wallet
   * @return WalletData containing the wallet object and the walletId (used in storage etc)
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
   */
  public WalletData createWallet(byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    return createWallet(applicationDataDirectory.getAbsolutePath(), seed, password);
  }


  /**
   * Create a wallet that contains only a single, random private key.
   * This is stored in the specified directory.
   * The name of the wallet file is derived from the seed.
   * <p/>
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   * <p/>
   * Autosave is hooked up so that the wallet is changed on modification
   *
   * @param parentDirectoryName the name of the directory in which the wallet directory will be created (normally the application data directory)
   * @param seed                the seed used to initialise the wallet
   * @param password            to use to encrypt the wallet
   * @return WalletData containing the wallet object and the walletId (used in storage etc)
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a wallet but the wallet version cannot be understood
   */
  public WalletData createWallet(String parentDirectoryName, byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {
    WalletData walletDataToReturn;

    this.seed = seed;
    this.password = password;

    // Create a wallet id from the seed to work out the wallet root directory
    WalletId walletId = new WalletId(seed);
    String walletRoot = createWalletRoot(walletId);

    File walletDirectory = WalletManager.getWalletDirectory(parentDirectoryName, walletRoot);
    File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    if (walletFile.exists()) {
      // There is already a wallet created with this root - if so loadContacts it and return that
      walletDataToReturn = loadFromFile(walletFile);
      if (Configurations.currentConfiguration != null) {
        Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot);
      }
      setCurrentWalletData(walletDataToReturn);
    } else {
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

      // Add the 'zero index key into the wallet
      createAndAddNewWalletKey(walletToReturn, 0);

      // Set up autosave on the wallet.
      // This ensures the wallet is saved on modification
      // The listener has a 'after save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
      walletToReturn.autosaveToFile(walletFile, AUTOSAVE_DELAY, TimeUnit.MILLISECONDS, new WalletAutoSaveListener());

      // Save it now to ensure it is on the disk
      saveWallet(walletToReturn, walletFile.getAbsolutePath());
      if (Configurations.currentConfiguration != null) {
        Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot);
      }
      walletDataToReturn = new WalletData(walletId, walletToReturn);
      setCurrentWalletData(walletDataToReturn);
    }

    // See if there is a checkpoints file - if not then get the InstallationManager to copy one in
    String checkpointsFilename = walletDirectory.getAbsolutePath() + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX;
    InstallationManager.copyCheckpointsTo(checkpointsFilename);

    // Create an initial rolling backup and zip backup
    BackupManager.INSTANCE.createRollingBackup(currentWalletData.get());
    BackupManager.INSTANCE.createLocalAndCloudBackup(currentWalletData.get().getWalletId());

    return walletDataToReturn;
  }

  /**
   * Create a new key for the wallet using the seed of the wallet and an index 0, 1,2,3,4 etc.
   *
   * TODO this will be replaced by the proper HD wallet algorithm when it is added to bitcoinj
   * Note that the "last used index"needs to be persisted - this is currently stored in the top level of the payments db
   * but it would be better in the wallet itself
   *
   * TODO also the seed and password should not be stored in memory as is currently being done
   */
  public ECKey createAndAddNewWalletKey(Wallet wallet, int indexToCreate) {
      // Ensure that the seed combined with the index is within the Bitcoin EC group.
      BigInteger privateKeyToUse = moduloSeedByECGroupSize(new BigInteger(1, seed).add(BigInteger.valueOf(indexToCreate)));

      ECKey newKey = new ECKey(privateKeyToUse);
      newKey = newKey.encrypt(wallet.getKeyCrypter(), wallet.getKeyCrypter().deriveKey(password));
      wallet.addKey(newKey);

    return newKey;
  }

  /**
   * Ensure that the seed is within the range of the bitcoin EC group
   *
   * @param seedAsBigInteger the seed - converted to a BigInteger
   * @return the seed, guaranteed to be within the Bitcoin EC group range
   */
  private BigInteger moduloSeedByECGroupSize(BigInteger seedAsBigInteger) {
    X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    BigInteger sizeOfGroup = params.getN();

    return seedAsBigInteger.mod(sizeOfGroup);
  }

  /**
   * Load up a Wallet from a specified wallet file.
   *
   * @param walletFile The file containing the wallet to loadContacts
   * @return Wallet - the loaded wallet
   * @throws IllegalArgumentException if wallet file is null
   * @throws WalletLoadException
   * @throws WalletVersionException
   */
  public WalletData loadFromFile(File walletFile) throws WalletLoadException, WalletVersionException {
    Preconditions.checkNotNull(walletFile);

    String walletFilename = walletFile.getAbsolutePath();

    WalletId walletId = WalletId.parseWalletFilename(walletFilename);

    try {
      if (isWalletSerialised(walletFile)) {
        // Serialised wallets are no longer supported.
        throw new WalletLoadException("Could not loadContacts wallet '" + walletFilename
                + "'. Serialized wallets are no longer supported.");
      }

      Wallet wallet;

      try (FileInputStream fileInputStream = new FileInputStream(walletFile); InputStream stream = new BufferedInputStream(fileInputStream);) {
        wallet = Wallet.loadFromFileStream(stream);
      } catch (WalletVersionException wve) {
        // We want this exception to propagate out.
        throw wve;
      } catch (Exception e) {
        log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
        throw new WalletLoadException(e.getMessage(), e);
      }

      WalletData walletData = new WalletData(walletId, wallet);
      setCurrentWalletData(walletData);

      // Set up autosave on the wallet.
      // This ensures the wallet is saved on modification
      // The listener has a 'post save' callback which ensures rolling backups and local/ cloud backups are also saved where necessary
      wallet.autosaveToFile(walletFile, AUTOSAVE_DELAY, TimeUnit.MILLISECONDS, new WalletAutoSaveListener());

      return walletData;
    } catch (WalletVersionException wve) {
      // We want this to propagate out as is
      throw wve;
    } catch (Exception e) {
      log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
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

    FileOutputStream fileOutputStream = null;

    // Save the wallet file
    try {
      if (wallet != null) {
        log.debug("Saving wallet file '" + walletFile.getAbsolutePath() + "' ...");
        if (3 == wallet.getVersion()) { // 3 = PROTOBUF_ENCRYPTED
          fileOutputStream = new FileOutputStream(walletFile);

          // Save as a Wallet message with a mandatory extension
          // to prevent loading by older versions of multibit.
          walletProtobufSerializer.writeWallet(wallet, fileOutputStream);
        } else {
          throw new WalletVersionException("Cannot save wallet '" + walletFilename
                  + "'. Its wallet version is '" + wallet.getVersion()
                  + "' but this version of MultiBit does not understand that format.");
        }
      }
      log.debug("... done saving wallet file.");

    } catch (IOException | UnsupportedOperationException e) {
      throw new WalletSaveException("Cannot save wallet '" + walletFilename, e);
    } finally {
      if (fileOutputStream != null) {
        try {
          fileOutputStream.flush();
          fileOutputStream.close();
        } catch (IOException e) {
          throw new WalletSaveException("Cannot save wallet '" + walletFilename, e);
        }
      }
    }
  }

  /**
   * @param walletFile the wallet to test serialisation for
   * @return true if the wallet file specified is serialised (this format is no longer supported)
   */
  private boolean isWalletSerialised(File walletFile) {
    boolean isWalletSerialised = false;
    InputStream stream = null;
    try {
      // Determine what kind of wallet stream this is: Java Serialization
      // or protobuf format.
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
   * @return directoryName in which the wallet is stored.
   */
  public static String createWalletRoot(WalletId walletId) {
    return WALLET_DIRECTORY_PREFIX + SEPARATOR + walletId.toFormattedString();
  }

  /**
   * Create a directory composed of the root directory and a subdirectory.
   * The subdirectory is created if it does not exist
   *
   * @param parentDirectory The root directory in which to create the directory
   * @param walletRoot      The name of the wallet which will be used to create a subdirectory
   * @return The directory composed of parentDirectory plus the walletRoot
   * @throws IllegalStateException if wallet could not be created
   */
  public static File getWalletDirectory(String parentDirectory, String walletRoot) {
    String fullWalletDirectoryName = parentDirectory + File.separator + walletRoot;
    File walletDirectory = new File(fullWalletDirectoryName);

    if (!walletDirectory.exists()) {
      // Create the wallet directory.
      if (!walletDirectory.mkdir()) {
        throw new IllegalStateException("Could not create missing wallet directory '" + walletRoot + "'");
      }
    }

    if (!walletDirectory.isDirectory()) {
      throw new IllegalStateException("Wallet directory '" + walletRoot + "' is not actually a directory");
    }

    return walletDirectory;
  }

  /**
   * Work out what wallets are available in a directory (typically the user data directory).
   * This is worked out by looking for directories with the name:
   * 'multibithd' + a wallet id
   *
   * @param directoryToSearch The directory to search
   * @return List<File> List of files of wallet directories
   */
  public List<File> findWalletDirectories(File directoryToSearch) {
    Preconditions.checkNotNull(directoryToSearch);

    File[] listOfFiles = directoryToSearch.listFiles();

    List<File> walletDirectories = Lists.newArrayList();
    // Look for filenames with format "mbhd"-"walletid" and are not empty.
    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isDirectory()) {
          String filename = listOfFiles[i].getName();
          if (filename.matches(REGEX_FOR_WALLET_DIRECTORY)) {
            if (listOfFiles[i].length() > 0) {
              walletDirectories.add(listOfFiles[i]);
            }
          }
        }
      }
    }

    return walletDirectories;
  }

  public Optional<WalletData> getCurrentWalletData() {
    return currentWalletData;
  }

  public void setCurrentWalletData(WalletData currentWalletData) {
    if (currentWalletData.getWallet() != null) {
      // Remove the previous WalletEventListener
      currentWalletData.getWallet().removeEventListener(this);
    }

    // Add the wallet event listener
    currentWalletData.getWallet().addEventListener(this);
    this.currentWalletData = Optional.of(currentWalletData);
  }

  public Optional<File> getCurrentWalletFilename() {
    if (applicationDataDirectory != null && currentWalletData.isPresent()) {
      String walletFilename = applicationDataDirectory + File.separator + WALLET_DIRECTORY_PREFIX + SEPARATOR +
              currentWalletData.get().getWalletId().toFormattedString() + File.separator + MBHD_WALLET_NAME;
      return Optional.of(new File(walletFilename));
    } else {
      return Optional.absent();
    }
  }
}
