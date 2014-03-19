package org.multibit.hd.core.config;

import org.junit.Test;

import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class ConfigurationReadAdapterTest {

  @Test
  public void testAdapt() throws Exception {

    // Arrange
    Configuration configuration = Configurations.newDefaultConfiguration();
    ConfigurationWriteAdapter adapter = new ConfigurationWriteAdapter(configuration);
    Properties properties = adapter.adapt();

    // Act
    ConfigurationReadAdapter testObject = new ConfigurationReadAdapter(properties);
    Configuration actualConfiguration = testObject.adapt();

    BitcoinConfiguration actualBitcoin = actualConfiguration.getBitcoinConfiguration();
    ApplicationConfiguration actualApplication = actualConfiguration.getApplicationConfiguration();
    LanguageConfiguration actualLanguage = actualConfiguration.getLanguageConfiguration();
    LoggingConfiguration actualLogging = actualConfiguration.getLoggingConfiguration();
    SoundConfiguration actualSound = actualConfiguration.getSoundConfiguration();

    // Assert
    for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
      switch (configurationKey) {
        case APP_VERSION:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getVersion());
          break;
        case APP_CURRENT_WALLET_FILENAME:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getCurrentWalletRoot());
          break;
        case APP_CURRENT_THEME:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getCurrentTheme());
          break;
        case APP_CURRENT_APP_DIRECTORY:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getApplicationDirectory());
          break;
        case APP_BITCOIN_URI_HANDLING:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getBitcoinUriHandling());
          break;
        case APP_RESTORE_LAYOUT:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(String.valueOf(actualApplication.isRestoreApplicationLayoutOnStartup()));
          break;
        case APP_CURRENT_SCREEN:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualApplication.getCurrentScreen());
          break;
        case SOUND_ALERT:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(String.valueOf(actualSound.isAlertSound()));
          break;
        case SOUND_RECEIVE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(String.valueOf(actualSound.isReceiveSound()));
          break;
        case BITCOIN_SYMBOL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getBitcoinSymbol());
          break;
        case BITCOIN_DECIMAL_SEPARATOR:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getDecimalSeparator());
          break;
        case BITCOIN_GROUPING_SEPARATOR:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getGroupingSeparator());
          break;
        case BITCOIN_IS_CURRENCY_LEADING:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(String.valueOf(actualBitcoin.isCurrencySymbolLeading()));
          break;
        case BITCOIN_LOCAL_DECIMAL_PLACES:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(String.valueOf(actualBitcoin.getLocalDecimalPlaces()));
          break;
        case BITCOIN_LOCAL_CURRENCY_CODE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getLocalCurrencyUnit().getCurrencyCode());
          break;
        case BITCOIN_LOCAL_CURRENCY_SYMBOL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getLocalCurrencySymbol());
          break;
        case BITCOIN_CURRENT_EXCHANGE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getCurrentExchange());
          break;
        case BITCOIN_EXCHANGE_PUBLIC_KEYS:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualBitcoin.getExchangeApiKeys().or(""));
          break;
        case LANGUAGE_LOCALE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualLanguage.getLocale().toString());
          break;
        case LOGGING_PACKAGE_PREFIX:
          // Do nothing - this is a prefix
          break;
        case LOGGING_ARCHIVE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualLogging.getFileConfiguration().getArchivedLogFilenamePattern());
          break;
        case LOGGING_FILE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualLogging.getFileConfiguration().getCurrentLogFilename());
          break;
        case LOGGING_LEVEL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(actualLogging.getLevel().toString());
          break;
        default:
          fail("Missing configuration key: '" + configurationKey.name() + "'");
      }
    }

  }
}
