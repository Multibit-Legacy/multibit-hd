package org.multibit.hd.core.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class DatesTest {
  @Test
  public void testFriendlyFormatDefaultLocale() {

    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 1, 0, 0, 0, 0).getMillis());

    assertThat(Dates.formatFriendlyDate(Dates.nowUtc())).isEqualTo("Saturday, January 01");
  }

  @Test
  public void testFriendlyFormatFrenchLocale() {

    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 1, 0, 0, 0, 0).getMillis());

    assertThat(Dates.formatFriendlyDate(Dates.nowUtc(), Locale.FRANCE)).isEqualTo("samedi, janvier 01");
  }

  @Test
  public void testFriendlyFormatThaiLocale() {

    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 1, 0, 0, 0, 0).getMillis());

    assertThat(Dates.formatFriendlyDate(Dates.nowUtc(), new Locale("th", "TH", "TH"))).isEqualTo("วันเสาร์, มกราคม 01");
  }

  @Test
  public void testISO8601DefaultLocale() {

    DateTime instant = Dates.parseISO8601("2000-01-01T12:00:00Z");
    assertThat(Dates.formatISO8601(instant)).isEqualTo("2000-01-01T12:00:00Z");
  }
}
