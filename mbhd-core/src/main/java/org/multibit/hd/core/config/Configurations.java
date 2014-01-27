package org.multibit.hd.core.config;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.multibit.hd.core.exceptions.CoreException;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Utility to provide the following to configuration:</p>
 * <ul>
 * <li>Default configuration</li>
 * <li>Default configuration</li>
 * <li>Read/write configuration files</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Configurations {

  private static final Logger log = LoggerFactory.getLogger(Configurations.class);

  // Application
  public static final String APP_VERSION = "app.version";

  // Location of current wallet directory (may be empty)
  public static final String APP_CURRENT_WALLET_FILENAME = "app.current-wallet-filename";


  // Bitcoin
  public static final String BITCOIN_SYMBOL = "bitcoin.symbol";

  // Internationalisation (i18n)
  public static final String I18N_LOCALE = "i18n.locale";
  public static final String I18N_DECIMAL_SEPARATOR = "i18n.decimal-separator";
  public static final String I18N_GROUPING_SEPARATOR = "i18n.grouping-separator";
  public static final String I18N_IS_CURRENCY_PREFIXED = "i18n.is-prefixed";
  public static final String I18N_LOCAL_DECIMAL_PLACES = "i18n.local-decimal-places";
  public static final String I18N_LOCAL_CURRENCY_UNIT = "i18n.local-currency-unit";

  // Logging
  public static final String LOGGING = "logging";
  public static final String LOGGING_LEVEL = LOGGING + ".level";
  public static final String LOGGING_FILE = LOGGING + ".file";
  public static final String LOGGING_ARCHIVE = LOGGING + ".archive";
  public static final String LOGGING_PACKAGE_PREFIX = LOGGING + ".package.";


  /**
   * The current runtime configuration (preserved across soft restarts)
   */
  public static Configuration currentConfiguration;

  /**
   * The previous configuration (preserved across soft restarts)
   */
  public static Configuration previousConfiguration;

  /**
   * Utilities have private constructors
   */
  private Configurations() {
  }

  /**
   * @return A new default configuration based on the UK locale
   */
  public static Configuration newDefaultConfiguration() {

    Properties properties = new Properties();

    // Application
    properties.put(APP_VERSION, "0.0.1");

    // Bitcoin
    properties.put(BITCOIN_SYMBOL, "ICON");

    // Localisation
    properties.put(I18N_LOCALE, "en_gb");
    properties.put(I18N_DECIMAL_SEPARATOR, ".");
    properties.put(I18N_GROUPING_SEPARATOR, ",");
    properties.put(I18N_IS_CURRENCY_PREFIXED, "true");
    properties.put(I18N_LOCAL_DECIMAL_PLACES, "4");
    properties.put(I18N_LOCAL_CURRENCY_UNIT, "GBP");

    // Logging
    properties.put(LOGGING_LEVEL, "warn");
    properties.put(LOGGING_FILE, "log/multibit-hd.log");
    properties.put(LOGGING_ARCHIVE, "log/multibit-hd-%d.log.gz");
    properties.put(LOGGING_PACKAGE_PREFIX + "com.google.bitcoinj", "warn");
    properties.put(LOGGING_PACKAGE_PREFIX + "org.multibit", "debug");

    return new ConfigurationReadAdapter(properties).adapt();

  }

  /**
   * @return The persisted configuration
   */
  public static Configuration readConfiguration() {

    File configurationFile = InstallationManager.getConfigurationFile();

    Properties properties = readProperties(configurationFile);
    if (!properties.isEmpty()) {
      return new ConfigurationReadAdapter(properties).adapt();
    } else {
      log.warn("Configuration file is missing. Using defaults.");
      return newDefaultConfiguration();
    }

  }

  /**
   * <p>Loads a properties file from the given location</p>
   *
   * @param propertiesFile The location of the properties file
   *
   * @return The raw properties file used for storing the configuration, or empty if not found
   */
  /* package for testing */
  static Properties readProperties(File propertiesFile) {

    Preconditions.checkNotNull(propertiesFile, "'propertiesFile' must be present");

    // Create an empty set of properties
    Properties properties = new Properties();

    if (propertiesFile.exists()) {
      log.debug("Loading properties from '{}'", propertiesFile.getAbsolutePath());
      try (FileInputStream fis = new FileInputStream(propertiesFile)) {
        properties.load(fis);
        log.debug("Properties loaded");
      } catch (IOException e) {
        throw new CoreException(e);
      }
    }

    return properties;
  }

  /**
   * <p>Writes the current configuration to the application directory</p>
   */
  /* package for testing */
  static void writeCurrentConfiguration() {

    File configurationFile = InstallationManager.getConfigurationFile();

    // Read in the existing properties in case we are legacy running
    // in a more recent version's environment
    Properties existingProperties = readProperties(configurationFile);

    // Create a properties file representation
    Properties newProperties = new ConfigurationWriteAdapter(currentConfiguration).adapt();

    // Merge the new into the existing, preserving the existing
    mergeProperties(newProperties, existingProperties);

    // Write the properties
    writeProperties(configurationFile, newProperties);

  }

  /**
   * <p>Writes the given properties to the given file location, deleting any existing file.</p>
   *
   * @param propertiesFile The file for writing the properties
   * @param properties     The properties that will replace
   */
  /* package for testing */
  static void writeProperties(File propertiesFile, Properties properties) {

    Preconditions.checkNotNull(propertiesFile, "'propertiesFile' must be present");
    Preconditions.checkNotNull(properties, "'properties' must be present");

    // Remove the old properties file to make way for the new
    if (propertiesFile.exists()) {
      if (!propertiesFile.delete()) {
        throw new CoreException("Unable to delete '" + propertiesFile.getAbsolutePath() + "'");
      }
    }

    try (Writer writer = Files.newWriter(propertiesFile, Charsets.UTF_8)) {
      properties.store(writer, "MultiBit HD Information");
      writer.flush();
    } catch (IOException e) {
      throw new CoreException(e);
    }
  }

  /**
   * <p>Merge the subset into the existing superset. The subset will overwrite the superset.
   * Items in the superset and not in the subset will be preserved.</p>
   *
   * @param subset   The subset contains the items that will overwrite those in the superset
   * @param superset The superset contains any items not in the subset that will be preserved
   */
  /* package for testing */
  static void mergeProperties(Properties subset, Properties superset) {

    Preconditions.checkNotNull(subset, "'subset' must be present");
    Preconditions.checkNotNull(superset, "'superset' must be present");

    // Drive the merge from the subset
    for (Map.Entry<Object, Object> entry : subset.entrySet()) {

      String key = (String) entry.getKey();
      String value = (String) entry.getValue();

      superset.put(key, value);

    }
  }

}
