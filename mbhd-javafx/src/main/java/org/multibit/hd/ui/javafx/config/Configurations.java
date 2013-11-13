package org.multibit.hd.ui.javafx.config;

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

  // Logging
  public static final String LOGGING = "logging";
  public static final String LOGGING_LEVEL = LOGGING + ".level";
  public static final String LOGGING_FILE = LOGGING + ".file";
  public static final String LOGGING_ARCHIVE = LOGGING + ".archive";
  public static final String LOGGING_PACKAGE_PREFIX = LOGGING + ".package.";

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

    // Logging
    properties.put(LOGGING_LEVEL, "warn");
    properties.put(LOGGING_FILE, "log/multibit-hd.log");
    properties.put(LOGGING_ARCHIVE, "log/multibit-hd-%d.log.gz");
    properties.put(LOGGING_PACKAGE_PREFIX + "com.google.bitcoinj", "warn");
    properties.put(LOGGING_PACKAGE_PREFIX + "org.multibit", "debug");

    return new ConfigurationAdapter(properties).adapt();

  }

}
