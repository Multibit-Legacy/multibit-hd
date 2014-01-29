package org.multibit.hd.core.managers;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *  <p>Manager to provide the following to other core classes:<br>
 *  <ul>
 *  <li>Location of the installation directory</li>
 * <li>Utility methods eg copying checkpoint files from installation directory</li>
 *  </ul>
 *  </p>
 *  
 */
public class InstallationManager {
  public static final String MBHD_APP_NAME = "MultiBitHD";
  public static final String MBHD_PREFIX = "mbhd";
  public static final String MBHD_CONFIGURATION_FILE = MBHD_PREFIX + ".properties";
  public static final String SPV_BLOCKCHAIN_SUFFIX = ".spvchain";
  public static final String CHECKPOINTS_SUFFIX = ".checkpoints";
  private static final Logger log = LoggerFactory.getLogger(InstallationManager.class);

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
  public static File createApplicationDataDirectory() {

    // Check the current working directory for the configuration file
    File multibitPropertiesFile = new File(MBHD_CONFIGURATION_FILE);
    if (multibitPropertiesFile.exists()) {
      return new File(".");
    }

    final String applicationDataDirectoryName;

    // Locations are OS-dependent
    if (OSUtils.isWindows()) {

      // Windows
      applicationDataDirectoryName = System.getenv("APPDATA") + File.separator + MBHD_APP_NAME;

    } else if (OSUtils.isMac()) {

      // OSX
      if ((new File("../../../../" + MBHD_CONFIGURATION_FILE)).exists()) {
        applicationDataDirectoryName = new File("../../../..").getAbsolutePath();
      } else {
        applicationDataDirectoryName = System.getProperty("user.home") + "/Library/Application Support/" + MBHD_APP_NAME;
      }
    } else {

      // Other (probably a Unix variant)
      applicationDataDirectoryName = System.getProperty("user.home") + "/" + MBHD_APP_NAME;
    }

    log.debug("Application data directory is '{}'", applicationDataDirectoryName);

    // Create the application data directory if it does not exist
    File applicationDataDirectory = new File(applicationDataDirectoryName);
    createDirectoryIfNecessary(applicationDataDirectory);

    return applicationDataDirectory;
  }

  public static void createDirectoryIfNecessary(File directoryToCreate) {
    if (!directoryToCreate.exists()) {
       Preconditions.checkState(directoryToCreate.mkdir(), "Could not create the directory of '" + directoryToCreate + "'");
     }
     Preconditions.checkState(directoryToCreate.isDirectory(), "Incorrectly identified the directory of '" + directoryToCreate + " as a file");
  }

  /**
   * Copy the checkpoints file from the MultiBitHD installation to the specified filename
   *
   * @param destinationCheckpointsFilename The file location to which to copy the checkpoints file
   */
  public static void copyCheckpointsTo(String destinationCheckpointsFilename) throws IOException {
    Preconditions.checkNotNull(destinationCheckpointsFilename);

    // See if the checkpoints in the user's application data directory exists
    File destinationCheckpoints = new File(destinationCheckpointsFilename);

    // TODO overwrite if larger/ newer.
    if (!destinationCheckpoints.exists()) {
      // Work out the source checkpoints (put into the program installation directory by the installer).
      File directory = new File(".");
      String currentWorkingDirectory = directory.getCanonicalPath();

      String checkpointsFilename = MBHD_PREFIX + CHECKPOINTS_SUFFIX;
      String sourceCheckpointsFilename = currentWorkingDirectory + File.separator + checkpointsFilename;
      File sourceBlockcheckpoints = new File(sourceCheckpointsFilename);

      // If this file does not exist then see if it is where the development environment stores it (convenience when running in Intellij
      if (!sourceBlockcheckpoints.exists()) {
        sourceCheckpointsFilename = currentWorkingDirectory + File.separator + "mbhd-core" + File.separator + "src" +
                File.separator + "main" + File.separator + "resources" + File.separator + MBHD_PREFIX + CHECKPOINTS_SUFFIX;
        sourceBlockcheckpoints = new File(sourceCheckpointsFilename);
      }
      if (sourceBlockcheckpoints.exists() && !destinationCheckpointsFilename.equals(sourceCheckpointsFilename)) {
        // It should exist since installer puts them in.
        log.info("Copying checkpoints from '" + sourceCheckpointsFilename + "' to '" + destinationCheckpointsFilename + "'");
        copyFile(sourceBlockcheckpoints, destinationCheckpoints);

        // Check all the data was copied.
        long sourceLength = sourceBlockcheckpoints.length();
        long destinationLength = destinationCheckpoints.length();
        if (sourceLength != destinationLength) {
          String errorText = "Checkpoints were not copied to user's application data directory correctly.\nThe source checkpoints '"
                  + sourceCheckpointsFilename
                  + "' is of length "
                  + sourceLength
                  + "\nbut the destination checkpoints '"
                  + destinationCheckpointsFilename + "' is of length " + destinationLength;
          log.error(errorText);
          throw new IllegalStateException(errorText);
        }
      }
    }
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
      long transfered = 0;
      long bytes = source.size();
      while (transfered < bytes) {
        transfered += destination.transferFrom(source, 0, source.size());
        destination.position(transfered);
      }
    } finally {
      if (source != null) {
        source.close();
        source = null;
      } else if (fileInputStream != null) {
        fileInputStream.close();
        fileInputStream = null;
      }
      if (destination != null) {
        destination.close();
        destination = null;
      } else if (fileOutputStream != null) {
        fileOutputStream.flush();
        fileOutputStream.close();
      }
    }
  }
}
