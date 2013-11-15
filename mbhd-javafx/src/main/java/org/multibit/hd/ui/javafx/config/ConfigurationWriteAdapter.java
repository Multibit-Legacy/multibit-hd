package org.multibit.hd.ui.javafx.config;

import ch.qos.logback.classic.Level;

import java.util.Map;
import java.util.Properties;

import static org.multibit.hd.ui.javafx.config.Configurations.*;

/**
 * <p>Adapter to provide the following to application:</p>
 * <ul>
 * <li>Creates a Properties from the given Configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ConfigurationWriteAdapter {

  private final Properties properties = new Properties();
  private final Configuration configuration;

  public ConfigurationWriteAdapter(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * @return A new Properties based on the configuration
   */
  public Properties adapt() {

    // Application

    // Bitcoin
    adaptBitcoin();

    // Internationalisation
    adaptI18N();

    // Logging
    adaptLogging();

    return properties;
  }

  private void adaptBitcoin() {

    BitcoinConfiguration bitcoin = configuration.getBitcoinConfiguration();

    properties.put(BITCOIN_SYMBOL, bitcoin.getBitcoinSymbol());

  }

  private void adaptI18N() {

    I18NConfiguration i18n = configuration.getI18NConfiguration();

    properties.put(I18N_LOCALE, i18n.getLocale().toString());
    properties.put(I18N_DECIMAL_SEPARATOR, i18n.getDecimalSeparator().orNull());
    properties.put(I18N_GROUPING_SEPARATOR, i18n.getGroupingSeparator().orNull());
    properties.put(I18N_IS_CURRENCY_PREFIXED, String.valueOf(i18n.isCurrencySymbolPrefixed()));
  }

  private void adaptLogging() {

    LoggingConfiguration logging = configuration.getLoggingConfiguration();

    properties.put(LOGGING_LEVEL, logging.getLevel().toString());
    properties.put(LOGGING_FILE, logging.getFileConfiguration().getCurrentLogFilename());
    properties.put(LOGGING_ARCHIVE, logging.getFileConfiguration().getArchivedLogFilenamePattern());

    for (Map.Entry<String, Level> entry : logging.getLoggers().entrySet()) {

      String key = LOGGING_PACKAGE_PREFIX + entry.getKey();
      properties.put(key, entry.getValue().toString());

    }
  }
}
