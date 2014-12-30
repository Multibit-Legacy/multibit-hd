package org.multibit.hd.core.managers;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;

/**
 * <p>Manager to provide the following to other core classes:</p>
 * <ul>
 * <li>Location of the installation directory</li>
 * <li>Access the configuration file</li>
 * <li>Utility methods eg copying checkpoint files from installation directory</li>
 * </ul>
 */
public class InstallationManager {

  private static final Logger log = LoggerFactory.getLogger(InstallationManager.class);

  /**
   * The main MultiBit download site (HTTPS)
   */
  public static final URI MBHD_WEBSITE_URI = URI.create("https://multibit.org");

  /**
   * The main MultiBit help site (HTTPS to allow secure connection without redirect, with fall back to local help on failure)
   */
  public static final String MBHD_WEBSITE_HELP_DOMAIN = "https://beta.multibit.org";  // TODO remove beta when release-4.0.0 website pushed to multibit.org
  public static final String MBHD_WEBSITE_HELP_BASE = MBHD_WEBSITE_HELP_DOMAIN + "/hd0.1";

  public static final String MBHD_APP_NAME = "MultiBitHD";
  public static final String MBHD_PREFIX = "mbhd";
  public static final String MBHD_CONFIGURATION_FILE = MBHD_PREFIX + ".yaml";

  public static final String SPV_BLOCKCHAIN_SUFFIX = ".spvchain";
  public static final String CHECKPOINTS_SUFFIX = ".checkpoints";
  public static final String CA_CERTS_NAME = MBHD_PREFIX + "-cacerts";

  /**
   * The current application data directory
   */
  public static File currentApplicationDataDirectory = null;

  /**
   * A test flag to allow FEST tests to run efficiently
   */
  public static boolean unrestricted = false;

  /**
   * <p>Handle any shutdown code</p>
   */
  public static void shutdownNow() {

    // Do nothing

    // Reset of the unrestricted field causes problems during FEST tests

    // Reset of the current application directory causes problems during
    // switch and is not required in normal operation

  }

  /**
   * @return A reference to where the configuration file should be located
   */
  public static File getConfigurationFile() {

    return new File(getOrCreateApplicationDataDirectory().getAbsolutePath() + File.separator + MBHD_CONFIGURATION_FILE);

  }

  /**
   * <p>Get the directory for the user's application data, creating if not present</p>
   * <p>Checks a few OS-dependent locations first</p>
   * <p>For tests (unrestricted mode) this will create a long-lived temporary directory - use reset() to clear in the tearDown() phase</p>
   *
   * @return A suitable application directory for the OS and if running unit tests (unrestricted mode)
   */
  public static File getOrCreateApplicationDataDirectory() {

    if (currentApplicationDataDirectory != null) {
      return currentApplicationDataDirectory;
    }

    if (unrestricted) {
      try {
        log.debug("Unrestricted mode requires a temporary application directory");
        // In order to preserve the same behaviour between the test and production environments
        // this must be maintained throughout the lifetime of a unit test
        // At tearDown() use reset() to clear
        currentApplicationDataDirectory = SecureFiles.createTemporaryDirectory();
        return currentApplicationDataDirectory;
      } catch (IOException e) {
        log.error("Failed to create temporary directory", e);
        return null;
      }
    } else {

      // Fail safe check for unit tests to avoid overwriting existing configuration file
      try {
        Class.forName("org.multibit.hd.core.managers.InstallationManagerTest");
        throw new IllegalStateException("Cannot run without unrestricted when unit tests are present. You could overwrite live configuration.");
      } catch (ClassNotFoundException e) {
        // We have passed the fail safe check
      }

    }

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
      // Keep a clean home directory by prefixing with "."
      applicationDataDirectoryName = System.getProperty("user.home") + "/." + MBHD_APP_NAME;
    }

    log.debug("Application data directory is\n'{}'", applicationDataDirectoryName);

    // Create the application data directory if it does not exist
    File applicationDataDirectory = new File(applicationDataDirectoryName);
    SecureFiles.verifyOrCreateDirectory(applicationDataDirectory);

    // Must be OK to be here so set this as the current
    currentApplicationDataDirectory = applicationDataDirectory;

    return applicationDataDirectory;
  }

  /**
   * Copy the checkpoints file from the MultiBitHD installation to the specified filename
   *
   * @param destinationCheckpointsFile The sink to receive the source checkpoints file
   */
  public static void copyCheckpointsTo(File destinationCheckpointsFile) throws IOException {

    Preconditions.checkNotNull(destinationCheckpointsFile, "'checkpointsFile' must be present");

    // TODO overwrite if larger/ newer
    if (!destinationCheckpointsFile.exists() || destinationCheckpointsFile.length() == 0) {

      log.debug("Copying checkpoints to '{}'", destinationCheckpointsFile);

      // Work out the source checkpoints (put into the program installation directory by the installer)
      File currentWorkingDirectory = new File(".");
      File sourceBlockCheckpointsFile = new File(currentWorkingDirectory.getAbsolutePath() + File.separator + MBHD_PREFIX + CHECKPOINTS_SUFFIX);

      // Prepare an input stream to the checkpoints
      final InputStream sourceCheckpointsStream;
      if (sourceBlockCheckpointsFile.exists()) {
        // Use the file system
        log.debug("Using source checkpoints from working directory.");
        sourceCheckpointsStream = new FileInputStream(sourceBlockCheckpointsFile);
      } else {
        // Use the classpath
        log.debug("Using source checkpoints from classpath.");
        sourceCheckpointsStream = InstallationManager.class.getResourceAsStream("/mbhd.checkpoints");
      }

      // Create the output stream
      FileOutputStream sinkCheckpointsStream = new FileOutputStream(destinationCheckpointsFile);

      // Copy the checkpoints
      long bytes = ByteStreams.copy(sourceCheckpointsStream, sinkCheckpointsStream);

      // Clean up
      sourceCheckpointsStream.close();
      sinkCheckpointsStream.flush();
      sinkCheckpointsStream.close();

      log.debug("New checkpoints are {} bytes in length.", bytes);

      if (bytes < 13_000) {
        log.warn("Checkpoints are short.");
      }

    } else {

      log.debug("Checkpoints already exist.");

    }
  }

  /**
   * Use for testing only (several different test packages use this)
   *
   * @param currentApplicationDataDirectory the application data directory to use
   */
  public static void setCurrentApplicationDataDirectory(File currentApplicationDataDirectory) {
    InstallationManager.currentApplicationDataDirectory = currentApplicationDataDirectory;
  }

  /**
   * Do the following, but with reflection to bypass access checks:
   *
   * JceSecurity.isRestricted = false;
   * JceSecurity.defaultPolicy.perms.clear();
   * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
   */
  public static void removeCryptographyRestrictions() {

    if (!isRestrictedCryptography()) {
      log.debug("Cryptography restrictions removal not needed");
      return;
    }

    try {
      final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
      final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
      final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

      final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
      isRestrictedField.setAccessible(true);
      isRestrictedField.set(null, false);

      final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
      defaultPolicyField.setAccessible(true);
      final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

      final Field perms = cryptoPermissions.getDeclaredField("perms");
      perms.setAccessible(true);
      ((Map<?, ?>) perms.get(defaultPolicy)).clear();

      final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
      instance.setAccessible(true);
      defaultPolicy.add((Permission) instance.get(null));

      log.debug("Successfully removed cryptography restrictions");
    } catch (final Exception e) {
      log.warn("Failed to remove cryptography restrictions", e);
    }

  }

  private static boolean isRestrictedCryptography() {

    // This simply matches the Oracle JRE, but not OpenJDK
    return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
  }

}
