package org.multibit.hd.core.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class NumbersTest {

  @BeforeClass
  public static void setUpConfiguration() {

    Configurations.currentConfiguration = new Configuration();

  }

  @Test
  public void testIsNumeric() throws Exception {

    // UK representations of good numbers
    setLocale(Locale.UK);
    assertThat(Numbers.isNumeric("1")).isTrue();
    assertThat(Numbers.isNumeric("-1")).isTrue();
    assertThat(Numbers.isNumeric("01")).isTrue();
    assertThat(Numbers.isNumeric("-01")).isTrue();
    assertThat(Numbers.isNumeric("1.0")).isTrue();
    assertThat(Numbers.isNumeric("1,000.01")).isTrue();
    assertThat(Numbers.isNumeric("1,00001")).isTrue();
    assertThat(Numbers.isNumeric("1,0,0,0,0,1")).isTrue();

    // Russian representations of good numbers
    setLocale(new Locale("ru"));
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
  public void testParseDouble() throws Exception {

    // UK representations of good numbers
    setLocale(Locale.UK);
    assertThat(Numbers.parseDouble("1").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("-1").get()).isEqualTo(-1.0);
    assertThat(Numbers.parseDouble("01").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("-01").get()).isEqualTo(-1.0);
    assertThat(Numbers.parseDouble("1.0").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("1,000.01").get()).isEqualTo(1_000.01);
    assertThat(Numbers.parseDouble("1,00001").get()).isEqualTo(100_001);
    assertThat(Numbers.parseDouble("1,0,0,0,0,1").get()).isEqualTo(100_001);

    // Russian representations of good numbers
    setLocale(new Locale("ru"));
    assertThat(Numbers.parseDouble("1").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("-1").get()).isEqualTo(-1.0);
    assertThat(Numbers.parseDouble("01").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("-01").get()).isEqualTo(-1.0);
    assertThat(Numbers.parseDouble("1,0").get()).isEqualTo(1.0);
    assertThat(Numbers.parseDouble("1 000,01").get()).isEqualTo(1_000.01);
    assertThat(Numbers.parseDouble("1 00001").get()).isEqualTo(100_001);
    assertThat(Numbers.parseDouble("1 0 0 0 0 1").get()).isEqualTo(100_001);

  }

  private void setLocale(Locale locale) {
    Configurations.currentConfiguration.getI18NConfiguration().setLocale(locale);
  }
}
