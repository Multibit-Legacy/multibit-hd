package org.multibit.hd.ui.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of Joda time formatters and parsers containing localised text</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class LocalisedDateUtils {

  private LocalisedDateUtils() {

  }

  /**
   * Format a datetime into a String where localised versions of 'Today' and 'Yesterday' are used for recent dates
   *
   * @param instant The date to convert to a string format in UTC
   *
   * @return Localised date string using today and yesterday as appropriate in UTC
   */
  public static String formatFriendlyDate(DateTime instant) {

    DateTime todayMidnight = Dates.midnightUtc();
    DateTime yesterdayMidnight = Dates.midnightUtc().minusDays(1);

    final String formattedDate;
    if (instant.isAfter(todayMidnight)) {
      // Use "Today"
      formattedDate = Languages.safeText(MessageKey.TODAY) + " " + Dates.formatShortTime(instant);
    } else if (instant.isAfter(yesterdayMidnight)) {
      // Use "Yesterday"
      formattedDate = Languages.safeText(MessageKey.YESTERDAY) + " " + Dates.formatShortTime(instant);
    } else {
      // Use long form
      formattedDate = Dates.formatTransactionDate(instant);
    }

    return formattedDate;
  }


  /**
   * Format a datetime into a String where localised versions of 'Today' and 'Yesterday' are used for recent dates
   *
   * @param instant The date to convert to a string format in UTC
   *
   * @return Localised date string using today and yesterday as appropriate in the system timezone
   */
  public static String formatFriendlyDateLocal(DateTime instant) {

    // Convert the instant to local time
    DateTime instantLocal = instant.withZone(DateTimeZone.getDefault());

    DateTime todayMidnightLocal = Dates.midnightLocal();
    DateTime yesterdayMidnightLocal = Dates.midnightLocal().minusDays(1);

    final String formattedDate;
    if (instantLocal.isAfter(todayMidnightLocal)) {
      // Use "Today"
      formattedDate = Languages.safeText(MessageKey.TODAY) + " " + Dates.formatShortTimeLocal(instantLocal);
    } else if (instantLocal.isAfter(yesterdayMidnightLocal)) {
      // Use "Yesterday"
      formattedDate = Languages.safeText(MessageKey.YESTERDAY) + " " + Dates.formatShortTimeLocal(instantLocal);
    } else {
      // Use long form
      formattedDate = Dates.formatTransactionDateLocal(instantLocal);
    }

    return formattedDate;

  }

}