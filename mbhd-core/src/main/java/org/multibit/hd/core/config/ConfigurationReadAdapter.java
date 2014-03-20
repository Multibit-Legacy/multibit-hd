package org.multibit.hd.core.config;

import ch.qos.logback.classic.Level;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.joda.money.CurrencyUnit;

import java.util.Map;
import java.util.Properties;

/**
 * <p>Adapter to provide the following to application:</p>
 * <ul>
 * <li>Creates a Configuration from the given Properties performing validation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ConfigurationReadAdapter {

  private final Properties properties;
  private final Configuration configuration = new Configuration();

  public ConfigurationReadAdapter(Properties properties) {
    this.properties = properties;
  }

  /**
   * @return A new Configuration based on the properties
   */
  public Configuration adapt() {

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {

      String key = (String) entry.getKey();
      String value = (String) entry.getValue();

      Preconditions.checkNotNull(key, "'key' must be present");
      Preconditions.checkNotNull(value, "'value' must be present");

      Optional<ConfigurationKey> configurationKey = ConfigurationKey.fromKey(key);

      if (configurationKey.isPresent()) {

        switch (configurationKey.get()) {
          case APP_VERSION:
            configuration.getApplicationConfiguration().setVersion(value);
            break;
          case APP_CURRENT_WALLET_FILENAME:
            configuration.getApplicationConfiguration().setCurrentWalletRoot(value);
            break;
          case APP_CURRENT_THEME:
            configuration.getApplicationConfiguration().setCurrentTheme(value);
            break;
          case APP_CURRENT_APP_DIRECTORY:
            configuration.getApplicationConfiguration().setApplicationDirectory(value);
            break;
          case APP_BITCOIN_URI_HANDLING:
            configuration.getApplicationConfiguration().setBitcoinUriHandling(value);
            break;
          case APP_RESTORE_LAYOUT:
            configuration.getApplicationConfiguration().setRestoreApplicationLayoutOnStartup(Boolean.valueOf(value));
            break;
          case APP_CURRENT_SCREEN:
            configuration.getApplicationConfiguration().setCurrentScreen(value);
            break;
          case BITCOIN_SYMBOL:
            configuration.getBitcoinConfiguration().setBitcoinSymbol(value);
            break;
          case BITCOIN_DECIMAL_SEPARATOR:
            configuration.getBitcoinConfiguration().setDecimalSeparator(value.substring(0, 1));
            break;
          case BITCOIN_GROUPING_SEPARATOR:
            configuration.getBitcoinConfiguration().setGroupingSeparator(value.substring(0, 1));
            break;
          case BITCOIN_IS_CURRENCY_LEADING:
            configuration.getBitcoinConfiguration().setCurrencySymbolLeading(Boolean.valueOf(value));
            break;
          case BITCOIN_LOCAL_DECIMAL_PLACES:
            configuration.getBitcoinConfiguration().setLocalDecimalPlaces(Integer.valueOf(value));
            break;
          case BITCOIN_LOCAL_CURRENCY_CODE:
            configuration.getBitcoinConfiguration().setLocalCurrencyUnit(CurrencyUnit.of(value));
            break;
          case BITCOIN_LOCAL_CURRENCY_SYMBOL:
            configuration.getBitcoinConfiguration().setLocalCurrencySymbol(value);
            break;
          case BITCOIN_CURRENT_EXCHANGE:
            configuration.getBitcoinConfiguration().setCurrentExchange(value);
            break;
          case BITCOIN_EXCHANGE_PUBLIC_KEYS:
            configuration.getBitcoinConfiguration().setExchangeApiKeys(value);
            break;
          case SOUND_ALERT:
            configuration.getSoundConfiguration().setAlertSound(Boolean.valueOf(value));
            break;
          case SOUND_RECEIVE:
            configuration.getSoundConfiguration().setReceiveSound(Boolean.valueOf(value));
            break;
          case LANGUAGE_LOCALE:
            configuration.getLanguageConfiguration().setLocale(value);
            break;
          case LOGGING_LEVEL:
            configuration.getLoggingConfiguration().setLevel(Level.valueOf(value));
            break;
          case LOGGING_FILE:
            configuration.getLoggingConfiguration().getFileConfiguration().setCurrentLogFilename(value);
            break;
          case LOGGING_ARCHIVE:
            configuration.getLoggingConfiguration().getFileConfiguration().setArchivedLogFilenamePattern(value);
            break;
          default:
            // Fail silently to allow the next item in the chain to complete
        }

      }

      // Logging (special case)
      if (key.startsWith("logging")) {

        // Loose matches first
        if (key.startsWith(ConfigurationKey.LOGGING_PACKAGE_PREFIX.getKey())) {
          String packageName = key.substring(ConfigurationKey.LOGGING_PACKAGE_PREFIX.getKey().length());
          configuration.getLoggingConfiguration().getLoggers().put(packageName, Level.valueOf(value));
        }

      }

    }

    return configuration;
  }

}
