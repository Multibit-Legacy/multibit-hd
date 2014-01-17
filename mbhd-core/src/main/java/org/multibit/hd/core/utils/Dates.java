package org.multibit.hd.core.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Locale;

/**
 * <p>Utility to provide the following to all layers:</p>
 * <ul>
 * <li>Provision of standard Joda time formatters and parsers</li>
 * </ul>
 * <p>All times use the UTC time zone unless otherwise specified</p>
 *
 * @since 0.0.1
 *         
 */
public class Dates {

  /**
   * Utilities have private constructor
   */
  private Dates() {
  }

  /**
   * Produces "Sat, 01 Jan 2000 23:59:59 GMT"
   */
  private static final DateTimeFormatter rfc1123 = DateTimeFormat
    .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
    .withLocale(Locale.US) // For common language
    .withZone(DateTimeZone.UTC); // For GMT

  /**
   * Produces Sunday, January 01
   */
  private static final DateTimeFormatter friendlyDateFormatter = DateTimeFormat.forPattern("EEEE, MMMM dd");

  /**
   * Parses ISO8601 without milliseconds (e.g. "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
   */
  private static final DateTimeFormatter utcIso8601 = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.UTC);

  /**
   * @return The current instant in UTC
   */
  public static DateTime nowUtc() {
    return new DateTime().withZone(DateTimeZone.UTC);
  }

  /**
   * @param year   The year (e.g. 2000)
   * @param month  The month (e.g. January is 1, December is 12)
   * @param day    The day of the month (e.g. 1 through to 31)
   * @param hour   The hour of the day (e.g. 0 through to 23)
   * @param minute The minute of the day (e.g. 0 through to 59)
   * @param second The second of the day (e.g. 0 through to 59)
   *
   * @return The given instant with a UTC timezone
   */
  public static DateTime thenUtc(
    int year,
    int month,
    int day,
    int hour,
    int minute,
    int second) {
    return new DateTime(year, month, day, hour, minute, second, 0).withZone(DateTimeZone.UTC);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyyMMdd"
   */
  public static String formatBasicDate(ReadableInstant when) {
    return ISODateTimeFormat.basicDate().print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "yyyyMMdd"
   */
  public static String formatBasicDate(ReadableInstant when, Locale locale) {
    return ISODateTimeFormat.basicDate().withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01)
   */
  public static String formatFriendlyDate(ReadableInstant when) {
    return friendlyDateFormatter.print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01)
   */
  public static String formatFriendlyDate(ReadableInstant when, Locale locale) {
    return friendlyDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as RFC 1123 e.g. "Sat, 01 Jan 2000 23:59:59 GMT"
   */
  public static String formatHttpDateHeader(ReadableInstant when) {
    return rfc1123.print(when);
  }

  /**
   * @param when The instant in its timezone
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z"
   */
  public static String formatISO8601(ReadableInstant when) {
    return utcIso8601.print(when);
  }

  /**
   * @param when The instant
   * @param locale The required locale
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z"
   */
  public static String formatISO8601(ReadableInstant when, Locale locale) {
    return utcIso8601.withLocale(locale).print(when);
  }

  /**
   * @param text The text representing a date and time in ISO8601 format
   *
   * @return The DateTime
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseISO8601(String text) {
    return utcIso8601.parseDateTime(text);
  }

}
