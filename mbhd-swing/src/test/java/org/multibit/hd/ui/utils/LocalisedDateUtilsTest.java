package org.multibit.hd.ui.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import java.util.Locale;
import java.util.TimeZone;

import static org.fest.assertions.Assertions.assertThat;


public class LocalisedDateUtilsTest {

  // Get the current timezone without using Joda time
  TimeZone original = TimeZone.getDefault();

  @Before
  public void setUp() throws Exception {

    // Required for Languages lookup
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // We work in the UK locale under GMT+2 for consistency
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 2, 23, 59, 58, 999, DateTimeZone.UTC).getMillis());
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(2));

  }

  @After
  public void tearDown() throws Exception {

    // Reset
    Configurations.currentConfiguration = null;

    // Ensure any changes are returned to UK locale
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisSystem();
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(original.getRawOffset()/3_600_000)); // Raw offset is in millis

  }

  @Test
  public void testFormatFriendlyDate_Today() {

    DateTime dateTime = new DateTime(2000, 1, 2, 3, 4, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("Today 03:04");

    // We have crossed midnight locally (i.e. 2000-01-02 23:59:58.999 GMT -> 2000-01-03 01:59:58.999 GMT+2)
    // therefore 2000-01-02 03:04:00.000 GMT -> 2000-01-02 05:04:00.000 GMT+2 which is yesterday
    assertThat(LocalisedDateUtils.formatFriendlyDateLocal(dateTime)).isEqualTo("Yesterday 05:04");

  }

  @Test
  public void testFormatFriendlyDate_Yesterday() {

    DateTime dateTime = new DateTime(2000, 1, 1, 5, 6, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("Yesterday 05:06");

    // We have crossed midnight locally (i.e. 2000-01-02 23:59:58.999 GMT -> 2000-01-03 01:59:58.999 GMT+2)
    // therefore 2000-01-01 05:06:00.000 GMT -> 2000-01-01 07:06:00.000 GMT+2 which is before yesterday
    assertThat(LocalisedDateUtils.formatFriendlyDateLocal(dateTime)).isEqualTo("01 Jan 2000 07:06");

  }

  @Test
  public void testFormatFriendlyDate_LongForm() {

    DateTime dateTime = new DateTime(1999, 12, 31, 7, 8, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("31 Dec 1999 07:08");

    // We have crossed midnight locally (i.e. 2000-01-02 23:59:58.999 GMT -> 2000-01-03 01:59:58.999 GMT+2)
    // therefore 1999-12-31 07:08:00.000 GMT -> 1999-12-31 09:08:00.000 GMT+2 which is before yesterday
    assertThat(LocalisedDateUtils.formatFriendlyDateLocal(dateTime)).isEqualTo("31 Dec 1999 09:08");

  }

}
