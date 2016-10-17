package org.multibit.hd.core.managers;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.bitcoinj.core.Wallet;
import org.joda.time.DateTime;
import org.multibit.commons.crypto.AESUtils;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.WalletLoadEvent;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.hd.core.exceptions.WalletLoadException;
import org.multibit.commons.files.SecureFiles;
import org.multibit.hd.core.files.ZipFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;
import org.multibit.hd.core.files.EncryptedWalletFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

  public static final String REGEX_FOR_TIMESTAMP_AND_WALLET_AND_AES_SUFFIX = ".*-\\d{14}\\.wallet\\.aes$";

  public static final String LOCAL_ZIP_BACKUP_DIRECTORY_NAME = "zip-backup";
  public static final int MAXIMUM_NUMBER_OF_ZIP_BACKUPS = 60; // Chosen so that you will have about weekly backups for a year, fortnightly over two years.
  public static final int NUMBER_OF_FIRST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 2;
  public static final int NUMBER_OF_LAST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 8; // Must be at least 1.

  public static final String BACKUP_TIMESTAMP_SUFFIX_FORMAT = "yyyyMMddHHmmss";
  private DateFormat dateFormat;

  private static final Logger log = LoggerFactory.getLogger(BackupManager.class);

  // Where wallets are stored
  private File applicationDataDirectory = null;

  // Where the cloud backups are stored (this is typically specified by the user and is a SpiderOak etc sync directory)
  private Optional<File> cloudBackupDirectory;

  /**
   * Initialise the backup manager to use the specified cloudBackupDirectory.
   * All the cloud backups will be written and read from this directory.
   * Note that each wallet also have a local copy of the zip backups.
   */
  public void initialise(File applicationDataDirectory, Optional<File> cloudBackupDirectory) {

    Preconditions.checkNotNull(applicationDataDirectory, "'applicationDataDirectory' must be present");
    Preconditions.checkNotNull(cloudBackupDirectory, "'cloudBackupDirectory' must not be null");

    this.applicationDataDirectory = applicationDataDirectory;
    this.cloudBackupDirectory = cloudBackupDirectory;
  }

  /**
   */
  public void shutdownNow() {
    this.applicationDataDirectory = null;
    this.cloudBackupDirectory = Optional.absent();
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
    createApplicationDataDirectoryIfNotSet();

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
   *
   * @return The wallet backups available
   */
  public List<BackupSummary> getWalletBackups(WalletId walletId, File directoryName) {
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

    log.debug("For the walletId {}, looking in directory {}, there were {} backups", walletId, directoryName, walletBackups.size());
    return walletBackups;
  }

  /**
   * Get all the available rolling backups
   * These are ordered in the order of timestamp i.e  the oldest one is first, the newest one is last
   *
   * @param walletId the wallet id of the wallet to search for rolling backups for
   *
   * @return a list of filenames of the rolling backups, oldest first
   */
  public List<File> getRollingBackups(WalletId walletId) {
    Preconditions.checkNotNull(walletId);
    createApplicationDataDirectoryIfNotSet();

    // Calculate the directory the rolling backups are stored in for this wallet id
    String rollingBackupDirectoryName = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId)) +
      File.separator + ROLLING_BACKUP_DIRECTORY_NAME;
    log.debug("Application data directory\n'{}'", applicationDataDirectory);
    log.debug("Rolling backup directory\n'{}'", rollingBackupDirectoryName);
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
                ExceptionHandler.handleThrowable(new IllegalArgumentException("Rolling backup files are in the wrong format. Error = '" + e.getMessage() + "'"));
              }
            }
          }
        }
      }
    }

    List<File> walletBackups = Lists.newArrayList();

    // Iterate over entry set for efficiency
    for (Map.Entry<Long, File> entry : mapOfTimeToFile.entrySet()) {
      // Note that these are added in order of creation time, oldest first (tree map)
      walletBackups.add(entry.getValue());
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
   *
   * @return the File of the created rolling wallet backup
   *
   * @throws java.io.IOException if the wallet backup could not be created
   */
  public File createRollingBackup(WalletSummary walletSummary, CharSequence password) throws IOException {
    Preconditions.checkNotNull(walletSummary, "'walletSummary' must be present");
    Preconditions.checkNotNull(walletSummary.getWallet(), "'wallet' must be present");
    Preconditions.checkNotNull(walletSummary.getWalletId(), "'walletId' must be present");
    createApplicationDataDirectoryIfNotSet();

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(
      applicationDataDirectory, WalletManager.createWalletRoot(walletSummary.getWalletId())
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
    log.debug("Creating rolling-backup\n'{}'", walletBackupFilename);
    walletSummary.getWallet().saveToFile(walletBackupFile);
    log.debug("Created rolling-backup successfully. Size = {}", walletBackupFile.length());

    File encryptedAESCopy = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletBackupFile, password);
    log.debug("Created rolling-backup AES copy successfully as file:\n'{}'", encryptedAESCopy == null ? "" : encryptedAESCopy.getAbsolutePath());

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
   * Create a local zip backup of the specified wallet id.
   * The wallet manager is interrogated to find the physical directory where the wallet is stored.
   * The whole directory (except the zip-backups) is then copied and zipped into a timestamped backup file
   * This is then written to the local backup directories
   *
   * @return The created local backup as a file
   */
  public File createLocalBackup(WalletId walletId, CharSequence password) throws IOException {
    Preconditions.checkNotNull(walletId);
    createApplicationDataDirectoryIfNotSet();

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

    log.debug("Creating local zip-backup\n'{}'", localBackupFilename);
    ZipFiles.zipFolder(walletRootDirectory.getAbsolutePath(), localBackupFilename, false);
    File localBackupEncryptedFilename = EncryptedFileReaderWriter.makeBackupAESEncryptedCopyAndDeleteOriginal(
      new File(localBackupFilename),
      (String) password,
      walletSummary);
    log.debug("Created encrypted local zip-backup successfully. Size = {} bytes", localBackupEncryptedFilename.length());

    // Thin the local backup directory
    thinBackupDirectory(walletId, localBackupDirectory);

    return localBackupEncryptedFilename;
  }

  /**
   * Create a cloud backup of the specified wallet id.
   * The wallet manager is interrogated to find the physical directory where the wallet is stored.
   * The whole directory (except the zip-backups) is then copied and zipped into a timestamped backup file
   * This is then written to the cloud backup directories
   *
   * @return The created cloud backup as a file or null if nothing was generated
   */
  public File createCloudBackup(WalletId walletId, CharSequence password) throws IOException {
    Preconditions.checkNotNull(walletId);
    createApplicationDataDirectoryIfNotSet();

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId));

    if (!walletRootDirectory.exists()) {
      throw new IOException("Directory " + walletRootDirectory + " does not exist. Cannot backup.");
    }

    WalletSummary walletSummary = WalletManager.getAndChangeWalletSummary(walletRootDirectory, walletId,password);


    String backupFilename = WalletManager.WALLET_DIRECTORY_PREFIX
      + WALLET_ID_SEPARATOR
      + walletId.toFormattedString()
      + WALLET_ID_SEPARATOR
      + Dates.formatBackupDate(Dates.nowUtc())
      + BACKUP_ZIP_FILE_EXTENSION;

    if (cloudBackupDirectory.isPresent() && cloudBackupDirectory.get().exists()) {
      String cloudBackupFilename = cloudBackupDirectory.get().getAbsolutePath() + File.separator + backupFilename;
      log.debug("Creating cloud zip-backup '" + cloudBackupFilename + "'");
      ZipFiles.zipFolder(walletRootDirectory.getAbsolutePath(), cloudBackupFilename, false);
      File cloudBackupEncryptedFilename = EncryptedFileReaderWriter.makeBackupAESEncryptedCopyAndDeleteOriginal(
        new File(cloudBackupFilename),
        (String) password,
        walletSummary);

      log.debug("Created encrypted cloud zip-backup successfully. Size = " + (cloudBackupEncryptedFilename).length() + " bytes");

      // Thin the local backup directory
      thinBackupDirectory(walletId, cloudBackupDirectory.get());

      return cloudBackupEncryptedFilename;
    } else {
      log.debug("No cloud backup made for wallet '" + walletId + "' as no cloudBackupDirectory is set.");
      return null;
    }
  }

  /**
   * Load a rolling backup file.
   * A BackupWalletLoadedEvent is emitted
   *
   * @param walletId The walletId of the wallet
   * @param password The credentials used to decrypt the encrypted wallet backup
   *
   * @throws WalletLoadException if no rolling backup could be loaded successfully, or none are available
   */
  public Wallet loadRollingBackup(final WalletId walletId, CharSequence password) throws WalletLoadException {
    // Get the available rolling backups
    List<File> rollingBackupFiles = getRollingBackups(walletId);

    if (rollingBackupFiles.isEmpty()) {
      // Throw WalletLoadException - no wallet could be loaded
      throw new WalletLoadException("No rolling backup to load");
    } else {
      Wallet wallet = null;
      File fileLoaded = null;

      // Try loading each rolling backup in turn, newest first
      for (int i = rollingBackupFiles.size(); i > 0; i--) {
        try {
          wallet = WalletManager.INSTANCE.loadWalletFromFile(rollingBackupFiles.get(i - 1), password);
          log.debug("Wallet read in from rolling backup file:\n'{}'", wallet.toString());
          fileLoaded = rollingBackupFiles.get(i - 1);
          break;
        } catch (Exception e) {
          // Log the initial error (and then carry on to the next rolling backup
          log.error("Could not load rolling backup:\n'{}', error was: {}", rollingBackupFiles.get(i - 1).getAbsolutePath(), e.getClass().getCanonicalName() + " " + e.getMessage());
        }
      }

      if (wallet == null) {
        // No rolling backup was successfully loaded
        throw new WalletLoadException("Could not load any rolling backup successfully.");
      } else {
        // Emit WalletLoadedEvent for notification on GUI
        if (fileLoaded != null) {
          log.debug("Loaded backup wallet file:\n'{}'", fileLoaded.getAbsolutePath());
          CoreEvents.fireWalletLoadEvent(new WalletLoadEvent(Optional.of(walletId), false, CoreMessageKey.BACKUP_WALLET_WAS_LOADED, null, Optional.of(fileLoaded)));
        }
        return wallet;
      }
    }
  }

  /**
   * Load a zip backup file, copying all the backup files to the appropriate wallet root directory
   *
   * @param backupFileToLoad The encrypted backup file to load
   * @param seedPhrase       The seed phrase to use to decrypt the backup file
   */
  public WalletId loadZipBackup(File backupFileToLoad, List<String> seedPhrase) throws IOException {
    try {
      SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();
      byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);

      KeyParameter seedDerivedAESKey = org.multibit.commons.crypto.AESUtils.createAESKey(seed, WalletManager.scryptSalt());

      return loadZipBackup(backupFileToLoad, seedDerivedAESKey);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot read and decrypt the backup file '" + backupFileToLoad.getAbsolutePath() + "'", e);
    }
  }

  /**
   * Load a zip backup file, copying all the backup files to the appropriate wallet root directory
   *
   * @param backupFileToLoad The encrypted backup file to load
   * @param backupAESKey     The AES key to use to decrypt the backup file
   */
  public WalletId loadZipBackup(File backupFileToLoad, KeyParameter backupAESKey) throws IOException {
    File temporaryFile = null;
    try {
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

      File walletRootDirectory = WalletManager.getOrCreateWalletDirectory(applicationDataDirectory, WalletManager.createWalletRoot(walletId));

      // Read the encrypted file in.
      byte[] fileBytes = Files.toByteArray(new File(backupFileToLoad.getAbsolutePath()));
      byte[] ivBytes = Arrays.copyOfRange(fileBytes, 0, 16);
      byte[] encryptedWalletBytes = Arrays.copyOfRange(fileBytes, 16, fileBytes.length);
      // Decrypt the backup bytes
      byte[] decryptedBytes = AESUtils.decrypt(encryptedWalletBytes, backupAESKey, ivBytes);
      if(!EncryptedWalletFile.isParseable(decryptedBytes)){
        decryptedBytes = AESUtils.decrypt(fileBytes, backupAESKey, WalletManager.deprecatedFixedAesInitializationVector());
      }

      File tempDirectory = Files.createTempDir();
      temporaryFile = File.createTempFile("backup", "zip", tempDirectory);
      try (FileOutputStream outputFileStream = new FileOutputStream(temporaryFile)) {
        ByteStreams.copy(new ByteArrayInputStream(decryptedBytes), outputFileStream);
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

  /**
   * Thin the wallet backups when they reach the MAXIMUM_NUMBER_OF_BACKUPS setting.
   * Thinning is done by removing the most quickly replaced backup, except for the first and last few
   * (as they are considered to be more valuable backups).
   *
   * @param walletId        the wallet id of wallet backups to thin
   * @param backupDirectory the directory to thin
   */
  private void thinBackupDirectory(WalletId walletId, File backupDirectory) {
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat(BACKUP_TIMESTAMP_SUFFIX_FORMAT);
    }

    if (walletId == null || backupDirectory == null) {
      return;
    }

    // Find out how many wallet backups there are.
    List<BackupSummary> backups = getWalletBackups(walletId, backupDirectory);

    if (backups.size() < MAXIMUM_NUMBER_OF_ZIP_BACKUPS) {
      // No thinning required.
      return;
    }

    // Work out the date the backup was made for each of the wallet.
    // This is done using the timestamp rather than the write time of the file.
    // A typical backup filename is: mbhd-0da4e1dc-3a1726d7-5456cb44-f474e117-285799bb-20140520110455.zip.aes
    // Constructed of :
    // 5 chars "mbhd-"
    // 44 chars of walletId
    // 1 char separator
    // 14 chars of timestamp
    // 8 chars of file type suffix
    Map<File, Date> mapOfFileToBackupTimes = new HashMap<>();
    for (BackupSummary backup : backups) {
      String filename = backup.getName();
      if (filename.length() > 71) {
        int startOfTimestamp = filename.length() - BACKUP_TIMESTAMP_SUFFIX_FORMAT.length() - ENCRYPTED_BACKUP_FILE_EXTENSION.length();
        String timestampText = filename.substring(startOfTimestamp, startOfTimestamp + BACKUP_TIMESTAMP_SUFFIX_FORMAT.length());
        try {
          Date parsedTimestamp = dateFormat.parse(timestampText);
          mapOfFileToBackupTimes.put(backup.getFile(), parsedTimestamp);
        } catch (ParseException pe) {
          // Cannot parse text - may be some other type of file the user has put in the directory.
          log.debug("For wallet '" + filename + " could not parse the timestamp of '" + timestampText + "'.");
        }
      }
    }

    // See which wallet is most quickly replaced by another backup - this will be thinned.
    int walletBackupToDeleteIndex = -1; // Not set yet.
    long walletBackupToDeleteReplacementTimeMillis = Integer.MAX_VALUE; // How quickly the wallet was replaced by a later one.

    for (int i = 0; i < backups.size(); i++) {
      if ((i < NUMBER_OF_FIRST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP)
        || (i >= backups.size() - NUMBER_OF_LAST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP)) {
        // Keep the very first and last wallets always.
      } else {
        // Work out how quickly the wallet is replaced by the next backup.
        Date thisWalletTimestamp = mapOfFileToBackupTimes.get(backups.get(i).getFile());
        Date nextWalletTimestamp = mapOfFileToBackupTimes.get(backups.get(i + 1).getFile());
        if (thisWalletTimestamp != null && nextWalletTimestamp != null) {
          long deltaTimeMillis = nextWalletTimestamp.getTime() - thisWalletTimestamp.getTime();
          if (deltaTimeMillis < walletBackupToDeleteReplacementTimeMillis) {
            // This is the best candidate for deletion so far.
            walletBackupToDeleteIndex = i;
            walletBackupToDeleteReplacementTimeMillis = deltaTimeMillis;
          }
        }
      }
    }

    if (walletBackupToDeleteIndex > -1) {
      try {
        // Secure delete the chosen backup wallet.
        log.debug(
          "To save space, secure deleting backup wallet\n'{}'", backups
            .get(walletBackupToDeleteIndex)
            .getFile()
            .getAbsolutePath()
        );
        SecureFiles.secureDelete(backups.get(walletBackupToDeleteIndex).getFile());
      } catch (IOException ioe) {
        log.error(ioe.getClass().getName() + " " + ioe.getMessage());
      }
    }
  }

  public void setApplicationDataDirectory(File applicationDataDirectory) {
    this.applicationDataDirectory = applicationDataDirectory;
  }

  private void createApplicationDataDirectoryIfNotSet() {
    if (applicationDataDirectory == null) {
      // Locate the standard installation directory
      applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      log.debug("Setting the application data directory\n'{}'", applicationDataDirectory);
    }
  }

  public void setCloudBackupDirectory(Optional<File> cloudBackupDirectory) {
    Preconditions.checkNotNull(cloudBackupDirectory, "'cloudBackupDirectory' must not be null");
    this.cloudBackupDirectory = cloudBackupDirectory;
  }
}
