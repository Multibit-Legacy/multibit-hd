package org.multibit.hd.ui.languages;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class LanguagesTest {

  @Test
  public void testGetOrdinalFor() throws Exception {

    // Test against all possible Trezor values just to be sure

    assertThat(Languages.getOrdinalFor(1)).isEqualTo("1st");
    assertThat(Languages.getOrdinalFor(2)).isEqualTo("2nd");
    assertThat(Languages.getOrdinalFor(3)).isEqualTo("3rd");
    assertThat(Languages.getOrdinalFor(4)).isEqualTo("4th");
    assertThat(Languages.getOrdinalFor(5)).isEqualTo("5th");
    assertThat(Languages.getOrdinalFor(6)).isEqualTo("6th");
    assertThat(Languages.getOrdinalFor(7)).isEqualTo("7th");
    assertThat(Languages.getOrdinalFor(8)).isEqualTo("8th");
    assertThat(Languages.getOrdinalFor(9)).isEqualTo("9th");
    assertThat(Languages.getOrdinalFor(10)).isEqualTo("10th");
    assertThat(Languages.getOrdinalFor(11)).isEqualTo("11th");
    assertThat(Languages.getOrdinalFor(12)).isEqualTo("12th");
    assertThat(Languages.getOrdinalFor(13)).isEqualTo("13th");
    assertThat(Languages.getOrdinalFor(14)).isEqualTo("14th");
    assertThat(Languages.getOrdinalFor(15)).isEqualTo("15th");
    assertThat(Languages.getOrdinalFor(16)).isEqualTo("16th");
    assertThat(Languages.getOrdinalFor(17)).isEqualTo("17th");
    assertThat(Languages.getOrdinalFor(18)).isEqualTo("18th");
    assertThat(Languages.getOrdinalFor(19)).isEqualTo("19th");
    assertThat(Languages.getOrdinalFor(20)).isEqualTo("20th");
    assertThat(Languages.getOrdinalFor(21)).isEqualTo("21st");
    assertThat(Languages.getOrdinalFor(22)).isEqualTo("22nd");
    assertThat(Languages.getOrdinalFor(23)).isEqualTo("23rd");
    assertThat(Languages.getOrdinalFor(24)).isEqualTo("24th");

  }
}