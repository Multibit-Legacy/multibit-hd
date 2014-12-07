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

    fastSecureDelete(file);
    log.trace("End of secureDelete");
    log.debug("Secure delete took {} milliseconds", System.currentTimeMillis() - start);

  }

  /**
   * An alternative secure delete algorithm from http://www.cafeaulait.org/books/javaio2/ioexamples/14/SecureDelete.java
   *
   * @param file the file to secure delete
   */
  private static void fastSecureDelete(File file) throws IOException {
    if (file.exists()) {
      SecureRandom random = new SecureRandom();
      RandomAccessFile raf = new RandomAccessFile(file, "rw");
      FileChannel channel = raf.getChannel();

      MappedByteBuffer buffer = channel.map(
        FileChannel.MapMode.READ_WRITE,
        0,
        raf.length()
      );

      // Overwrite with random data; one byte at a time
      byte[] data = new byte[1];
      while (buffer.hasRemaining()) {
        random.nextBytes(data);
        buffer.put(data[0]);
      }
      buffer.force();

      boolean deleteSuccess = file.delete();
      log.trace("Result of delete was {} for:\n'{}'", deleteSuccess, file.getAbsolutePath());
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

    // Use Guava's atomic temporary file creation for a more secure operation
    File temporaryDirectory = Files.createTempDir();
    temporaryDirectory.deleteOnExit();

    if (temporaryDirectory.exists() && temporaryDirectory.canWrite() && temporaryDirectory.canRead()) {
      log.debug("Created temporary directory:\n'{}'", temporaryDirectory.getAbsolutePath());
      return temporaryDirectory;
    }

    // Must have failed to be here
    throw new IOException("Did not create '"+temporaryDirectory.getAbsolutePath()+"' with RW permissions");
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
