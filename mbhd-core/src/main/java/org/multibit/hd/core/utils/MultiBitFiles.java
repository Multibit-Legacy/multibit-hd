package org.multibit.hd.core.utils;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.exceptions.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
public class MultiBitFiles {

  private static final Logger log = LoggerFactory.getLogger(MultiBitFiles.class);

  /**
   * Utilities have private constructor
   */
  private MultiBitFiles() {
  }

  /**
   * @param filePrefix The file prefix
   *
   * @return A temporary directory to allow separate block chains
   *
   * @throws java.io.IOException If something goes wrong
   */
  public synchronized static File createTempDirectory(String filePrefix) throws IOException {

    Preconditions.checkNotNull(filePrefix, "'filePrefix' must be present");

    // Create a temporary file
    final File tempFile = File.createTempFile(filePrefix, Long.toString(System.currentTimeMillis()));

    // Attempt some operations to verify permissions

    // Delete as file
    if (!(tempFile.delete())) {
      throw new IOException("Could not delete temp file: " + tempFile.getAbsolutePath());
    }

    // Create as directory
    if (!(tempFile.mkdir())) {
      throw new IOException("Could not create temp directory: " + tempFile.getAbsolutePath());
    }

    // Test for locked
    isFileLocked("Checking file lock", tempFile);

    return tempFile;
  }

  /**
   * <p>Test file lock using rename to self technique</p>
   *
   * @param message The message to add to the log
   * @param file    The file or directory to test
   *
   * @return True if the file is locked
   */
  private static boolean isFileLocked(String message, File file) {

    Preconditions.checkNotNull(message, "'message' must be present");
    Preconditions.checkNotNull(file, "'file' must be present");

    boolean result = !file.renameTo(file);
    log.debug("{}. File '{}' locked: {}", message, file.getAbsolutePath(), result);

    return result;
  }

  /**
   * <p>Deletes directories recursively</p>
   *
   * @param fileToDelete The file or directory to delete
   */
  public static void deleteRecursively(File fileToDelete) {

    Preconditions.checkNotNull(fileToDelete, "'fileToDelete' must be present");

    if (fileToDelete.isDirectory()) {
      // Clear out the contents first
      File[] files = fileToDelete.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteRecursively(file);
        }
      }
    }

    // Keep track of file locks
    MultiBitFiles.isFileLocked("Deleting", fileToDelete);

    if (!fileToDelete.delete()) {
      log.warn("Failed to delete '{}'", fileToDelete);
    } else {
      log.debug("Deleted '{}'", fileToDelete);
    }
  }

  /**
   * @return The current working directory in canonical form (OS-dependent) usually for display to the user
   */
  public static String getCanonicalWorkingDirectory() {
    try {
      return new File("").getCanonicalPath();
    } catch (IOException e) {
      throw new CoreException(e);
    }
  }

  /**
   * @return The current working directory in absolute (Java) form
   */
  public static String getWorkingDirectory() {
    return new File("").getAbsolutePath();
  }

}
