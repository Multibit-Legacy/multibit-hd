package org.multibit.hd.core.utils;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>Utility to provide the following to file system:</p>
 * <ul>
 * <li>Handling temporary files</li>
 * <li>Locating platform-specific support files (e.g. configuration)</li>
 * <li>Locating platform-specific directories</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class FileUtils {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

  // Nonsense bytes to fill up deleted files - these have no meaning.
  public static byte[] NONSENSE_BYTES = new byte[]{(byte) 0xF0, (byte) 0xA6, (byte) 0x55, (byte) 0xAA, (byte) 0x33,
          (byte) 0x77, (byte) 0x33, (byte) 0x37, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0xC2, (byte) 0xB3,
          (byte) 0xA4, (byte) 0x9A, (byte) 0x30, (byte) 0x7F, (byte) 0xE5, (byte) 0x5A, (byte) 0x23, (byte) 0x47, (byte) 0x13,
          (byte) 0x17, (byte) 0x15, (byte) 0x32, (byte) 0x5C, (byte) 0x77, (byte) 0xC9, (byte) 0x73, (byte) 0x04, (byte) 0x2D,
          (byte) 0x40, (byte) 0x0F, (byte) 0xA5, (byte) 0xA6, (byte) 0x43, (byte) 0x77, (byte) 0x33, (byte) 0x3B, (byte) 0x62,
          (byte) 0x34, (byte) 0xB6, (byte) 0x72, (byte) 0x32, (byte) 0xB3, (byte) 0xA4, (byte) 0x4B, (byte) 0x80, (byte) 0x7F,
          (byte) 0xC5, (byte) 0x43, (byte) 0x23, (byte) 0x47, (byte) 0x13, (byte) 0xB7, (byte) 0xA5, (byte) 0x32, (byte) 0xDC,
          (byte) 0x79, (byte) 0x19, (byte) 0xB1, (byte) 0x03, (byte) 0x9D};
  public static int BULKING_UP_FACTOR = 16;
  public static byte[] SECURE_DELETE_FILL_BYTES = new byte[NONSENSE_BYTES.length * BULKING_UP_FACTOR];

  static {
    // Make some SECURE_DELETE_FILL_BYTES bytes = x BULKING_UP_FACTOR the
    // NONSENSE just to save write time.
    for (int i = 0; i < FileUtils.BULKING_UP_FACTOR; i++) {
      System.arraycopy(FileUtils.NONSENSE_BYTES, 0, FileUtils.SECURE_DELETE_FILL_BYTES, FileUtils.NONSENSE_BYTES.length * i, FileUtils.NONSENSE_BYTES.length);
    }
  }

  /**
   * Utilities have private constructor
   */
  private FileUtils() {
  }

  /**
   * Delete a file with an overwrite of all of the data.
   * <p/>
   * Set bit patterns are used rather than random numbers to avoid a
   * futex_wait_queue_me error on Linux systems (related to /dev/random usage)
   *
   * @param file The file to secure delete
   * @throws java.io.IOException if the operation fails for any reason
   */
  public static void secureDelete(File file) throws IOException {
    log.debug("Start of secureDelete");

    if (file != null && file.exists()) {
      try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {
        // Prep for file delete as this can be fiddly on windows.
        // Make sure it is writable and any references to it are garbage
        // collected and finalized.
        file.setWritable(true);
        System.gc();

        long length = file.length();
        raf.seek(0);
        raf.getFilePointer();
        int pos = 0;
        while (pos < length) {
          raf.write(SECURE_DELETE_FILL_BYTES);
          pos += SECURE_DELETE_FILL_BYTES.length;
        }
      }
      boolean deleteSuccess = file.delete();
      log.debug("Result of delete of file '" + file.getAbsolutePath() + "' was " + deleteSuccess);
    }
    log.debug("End of secureDelete");
  }

  /**
   * Work out the directory part of a filename
   *
   * @param name of file
   * @return directory part of filename
   */
  public static String directoryPart(String name) {
    int s = name.lastIndexOf(File.separatorChar);
    return s == -1 ? null : name.substring(0, s);
  }

  /**
    * Work out the file part of a filename
    *
    * @param name of file
    * @return file part of filename
    */
   public static String filePart(String name) {
     int s = name.lastIndexOf(File.separatorChar);
     if (s == -1) {
       return name;
     } else {
       return name.substring(s + 1);
     }
   }
  public static void createDirectoryIfNecessary(File directoryToCreate) {
    if (!directoryToCreate.exists()) {
      Preconditions.checkState(directoryToCreate.mkdir(), "Could not create the directory of '" + directoryToCreate + "'");
    }
    Preconditions.checkState(directoryToCreate.isDirectory(), "Incorrectly identified the directory of '" + directoryToCreate + " as a file");
  }

  public static void copyFile(File sourceFile, File destinationFile) throws IOException {
    if (!destinationFile.exists()) {
      destinationFile.createNewFile();
    }
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    FileChannel source = null;
    FileChannel destination = null;
    try {
      fileInputStream = new FileInputStream(sourceFile);
      source = fileInputStream.getChannel();
      fileOutputStream = new FileOutputStream(destinationFile);
      destination = fileOutputStream.getChannel();
      long transferred = 0;
      long bytes = source.size();
      while (transferred < bytes) {
        transferred += destination.transferFrom(source, 0, source.size());
        destination.position(transferred);
      }
    } finally {
      if (source != null) {
        source.close();
      } else if (fileInputStream != null) {
        fileInputStream.close();
      }
      if (destination != null) {
        destination.close();
      } else if (fileOutputStream != null) {
        fileOutputStream.flush();
        fileOutputStream.close();
      }
    }
  }

  /**
   * Write a file from the inputstream to the outputstream
   */
  public static void writeFile(InputStream in, OutputStream out)
          throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();
    out.close();
  }

  /**
   * Copy the files in the specified srcFolder to the destZipFile
   * The zip-backups are not stored in the backup (as they are zip-backups themselves) but the rolling backups
   * are to increase backup coverage
   *
   * @param srcFolder         The directory holding the files to zip
   * @param destZipFile       The zip file to copy files into
   * @param includeBlockStore if true then include the blockstore, if false then don't
   * @throws java.io.IOException
   */
  public static void zipFolder(String srcFolder, String destZipFile, boolean includeBlockStore) throws IOException {
    ZipOutputStream zip;
    FileOutputStream fileWriter;

    fileWriter = new FileOutputStream(destZipFile);
    zip = new ZipOutputStream(fileWriter);

    try {
      // Add the contents of the srcFolder to the zip - note the top folder (with the wallet id) is not added as it is coded in the name of the zip
      if (new File(srcFolder).list() != null) {
        for (String fileName : new File(srcFolder).list()) {
          if (!includeBlockStore && fileName.endsWith(InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX)) {
            // Do not include the block writeContacts (to save space)
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

  private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, Boolean includeBlockStore)
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

  private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, Boolean includeBlockStore)
          throws IOException {
    File folder = new File(srcFolder);
    File folderOnDisk = new File(path + File.separator + srcFolder);

    // Don't include the zip-backups folder in the backups
    if (folder.getAbsolutePath().contains(BackupManager.LOCAL_ZIP_BACKUP_DIRECTORY_NAME)) {
      return;
    }

    if (folderOnDisk.list() != null) {
      for (String fileName : folderOnDisk.list()) {
        if (!includeBlockStore && fileName.endsWith(InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX)) {
          // Do not include the block writeContacts (to save space)
          continue;
        }
        addFileToZip(path, srcFolder + File.separator + fileName, zip, includeBlockStore);
      }
    }
  }

  public static void unzip(String zipFileName, String directoryToExtractTo) throws IOException {
    Enumeration entriesEnum;
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(zipFileName);

      entriesEnum = zipFile.entries();

      File directory = new File(directoryToExtractTo);
      createDirectoryIfNecessary(directory);

      while (entriesEnum.hasMoreElements()) {

        ZipEntry entry = (ZipEntry) entriesEnum.nextElement();

        if (entry.isDirectory()) {
          createDirectoryIfNecessary(new File(directoryToExtractTo + File.separator + entry.getName()));
        } else {
          log.debug("Extracting file: " + entry.getName());

          // This part is necessary because file entry can come before
          // directory entry where is file located
          // i.e.:
          //   /foo/foo.txt
          //   /foo/
          String dir = directoryPart(entry.getName());
          if (dir != null) {
            createDirectoryIfNecessary(new File(directoryToExtractTo + File.separator + dir));
          }
          String name = entry.getName();

          writeFile(zipFile.getInputStream(entry),
                  new BufferedOutputStream(new FileOutputStream(
                          directoryToExtractTo + File.separator + name)));
        }
      }
    } finally {
      if (zipFile != null) {
        zipFile.close();
      }
    }
  }
}
