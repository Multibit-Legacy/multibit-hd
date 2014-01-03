package org.multibit.hd.core.managers;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.config.Configurations;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  <p>Manager to provide the following to core users:<br>
 *  <ul>
 *  <li>create wallet</li>
 *  <li>load wallet</li>
 *  <li>store wallet</li>
 *  <li>backup wallet</li>
 *  </ul>
 */
public class WalletManager {
  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  public static final String WALLET_DIRECTORY_PREFIX = "multibithd";

  private static final String SEPARATOR = "-";

  // The format of the wallet directories is "multibithd" + a wallet id.
  // A walletid is 5 groups of 4 bytes in lowercase hex, with a "-' separator e.g. multibithd-11111111-22222222-33333333-44444444-55555555
  public static final String REGEX_FOR_WALLET_DIRECTORY = "^" + WALLET_DIRECTORY_PREFIX + SEPARATOR + "[0-9a-f]{8}"
          + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}" + SEPARATOR + "[0-9a-f]{8}$";

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit
   */
  public static final int ENCRYPTED_WALLET_VERSION = 3;

  public static final String MBHD_WALLET_NAME = "mbhd.wallet";

  private WalletProtobufSerializer walletProtobufSerializer;

  private Wallet currentWallet;

  public WalletManager() {
    walletProtobufSerializer = new WalletProtobufSerializer();
    // TODO was originally multibit protobuf serializer - ok ?
  }


  /**
   * Create a wallet that contains only a single, random private key.
   * This is stored in the MultiBitHD application data directory
   * The name of the wallet file is derived from the seed.
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   *
   * @param seed the seed used to initialise the wallet
   * @param password to use to encrypt the wallet
   * @return Wallet
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
   */
  public Wallet createWallet(byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {
    String applicationDataDirectoryName = InstallationManager.createApplicationDataDirectory();
    return createWallet(applicationDataDirectoryName, seed, password);
  }


    /**
      * Create a simple wallet that contains only a single, random private key.
      * This is stored in the specified directory.
      * The name of the wallet file is derived from the seed.
      * <p/>
      * If the wallet file already exists it is loaded and returned (and the input password is not used)
      *
      * @param parentDirectoryName the name of the directory in which the wallet directory will be created (normally the application data directory)
      * @param seed the seed used to initialise the wallet
      * @param password to use to encrypt the wallet
      * @return Wallet
      * @throws IllegalStateException  if applicationDataDirectory is incorrect
      * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
      * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
      */
     public Wallet createWallet(String parentDirectoryName, byte[] seed, CharSequence password) throws WalletLoadException, WalletVersionException, IOException {

    // Work out the file location of the simple wallet
    Wallet walletToReturn;

    // Create a wallet id from the seed to work out the name of the wallet
    WalletId walletId = new WalletId(seed);
    String walletRoot = WALLET_DIRECTORY_PREFIX + SEPARATOR + walletId.toFormattedString();

    File walletDirectory = WalletManager.getWalletDirectory(parentDirectoryName, walletRoot);
    File walletFile = new File(walletDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    if (walletFile.exists()) {
      // There is already a wallet created with this root - if so load it and return that
      walletToReturn = loadFromFile(walletFile);
      Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot);
    } else {
      // Create the containing directory if it does not exist
      if (!walletDirectory.exists()) {
        if (!walletDirectory.mkdir()) {
          throw new IllegalStateException("The directory for the wallet '" + walletDirectory.getAbsoluteFile() + "' could not be created");
        }
      }
      // Create a wallet with a single private key using the seed (modulo-ed), encrypted with the password
      KeyCrypter keyCrypter = new KeyCrypterScrypt();

      walletToReturn = new Wallet(BitcoinNetworkService.NETWORK_PARAMETERS, keyCrypter);
      walletToReturn.setVersion(ENCRYPTED_WALLET_VERSION);

      // Ensure that the seed is within the Bitcoin EC group.
      X9ECParameters params = SECNamedCurves.getByName("secp256k1");
      BigInteger sizeOfGroup = params.getN();

      BigInteger seedBigInteger = new BigInteger(1, seed);
      seedBigInteger = seedBigInteger.mod(sizeOfGroup);

      ECKey newKey = new ECKey(seedBigInteger);
      newKey = newKey.encrypt(walletToReturn.getKeyCrypter(), walletToReturn.getKeyCrypter().deriveKey(password));
      walletToReturn.addKey(newKey);

      // Save it
      saveWallet(walletToReturn, walletFile.getAbsolutePath());
      Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot);
      setCurrentWallet(walletToReturn);
    }

    // See if there is a checkpoints file - if not then get the InstallationManager to copy one in
    String checkpointsFilename = walletDirectory.getAbsolutePath() + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX;
    InstallationManager.copyCheckpointsTo(checkpointsFilename);

    return walletToReturn;
  }

  /**
   * Load up a Wallet from a specified wallet file.
   *
   * @param walletFile
   * @return Wallet - the loaded wallet
   * @throws IllegalArgumentException if wallet file is null
   * @throws WalletLoadException
   * @throws WalletVersionException
   */
  public Wallet loadFromFile(File walletFile) throws WalletLoadException, WalletVersionException {
    Preconditions.checkNotNull(walletFile);

    String walletFilenameToUseInModel = walletFile.getAbsolutePath();

    try {

      if (isWalletSerialised(walletFile)) {
        // Serialised wallets are no longer supported.
        throw new WalletLoadException("Could not load wallet '" + walletFilenameToUseInModel
                + "'. Serialized wallets are no longer supported.");
      }

      // If the wallet file is missing or empty but the backup file exists
      // load that instead. This indicates that the write was interrupted
      // (e.g. power loss).
      boolean useBackupWallets = (!walletFile.exists() || walletFile.length() == 0);
      boolean walletWasLoadedSuccessfully = false;
      Collection<String> errorMessages = new ArrayList<String>();

      Wallet wallet = null;

      // Try the main wallet first unless it is obviously broken.
      if (!useBackupWallets) {
        FileInputStream fileInputStream = new FileInputStream(walletFile);
        InputStream stream = null;

        try {
          stream = new BufferedInputStream(fileInputStream);
          wallet = Wallet.loadFromFileStream(stream);
          walletWasLoadedSuccessfully = true;
        } catch (WalletVersionException wve) {
          // We want this exception to propagate out.
          throw wve;
        } catch (Exception e) {
          e.printStackTrace();
          String description = e.getClass().getCanonicalName() + " " + e.getMessage();
          log.error(description);
          errorMessages.add(description);
        } finally {
          if (stream != null) {
            stream.close();
          }
          fileInputStream.close();
        }
      }

//          if (!walletWasLoadedSuccessfully) {
//              // If the main wallet was not loaded successfully, work out the best backup
//              // wallets to try and load them.
//              useBackupWallets = true;
//
//              Collection<String> backupWalletsToTry = BackupManager.INSTANCE.calculateBestWalletBackups(walletFile, walletInfo);
//
//              Iterator<String> iterator = backupWalletsToTry.iterator();
//              while (!walletWasLoadedSuccessfully && iterator.hasNext()) {
//                 String walletToTry = iterator.next();
//
//                 FileInputStream fileInputStream = new FileInputStream(new File(walletToTry));
//                 InputStream stream = null;
//
//                 try {
//                     stream = new BufferedInputStream(fileInputStream);
//                     wallet = Wallet.loadFromFileStream(stream);
//                     walletWasLoadedSuccessfully = true;
//
//                     // TODO Mention to user that backup is being used.
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                     String description = e.getClass().getCanonicalName() + " " + e.getMessage();
//                     log.error(description);
//                     errorMessages.add(description);
//                 } finally {
//                     if (stream != null) {
//                         stream.close();
//                     }
//                     fileInputStream.close();
//                 }
//             }
//         }


//      if (walletWasLoadedSuccessfully) {
      // Ensure that the directories for the backups of the
      // rolling backups and regular backups exist.
      //BackupManager.INSTANCE.createBackupDirectories(walletFile);


      // If the backup files were used save them immediately and don't
      // delete any rolling backups.
//             if (useBackupWallets) {
//                 // Wipe the wallet backup property so that the rolling
//                 // backup file will not be overwritten
//                 walletInfo.put(BitcoinModel.WALLET_BACKUP_FILE, "");
//
//                 // Save the wallet immediately just to be on the safe side.
//                 savePerWalletModelData(perWalletModelData, true);
//             }
//
//             synchronized (walletInfo) {
//                 rememberFileSizesAndLastModified(new File(walletFilenameToUseInModel), walletInfo);
//                 perWalletModelData.setDirty(false);
//             }
//      } else {
//        // No wallet was loaded successfully.
//        // Wipe the rolling backup property to ensure that file wont be deleted.
//             if (walletInfo != null) {
//                 walletInfo.put(BitcoinModel.WALLET_BACKUP_FILE, "");
//             }
//
//        // Report failure to user.
//             String messageText = bitcoinController.getLocaliser().getString("fileHandler.unableToLoadWalletOrBackups", new String[] {walletFilenameToUseInModel});
//             if (!errorMessages.isEmpty()) {
//                 StringBuilder errorMessagesAsString = new StringBuilder();
//                 for (String errorText : errorMessages) {
//                     if (errorMessagesAsString.length()>0) {
//                         errorMessagesAsString.append("\n");
//                     }
//                     errorMessagesAsString.append(errorText);
//                 }
//                 messageText = messageText + "\n" + bitcoinController.getLocaliser().getString("deleteWalletConfirmDialog.walletDeleteError2", new String[]{errorMessagesAsString.toString()});
//             }
//             MessageManager.INSTANCE.addMessage(new Message(messageText));
//      }
      setCurrentWallet(wallet);
      return wallet;
    } catch (WalletVersionException wve) {
      // We want this to propagate out.
      throw wve;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
      throw new WalletLoadException(e.getClass().getCanonicalName() + " " + e.getMessage(), e);
    }
  }

  /**
   * Save the wallet
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

    } catch (IOException ioe) {
      throw new WalletSaveException("Cannot save wallet '" + walletFilename, ioe);
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
   * @param walletFile
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
    } catch (FileNotFoundException e) {
      log.error(e.getClass().getCanonicalName() + " " + e.getMessage());
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
   * Returns the filename of the current wallet.
   *
   * @throws IllegalStateException if no wallet is currently defined or if the application data directory is undefined
   */
  public String getCurrentWalletFilename() {
    String currentWalletRoot = Configurations.currentConfiguration.getApplicationConfiguration().getCurrentWalletRoot();

    String applicationDataDirectoryName = InstallationManager.createApplicationDataDirectory();

    File walletRootFile = getWalletDirectory(applicationDataDirectoryName, currentWalletRoot);
    String walletFilename = walletRootFile.getAbsolutePath() + File.separator + MBHD_WALLET_NAME;

    return walletFilename;
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

    List<File> walletDirectories = new ArrayList<File>();
    // Look for filenames with format "multibithd"-"walletid" and are not empty.
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

  public Wallet getCurrentWallet() {
    return currentWallet;
  }

  public void setCurrentWallet(Wallet currentWallet) {
    this.currentWallet = currentWallet;
  }
}
