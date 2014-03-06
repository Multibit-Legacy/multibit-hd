package org.multibit.hd.core.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class DatesTest {

  @Before
  public void setUp() throws Exception {

    // We work in the UK locale for consistency
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC).getMillis());

  }

  @After
  public void tearDown() throws Exception {

    // Ensure any changes are returned to UK locale
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisSystem();

  }

  @Test
  public void testMidnightUtc() {

    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 2, 3, 4, 5, 6).getMillis());

    assertThat(Dates.formatISO8601(Dates.midnightUtc())).isEqualTo("2000-01-02T00:00:00Z");
  }

  @Test
  public void testFormatDelivery_DefaultLocale() {

    assertThat(Dates.formatDeliveryDate(Dates.nowUtc())).isEqualTo("Saturday, January 01");
  }

  @Test
  public void testFormatDelivery_FrenchLocale() {

    assertThat(Dates.formatDeliveryDate(Dates.nowUtc(), Locale.FRANCE)).isEqualTo("samedi, janvier 01");
  }

  @Test
  public void testFormatDelivery_ThaiLocale() {

    assertThat(Dates.formatDeliveryDate(Dates.nowUtc(), new Locale("th", "TH", "TH"))).isEqualTo("วันเสาร์, มกราคม 01");
  }

  @Test
  public void testFormatSmtp_DefaultLocale() {

    assertThat(Dates.formatSmtpDate(Dates.nowUtc())).isEqualTo("01 Jan 2000");
  }

  @Test
  public void testFormatSmtp_FrenchLocale() {

    assertThat(Dates.formatSmtpDate(Dates.nowUtc(), Locale.FRANCE)).isEqualTo("01 janv. 2000");
  }

  @Test
  public void testFormatSmtp_ThaiLocale() {

    assertThat(Dates.formatSmtpDate(Dates.nowUtc(), new Locale("th", "TH", "TH"))).isEqualTo("01 ม.ค. 2000");
  }

  @Test
  public void testParseISO8601_DefaultLocale() {

    DateTime instant = Dates.parseISO8601("2000-01-01T12:00:00Z");

    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T12:00:00Z");
  }

  @Test
  public void testParseSmtpUtc_DefaultLocale() {

    DateTime instant = Dates.parseSmtpUtc("01 Jan 2000").withZone(DateTimeZone.UTC);
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T00:00:00Z");

    instant = Dates.parseSmtpUtc("1 jan 2000").withZone(DateTimeZone.UTC);
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T00:00:00Z");

    instant = Dates.parseSmtpUtc("1 january 2000").withZone(DateTimeZone.UTC);
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T00:00:00Z");

  }

  @Test
  public void testParseSmtpUtc_FrenchLocale() {

    // Failed
    DateTime instant = Dates.parseSmtpUtc("01 janv. 2000", Locale.FRANCE).withZone(DateTimeZone.UTC);
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T00:00:00Z");

  }

  @Test
  public void testParseSmtpUtc_ThaiLocale() {

    // Failed
    DateTime instant = Dates.parseSmtpUtc("01 ม.ค. 2000", new Locale("th", "TH", "TH")).withZone(DateTimeZone.UTC);
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T00:00:00Z");

  }

  @Test
  public void testNewSeedTimestamp() {

    // Failed
    DateTimeUtils.setCurrentMillisFixed(new DateTime(2014, 1, 27, 0, 0, 0, 0).getMillis());
    assertThat(Dates.newSeedTimestamp()).isEqualTo("1850/07");

    DateTimeUtils.setCurrentMillisFixed(new DateTime(2014, 1, 17, 0, 0, 0, 0).getMillis());
    assertThat(Dates.newSeedTimestamp()).isEqualTo("1840/94");

  }

  @Test
  public void testParseSeedTimestamp() {

    DateTime expected = new DateTime(2014, 1, 17, 0, 0, 0, 0, DateTimeZone.UTC);

    assertThat(Dates.parseSeedTimestamp("1840/94")).isEqualTo(expected);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseSeedTimestamp_Checksum() {

    DateTime expected = new DateTime(2014, 1, 27, 0, 0, 0, 0, DateTimeZone.UTC);

    assertThat(Dates.parseSeedTimestamp("1850/01")).isEqualTo(expected);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseSeedTimestamp_Length() {

    DateTime expected = new DateTime(2014, 1, 27, 0, 0, 0, 0, DateTimeZone.UTC);

    assertThat(Dates.parseSeedTimestamp("180/12")).isEqualTo(expected);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseSeedTimestamp_Format1() {

    DateTime expected = new DateTime(2014, 1, 27, 0, 0, 0, 0, DateTimeZone.UTC);

    assertThat(Dates.parseSeedTimestamp("1850-20")).isEqualTo(expected);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseSeedTimestamp_Format2() {

    DateTime expected = new DateTime(2014, 1, 27, 0, 0, 0, 0, DateTimeZone.UTC);

    assertThat(Dates.parseSeedTimestamp("1850/")).isEqualTo(expected);

  }

}
