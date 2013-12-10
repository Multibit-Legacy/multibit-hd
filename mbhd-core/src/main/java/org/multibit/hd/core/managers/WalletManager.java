package org.multibit.hd.core.managers;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.hd.core.exceptions.WalletSaveException;
import org.multibit.hd.core.exceptions.WalletVersionException;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.utils.MultiBitFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

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

  public static final String SIMPLE_WALLET_DIRECTORY_NAME = "simple";

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit
   */
  public static final int ENCRYPTED_WALLET_VERSION = 3;

  public static final String MBHD_WALLET_NAME = "mbhd.wallet";

  private WalletProtobufSerializer walletProtobufSerializer;

  public WalletManager() {
    walletProtobufSerializer = new WalletProtobufSerializer();
    // TODO was originally multibit protobuf serializer - ok ?
  }


  /**
   * Create a simple wallet that contains only a single, random private key.
   * This is stored in the MultiBitHD application data directory and is called "simple"
   * <p/>
   * If the wallet file already exists it is loaded and returned (and the input password is not used)
   *
   * @param password to use to encrypt the wallet
   * @return Wallet
   * @throws IllegalStateException  if applicationDataDirectory is incorrect
   * @throws WalletLoadException    if there is already a simple wallet created but it could not be loaded
   * @throws WalletVersionException if there is already a simple wallet but the wallet version cannot be understood
   */
  public Wallet createSimpleWallet(CharSequence password) throws WalletLoadException, WalletVersionException {
    // Work out the file location of the simple wallet
    String applicationDataDirectoryName = MultiBitFiles.createApplicationDataDirectory();

    File simpleDirectory = WalletManager.getWalletDirectory(applicationDataDirectoryName, SIMPLE_WALLET_DIRECTORY_NAME);
    File simpleWalletFile = new File(simpleDirectory.getAbsolutePath() + File.separator + MBHD_WALLET_NAME);
    if (simpleWalletFile.exists()) {
      // There is already a simple wallet created - if so load it and return that
      return loadFromFile(simpleWalletFile);
    } else {
      // Create the containing directory if it does not exist
      if (!simpleDirectory.exists()) {
        if (!simpleDirectory.mkdir()) {
          throw new IllegalStateException("The directory for the simple wallet '" + simpleDirectory.getAbsoluteFile() + "' could not be created");
        }
      }
      // Create a simple wallet with a single private key, encrypted with the password
      KeyCrypter keyCrypter = new KeyCrypterScrypt();

      Wallet newWallet = new Wallet(BitcoinNetworkService.NETWORK_PARAMETERS, keyCrypter);
      newWallet.setVersion(ENCRYPTED_WALLET_VERSION);

      ECKey newKey = new ECKey();
      newKey = newKey.encrypt(newWallet.getKeyCrypter(), newWallet.getKeyCrypter().deriveKey(password));
      newWallet.addKey(newKey);

      // Save it
      saveWallet(newWallet, simpleWalletFile.getAbsolutePath());

      return newWallet;
    }
  }

  /**
   * Load up a Wallet from a specified wallet file.
   *
   * @param walletFile
   * @return Wallet - the loaded wallet
   * @throws WalletLoadException
   * @throws WalletVersionException
   */
  public Wallet loadFromFile(File walletFile) throws WalletLoadException, WalletVersionException {
    if (walletFile == null) {
      return null;
    }

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
   * Returns the name of the current wallet.
   *
   * @throws IllegalStateException if no wallet is currently defined
   */
  public String getCurrentWalletFilename() {
    String currentWalletFilename = Configurations.currentConfiguration.getApplicationConfiguration().getCurrentWalletFilename();

    Preconditions.checkState(currentWalletFilename != null && !"".equals(currentWalletFilename.trim()));

    return currentWalletFilename;
  }

  /**
   * Create a directory composed of the root directory and a suddirectory.
   * The subdirectory is created if it does not exist
   *
   * @param rootDirectory       The root directory in which to create the directory
   * @param walletDirectoryName The name of the wallet which will be used to create a subdirectory
   * @return The directory composed of rootDirectory plus the walletDirectoryName
   * @throws IllegalStateException if wallet could not be created
   */
  public static File getWalletDirectory(String rootDirectory, String walletDirectoryName) {
    String fullWalletDirectoryName = rootDirectory + File.separator + walletDirectoryName;
    File walletDirectory = new File(fullWalletDirectoryName);

    if (!walletDirectory.exists()) {
      // Create the wallet directory.
      if (!walletDirectory.mkdir()) {
        throw new IllegalStateException("Could not create missing wallet directory '" + walletDirectoryName + "'");
      }
    }

    if (!walletDirectory.isDirectory()) {
      throw new IllegalStateException("Wallet directory '" + walletDirectoryName + "' is not actually a directory");
    }

    return walletDirectory;
  }
}
