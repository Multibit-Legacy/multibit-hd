package org.multibit.hd.core.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.multibit.hd.core.exceptions.CoreException;
import org.multibit.hd.core.utils.MultiBitFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
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

  // Bitcoin
  public static final String BITCOIN_SYMBOL = "bitcoin.symbol";

  // Internationalisation (i18n)
  public static final String I18N_LOCALE = "i18n.locale";
  public static final String I18N_DECIMAL_SEPARATOR = "i18n.decimal-separator";
  public static final String I18N_GROUPING_SEPARATOR = "i18n.grouping-separator";
  public static final String I18N_IS_CURRENCY_PREFIXED = "i18n.is-prefixed";

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
   * @return A new default configuration based on the default locale
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

    // Logging
    properties.put(LOGGING_LEVEL, "warn");
    properties.put(LOGGING_FILE, "log/multibit-hd.log");
    properties.put(LOGGING_ARCHIVE, "log/multibit-hd-%d.log.gz");
    properties.put(LOGGING_PACKAGE_PREFIX + "com.google.bitcoinj", "warn");
    properties.put(LOGGING_PACKAGE_PREFIX + "org.multibit", "debug");

    return new ConfigurationReadAdapter(properties).adapt();

  }

  /**
   * @return A new default configuration based on the default locale
   */
  public static Configuration readConfiguration() {

    // Read the external configuration
    final Configuration configuration;

    Properties properties = new Properties();

    File configurationFile = MultiBitFiles.getConfigurationFile();
    if (configurationFile.exists()) {
      try (FileInputStream fis = new FileInputStream(configurationFile)) {
        properties.load(fis);
      } catch (IOException e) {
        throw new CoreException(e);
      }
    } else {
      log.warn("Configuration file is missing. Using defaults.");
      return newDefaultConfiguration();
    }

    return new ConfigurationReadAdapter(properties).adapt();

  }

  /**
   * <p>Writes the current configuration to the application directory</p>
   */
  public static void writeCurrentConfiguration() {

    // Get the existing configuration file and delete it if necessary
    File configurationFile = MultiBitFiles.getConfigurationFile();
    if (configurationFile.exists()) {
      if (!configurationFile.delete()) {
        throw new CoreException("Unable to delete configuration file");
      }
    }

    // Create a properties file representation
    Properties properties = new ConfigurationWriteAdapter(currentConfiguration).adapt();

    // Uses Java7 try-with-resources syntax
    try (Writer writer = Files.newWriter(configurationFile, Charsets.UTF_8)) {
      properties.store(writer, "MultiBit HD " + currentConfiguration.getPropertiesVersion());
      writer.flush();
    } catch (IOException e) {
      throw new CoreException(e);
    }

  }

}
