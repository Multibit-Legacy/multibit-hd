package org.multibit.hd.core.files;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

  private static SecureRandom secureRandom = new SecureRandom();

  private static boolean initialised = false;

   // Nonsense bytes to fill up deleted files - these have no meaning.
  static final byte[] NONSENSE_BYTES = new byte[]{(byte) 0xF0, (byte) 0xA6, (byte) 0x55, (byte) 0xAA, (byte) 0x33,
    (byte) 0x77, (byte) 0x33, (byte) 0x37, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0xC2, (byte) 0xB3,
    (byte) 0xA4, (byte) 0x9A, (byte) 0x30, (byte) 0x7F, (byte) 0xE5, (byte) 0x5A, (byte) 0x23, (byte) 0x47, (byte) 0x13,
    (byte) 0x17, (byte) 0x15, (byte) 0x32, (byte) 0x5C, (byte) 0x77, (byte) 0xC9, (byte) 0x73, (byte) 0x04, (byte) 0x2D,
    (byte) 0x40, (byte) 0x0F, (byte) 0xA5, (byte) 0xA6, (byte) 0x43, (byte) 0x77, (byte) 0x33, (byte) 0x3B, (byte) 0x62,
    (byte) 0x34, (byte) 0xB6, (byte) 0x72, (byte) 0x32, (byte) 0xB3, (byte) 0xA4, (byte) 0x4B, (byte) 0x80, (byte) 0x7F,
    (byte) 0xC5, (byte) 0x43, (byte) 0x23, (byte) 0x47, (byte) 0x13, (byte) 0xB7, (byte) 0xA5, (byte) 0x32, (byte) 0xDC,
    (byte) 0x79, (byte) 0x19, (byte) 0xB1, (byte) 0x03, (byte) 0x9D};
  static final int BULKING_UP_FACTOR = 16;
  static final byte[] SECURE_DELETE_FILL_BYTES = new byte[NONSENSE_BYTES.length * BULKING_UP_FACTOR];

  private static void initialise() {
    // Make some SECURE_DELETE_FILL_BYTES bytes = x BULKING_UP_FACTOR the
    // NONSENSE just to save write time.
    for (int i = 0; i < BULKING_UP_FACTOR; i++) {
      System.arraycopy(
        NONSENSE_BYTES, 0,
        SECURE_DELETE_FILL_BYTES, NONSENSE_BYTES.length * i,
        NONSENSE_BYTES.length);
    }
    initialised = true;
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
    if (!initialised) {
      initialise();
    }

    long start = System.currentTimeMillis();
    log.trace("Start of secureDelete");

    if (Utils.isWindows()) {
      // Use slow secure delete
      slowSecureDelete(file);
    } else {
      fastSecureDelete(file);
    }
    log.trace("End of secureDelete");
    log.debug("Secure delete took {} milliseconds", System.currentTimeMillis() - start);

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
    @SuppressFBWarnings({"DM_GC"})
    public static synchronized void slowSecureDelete(File file) throws IOException {
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
    }

  /**
   * An alternative secure delete algorithm from http://www.cafeaulait.org/books/javaio2/ioexamples/14/SecureDelete.java
   *
   * @param file the file to secure delete
   */
  private static void fastSecureDelete(File file) throws IOException {
    if (file.exists()) {
      RandomAccessFile raf = null;
      FileChannel channel = null;
      MappedByteBuffer buffer;
      try {
        raf = new RandomAccessFile(file, "rw");
        channel = raf.getChannel();

        buffer = channel.map(
                FileChannel.MapMode.READ_WRITE,
                0,
                raf.length()
        );

        // Overwrite with random data; one byte at a time
        byte[] data = new byte[1];
        while (buffer.hasRemaining()) {
          secureRandom.nextBytes(data);
          buffer.put(data[0]);
        }

        // Ensure we push this out to the file system
        buffer.force();
        channel.close();
      } finally {
        buffer = null;

        if (channel != null) {
          channel.close();
          channel = null;
        }
        if (raf != null) {
          raf.close();
          raf = null;
        }
      }

      // Delete file
      // Use JDK7 NIO Files to delete the file since it offers the following benefits:
      // * best chance at an atomic operation
      // * relies on native code
      // * works on Windows
      boolean deleteSuccess = Files.deleteIfExists(file.toPath());
      log.debug("Result of initial delete was {} for:\n'{}'", deleteSuccess, file.getAbsolutePath());

      if (Utils.isWindows()) {
        // Work around an issue on Windows whereby files are not deleted
        File canonical = file.getCanonicalFile();
        if (canonical.exists() && !canonical.delete())
          log.debug("Failed to delete canonical file {}", file.getCanonicalPath());
      }
    }
  }

  /**
   * @param directory The directory to verify or create
   * @return The directory
   * @throws java.lang.IllegalStateException If the file could not be created
   */
  public static File verifyOrCreateDirectory(File directory) {

    log.debug("Verify or create directory:\n'{}'", directory.getAbsolutePath());

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

    log.debug("Verify or create directory:\n'{}'", directory.getAbsolutePath());

    if (!parentDirectory.exists()) {
      Preconditions.checkState(parentDirectory.mkdir(), "Could not create parentDirectory: '" + parentDirectory + "'");
    }
    if (!directory.exists()) {
      Preconditions.checkState(directory.mkdir(), "Could not create directory: '" + directory + "'");
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

    log.debug("Verify or create file:\n'{}'", file.getAbsolutePath());

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

    // Use JDK7 NIO Files for a more secure operation than Guava
    File topLevelTemporaryDirectory = Files.createTempDirectory("mbhd").toFile();

    topLevelTemporaryDirectory.deleteOnExit();

    // Add a random number to the topLevelTemporaryDirectory
    String temporaryDirectoryName = topLevelTemporaryDirectory.getAbsolutePath() + File.separator + secureRandom.nextInt(Integer.MAX_VALUE);
    log.debug("Temporary directory name:\n'{}'", temporaryDirectoryName);
    File temporaryDirectory = new File(temporaryDirectoryName);
    temporaryDirectory.deleteOnExit();

    if (temporaryDirectory.mkdir() && temporaryDirectory.exists() && temporaryDirectory.canWrite() && temporaryDirectory.canRead()) {
      log.debug("Created temporary directory:\n'{}'", temporaryDirectory.getAbsolutePath());
      return temporaryDirectory;
    }

    // Must have failed to be here
    throw new IOException("Did not create '" + temporaryDirectory.getAbsolutePath() + "' with RW permissions");
  }

  /**
   * Create a temporary filename but do not create the actual file
   */
  public static File createTemporaryFilename(String prefix, String suffix, File dir) throws IOException {
    long n = secureRandom.nextLong();
    if (n == Long.MIN_VALUE) {
      n = 0;      // corner case
    } else {
      n = Math.abs(n);
    }
    String name = prefix + Long.toString(n) + suffix;
    File f = new File(dir, name);
    if (!name.equals(f.getName()))
      throw new IOException("Unable to create temporary file");
    return f;

  }

  /**
   * Securely write a file to the file system using temporary file then renaming to the destination
   */
  public static void writeFile(InputStream inputStream, File tempFile, File destFile) throws IOException {

    try (OutputStream tempStream = new FileOutputStream(tempFile)) {
      // Copy the original to the temporary location
      ByteStreams.copy(inputStream, tempStream);
      // Attempt to force the bits to hit the disk. In reality the OS or hard disk itself may still decide
      // to not write through to physical media for at least a few seconds, but this is the best we can do.
      tempStream.flush();
    }

    // Use JDK7 NIO Files to move the file since it offers the following benefits:
    // * best chance at an atomic operation
    // * relies on native code
    // * ensures destination is deleted
    // * performs a rename where possible to reduce data corruption if power fails
    // * works on Windows
    Path tempFilePath = tempFile.toPath();
    Path destFilePath = destFile.toPath();
    java.nio.file.Files.move(
            tempFilePath,
            destFilePath,
            StandardCopyOption.REPLACE_EXISTING
    );

  }

}
