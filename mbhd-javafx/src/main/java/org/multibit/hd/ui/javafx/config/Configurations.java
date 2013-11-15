package org.multibit.hd.ui.javafx.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.multibit.hd.ui.javafx.exceptions.UIException;
import org.multibit.hd.ui.javafx.utils.MultiBitFiles;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class Configurations {

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

    Properties properties = new Properties();

    // Application
    properties.put(APP_VERSION, "0.0.1");

    // Bitcoin
    properties.put(BITCOIN_SYMBOL, "ICON");

    // Localisation
    properties.put(I18N_LOCALE, "en_gb");
    properties.put(I18N_DECIMAL_SEPARATOR, ".");
    properties.put(I18N_GROUPING_SEPARATOR, ",");

    // Logging
    properties.put(LOGGING_LEVEL, "warn");
    properties.put(LOGGING_FILE, "log/multibit-hd.log");
    properties.put(LOGGING_ARCHIVE, "log/multibit-hd-%d.log.gz");
    properties.put(LOGGING_PACKAGE_PREFIX + "com.google.bitcoinj", "warn");
    properties.put(LOGGING_PACKAGE_PREFIX + "org.multibit", "debug");

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
        throw new UIException("Unable to delete configuration file");
      }
    }

    // Create a properties file representation
    Properties properties = new ConfigurationWriteAdapter(currentConfiguration).adapt();

    // Uses Java7 try-with-resources syntax
    try (Writer writer = Files.newWriter(configurationFile, Charsets.UTF_8)) {
      properties.store(writer, "MultiBit HD " + currentConfiguration.getPropertiesVersion());
    } catch (IOException e) {
      throw new UIException(e);
    }

  }

}
