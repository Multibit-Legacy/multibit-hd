package org.multibit.hd.core.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.multibit.hd.core.api.WalletData;
import org.multibit.hd.core.api.WalletId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * Class to manage creation and reading back of the wallet backups.
 */
public enum BackupManager {
  INSTANCE;

  public static final String BACKUP_SUFFIX_FORMAT = "yyyyMMddHHmmss";
  public static final String BACKUP_ZIP_FILE_EXTENSION = ".zip";
  public static final String BACKUP_ZIP_FILE_EXTENSION_REGEX = "\\.zip";

  public static final String ROLLING_BACKUP_DIRECTORY_NAME = "rolling-backup";
  public static final int MAXIMUM_NUMBER_OF_ROLLING_BACKUPS = 4;
  public static final String REGEX_FOR_TIMESTAMP_AND_WALLET_SUFFIX = ".*-\\d{" + BACKUP_SUFFIX_FORMAT.length() + "}\\.wallet$";

  public static final String LOCAL_ZIP_BACKUP_DIRECTORY_NAME = "zip-backup";
  public static final int MAXIMUM_NUMBER_OF_ZIP_BACKUPS = 60; // Chosen so that you will have about weekly backups for a year, fortnightly over two years.
  public static final int NUMBER_OF_FIRST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 2;
  public static final int NUMBER_OF_LAST_WALLET_ZIP_BACKUPS_TO_ALWAYS_KEEP = 8; // Must be at least 1.

  private static final Logger log = LoggerFactory.getLogger(BackupManager.class);

  // Where wallets are stored
  private File applicationDataDirectory;

  // Where the cloud backups are stored (this is typically specified by the user and is a SpiderOak etc sync directory)
  private File cloudBackupDirectory;

  private SimpleDateFormat dateFormat;

  // Nonsense bytes to fill up deleted files - these have no meaning.
  private static byte[] NONSENSE_BYTES = new byte[]{(byte) 0xF0, (byte) 0xA6, (byte) 0x55, (byte) 0xAA, (byte) 0x33,
          (byte) 0x77, (byte) 0x33, (byte) 0x37, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0xC2, (byte) 0xB3,
          (byte) 0xA4, (byte) 0x9A, (byte) 0x30, (byte) 0x7F, (byte) 0xE5, (byte) 0x5A, (byte) 0x23, (byte) 0x47, (byte) 0x13,
          (byte) 0x17, (byte) 0x15, (byte) 0x32, (byte) 0x5C, (byte) 0x77, (byte) 0xC9, (byte) 0x73, (byte) 0x04, (byte) 0x2D,
          (byte) 0x40, (byte) 0x0F, (byte) 0xA5, (byte) 0xA6, (byte) 0x43, (byte) 0x77, (byte) 0x33, (byte) 0x3B, (byte) 0x62,
          (byte) 0x34, (byte) 0xB6, (byte) 0x72, (byte) 0x32, (byte) 0xB3, (byte) 0xA4, (byte) 0x4B, (byte) 0x80, (byte) 0x7F,
          (byte) 0xC5, (byte) 0x43, (byte) 0x23, (byte) 0x47, (byte) 0x13, (byte) 0xB7, (byte) 0xA5, (byte) 0x32, (byte) 0xDC,
          (byte) 0x79, (byte) 0x19, (byte) 0xB1, (byte) 0x03, (byte) 0x9D};

  private static int BULKING_UP_FACTOR = 16;
  private static byte[] SECURE_DELETE_FILL_BYTES = new byte[NONSENSE_BYTES.length * BULKING_UP_FACTOR];

  static {
    // Make some SECURE_DELETE_FILL_BYTES bytes = x BULKING_UP_FACTOR the
    // NONSENSE just to save write time.
    for (int i = 0; i < BULKING_UP_FACTOR; i++) {
      System.arraycopy(NONSENSE_BYTES, 0, SECURE_DELETE_FILL_BYTES, NONSENSE_BYTES.length * i, NONSENSE_BYTES.length);
    }
  }

  /**
   * Initialise the backup manager to use the specified cloudBackupDirectory.
   * All the cloud backups will be written and read from this directory.
   * Note that each wallet also have a local copy of the zip backups.
   */
  public void initialise(File applicationDataDirectory, File cloudBackupDirectory) {
    Preconditions.checkNotNull(applicationDataDirectory);

    this.applicationDataDirectory = applicationDataDirectory;
    this.cloudBackupDirectory = cloudBackupDirectory;
  }

  /**
   * Get all the backups available in the cloud backup directory for the wallet id specified.
   * Wallet backups are called mbhd-[formatted wallet id]-timestamp.zip and the specified wallet id is used to subset all backups
   */
  // TODO would also be nice to return the dates of the backups (from the timestamp) or return them sorted by age
  // then the latest backup can be used easily
  public List<File> getCloudBackups(WalletId walletId) {

    List<File> walletBackups = Lists.newArrayList();

    if (cloudBackupDirectory == null || !cloudBackupDirectory.exists()) {
      // No directory - no backups
      return walletBackups;
    }

    File[] listOfFiles = cloudBackupDirectory.listFiles();

    // Look for filenames with format "mbhd-" + [formatted wallet id ] + "-YYYYMMDDHHMMSS.zip"
    String backupRegex = WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR + walletId.toFormattedString() +
            WalletManager.SEPARATOR + "\\d{" + BACKUP_SUFFIX_FORMAT.length() + "}" + BACKUP_ZIP_FILE_EXTENSION_REGEX;
    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
          if (listOfFiles[i].getName().matches(backupRegex)) {
            if (listOfFiles[i].length() > 0) {
              walletBackups.add(listOfFiles[i]);
            }
          }
        }
      }
    }

    return walletBackups;
  }

  /**
   * Get all the backups available in the local zip backup directory for the wallet id specified.
   * Wallet backups are called mbhd-[formatted wallet id]-timestamp.zip and the specified wallet id is used to subset all backups
   */
  // TODO would also be nice to return the dates of the backups (from the timestamp) or return them sorted by age
  // TODO then the latest backup can be used easily
  public List<File> getLocalZipBackups(WalletId walletId) {

    List<File> walletBackups = Lists.newArrayList();

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), WalletManager.createWalletRoot(walletId));

    if (!walletRootDirectory.exists()) {
      // No directory - no backups
      return walletBackups;
    }

    // Find the zip-backups directory containing the local backups
    File zipBackupsDirectory = new File(walletRootDirectory.getAbsoluteFile() + File.separator + LOCAL_ZIP_BACKUP_DIRECTORY_NAME);
    if (!zipBackupsDirectory.exists()) {
      // No directory - no backups
      return walletBackups;
    }

    File[] listOfFiles = zipBackupsDirectory.listFiles();

    // Look for filenames with format "mbhd"-[formatted wallet id ] -YYYYMMDDHHMMSS.zip"
    String backupRegex = WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR + walletId.toFormattedString() +
            WalletManager.SEPARATOR + "\\d{" + BACKUP_SUFFIX_FORMAT.length() + "}" + BACKUP_ZIP_FILE_EXTENSION_REGEX;
    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
          if (listOfFiles[i].getName().matches(backupRegex)) {
            if (listOfFiles[i].length() > 0) {
              walletBackups.add(listOfFiles[i]);
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
    String rollingBackupDirectoryName = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), WalletManager.createWalletRoot(walletId)) +
            File.separator + ROLLING_BACKUP_DIRECTORY_NAME;
    File rollingBackupDirectory = new File(rollingBackupDirectoryName);

    if (!rollingBackupDirectory.exists()) {
      // no directory - no backups
      return Lists.newArrayList();
    }

    // See if there are any wallet rolling backups.
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat(BACKUP_SUFFIX_FORMAT);
    }

    File[] listOfFiles = rollingBackupDirectory.listFiles();

    Map<Long, File> mapOfTimeToFile = Maps.newTreeMap(); // Note that this is sorted by long

    // Look for filenames with format "text"-YYYYMMDDHHMMSS.wallet<eol> and are not empty.
    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
          if (listOfFiles[i].getName().matches(REGEX_FOR_TIMESTAMP_AND_WALLET_SUFFIX)) {
            if (listOfFiles[i].length() > 0) {
              // Work out timestamp
              int start = (WalletManager.MBHD_WALLET_PREFIX + WalletManager.SEPARATOR).length();
              int stop = start + BACKUP_SUFFIX_FORMAT.length();
              String timeStampString = listOfFiles[i].getName().substring(start, stop);
              try {
                long timestamp = dateFormat.parse(timeStampString).getTime();
                mapOfTimeToFile.put(timestamp, listOfFiles[i]);
              } catch (ParseException pe) {
                pe.printStackTrace();
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
   * @param walletData The wallet data with the wallet to backup
   * @return the File of the created rolling wallet backup
   * @throws java.io.IOException if the wallet backup could not be created
   */
  public File createRollingBackup(WalletData walletData) throws IOException {
    log.debug("Creating rolling-backup called.");
    Preconditions.checkNotNull(walletData);
    Preconditions.checkNotNull(walletData.getWallet());
    Preconditions.checkNotNull(walletData.getWalletId());

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), WalletManager.createWalletRoot(walletData.getWalletId()));

    if (!walletRootDirectory.exists()) {
      throw new IOException("Directory " + walletRootDirectory + " does not exist. Cannot create rolling backup.");
    }

    String rollingBackupDirectoryName = walletRootDirectory + File.separator + BackupManager.ROLLING_BACKUP_DIRECTORY_NAME;
    InstallationManager.createDirectoryIfNecessary(new File(rollingBackupDirectoryName));

    String walletBackupFilename = rollingBackupDirectoryName + File.separator + WalletManager.MBHD_WALLET_PREFIX + WalletManager.SEPARATOR
            + getDateFormat().format(new Date()) + WalletManager.MBHD_WALLET_SUFFIX;

    File walletBackupFile = new File(walletBackupFilename);
    log.debug("Creating rolling-backup '" + walletBackupFilename + "'");
    walletData.getWallet().saveToFile(walletBackupFile);
    log.debug("Written rolling-backup '" + walletBackupFilename + "' successfully. Size : " + walletBackupFile.length() + " bytes");

    List<File> rollingBackups = getRollingBackups(walletData.getWalletId());

    // If there are more than the maximum number of rolling backups, secure delete the eldest
    if (rollingBackups.size() > MAXIMUM_NUMBER_OF_ROLLING_BACKUPS) {
      // Delete the eldest
      secureDelete(rollingBackups.get(0));
    }

    // If there are even more than that trim off another one - over time this will gently reduce the number to the maximum
    if (rollingBackups.size() > MAXIMUM_NUMBER_OF_ROLLING_BACKUPS + 1) {
      // Delete the second eldest
      secureDelete(rollingBackups.get(1));
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
  public File createLocalAndCloudBackup(WalletId walletId) throws IOException {
    Preconditions.checkNotNull(applicationDataDirectory);
    Preconditions.checkNotNull(walletId);

    // Find the wallet root directory for this wallet id
    File walletRootDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), WalletManager.createWalletRoot(walletId));

    if (!walletRootDirectory.exists()) {
      throw new IOException("Directory " + walletRootDirectory + " does not exist. Cannot backup.");
    }

    File localBackupDirectory = new File(walletRootDirectory.getAbsoluteFile() + File.separator + LOCAL_ZIP_BACKUP_DIRECTORY_NAME);
    InstallationManager.createDirectoryIfNecessary(localBackupDirectory);

    String backupFilename = WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR + walletId.toFormattedString() + WalletManager.SEPARATOR + getDateFormat().format(new Date()) + BACKUP_ZIP_FILE_EXTENSION;
    String localBackupFilename = localBackupDirectory.getAbsolutePath() + File.separator + backupFilename;

    zipFolder(walletRootDirectory.getAbsolutePath(), localBackupFilename, true);

    if (cloudBackupDirectory != null && cloudBackupDirectory.exists()) {
      String cloudBackupFilename = cloudBackupDirectory.getAbsolutePath() + File.separator + backupFilename;
      zipFolder(walletRootDirectory.getAbsolutePath(), cloudBackupFilename, false);
    } else {
      log.debug("No cloud backup made for wallet '" + walletId + "' as no cloudBackupDirectory is set.");
    }
    return new File(localBackupFilename);
  }


  /**
   * Load a backup file, copying all the backup files to the appropriate wallet root directory
   */
  public WalletId loadBackup(File backupFileToLoad) throws IOException {
    // Work out the walletId of the backup file being loaded
    String backupFilename = backupFileToLoad.getName();

    // Remove "mbhd-" prefix
    String walletRoot = backupFilename.replace(WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR, "");

    // Remove  ".zip" suffix
    walletRoot = walletRoot.replace(BACKUP_ZIP_FILE_EXTENSION, "");

    // Remove the timestamp
    if (walletRoot.length() > WalletId.LENGTH_OF_FORMATTED_WALLETID) {
      walletRoot = walletRoot.substring(0, WalletId.LENGTH_OF_FORMATTED_WALLETID);
    }
    WalletId walletId = new WalletId(walletRoot);

    // Make a backup of all the current file in the wallet root directory if it exists (except zip-backups)
    File walletRootDirectory = WalletManager.getWalletDirectory(applicationDataDirectory.getAbsolutePath(), WalletManager.createWalletRoot(walletId));

    if (walletRootDirectory.exists()) {
      createLocalAndCloudBackup(walletId);
    }

    // Unzip the backup into the wallet root directory - this overwrites files if already present (hence the backup just done)
    unzip(backupFileToLoad.getAbsolutePath(), walletRootDirectory.getAbsolutePath());

    return walletId;
  }

  /**
   * Get the date formatter used to create timestamps
   *
   * @return dateFormat for formatting dates to timestamps
   */
  private SimpleDateFormat getDateFormat() {
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat(BACKUP_SUFFIX_FORMAT);
    }
    return dateFormat;
  }

  /**
   * Write a file. This method:
   * --Reads an input stream
   * --Writes the value to the output stream
   * --Uses 1KB buffer.
   */
  private void writeFile(InputStream in, OutputStream out)
          throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();
    out.close();
  }

  /**
   * Delete a file with an overwrite of all of the data.
   * <p/>
   * Set bit patterns are used rather than random numbers to avoid a
   * futex_wait_queue_me error on Linux systems (related to /dev/random usage)
   *
   * @param file The file to secure delete
   * @throws IOException if the operation fails for any reason
   */
  public static void secureDelete(File file) throws IOException {
    log.debug("Start of secureDelete");

    RandomAccessFile raf = null;
    if (file != null && file.exists()) {
      try {
        // Prep for file delete as this can be fiddly on windows.
        // Make sure it is writable and any references to it are garbage
        // collected and finalized.
        file.setWritable(true);
        System.gc();

        long length = file.length();
        raf = new RandomAccessFile(file, "rws");
        raf.seek(0);
        raf.getFilePointer();
        int pos = 0;
        while (pos < length) {
          raf.write(SECURE_DELETE_FILL_BYTES);
          pos += SECURE_DELETE_FILL_BYTES.length;
        }
      } finally {
        if (raf != null) {
          raf.close();
          raf = null;
        }
      }
      boolean deleteSuccess = file.delete();
      log.debug("Result of delete of file '" + file.getAbsolutePath() + "' was " + deleteSuccess);
    }
    log.debug("End of secureDelete");
  }

  /**
   * Copy the files in the specified srcFolder to the destZipFile
   * The zip-backups are not stored in the backup (as they are zip-backups themselves) but the rolling backups
   * are to increase backup coverage
   *
   * @param srcFolder         The directory holding the files to zip
   * @param destZipFile       The zip file to copy files into
   * @param includeBlockStore if true then include the blockstore, if false then don't
   * @throws IOException
   */
  private void zipFolder(String srcFolder, String destZipFile, boolean includeBlockStore) throws IOException {
    ZipOutputStream zip;
    FileOutputStream fileWriter;

    fileWriter = new FileOutputStream(destZipFile);
    zip = new ZipOutputStream(fileWriter);

    try {
      // Add the contents of the srcFolder to the zip - note the top folder (with the wallet id) is not added as it is coded in the name of the zip
      if (new File(srcFolder).list() != null) {
        for (String fileName : new File(srcFolder).list()) {
          if (!includeBlockStore && fileName.endsWith(InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX)) {
            // Do not include the block store (to save space)
            continue;
          }
          addFileToZip(srcFolder, fileName, zip, includeBlockStore);
        }
      }
    } finally {
      zip.flush();
      zip.close();
    }
  }

  private void addFileToZip(String path, String srcFile, ZipOutputStream zip, Boolean includeBlockStore)
          throws IOException {

    File srcFileOnDisk = new File(path + File.separator + srcFile);
    if (srcFileOnDisk.isDirectory()) {
      addFolderToZip(path, srcFile, zip, includeBlockStore);
    } else {
      byte[] buf = new byte[1024];
      int len;
      FileInputStream in = new FileInputStream(srcFileOnDisk);
      zip.putNextEntry(new ZipEntry(srcFile));

      while ((len = in.read(buf)) > 0) {
        zip.write(buf, 0, len);
      }
    }
  }

  private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, Boolean includeBlockStore)
          throws IOException {
    File folder = new File(srcFolder);
    File folderOnDisk = new File(path + File.separator + srcFolder);

    // Don't include the zip-backups folder in the backups
    if (folder.getAbsolutePath().contains(LOCAL_ZIP_BACKUP_DIRECTORY_NAME)) {
      return;
    }

    if (folderOnDisk.list() != null) {
      for (String fileName : folderOnDisk.list()) {
        if (!includeBlockStore && fileName.endsWith(InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX)) {
          // Do not include the block store (to save space)
          continue;
        }

        log.debug("Adding file to zip :" + fileName);
        addFileToZip(path, srcFolder + File.separator + fileName, zip, includeBlockStore);
      }
    }
  }

  private void unzip(String zipFileName, String directoryToExtractTo) throws IOException {
    Enumeration entriesEnum;
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(zipFileName);

      entriesEnum = zipFile.entries();

      File directory = new File(directoryToExtractTo);
      InstallationManager.createDirectoryIfNecessary(directory);

      while (entriesEnum.hasMoreElements()) {

        ZipEntry entry = (ZipEntry) entriesEnum.nextElement();

        if (entry.isDirectory()) {
          InstallationManager.createDirectoryIfNecessary(new File(directoryToExtractTo + File.separator + entry.getName()));
        } else {

          log.debug("Extracting file: " + entry.getName());

        /* This part is necessary because file entry can come before
         * directory entry where is file located
         * i.e.:
         *   /foo/foo.txt
         *   /foo/
         */
          String dir = dirpart(entry.getName());
          if (dir != null) {
            InstallationManager.createDirectoryIfNecessary(new File(directoryToExtractTo + File.separator + dir));
          }
          String name = entry.getName();

          writeFile(zipFile.getInputStream(entry),
                  new BufferedOutputStream(new FileOutputStream(
                          directoryToExtractTo + name)));
        }
      }

    } finally {

      if (zipFile != null) {
        zipFile.close();
      }
    }
  }

  private static String dirpart(String name) {
    int s = name.lastIndexOf(File.separatorChar);
    return s == -1 ? null : name.substring(0, s);
  }
}

//  /**
//   * Thin the wallet backups when they reach the MAXIMUM_NUMBER_OF_BACKUPS setting.
//   * Thinning is done by removing the most quickly replaced backup, except for the first and last few
//   * (as they are considered to be more valuable backups).
//   *
//   * @param backupDirectoryName
//   */
//  void thinBackupDirectory(String walletFilename, String backupSuffixText) {
//    if (dateFormat == null) {
//      dateFormat = new SimpleDateFormat(BACKUP_SUFFIX_FORMAT);
//    }
//
//    if (walletFilename == null || backupSuffixText == null) {
//      return;
//    }
//
//    // Find out how many wallet backups there are.
//    List<File> backupWallets = getWalletsInBackupDirectory(walletFilename, backupSuffixText);
//
//    if (backupWallets.size() < MAXIMUM_NUMBER_OF_BACKUPS) {
//      // No thinning required.
//      return;
//    }
//
//    // Work out the date the backup was made for each of the wallet.
//    // This is done using the timestamp rather than the write time of the file.
//    Map<File, Date> mapOfFileToBackupTimes = new HashMap<File, Date>();
//    for (int i = 0; i < backupWallets.size(); i++) {
//      String filename = backupWallets.get(i).getName();
//      if (filename.length() > 22) { // 22 = 1 for hyphen + 14 for timestamp + 1 for dot + 6 for wallet.
//        int startOfTimestamp = filename.length() - 21; // 21 = 14 for timestamp + 1 for dot + 6 for wallet.
//        String timestampText = filename.substring(startOfTimestamp, startOfTimestamp + BACKUP_SUFFIX_FORMAT.length());
//        try {
//          Date parsedTimestamp = dateFormat.parse(timestampText);
//          mapOfFileToBackupTimes.put(backupWallets.get(i), parsedTimestamp);
//        } catch (ParseException pe) {
//          // Cannot parse text - may be some other type of file the user has put in the directory.
//          log.debug("For wallet '" + filename + " could not parse the timestamp of '" + timestampText + "'.");
//        }
//      }
//    }
//
//    // See which wallet is most quickly replaced by another backup - this will be thinned.
//    int walletBackupToDeleteIndex = -1; // Not set yet.
//    long walletBackupToDeleteReplacementTimeMillis = Integer.MAX_VALUE; // How quickly the wallet was replaced by a later one.
//
//    for (int i = 0; i < backupWallets.size(); i++) {
//      if ((i < NUMBER_OF_FIRST_WALLETS_TO_ALWAYS_KEEP)
//              || (i >= backupWallets.size() - NUMBER_OF_LAST_WALLETS_TO_ALWAYS_KEEP)) {
//        // Keep the very first and last wallets always.
//      } else {
//        // If there is a data directory for the backup then it may have been opened
//        // in MultiBit so we will skip considering it for deletion.
//        String possibleDataDirectory = calculateTopLevelBackupDirectoryName(backupWallets.get(i));
//        boolean theWalletHasADataDirectory = (new File(possibleDataDirectory)).exists();
//
//        // Work out how quickly the wallet is replaced by the next backup.
//        Date thisWalletTimestamp = mapOfFileToBackupTimes.get(backupWallets.get(i));
//        Date nextWalletTimestamp = mapOfFileToBackupTimes.get(backupWallets.get(i + 1));
//        if (thisWalletTimestamp != null && nextWalletTimestamp != null) {
//          long deltaTimeMillis = nextWalletTimestamp.getTime() - thisWalletTimestamp.getTime();
//          if (deltaTimeMillis < walletBackupToDeleteReplacementTimeMillis && !theWalletHasADataDirectory) {
//            // This is the best candidate for deletion so far.
//            walletBackupToDeleteIndex = i;
//            walletBackupToDeleteReplacementTimeMillis = deltaTimeMillis;
//          }
//        }
//      }
//    }
//
//    if (walletBackupToDeleteIndex > -1) {
//      try {
//        // Secure delete the chosen backup wallet and its info file if present.
//        log.debug("To save space, secure deleting backup wallet '"
//                + backupWallets.get(walletBackupToDeleteIndex).getAbsolutePath() + "'.");
//        FileHandler.secureDelete(backupWallets.get(walletBackupToDeleteIndex));
//
//        String walletInfoBackupFilename = backupWallets.get(walletBackupToDeleteIndex).getAbsolutePath()
//                .replaceAll(BitcoinModel.WALLET_FILE_EXTENSION + "$", INFO_FILE_SUFFIX_STRING);
//        File walletInfoBackup = new File(walletInfoBackupFilename);
//        if (walletInfoBackup.exists()) {
//          log.debug("To save space, secure deleting backup info file '" + walletInfoBackup.getAbsolutePath() + "'.");
//          FileHandler.secureDelete(walletInfoBackup);
//        }
//      } catch (IOException ioe) {
//        log.error(ioe.getClass().getName() + " " + ioe.getMessage());
//      }
//    }
//  }
//
//  void copyFileAndEncrypt(File sourceFile, File destinationFile, CharSequence passwordToUse) throws IOException {
//    if (passwordToUse == null || passwordToUse.length() == 0) {
//      throw new IllegalArgumentException("Password cannot be blank");
//    }
//
//    if (destinationFile.exists()) {
//      throw new IllegalArgumentException("The destination file '" + destinationFile.getAbsolutePath() + "' already exists.");
//    } else {
//      // Attempt to create it
//      if (!destinationFile.createNewFile()) {
//        throw new IllegalArgumentException("The destination file '" + destinationFile.getAbsolutePath() + "' could not be created. Check permissions.");
//      }
//    }
//
//    // Read in the source file.
//    byte[] sourceFileUnencrypted = FileHandler.read(sourceFile);
//
//    // Encrypt the data.
//    byte[] salt = new byte[KeyCrypterScrypt.SALT_LENGTH];
//    secureRandom.nextBytes(salt);
//
//    Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder()
//            .setSalt(ByteString.copyFrom(salt));
//    Protos.ScryptParameters scryptParameters = scryptParametersBuilder.build();
//    KeyCrypterScrypt keyCrypter = new KeyCrypterScrypt(scryptParameters);
//    EncryptedPrivateKey encryptedData = keyCrypter.encrypt(sourceFileUnencrypted, keyCrypter.deriveKey(passwordToUse));
//
//    // The format of the encrypted data is:
//    // 7 magic bytes 'mendoza' in ASCII.
//    // 1 byte version number of format - initially set to 0
//    // 8 bytes salt
//    // 16 bytes iv
//    // rest of file is the encrypted byte data
//
//    FileOutputStream fileOutputStream = null;
//    try {
//      fileOutputStream = new FileOutputStream(destinationFile);
//      fileOutputStream.write(ENCRYPTED_FILE_FORMAT_MAGIC_BYTES);
//
//      // file format version.
//      fileOutputStream.write(FILE_ENCRYPTED_VERSION_NUMBER);
//
//      fileOutputStream.write(salt); // 8 bytes.
//      fileOutputStream.write(encryptedData.getInitialisationVector()); // 16 bytes.
//      System.out.println(Utils.bytesToHexString(encryptedData.getInitialisationVector()));
//
//      fileOutputStream.write(encryptedData.getEncryptedBytes());
//      System.out.println(Utils.bytesToHexString(encryptedData.getEncryptedBytes()));
//    } finally {
//      if (fileOutputStream != null) {
//        fileOutputStream.flush();
//        fileOutputStream.close();
//      }
//    }
//
//    // Read in the file again and decrypt it to make sure everything was ok.
//    byte[] phoenix = readFileAndDecrypt(destinationFile, passwordToUse);
//
//    if (!org.spongycastle.util.Arrays.areEqual(sourceFileUnencrypted, phoenix)) {
//      throw new IOException("File '" + sourceFile.getAbsolutePath() + "' was not correctly encrypted to file '" + destinationFile.getAbsolutePath());
//    }
//  }
//
//  public byte[] readFileAndDecrypt(File encryptedFile, CharSequence passwordToUse) throws IOException {
//    // Read in the encrypted file.
//    byte[] sourceFileEncrypted = FileHandler.read(encryptedFile);
//
//    // Check the first bytes match the magic number.
//    if (!org.spongycastle.util.Arrays.areEqual(ENCRYPTED_FILE_FORMAT_MAGIC_BYTES, org.spongycastle.util.Arrays.copyOfRange(sourceFileEncrypted, 0, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length))) {
//      throw new IOException("File '" + encryptedFile.getAbsolutePath() + "' did not start with the correct magic bytes.");
//    }
//
//    // If the file is too short don't process it.
//    if (sourceFileEncrypted.length < ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1 + KeyCrypterScrypt.SALT_LENGTH + KeyCrypterScrypt.BLOCK_LENGTH) {
//      throw new IOException("File '" + encryptedFile.getAbsolutePath() + "' is too short to decrypt. It is " + sourceFileEncrypted.length + " bytes long.");
//    }
//
//    // Check the format version.
//    String versionNumber = "" + sourceFileEncrypted[ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length];
//    //System.out.println("FileHandler - versionNumber = " + versionNumber);
//    if (!("0".equals(versionNumber))) {
//      throw new IOException("File '" + encryptedFile.getAbsolutePath() + "' did not have the expected version number of 0. It was " + versionNumber);
//    }
//
//    // Extract the salt.
//    byte[] salt = org.spongycastle.util.Arrays.copyOfRange(sourceFileEncrypted, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1 + KeyCrypterScrypt.SALT_LENGTH);
//    //System.out.println("FileHandler - salt = " + Utils.bytesToHexString(salt));
//
//    // Extract the IV.
//    byte[] iv = org.spongycastle.util.Arrays.copyOfRange(sourceFileEncrypted, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1 + KeyCrypterScrypt.SALT_LENGTH, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1 + KeyCrypterScrypt.SALT_LENGTH + KeyCrypterScrypt.BLOCK_LENGTH);
//    //System.out.println("FileHandler - iv = " + Utils.bytesToHexString(iv));
//
//    // Extract the encrypted bytes.
//    byte[] encryptedBytes = org.spongycastle.util.Arrays.copyOfRange(sourceFileEncrypted, ENCRYPTED_FILE_FORMAT_MAGIC_BYTES.length + 1 + KeyCrypterScrypt.SALT_LENGTH + KeyCrypterScrypt.BLOCK_LENGTH, sourceFileEncrypted.length);
//    //System.out.println("FileHandler - encryptedBytes = " + Utils.bytesToHexString(encryptedBytes));
//
//    // Decrypt the data.
//    Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(salt));
//    Protos.ScryptParameters scryptParameters = scryptParametersBuilder.build();
//    KeyCrypter keyCrypter = new KeyCrypterScrypt(scryptParameters);
//    EncryptedPrivateKey encryptedPrivateKey = new EncryptedPrivateKey(iv, encryptedBytes);
//    return keyCrypter.decrypt(encryptedPrivateKey, keyCrypter.deriveKey(passwordToUse));
//  }
//
//  /**
//   * Work out the best wallet backups to try to load
//   *
//   * @param walletFile
//   * @return Collection<String> The best wallets to try to load, in order of goodness.
//   */
//  Collection<String> calculateBestWalletBackups(File walletFile, WalletInfoData walletInfo) {
//    Collection<String> backupWalletsToTry = new ArrayList<String>();
//
//    // Get the name of the rolling backup file.
//    String walletBackupFilenameLong = walletInfo.getProperty(BitcoinModel.WALLET_BACKUP_FILE);
//    String walletBackupFilenameShort = null;
//    if (walletBackupFilenameLong != null && !"".equals(walletBackupFilenameLong)) {
//      File walletBackupFile = new File(walletBackupFilenameLong);
//      walletBackupFilenameShort = walletBackupFile.getName();
//      if (!walletBackupFile.exists()) {
//        walletBackupFilenameLong = null;
//        walletBackupFilenameShort = null;
//      }
//    } else {
//      // No backup file was listed in the info file. Maybe it is damaged so take the most recent
//      // file in the rolling backup directory, if there is one.
//      Collection<File> rollingWalletBackups = getWalletsInBackupDirectory(walletFile.getAbsolutePath(),
//              ROLLING_WALLET_BACKUP_DIRECTORY_NAME);
//      if (rollingWalletBackups != null && !rollingWalletBackups.isEmpty()) {
//        List<String> rollingWalletBackupFilenames = new ArrayList<String>();
//        for (File file : rollingWalletBackups) {
//          rollingWalletBackupFilenames.add(file.getAbsolutePath());
//        }
//        Collections.sort(rollingWalletBackupFilenames);
//        walletBackupFilenameLong = rollingWalletBackupFilenames.get(rollingWalletBackupFilenames.size() - 1);
//        walletBackupFilenameShort = (new File(walletBackupFilenameLong)).getName();
//      }
//    }
//
//    Collection<File> unencryptedWalletBackups = getWalletsInBackupDirectory(walletFile.getAbsolutePath(),
//            UNENCRYPTED_WALLET_BACKUP_DIRECTORY_NAME);
//    Collection<File> encryptedWalletBackups = getWalletsInBackupDirectory(walletFile.getAbsolutePath(),
//            ENCRYPTED_WALLET_BACKUP_DIRECTORY_NAME);
//
//    // Make a list of ALL the unencrypted and encrypted backup names and sort them.
//    // Because the backups have a timestamp YYYYMMDDHHMMSS sort in ascending order gives most recent - we will use this one.
//    List<String> encryptedAndUnencryptedFilenames = new ArrayList<String>();
//
//    // Sorting is done by the filename, keep track of the corresponding absolute path.
//    Map<String, String> shortNamesToLongMap = new HashMap<String, String>();
//
//    if (unencryptedWalletBackups != null) {
//      for (File file : unencryptedWalletBackups) {
//        encryptedAndUnencryptedFilenames.add(file.getName());
//        shortNamesToLongMap.put(file.getName(), file.getAbsolutePath());
//      }
//    }
//    if (encryptedWalletBackups != null) {
//      for (File file : encryptedWalletBackups) {
//        encryptedAndUnencryptedFilenames.add(file.getName());
//        // If there is a duplicate, encrypted wallets are preferred.
//        shortNamesToLongMap.put(file.getName(), file.getAbsolutePath());
//      }
//    }
//
//    Collections.sort(encryptedAndUnencryptedFilenames);
//
//    String bestCandidateShort = null;
//    String bestCandidateLong = null;
//    if (encryptedAndUnencryptedFilenames.size() > 0) {
//      bestCandidateShort = encryptedAndUnencryptedFilenames.get(encryptedAndUnencryptedFilenames.size() - 1);
//      if (bestCandidateShort != null) {
//        bestCandidateLong = shortNamesToLongMap.get(bestCandidateShort);
//      }
//    }
//    log.debug("For wallet '" + walletFile + "' the rolling backup file was '" + walletBackupFilenameLong + "' and the best encrypted/ unencrypted backup was '" + bestCandidateLong + "'");
//
//    if (walletBackupFilenameLong == null) {
//      if (bestCandidateLong == null) {
//        // No backups to try.
//      } else {
//        // bestCandidate only.
//        backupWalletsToTry.add(bestCandidateLong);
//      }
//    } else {
//      if (bestCandidateLong == null) {
//        // WalletBackupFilename only.
//        backupWalletsToTry.add(walletBackupFilenameLong);
//      } else {
//        // Have both. Try the most recent first (preferring the backups to the rolling backups if there is a tie).
//        if (walletBackupFilenameShort.compareTo(bestCandidateShort) <= 0) {
//          backupWalletsToTry.add(bestCandidateLong);
//          backupWalletsToTry.add(walletBackupFilenameLong);//    return backupWalletsToTry;
//  }
//        } else {
//          backupWalletsToTry.add(walletBackupFilenameLong);
//          backupWalletsToTry.add(bestCandidateLong);
//        }
//      }
//    }



