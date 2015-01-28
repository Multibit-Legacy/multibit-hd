package org.multibit.hd.core.utils;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.InstallationManager;

import java.util.Currency;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class CurrencyUtilsTest {



  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    Locale.setDefault(Locale.UK);

  }

  public void tearDown() {

    InstallationManager.unrestricted = false;
    Locale.setDefault(Locale.UK);
  }

  @Test
  public void testCurrentCode() throws Exception {

    final String actual_US = Configurations.currentConfiguration.getLocalCurrency().getCurrencyCode();

    assertThat(actual_US).isEqualTo("USD");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyCode(Currency.getInstance(Locale.UK).getCurrencyCode());

    final String actual_UK = Configurations.currentConfiguration.getLocalCurrency().getCurrencyCode();

    assertThat(actual_UK).isEqualTo("GBP");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyCode(Currency.getInstance(new Locale("ar", "SA")).getCurrencyCode());

    final String actual_AR = Configurations.currentConfiguration.getLocalCurrency().getCurrencyCode();

    assertThat(actual_AR).isEqualTo("SAR");

  }

  @Test
  public void testCurrentSymbol() throws Exception {

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyCode(Currency.getInstance(Locale.US).getCurrencyCode());

    final String actual_US = CurrencyUtils.currentSymbol();

    assertThat(actual_US).isEqualTo("$");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyCode(Currency.getInstance(Locale.UK).getCurrencyCode());

    final String actual_UK = CurrencyUtils.currentSymbol();

    assertThat(actual_UK).isEqualTo("£");

    Configurations.currentConfiguration.getBitcoin().setLocalCurrencyCode(Currency.getInstance(new Locale("ar", "SA")).getCurrencyCode());

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
