package org.multibit.hd.core.files;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <p>Utilties to provide the following to applications:</p>
 * <ul>
 * <li>Access to secure file operations (delete, create with access restrictions etc)</li>
 * </ul>
 * <p>Uses Java new I/O and Guava Files where possible</p>
 *
 * @since 0.0.1
 *  
 */
public class SecureFiles {

  private static final Logger log = LoggerFactory.getLogger(SecureFiles.class);

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
    for (int i = 0; i < BULKING_UP_FACTOR; i++) {
      System.arraycopy(
        NONSENSE_BYTES, 0,
        SECURE_DELETE_FILL_BYTES, NONSENSE_BYTES.length * i,
        NONSENSE_BYTES.length);
    }
  }

  /**
   * Utilities have private constructor
   */
  private SecureFiles() {
  }

  /**
   * Delete a file with an overwrite of all of the data.
   * <p/>
   * Set bit patterns are used rather than random numbers to avoid a
   * futex_wait_queue_me error on Linux systems (related to /dev/random usage)
   *
   * @param file The file to secure delete
   *
   * @throws java.io.IOException if the operation fails for any reason
   */
  public static void secureDelete(File file) throws IOException {
    log.debug("Start of secureDelete");

    if (file != null && file.exists()) {
      try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {
        // Prep for file delete as this can be fiddly on Windows
        // Make sure it is writable and any references to it are garbage
        // collected and finalized.
        if (!file.setWritable(true)) {
          throw new IOException("Could not write to file " + file.getAbsolutePath());
        }
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
   * @param directory The directory to verify or create
   *
   * @return The directory
   *
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateDirectory(File directory) {

    if (!directory.exists()) {
      Preconditions.checkState(directory.mkdirs(), "Could not create directory: '" + directory + "'");
    }

    Preconditions.checkState(directory.isDirectory(), "Incorrectly identified the directory of '" + directory + " as a file.");

    return directory;
  }

  /**
   * @param parentDirectory The parent directory
   * @param childDirectory  The child directory (will be created if absent)
   *
   * @return The child directory
   *
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateDirectory(File parentDirectory, String childDirectory) {

    File directory = new File(parentDirectory, childDirectory);

    log.debug("Attempting to create directory '{}'", directory.getAbsolutePath());

    if (!directory.exists()) {
      Preconditions.checkState(directory.mkdirs(), "Could not create directory: '" + directory + "'");
    }

    Preconditions.checkState(directory.isDirectory(), "Incorrectly identified the directory of '" + directory + " as a file.");

    return directory;
  }

  /**
   * <p>Use atomic file operations to create a file with all parent directories in place</p>
   *
   * @param parentDirectory The parent directory
   * @param filename        The filename
   *
   * @return A File referring to the existent file
   *
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateFile(File parentDirectory, String filename) {

    Preconditions.checkNotNull(parentDirectory, "'parentDirectory' must be present");
    Preconditions.checkState(parentDirectory.isDirectory(), "'parentDirectory' must be a directory");

    Preconditions.checkNotNull(filename, "'filename' must be present");

    File file = new File(parentDirectory.getAbsolutePath() + "/" + filename);

    if (!file.exists()) {
      try {
        Preconditions.checkState(file.createNewFile(), "Could not create file: '" + file.getAbsolutePath() + "'");
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }

    Preconditions.checkState(file.isFile(), "Incorrectly identified the file of '" + file + " as a directory.");

    return file;

  }
}
