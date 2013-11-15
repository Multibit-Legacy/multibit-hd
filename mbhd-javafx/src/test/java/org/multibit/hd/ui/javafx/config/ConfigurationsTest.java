package org.multibit.hd.ui.javafx.config;

import ch.qos.logback.classic.Level;
import org.junit.Test;
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;

import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.multibit.hd.ui.javafx.config.Configurations.*;

public class ConfigurationsTest {

  @Test
  public void testDefaultConfiguration() throws Exception {

    Configuration configuration = Configurations.newDefaultConfiguration();

    // Bitcoin
    assertThat(configuration.getBitcoinConfiguration().getBitcoinSymbol()).isEqualTo(BitcoinSymbol.ICON);

    // Internationalisation
    assertThat(configuration.getLocale().getLanguage()).isEqualTo("en_gb");
    assertThat(configuration.getI18NConfiguration().getDecimalSeparator().get()).isEqualTo('.');
    assertThat(configuration.getI18NConfiguration().getGroupingSeparator().get()).isEqualTo(',');
    assertThat(configuration.getI18NConfiguration().isCurrencySymbolPrefixed()).isTrue();

    // Logging
    assertThat(configuration.getLoggingConfiguration().getLoggers().get("org.multibit")).isEqualTo(Level.DEBUG);

  }

  @Test
  public void testDefaultProperties() throws Exception {

    Configuration configuration = Configurations.newDefaultConfiguration();

    Properties properties = new ConfigurationWriteAdapter(configuration).adapt();

    // Bitcoin
    assertThat(properties.get(BITCOIN_SYMBOL)).isEqualTo(BitcoinSymbol.ICON);

    // Internationalisation
    assertThat(properties.get(I18N_LOCALE).toString()).isEqualTo("en_gb");
    assertThat(properties.get(I18N_DECIMAL_SEPARATOR)).isEqualTo('.');
    assertThat(properties.get(I18N_GROUPING_SEPARATOR)).isEqualTo(',');
    assertThat(properties.get(I18N_IS_CURRENCY_PREFIXED)).isEqualTo("true");

    // Logging
    assertThat(properties.get(LOGGING_PACKAGE_PREFIX+"org.multibit")).isEqualTo("DEBUG");

  }

}
