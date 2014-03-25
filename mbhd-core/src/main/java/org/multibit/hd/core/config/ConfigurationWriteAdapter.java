package org.multibit.hd.core.config;

import ch.qos.logback.classic.Level;

import java.util.Map;
import java.util.Properties;

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

    ApplicationConfiguration application = configuration.getApplicationConfiguration();
    BitcoinConfiguration bitcoin = configuration.getBitcoinConfiguration();
    LanguageConfiguration language = configuration.getLanguageConfiguration();
    SoundConfiguration sound = configuration.getSoundConfiguration();
    LoggingConfiguration logging = configuration.getLoggingConfiguration();

    // Having a giant switch avoids the potential for a missing supported key
    for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
      switch (configurationKey) {
        case APP_VERSION:
          properties.put(configurationKey.getKey(), application.getVersion());
          break;
        case APP_CURRENT_WALLET_FILENAME:
          properties.put(configurationKey.getKey(), application.getCurrentWalletRoot());
          break;
        case APP_CURRENT_THEME:
          properties.put(configurationKey.getKey(), application.getCurrentTheme());
          break;
        case APP_CURRENT_APP_DIRECTORY:
          properties.put(configurationKey.getKey(), application.getApplicationDirectory());
          break;
        case APP_BITCOIN_URI_HANDLING:
          properties.put(configurationKey.getKey(), application.getBitcoinUriHandling());
          break;
        case APP_RESTORE_LAYOUT:
          properties.put(configurationKey.getKey(), String.valueOf(application.isRestoreApplicationLayoutOnStartup()));
          break;
        case APP_FRAME_BOUNDS:
          properties.put(configurationKey.getKey(), application.getLastFrameBounds());
          break;
        case APP_SIDEBAR_WIDTH:
          properties.put(configurationKey.getKey(), application.getSidebarWidth());
          break;
        case APP_CURRENT_SCREEN:
          properties.put(configurationKey.getKey(), application.getCurrentScreen());
          break;
        case SOUND_ALERT:
          properties.put(configurationKey.getKey(), String.valueOf(sound.isAlertSound()));
          break;
        case SOUND_RECEIVE:
          properties.put(configurationKey.getKey(), String.valueOf(sound.isReceiveSound()));
          break;
        case BITCOIN_SYMBOL:
          properties.put(configurationKey.getKey(), bitcoin.getBitcoinSymbol());
          break;
        case BITCOIN_DECIMAL_SEPARATOR:
          properties.put(configurationKey.getKey(), bitcoin.getDecimalSeparator());
          break;
        case BITCOIN_GROUPING_SEPARATOR:
          properties.put(configurationKey.getKey(), bitcoin.getGroupingSeparator());
          break;
        case BITCOIN_IS_CURRENCY_LEADING:
          properties.put(configurationKey.getKey(), String.valueOf(bitcoin.isCurrencySymbolLeading()));
          break;
        case BITCOIN_LOCAL_DECIMAL_PLACES:
          properties.put(configurationKey.getKey(), String.valueOf(bitcoin.getLocalDecimalPlaces()));
          break;
        case BITCOIN_LOCAL_CURRENCY_CODE:
          properties.put(configurationKey.getKey(), bitcoin.getLocalCurrencyUnit().getCurrencyCode());
          break;
        case BITCOIN_LOCAL_CURRENCY_SYMBOL:
          properties.put(configurationKey.getKey(), bitcoin.getLocalCurrencySymbol());
          break;
        case BITCOIN_CURRENT_EXCHANGE:
          properties.put(configurationKey.getKey(), bitcoin.getCurrentExchange());
          break;
        case BITCOIN_EXCHANGE_PUBLIC_KEYS:
          properties.put(configurationKey.getKey(), bitcoin.getExchangeApiKeys().or(""));
          break;
        case LANGUAGE_LOCALE:
          properties.put(configurationKey.getKey(), language.getLocale().toString());
          break;
        case LOGGING_PACKAGE_PREFIX:
          // Do nothing - this is a prefix
          break;
        case LOGGING_ARCHIVE:
          properties.put(configurationKey.getKey(), logging.getFileConfiguration().getArchivedLogFilenamePattern());
          break;
        case LOGGING_FILE:
          properties.put(configurationKey.getKey(), logging.getFileConfiguration().getCurrentLogFilename());
          break;
        case LOGGING_LEVEL:
          properties.put(configurationKey.getKey(), logging.getLevel().toString());
          break;
        default:
          throw new IllegalStateException("Missing configuration key: '" + configurationKey.name() + "'");
      }
    }

    for (Map.Entry<String, Level> entry : logging.getLoggers().entrySet()) {

      String key = ConfigurationKey.LOGGING_PACKAGE_PREFIX.getKey() + entry.getKey();
      properties.put(key, entry.getValue().toString());

    }

    return properties;
  }

}
