package org.multibit.hd.ui.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;


public class LocalisedDateUtilsTest {

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // We work in the UK locale for consistency
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 1, 2, 23, 59, 58, 999, DateTimeZone.UTC).getMillis());

  }

  @After
  public void tearDown() throws Exception {

    Configurations.currentConfiguration = null;

    // Ensure any changes are returned to UK locale
    Locale.setDefault(Locale.UK);
    DateTimeUtils.setCurrentMillisSystem();

  }

  @Test
  public void testFormatFriendlyDate_Today() {

    DateTime dateTime = new DateTime(2000, 1, 2, 3, 4, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("Today 03:04");
  }

  @Test
  public void testFormatFriendlyDate_Yesterday() {

    DateTime dateTime = new DateTime(2000, 1, 1, 5, 6, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("Yesterday 05:06");
  }

  @Test
  public void testFormatFriendlyDate_LongForm() {

    DateTime dateTime = new DateTime(1999, 12, 31, 7, 8, 0, 0, DateTimeZone.UTC);

    assertThat(LocalisedDateUtils.formatFriendlyDate(dateTime)).isEqualTo("31 Dec 1999 07:08");
  }

}
