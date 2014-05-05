package org.multibit.hd.core.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.utils.FileUtils;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.files.ZipFiles;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.multibit.hd.core.dto.WalletId.LENGTH_OF_FORMATTED_WALLET_ID;
import static org.multibit.hd.core.dto.WalletId.WALLET_ID_SEPARATOR;


/**
 * Class to manage creation and reading back of the wallet backups.
 */
public enum BackupManager {

  INSTANCE;

  public static final String BACKUP_ZIP_FILE_EXTENSION = ".zip";
  public static final String ENCRYPTED_BACKUP_FILE_EXTENSION = ".zip.aes";
  public static final String ENCRYPTED_BACKUP_ZIP_FILE_EXTENSION_REGEX = "\\.zip\\.aes";

  public static final String ROLLING_BACKUP_DIRECTORY_NAME = "rolling-backup";
  public static final int MAXIMUM_NUMBER_OF_ROLLING_BACKUPS = 4;

  // Backup suffix format is "yyyyMMddHHmmss"
  public static final String REGEX_FOR_TIMESTAMP_AND_WALLET_AND_AES_SUFFIX = ".*-\\d{14}\\.wallet\\.aes$";

  public static final String LOCAL_ZIP_BACKUP_DIRECTORY_NAME = "zip-backup";
  public static final int MAXIMUM_NUMBER_OF_ZIP_BACKUPS = 60; // Chosen so that you will have about weekly backups for a year, fortnightly over two years.
  public static final int NUMBER_OF_FIRST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 2;
  public static final int NUMBER_OF_LAST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 8; // Must be at least 1.

  private static final Logger log = LoggerFactory.getLogger(BackupManager.class);

  // Where wallets are stored
  private File applicationDataDirectory;

  // Where the cloud backups are stored (this is typically specified by the user and is a SpiderOak etc sync directory)
  private File cloudBackupDirectory;

  /**
   * Initialise the backup manager to use the specified cloudBackupDirectory.
   * All the cloud backups will be written and read from this directory.
   * Note that each wallet also have a local copy of the zip backups.
   * TODO (GR) cloud backup must be optional
   */
  public void initialise(File applicationDataDirectory, File cloudBackupDirectory) {
    Preconditions.checkNotNull(applicationDataDirectory);

    this.applicationDataDirectory = applicationDataDirectory;
    this.cloudBackupDirectory = cloudBackupDirectory;
  }

  /**
   * @param shutdownEvent The shutdown event
   */
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    this.applicationDataDirectory = null;
  }

  /**
   * Get all the backups available in the cloud backup directory for the wallet id specified.
   */
  public List<BackupSummary> getCloudBackups(WalletId walletId, File cloudBackupDirectory) {
    return getWalletBackups(walletId, cloudBackupDirectory);
  }

  /**
   * Get all the backups available in the local zip backup directory for the wallet id specified.
   */
  public List<BackupSummary> getLocalZipBackups(WalletId walletId) {

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId));

    if (!walletRootDirectory.exists()) {
      // No directory - no backups
      return Lists.newArrayList();
    }

    // Find the zip-backups directory containing the local backups
    File zipBackupsDirectory = new File(walletRootDirectory.getAbsoluteFile() + File.separator + LOCAL_ZIP_BACKUP_DIRECTORY_NAME);

    return getWalletBackups(walletId, zipBackupsDirectory);
  }

  /**
   * Find the wallet backups in a directory.
   * Wallet backups are called mbhd-[formatted wallet id]-timestamp.zip and the specified wallet id is used to subset all backups
   *
   * @param walletId      The walletId to subset on
   * @param directoryName The directory to look in
   * @return The wallet backups available
   */
  public List<BackupSummary> getWalletBackups(WalletId walletId, File directoryName) {

    // TODO would also be nice return them sorted by age

    List<BackupSummary> walletBackups = Lists.newArrayList();

    if (directoryName == null || !directoryName.exists()) {
      // No directory - no backups
      return walletBackups;
    }

    File[] files = directoryName.listFiles();

    // Look for filenames with format "mbhd-" + [formatted wallet id ] + "-YYYYMMDDHHMMSS.aes"
    String backupRegex = WalletManager.WALLET_DIRECTORY_PREFIX
            + WALLET_ID_SEPARATOR
            + walletId.toFormattedString()
            + WALLET_ID_SEPARATOR
            + "\\d{14}"
            + ENCRYPTED_BACKUP_ZIP_FILE_EXTENSION_REGEX;

    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          if (file.getName().matches(backupRegex)) {
            if (file.length() > 0) {
              BackupSummary backupSummary = new BackupSummary(walletId, file.getName(), file);
              // Work out timestamp
              // TODO (JB) Is this correct? Should it be +1 instead
              int start = (WalletManager.MBHD_WALLET_PREFIX + WALLET_ID_SEPARATOR + WALLET_ID_SEPARATOR).length() + LENGTH_OF_FORMATTED_WALLET_ID;
              int stop = start + 14;
              String timeStampString = file.getName().substring(start, stop);
              try {
                DateTime timestamp = Dates.parseBackupDate(timeStampString);
                backupSummary.setCreated(timestamp);
              } catch (IllegalArgumentException e) {
                // Serious problem if the backup format has failed
                ExceptionHandler.handleThrowable(e);
              }
              walletBackups.add(backupSummary);
            }
          }
        }
      }
    }

    return walletBackups;
  }

  /**
   * Get all the available rolling backups
   * These are ordered in the order of timestamp i.e  the oldest one is first, the newest one is last
   *
   * @param walletId the wallet id of the wallet to search for rolling backups for
   * @return a list of filenames of the rolling backups, oldest first
   */
  public List<File> getRollingBackups(WalletId walletId) {

    Preconditions.checkNotNull(walletId);

    // Calculate the directory the rolling backups are stored in for this wallet id
    String rollingBackupDirectoryName = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId)) +
            File.separator + ROLLING_BACKUP_DIRECTORY_NAME;
    File rollingBackupDirectory = new File(rollingBackupDirectoryName);

    if (!rollingBackupDirectory.exists()) {
      // no directory - no backups
      return Lists.newArrayList();
    }

    // See if there are any wallet rolling backups.
    File[] files = rollingBackupDirectory.listFiles();

    Map<Long, File> mapOfTimeToFile = Maps.newTreeMap(); // Note that this is sorted by long

    // Look for file names with format "text"-YYYYMMDDHHMMSS.wallet.aes<eol> and are not empty.
    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          if (file.getName().matches(REGEX_FOR_TIMESTAMP_AND_WALLET_AND_AES_SUFFIX)) {
            if (file.length() > 0) {
              // Work out timestamp
              int start = (WalletManager.MBHD_WALLET_PREFIX + WALLET_ID_SEPARATOR).length();
              int stop = start + 14;
              String timeStampString = file.getName().substring(start, stop);
              try {
                long timestamp = Dates.parseBackupDate(timeStampString).getMillis();
                mapOfTimeToFile.put(timestamp, file);
              } catch (IllegalArgumentException e) {
                // Serious problem if the backup format has failed
                ExceptionHandler.handleThrowable(e);
              }
            }
          }
        }
      }
    }

    List<File> walletBackups = Lists.newArrayList();

    for (Long key : mapOfTimeToFile.keySet()) {
      // Note that these are added in order of creation time, oldest first
      // log.debug("Adding rolling backup '" + mapOfTimeToFile.get(key).getAbsoluteFile() + "' to rolling backup results");
      walletBackups.add(mapOfTimeToFile.get(key));
    }

    return walletBackups;
  }

  /**
   * Create a rolling backup of the wallet, specified by the walletId.
   * <p/>
   * This is a copy of the supplied wallet file, timestamped and copied to the rolling-backup directory
   * There is a maximum number of rolling backups, removals are done using a first in - first out rule.
   *
   * @param walletSummary The wallet data with the wallet to backup
   * @return the File of the created rolling wallet backup
   * @throws java.io.IOException if the wallet backup could not be created
   */
  public File createRollingBackup(WalletSummary walletSummary, CharSequence password) throws IOException {

    Preconditions.checkNotNull(walletSummary, "'walletSummary' must be present");
    Preconditions.checkNotNull(walletSummary.getWallet(), "'wallet' must be present");
    Preconditions.checkNotNull(walletSummary.getWalletId(), "'walletId' must be present");
    Preconditions.checkNotNull(applicationDataDirectory, "'applicationDataDirectory' must be present. Check BackupManager has been initialised");

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletSummary.getWalletId())
    );

    if (!walletRootDirectory.exists()) {
      throw new IOException("Directory " + walletRootDirectory + " does not exist. Cannot create rolling backup.");
    }

    String rollingBackupDirectoryName = walletRootDirectory
            + File.separator
            + BackupManager.ROLLING_BACKUP_DIRECTORY_NAME;
    SecureFiles.verifyOrCreateDirectory(new File(rollingBackupDirectoryName));

    String walletBackupFilename = rollingBackupDirectoryName
            + File.separator
            + WalletManager.MBHD_WALLET_PREFIX
            + WALLET_ID_SEPARATOR
            + Dates.formatBackupDate(Dates.nowUtc())
            + WalletManager.MBHD_WALLET_SUFFIX;

    File walletBackupFile = new File(walletBackupFilename);
    log.debug("Creating rolling-backup '" + walletBackupFilename + "'");
    walletSummary.getWallet().saveToFile(walletBackupFile);
    log.debug("Created rolling-backup successfully. Size = " + walletBackupFile.length() + " bytes");

    File encryptedAESCopy = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletBackupFile, password);
    log.debug("Created rolling-backup AES copy successfully as file '{}'", encryptedAESCopy == null ? "null" : encryptedAESCopy.getAbsolutePath());

    List<File> rollingBackups = getRollingBackups(walletSummary.getWalletId());

    // If there are more than the maximum number of rolling backups, secure delete the eldest
    if (rollingBackups.size() > MAXIMUM_NUMBER_OF_ROLLING_BACKUPS) {
      // Delete the eldest
      SecureFiles.secureDelete(rollingBackups.get(0));
    }

    // If there are even more than that trim off another one - over time this will gently reduce the number to the maximum
    if (rollingBackups.size() > MAXIMUM_NUMBER_OF_ROLLING_BACKUPS + 1) {
      // Delete the second eldest
      SecureFiles.secureDelete(rollingBackups.get(1));
    }
    return walletBackupFile;
  }

  /**
   * Create a backup of the specified wallet id.
   * The wallet manager is interrogated to find the physical directory where the wallet is stored.
   * The whole directory (except the zip-backups) is then copied and zipped into a timestamped backup file
   * This is then written to the local and cloud backup directories
   *
   * @return The created local backup as a file
   */
  public File createLocalAndCloudBackup(WalletId walletId, CharSequence password) throws IOException {

    Preconditions.checkNotNull(applicationDataDirectory);
    Preconditions.checkNotNull(walletId);

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId));

    if (!walletRootDirectory.exists()) {
      throw new IOException("Directory " + walletRootDirectory + " does not exist. Cannot backup.");
    }

    WalletSummary walletSummary = WalletManager.getOrCreateWalletSummary(walletRootDirectory, walletId);

    File localBackupDirectory = new File(walletRootDirectory.getAbsoluteFile() + File.separator + LOCAL_ZIP_BACKUP_DIRECTORY_NAME);
    SecureFiles.verifyOrCreateDirectory(localBackupDirectory);

    String backupFilename = WalletManager.WALLET_DIRECTORY_PREFIX
            + WALLET_ID_SEPARATOR
            + walletId.toFormattedString()
            + WALLET_ID_SEPARATOR
            + Dates.formatBackupDate(Dates.nowUtc())
            + BACKUP_ZIP_FILE_EXTENSION;
    String localBackupFilename = localBackupDirectory.getAbsolutePath() + File.separator + backupFilename;

    log.debug("Creating local zip-backup '" + localBackupFilename + "'");
    ZipFiles.zipFolder(walletRootDirectory.getAbsolutePath(), localBackupFilename, false);
    File localBackupEncryptedFilename = EncryptedFileReaderWriter.makeBackupAESEncryptedCopyAndDeleteOriginal(new File(localBackupFilename), (String) password, walletSummary.getEncryptedBackupKey());
    log.debug("Created encrypted local zip-backup successfully. Size = " + (localBackupEncryptedFilename).length() + " bytes");

    if (cloudBackupDirectory != null && cloudBackupDirectory.exists()) {
      String cloudBackupFilename = cloudBackupDirectory.getAbsolutePath() + File.separator + backupFilename;
      log.debug("Creating cloud zip-backup '" + cloudBackupFilename + "'");
      ZipFiles.zipFolder(walletRootDirectory.getAbsolutePath(), cloudBackupFilename, false);
      File cloudBackupEncryptedFilename = EncryptedFileReaderWriter.makeBackupAESEncryptedCopyAndDeleteOriginal(new File(cloudBackupFilename), (String) password, walletSummary.getEncryptedBackupKey());

      log.debug("Created encrypted cloud zip-backup successfully. Size = " + (cloudBackupEncryptedFilename).length() + " bytes");
    } else {
      log.debug("No cloud backup made for wallet '" + walletId + "' as no cloudBackupDirectory is set.");
    }

    return localBackupEncryptedFilename;
  }


  /**
   * Load a backup file, copying all the backup files to the appropriate wallet root directory
   */
  public WalletId loadBackup(File backupFileToLoad, List<String> seedPhrase) throws IOException {

    SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);

    // Work out the walletId of the backup file being loaded
    String backupFilename = backupFileToLoad.getName();

    // Remove "mbhd-" prefix
    String walletRoot = backupFilename.replace(WalletManager.WALLET_DIRECTORY_PREFIX + WALLET_ID_SEPARATOR, "");

    // Remove  ".zip.aes" suffix
    walletRoot = walletRoot.replace(ENCRYPTED_BACKUP_FILE_EXTENSION, "");

    // Remove the timestamp
    if (walletRoot.length() > LENGTH_OF_FORMATTED_WALLET_ID) {
      walletRoot = walletRoot.substring(0, LENGTH_OF_FORMATTED_WALLET_ID);
    }
    WalletId walletId = new WalletId(walletRoot);

    // Make a backup of all the current file in the wallet root directory if it exists
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId));

    // TODO backup original
    //if (walletRootDirectory.exists()) {
    //  createLocalAndCloudBackup(walletId, seed);
    //}

    File temporaryFile = null;
    try {
      // Read the encrypted file in.
      byte[] encryptedBytes = org.multibit.hd.brit.utils.FileUtils.readFile(new File(backupFileToLoad.getAbsolutePath()));

      KeyParameter seedDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(seed, WalletManager.SCRYPT_SALT);

      // Decrypt the backup bytes
      byte[] decryptedBytes = AESUtils.decrypt(encryptedBytes, seedDerivedAESKey, WalletManager.AES_INITIALISATION_VECTOR);

      File tempDirectory = FileUtils.makeRandomTemporaryDirectory();
      temporaryFile = File.createTempFile("backup", "zip", tempDirectory);
      FileOutputStream outputFileStream = new FileOutputStream(temporaryFile);
      try {
        FileUtils.writeFile(new ByteArrayInputStream(decryptedBytes), outputFileStream);
      } finally {
        outputFileStream.close();
      }

      // Unzip the backup into the wallet root directory - this overwrites files if already present (hence the backup just done)
      ZipFiles.unzip(temporaryFile.getAbsolutePath(), walletRootDirectory.getAbsolutePath());

      return walletId;
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot read and decrypt the backup file '" + backupFileToLoad.getAbsolutePath() + "'", e);
    } finally {
      if (temporaryFile != null) {
        SecureFiles.secureDelete(temporaryFile);
      }
    }
  }

}
