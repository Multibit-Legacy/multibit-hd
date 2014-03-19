package org.multibit.hd.core.config;

import org.junit.Test;

import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class ConfigurationWriteAdapterTest {

  @Test
  public void testAdapt() throws Exception {

    // Arrange
    Configuration configuration = Configurations.newDefaultConfiguration();

    // Act
    ConfigurationWriteAdapter testObject = new ConfigurationWriteAdapter(configuration);
    Properties properties = testObject.adapt();

    // Assert
    for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
      switch (configurationKey) {
        case APP_VERSION:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("0.0.1");
          break;
        case APP_CURRENT_WALLET_FILENAME:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("");
          break;
        case APP_CURRENT_THEME:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("LIGHT");
          break;
        case APP_CURRENT_APP_DIRECTORY:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(".");
          break;
        case APP_BITCOIN_URI_HANDLING:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("FILL");
          break;
        case APP_RESTORE_LAYOUT:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("false");
          break;
        case APP_CURRENT_SCREEN:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("HELP");
          break;
        case SOUND_ALERT:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("true");
          break;
        case SOUND_RECEIVE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("true");
          break;
        case BITCOIN_SYMBOL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("MICON");
          break;
        case BITCOIN_DECIMAL_SEPARATOR:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(".");
          break;
        case BITCOIN_GROUPING_SEPARATOR:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo(",");
          break;
        case BITCOIN_IS_CURRENCY_LEADING:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("true");
          break;
        case BITCOIN_LOCAL_DECIMAL_PLACES:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("2");
          break;
        case BITCOIN_LOCAL_CURRENCY_CODE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("USD");
          break;
        case BITCOIN_LOCAL_CURRENCY_SYMBOL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("$");
          break;
        case BITCOIN_CURRENT_EXCHANGE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("BITSTAMP");
          break;
        case BITCOIN_EXCHANGE_PUBLIC_KEYS:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("");
          break;
        case LANGUAGE_LOCALE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("en_GB");
          break;
        case LOGGING_PACKAGE_PREFIX:
          // Do nothing - this is a prefix
          break;
        case LOGGING_ARCHIVE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("log/multibit-hd-%d.log.gz");
          break;
        case LOGGING_FILE:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("log/multibit-hd.log");
          break;
        case LOGGING_LEVEL:
          assertThat(properties.getProperty(configurationKey.getKey())).isEqualTo("WARN");
          break;
        default:
          fail("Missing configuration key: '" + configurationKey.name() + "'");
      }
    }

  }
}
