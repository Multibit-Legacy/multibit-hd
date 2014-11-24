package org.multibit.hd.core.files;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

/**
 * <p>Utilties to provide the following to applications:</p>
 * <ul>
 * <li>Access to secure file operations (delete, create with access restrictions etc)</li>
 * </ul>
 * <p>Uses Java new I/O and Guava Files where possible</p>
 *
 * @since 0.0.1
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
   * @throws java.io.IOException if the operation fails for any reason
   */
  public static synchronized void secureDelete(File file) throws IOException {

    long start = System.currentTimeMillis();
    log.trace("Start of secureDelete");

    alternativeSecureDelete(file);
    log.trace("End of secureDelete");
    log.debug("Secure delete of took {} milliseconds", System.currentTimeMillis() - start);
  }

  /**
   * Single pass secure delete
   *    *
      * @param file the file to secure delete
   */
   private static void singlePassSecureDelete(File file) throws IOException {
     long length;
     if (file != null && file.exists()) {
       try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {
         // Prep for file delete as this can be fiddly on Windows
         // Make sure it is writable and any references to it are garbage
         // collected and finalized.
         if (!file.setWritable(true)) {
           throw new IOException("Could not write to file " + file.getAbsolutePath());
         }
         System.gc();

         length = file.length();
         raf.seek(0);
         raf.getFilePointer();
         int pos = 0;
         while (pos < length) {
           raf.write(SECURE_DELETE_FILL_BYTES);
           pos += SECURE_DELETE_FILL_BYTES.length;
         }
       }
       boolean deleteSuccess = file.delete();
       log.trace("Result of delete of file '" + file.getAbsolutePath() + "' was " + deleteSuccess);
     }
   }

  /**
   * An alternative secure delete algorithm from http://www.cafeaulait.org/books/javaio2/ioexamples/14/SecureDelete.java
   *
   * @param file the file to secure delete
   */
  private static void alternativeSecureDelete(File file) throws IOException {
    if (file.exists()) {
      SecureRandom random = new SecureRandom();
      RandomAccessFile raf = new RandomAccessFile(file, "rw");
      FileChannel channel = raf.getChannel();
      MappedByteBuffer buffer
              = channel.map(FileChannel.MapMode.READ_WRITE, 0, raf.length());

      // overwrite with random data; one byte at a time
      byte[] data = new byte[1];
      while (buffer.hasRemaining()) {
        random.nextBytes(data);
        buffer.put(data[0]);
      }
      buffer.force();
      boolean deleteSuccess = file.delete();
      log.trace("Result of delete of file '" + file.getAbsolutePath() + "' was " + deleteSuccess);
    }
  }

  /**
   * @param directory The directory to verify or create
   * @return The directory
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateDirectory(File directory) {

    log.debug("Verify or create directory: '{}'", directory.getAbsolutePath());

    if (!directory.exists()) {
      Preconditions.checkState(directory.mkdirs(), "Could not create directory: '" + directory + "'");
    }

    Preconditions.checkState(directory.isDirectory(), "Incorrectly identified the directory of '" + directory + " as a file.");

    return directory;
  }

  /**
   * @param parentDirectory The parent directory
   * @param childDirectory  The child directory (will be created if absent)
   * @return The child directory
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateDirectory(File parentDirectory, String childDirectory) {

    File directory = new File(parentDirectory, childDirectory);

    log.debug("Verify or create directory: '{}'", directory.getAbsolutePath());

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
   * @return A File referring to the existent file
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateFile(File parentDirectory, String filename) {

    Preconditions.checkNotNull(parentDirectory, "'parentDirectory' must be present");
    Preconditions.checkState(parentDirectory.isDirectory(), "'parentDirectory' must be a directory");

    Preconditions.checkNotNull(filename, "'filename' must be present");

    File file = new File(parentDirectory.getAbsolutePath() + File.separator + filename);

    log.debug("Verify or create file: '{}'", file.getAbsolutePath());

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

  /**
   * <p>Atomically create a temporary directory that will be removed when the JVM exits</p>
   *
   * @return A random temporary directory
   * @throws java.io.IOException If something goes wrong
   */
  public static File createTemporaryDirectory() throws IOException {

    // Use Guava's atomic temporary file creation for a more secure operation
    File temporaryDirectory = Files.createTempDir();
    temporaryDirectory.deleteOnExit();

    log.debug("Created temporary directory: '{}'", temporaryDirectory.getAbsolutePath());

    return temporaryDirectory;
  }

  /**
   * Securely write a file to the file system using temporary file then renaming to the destination
   */
  public static void writeFile(InputStream inputStream, File tempFile, File destFile) throws IOException {

    try (OutputStream tempStream = new FileOutputStream(tempFile)) {
      //tempStream = new FileOutputStream(temp);
      ByteStreams.copy(inputStream, tempStream);
      // Attempt to force the bits to hit the disk. In reality the OS or hard disk itself may still decide
      // to not write through to physical media for at least a few seconds, but this is the best we can do.
      if (Utils.isWindows()) {
        // Work around an issue on Windows whereby you can't rename over existing files.
        File canonical = destFile.getCanonicalFile();
        if (canonical.exists() && !canonical.delete()) {
          throw new IOException("Failed to delete canonical wallet file for replacement with auto save");
        }
        if (tempFile.renameTo(canonical)) return; // else fall through.
        throw new IOException("Failed to rename '" + tempFile + "' to " + canonical);
      } else if (!tempFile.renameTo(destFile)) {
        throw new IOException("Failed to rename '" + tempFile + "' to " + destFile);
      }
    }
  }

}
