package org.multibit.hd.core.utils;

import com.google.common.base.Preconditions;
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

  public static final int CHECKSUM_MODULUS = 97;

  /**
   * Utilities have private constructor
   */
  private Dates() {
  }

  /**
   * Produces "Sat, 01 Jan 2000 23:59:59 GMT"
   */
  private static final DateTimeFormatter utcHttpDateFormatter = DateTimeFormat
    .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
    .withLocale(Locale.US) // For common language
    .withZone(DateTimeZone.UTC); // For GMT

  /**
   * Produces "01 Jan 2000" for simplified unambiguous user date as defined in RFC1123 (SMTP)
   */
  private static final DateTimeFormatter utcSmtpDateFormatter = DateTimeFormat.forPattern("dd MMM yyyy").withZoneUTC();

  /**
   * Produces Saturday, January 01 (no year component since this is for nearby dates)
   */
  private static final DateTimeFormatter utcDeliveryDateFormatter = DateTimeFormat.forPattern("EEEE, MMMM dd").withZoneUTC();

  /**
   * Parses ISO8601 in UTC without milliseconds (e.g. "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
   */
  private static final DateTimeFormatter utcIso8601 = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

  /**
   * @return The current midnight in UTC
   */
  public static DateTime midnightUtc() {
    return nowUtc().toDateMidnight().toDateTime();
  }

  /**
   * @return The current instant in UTC
   */
  public static DateTime nowUtc() {
    return DateTime.now(DateTimeZone.UTC);
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
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZoneUTC().print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "yyyyMMdd"
   */
  public static String formatBasicDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZoneUTC().withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01)
   */
  public static String formatDeliveryDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01)
   */
  public static String formatDeliveryDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000)
   */
  public static String formatSmtpDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000)
   */
  public static String formatSmtpDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted for HTTP as defined in RFC 1123 e.g. "Sat, 01 Jan 2000 23:59:59 GMT"
   */
  public static String formatHttpDateHeader(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcHttpDateFormatter.print(when);
  }

  /**
   * @param when The instant in its timezone
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z"
   */
  public static String formatISO8601(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcIso8601.print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z"
   */
  public static String formatISO8601(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcIso8601.withLocale(locale).print(when);
  }

  /**
   * @param text The text representing a date and time in ISO8601 format (e.g. "2000-01-02T03:04:05Z")
   *
   * @return The DateTime
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseISO8601(String text) {
    return utcIso8601.parseDateTime(text);
  }

  /**
   * @param text The text representing a date and time in SMTP format (e.g. "01 Jan 2000")
   *
   * @return The DateTime in the UTC timezone for the default locale
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseSmtpUtc(String text) {
    return utcSmtpDateFormatter.parseDateTime(text);
  }

  /**
   * @param text   The text representing a date and time in SMTP format (e.g. "01 Jan 2000")
   * @param locale The specific local to use
   *
   * @return The DateTime in the UTC timezone for the given locale
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseSmtpUtc(String text, Locale locale) {
    return utcSmtpDateFormatter.withLocale(locale).parseDateTime(text);
  }

  /**
   * @return The fixed date of the Bitcoin Genesis block (2009-01-03T18:15:00Z)
   */
  public static DateTime bitcoinGenesis() {

    return new DateTime(2009, 1, 3, 18, 15, 0, 0, DateTimeZone.UTC);

  }

  /**
   * <p>A seed timestamp is the number of days elapsed since Bitcoin genesis block with a modulo 97 checksum appended.
   * This gives a short representation that avoids user error during input and works in all locales.</p>
   *
   * @return Create a new seed timestamp (e.g. "1850/07")
   */
  public static String newSeedTimestamp() {

    long genesisMidnight = bitcoinGenesis().toDateMidnight().toDateTime().getMillis();
    long nowMidnight = Dates.midnightUtc().getMillis();

    long days = (nowMidnight - genesisMidnight) / 86_400_000;

    long modulo97 = days % 97;

    return String.format("%d/%02d", days, modulo97);

  }

  /**
   * @param text The text representing a date in seed timestamp format (e.g. "1850/07")
   *
   * @return The DateTime in the UTC timezone for the seed
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseSeedTimestamp(String text) {

    int separatorIndex = text.indexOf("/");
    Preconditions.checkArgument(separatorIndex > 3, "'" + text + "' does not contain '/' in the correct location");

    try {
      int days = Integer.valueOf(text.substring(0, separatorIndex));
      int checksum = Integer.valueOf(text.substring(separatorIndex + 1));

      Preconditions.checkArgument(days % CHECKSUM_MODULUS == checksum, "'" + text + "' has incorrect checksum. Days=" + days + " checksum=" + checksum);

      return bitcoinGenesis().plusDays(days).toDateMidnight().toDateTime();

    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("'text' does not parse into 2 integers");
    }
  }

}
