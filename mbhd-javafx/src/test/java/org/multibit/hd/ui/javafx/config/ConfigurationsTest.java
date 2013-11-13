package org.multibit.hd.ui.javafx.config;

import ch.qos.logback.classic.Level;
import org.junit.Test;
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationsTest {
  @Test
  public void testDefaultConfiguration() throws Exception {

    Configuration configuration = Configurations.newDefaultConfiguration();

    // Bitcoin
    assertThat(configuration.getBitcoinSymbol()).isEqualTo(BitcoinSymbol.ICON);

    // Internationalisation
    assertThat(configuration.getLocale().getLanguage()).isEqualTo("en_gb");
    assertThat(configuration.getI18NConfiguration().getDecimalSeparator().get()).isEqualTo('.');
    assertThat(configuration.getI18NConfiguration().getGroupingSeparator().get()).isEqualTo(',');

    // Logging
    assertThat(configuration.getLogging().getLoggers().get("org.multibit")).isEqualTo(Level.DEBUG);

  }
}
