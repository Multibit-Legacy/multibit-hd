package org.multibit.hd.core.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class NumbersTest {

  @BeforeClass
  public static void setUpConfiguration() {

    Configurations.currentConfiguration = new Configuration();

  }

  @Test
  public void testIsNumeric() throws Exception {

    // UK representations of good numbers
    setConfigurationLocale(Locale.UK);
    assertThat(Numbers.isNumeric("1")).isTrue();
    assertThat(Numbers.isNumeric("-1")).isTrue();
    assertThat(Numbers.isNumeric("01")).isTrue();
    assertThat(Numbers.isNumeric("-01")).isTrue();
    assertThat(Numbers.isNumeric("1.0")).isTrue();
    assertThat(Numbers.isNumeric("1,000.01")).isTrue();
    assertThat(Numbers.isNumeric("1,00001")).isTrue();
    assertThat(Numbers.isNumeric("1,0,0,0,0,1")).isTrue();

    // Russian representations of good numbers
    setConfigurationLocale(new Locale("ru"));
    assertThat(Numbers.isNumeric("1")).isTrue();
    assertThat(Numbers.isNumeric("-1")).isTrue();
    assertThat(Numbers.isNumeric("01")).isTrue();
    assertThat(Numbers.isNumeric("-01")).isTrue();
    assertThat(Numbers.isNumeric("1,0")).isTrue();
    assertThat(Numbers.isNumeric("1 000,01")).isTrue();
    assertThat(Numbers.isNumeric("1 00001")).isTrue();
    assertThat(Numbers.isNumeric("1 0 0 0 0 1")).isTrue();

  }

  @Test
  public void testParseBigDecimal() throws Exception {

    // UK representations of good numbers
    setConfigurationLocale(Locale.UK);
    assertThat(Numbers.parseBigDecimal("1").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("-1").get().toPlainString()).isEqualTo("-1");
    assertThat(Numbers.parseBigDecimal("01").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("-01").get().toPlainString()).isEqualTo("-1");
    assertThat(Numbers.parseBigDecimal("1.0").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("1,000.01").get().toPlainString()).isEqualTo("1000.01");
    assertThat(Numbers.parseBigDecimal("1,00001").get().toPlainString()).isEqualTo("100001");
    assertThat(Numbers.parseBigDecimal("1,0,0,0,0,1").get().toPlainString()).isEqualTo("100001");
    assertThat(Numbers.parseBigDecimal("1,0,0,0,0,1").get().toPlainString()).isEqualTo("100001");

    // Russian representations of good numbers
    setConfigurationLocale(new Locale("ru"));
    assertThat(Numbers.parseBigDecimal("1").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("-1").get().toPlainString()).isEqualTo("-1");
    assertThat(Numbers.parseBigDecimal("01").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("-01").get().toPlainString()).isEqualTo("-1");
    assertThat(Numbers.parseBigDecimal("1,0").get().toPlainString()).isEqualTo("1");
    assertThat(Numbers.parseBigDecimal("1 000,01").get().toPlainString()).isEqualTo("1000.01");
    assertThat(Numbers.parseBigDecimal("1 00001").get().toPlainString()).isEqualTo("100001");
    assertThat(Numbers.parseBigDecimal("1 0 0 0 0 1").get().toPlainString()).isEqualTo("100001");

  }

  /**
   * @param locale The locale providing the language and decimal configuration
   */
  private void setConfigurationLocale(Locale locale) {

    Configurations.currentConfiguration.getLanguage().setLocale(locale);

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

    Configurations.currentConfiguration.getBitcoin().setDecimalSeparator(String.valueOf(symbols.getDecimalSeparator()));
    Configurations.currentConfiguration.getBitcoin().setGroupingSeparator(String.valueOf(symbols.getGroupingSeparator()));

  }
}
