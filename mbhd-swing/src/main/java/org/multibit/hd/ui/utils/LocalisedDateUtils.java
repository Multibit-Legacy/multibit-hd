package org.multibit.hd.ui.utils;

import org.joda.time.DateTime;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.text.SimpleDateFormat;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  TODO Pull this into Dates and avoid static use of SimpleDateFormat (not thread safe)
 */
public class LocalisedDateUtils {

  public static SimpleDateFormat longDateFormatter;
  public static SimpleDateFormat shortDateFormatter = new SimpleDateFormat("HH:mm");

  private LocalisedDateUtils() {

  }

  /**
   * Format a datetime into a String where 'Today' and 'Yesterday' are used for recent dates
   *
   * @param date The date to convert to a string format
   * @return Localised date string using today and yesterday as appropriate
   */
  public static String formatFriendlyDate(DateTime date) {
    String formattedDate = "";

    if (date.getMillis() != 0) {
      try {
        if (date.toDateMidnight().equals((DateTime.now().toDateMidnight()))) {
          formattedDate = Languages.safeText(MessageKey.TODAY) + " " + shortDateFormatter.format(date.toDate());
        } else {
          if (date.toDateMidnight().equals((DateTime.now().minusDays(1).toDateMidnight()))) {
            formattedDate = Languages.safeText(MessageKey.YESTERDAY) + " " + shortDateFormatter.format(date.toDate());
          } else {
            formattedDate = longDateFormatter.format(date.toDate());
          }
        }
      } catch (IllegalArgumentException iae) {
        // ok
      }
    }
    return formattedDate;
  }

  /**
   * Format a datetime into a String where just the time part is used
   *
   * @param date The date to convert to a string format
   * @return The time part of the date
   */
  public static String formatShortDate(DateTime date) {
    String formattedDate = "";

    if (date != null && date.getMillis() != 0) {
      try {
        formattedDate = shortDateFormatter.format(date.toDate());
      } catch (IllegalArgumentException iae) {
        // ok
      }
    }
    return formattedDate;
  }
}
