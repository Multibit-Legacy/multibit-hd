package org.multibit.hd.core.config;

import ch.qos.logback.classic.Level;

import java.util.Map;
import java.util.Properties;

import static org.multibit.hd.core.config.Configurations.*;

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

    // Language
    adaptLanguageN();

    // Logging
    adaptLogging();

    return properties;
  }

  private void adaptBitcoin() {

    BitcoinConfiguration bitcoin = configuration.getBitcoinConfiguration();

    properties.put(BITCOIN_SYMBOL, bitcoin.getBitcoinSymbol());
    properties.put(BITCOIN_DECIMAL_SEPARATOR, bitcoin.getDecimalSeparator());
    properties.put(BITCOIN_GROUPING_SEPARATOR, bitcoin.getGroupingSeparator());
    properties.put(BITCOIN_IS_CURRENCY_PREFIXED, String.valueOf(bitcoin.isCurrencySymbolLeading()));

  }

  private void adaptLanguageN() {

    LanguageConfiguration language = configuration.getLanguageConfiguration();

    properties.put(LANGUAGE_LOCALE, language.getLocale().toString());
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
