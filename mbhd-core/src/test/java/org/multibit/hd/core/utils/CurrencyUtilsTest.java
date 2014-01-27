package org.multibit.hd.core.utils;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import java.math.BigDecimal;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class CurrencyUtilsTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

  }

  @Test
  public void testCurrentZero() throws Exception {

    final BigMoney actual_UK = CurrencyUtils.currentZero();

    assertThat(actual_UK.getCurrencyUnit().getCode()).isEqualTo("GBP");
    assertThat(actual_UK.getAmount()).isEqualTo(BigDecimal.ZERO);

    setCurrencyUnit(Locale.US);

    final BigMoney actual_US = CurrencyUtils.currentZero();

    assertThat(actual_US.getCurrencyUnit().getCode()).isEqualTo("USD");
    assertThat(actual_US.getAmount()).isEqualTo(BigDecimal.ZERO);

    setCurrencyUnit(new Locale("ar", "SA"));

    final BigMoney actual_AR = CurrencyUtils.currentZero();

    assertThat(actual_AR.getCurrencyUnit().getCode()).isEqualTo("SAR");
    assertThat(actual_AR.getAmount()).isEqualTo(BigDecimal.ZERO);

  }

  @Test
  public void testCurrentCode() throws Exception {

    final String actual_UK = CurrencyUtils.currentCode();

    assertThat(actual_UK).isEqualTo("GBP");

    setCurrencyUnit(Locale.US);

    final String actual_US = CurrencyUtils.currentCode();

    assertThat(actual_US).isEqualTo("USD");

    setCurrencyUnit(new Locale("ar", "SA"));

    final String actual_AR = CurrencyUtils.currentCode();

    assertThat(actual_AR).isEqualTo("SAR");

  }

  @Test
  public void testCurrentSymbol() throws Exception {

    final String actual_UK = CurrencyUtils.currentSymbol();

    assertThat(actual_UK).isEqualTo("£");

    setCurrencyUnit(Locale.US);

    final String actual_US = CurrencyUtils.currentSymbol();

    assertThat(actual_US).isEqualTo("$");

    setCurrencyUnit(new Locale("ar", "SA"));

    final char[] actual_AR = CurrencyUtils.currentSymbol().toCharArray();

    assertThat((int) actual_AR[0]).isEqualTo(1585);
    assertThat((int) actual_AR[1]).isEqualTo(46);
    assertThat((int) actual_AR[2]).isEqualTo(1587);
    assertThat((int) actual_AR[3]).isEqualTo(46);
    assertThat((int) actual_AR[4]).isEqualTo(8207);

  }

  private void setCurrencyUnit(Locale locale) {

    Configurations.currentConfiguration.getI18NConfiguration().setLocalCurrencyUnit(CurrencyUnit.getInstance(locale));

  }
}
