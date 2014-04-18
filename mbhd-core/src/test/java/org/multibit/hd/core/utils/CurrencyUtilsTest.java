package org.multibit.hd.core.utils;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import java.math.BigDecimal;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class CurrencyUtilsTest {

  @Before
  public void setUp() throws Exception {

    Configurations.switchConfiguration(Configurations.newDefaultConfiguration());

    Locale.setDefault(Locale.UK);

  }

  public void tearDown() {

    Locale.setDefault(Locale.UK);
  }

  @Test
  public void testCurrentZero() throws Exception {

    final BigMoney actual_US = CurrencyUtils.currentZero();

    assertThat(actual_US.getCurrencyUnit().getCode()).isEqualTo("USD");
    assertThat(actual_US.getAmount()).isEqualTo(BigDecimal.ZERO);

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(Locale.UK));

    final BigMoney actual_UK = CurrencyUtils.currentZero();

    assertThat(actual_UK.getCurrencyUnit().getCode()).isEqualTo("GBP");
    assertThat(actual_UK.getAmount()).isEqualTo(BigDecimal.ZERO);

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(new Locale("ar", "SA")));

    final BigMoney actual_AR = CurrencyUtils.currentZero();

    assertThat(actual_AR.getCurrencyUnit().getCode()).isEqualTo("SAR");
    assertThat(actual_AR.getAmount()).isEqualTo(BigDecimal.ZERO);

  }

  @Test
  public void testCurrentCode() throws Exception {

    final String actual_US = CurrencyUtils.currentCode();

    assertThat(actual_US).isEqualTo("USD");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(Locale.UK));

    final String actual_UK = CurrencyUtils.currentCode();

    assertThat(actual_UK).isEqualTo("GBP");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(new Locale("ar", "SA")));

    final String actual_AR = CurrencyUtils.currentCode();

    assertThat(actual_AR).isEqualTo("SAR");

  }

  @Test
  public void testCurrentSymbol() throws Exception {

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(Locale.US));

    final String actual_US = CurrencyUtils.currentSymbol();

    assertThat(actual_US).isEqualTo("$");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(Locale.UK));

    final String actual_UK = CurrencyUtils.currentSymbol();

    assertThat(actual_UK).isEqualTo("£");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyUnit(CurrencyUnit.getInstance(new Locale("ar", "SA")));

    final char[] actual_AR = CurrencyUtils.currentSymbol().toCharArray();

    assertThat((int) actual_AR[0]).isEqualTo(1585);
    assertThat((int) actual_AR[1]).isEqualTo(46);
    assertThat((int) actual_AR[2]).isEqualTo(1587);
    assertThat((int) actual_AR[3]).isEqualTo(46);
    assertThat((int) actual_AR[4]).isEqualTo(8207);

  }

  @Test
  public void testIsoCandidate() throws Exception {

    assertThat(CurrencyUtils.isoCandidateFor("XBT")).isEqualTo("XBT");
    assertThat(CurrencyUtils.isoCandidateFor("BTC")).isEqualTo("XBT");
    assertThat(CurrencyUtils.isoCandidateFor("RUR")).isEqualTo("RUB");
    assertThat(CurrencyUtils.isoCandidateFor("USD")).isEqualTo("USD");

  }

}
