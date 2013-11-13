package org.multibit.hd.ui.javafx.i18n;

import org.junit.Test;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.views.Stages;

import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

public class FormatsTest {

  @Test
  public void testFormatBitcoinBalance() throws Exception {

    Stages.setConfiguration(new Configuration());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("12345.67890"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("12,345.67");
    assertThat(balance[1]).isEqualTo("89");

  }
}
