package org.multibit.hd.ui.javafx.utils;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.javafx.exceptions.UIException;
import org.multibit.hd.ui.javafx.platform.builder.OSUtils;
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

  public static final String MBHD_APP_NAME = "MultiBitHD";
  public static final String MBHD_PREFIX = "multibit-hd";

  private static final Logger log = LoggerFactory.getLogger(MultiBitFiles.class);

  public static final String MBHD_CONFIGURATION_FILE = MBHD_PREFIX + ".properties";
  public static final String MBHD_LOG_FILE = "log/"+MBHD_PREFIX + ".log";
  public static final String MBHD_ARCHIVE_FILE = "log/"+MBHD_PREFIX + "-%d.log.gz";

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
  public static boolean isFileLocked(String message, File file) {

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
   * @return A reference to the configuration file
   */
  public static File getConfigurationFile() {

    return new File(createApplicationDataDirectory() + "/" + MBHD_CONFIGURATION_FILE);

  }

  /**
   * <p>Get the directory for the user's application data, creating if not present</p>
   * <p>Checks a few OS-dependent locations first</p>
   */
  public static String createApplicationDataDirectory() {

    // Check the current working directory for the configuration file
    File multibitPropertiesFile = new File(MBHD_CONFIGURATION_FILE);
    if (multibitPropertiesFile.exists()) {
      return ".";
    }

    final String applicationDataDirectory;

    // Locations are OS-dependent
    if (OSUtils.isWindows()) {

      // Windows
      applicationDataDirectory = System.getenv("APPDATA") + File.separator + MBHD_APP_NAME;

    } else if (OSUtils.isMac()) {

      // OSX
      if ((new File("../../../../" + MBHD_CONFIGURATION_FILE)).exists()) {
        applicationDataDirectory = new File("../../../..").getAbsolutePath();
      } else {
        applicationDataDirectory = System.getProperty("user.home") + "/Library/Application Support/" + MBHD_APP_NAME;
      }
    } else {

      // Other (probably a Unix variant)
      applicationDataDirectory = System.getProperty("user.home") + "/" + MBHD_APP_NAME;
    }

    log.debug("Application data directory is '{}'", applicationDataDirectory);

    // Create the application data directory if it does not exist
    File directory = new File(applicationDataDirectory);
    if (!directory.exists()) {
      Preconditions.checkState(directory.mkdir(), "Could not create the application data directory of '" + applicationDataDirectory + "'");
    }
    Preconditions.checkState(directory.isDirectory(), "Incorrectly identified the application data directory as a file");

    return applicationDataDirectory;
  }

  /**
   * @return The current working directory in canonical form (OS-dependent) usually for display to the user
   */
  public static String getCanonicalWorkingDirectory() {
    try {
      return new File("").getCanonicalPath();
    } catch (IOException e) {
      throw new UIException(e);
    }
  }

  /**
   * @return The current working directory in absolute (Java) form
   */
  public static String getWorkingDirectory() {
    return new File("").getAbsolutePath();
  }

}
