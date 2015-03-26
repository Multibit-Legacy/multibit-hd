package org.multibit.hd.core.utils;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.sntp.NtpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * <p>Utility to provide the following to all layers:</p>
 * <ul>
 * <li>Provision of standard Joda time formatters and parsers</li>
 * </ul>
 * <p>All times use the UTC time zone unless otherwise specified</p>
 *
 * @since 0.0.1
 */
public class Dates {

  private static final Logger log = LoggerFactory.getLogger(Dates.class);

  public static final int CHECKSUM_MODULUS = 97;

  public static final int NUMBER_OF_SECONDS_IN_A_DAY = 60 * 60 * 24;

  /**
   * Provides asynchronous NTP lookup
   */
  private static final ListeningExecutorService systemTimeDriftExecutorService = SafeExecutors.newSingleThreadExecutor("system-time-drift");

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
   * Produces 01 Jan 2000 23:59 for maximum clarity
   */
  private static final DateTimeFormatter utcTransactionDateFormatter = DateTimeFormat.forPattern("dd MMM yyyy HH:mm").withZoneUTC();

  /**
   * Parses ISO8601 in UTC without milliseconds (e.g. "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
   */
  private static final DateTimeFormatter utcIso8601 = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

  /**
   * Produces "2000-04-01" for simplified short user date
   */
  private static final DateTimeFormatter utcShortDateWithHyphensFormatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

  /**
   * Produces "23:59" for simplified short user time
   */
  private static final DateTimeFormatter utcShortTimeFormatter = DateTimeFormat.forPattern("HH:mm").withZoneUTC();

  /**
   * Produces "20000102235958" for condensed backup suffix time format
   */
  private static final DateTimeFormatter utcBackupFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZoneUTC();

  /**
   * @return The current midnight in UTC
   */
  public static DateTime midnightUtc() {
    return nowUtc().toDateMidnight().toDateTime();
  }

  /**
   * @return The current midnight in the system timezone
   */
  public static DateTime midnightLocal() {
    return nowUtc().withZone(DateTimeZone.getDefault()).toDateMidnight().toDateTime();
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
   * Get the current date since epoch in seconds
   *
   * @return the current date in seconds
   */
  public static long nowInSeconds() {
    return (long) (DateTime.now().getMillis() * 0.001);
  }

  /**
   * Get the number of seconds since epoch for the given instant
   *
   * @param then The instant
   *
   * @return the number of seconds since the epoch
   */
  public static long thenInSeconds(ReadableInstant then) {
    return (long) (then.getMillis() * 0.001);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "HH:mm" in UTC
   */
  public static String formatShortTime(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcShortTimeFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "HH:mm" in the system timezone
   */
  public static String formatShortTimeLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcShortTimeFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyyMMdd" in UTC
   */
  public static String formatCompactDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZoneUTC().print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyyMMdd" in the system timezone
   */
  public static String formatCompactDateLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "yyyyMMdd" in UTC
   */
  public static String formatCompactDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZoneUTC().withLocale(locale).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "yyyyMMdd" in the system timezone
   */
  public static String formatCompactDateLocal(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return ISODateTimeFormat.basicDate().withZone(DateTimeZone.getDefault()).withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyyMMddHHmmss" in UTC
   */
  public static String formatBackupDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcBackupFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyy-MM-dd" in UTC
   */
  public static String formatCompactDateWithHyphens(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcShortDateWithHyphensFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "yyyy-MM-dd" in the system timezone
   */
  public static String formatCompactDateWithHyphensLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcShortDateWithHyphensFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01) in UTC
   */
  public static String formatDeliveryDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01) in the system timezone
   */
  public static String formatDeliveryDateLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01) in UTC
   */
  public static String formatDeliveryDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01) in the system timezone
   */
  public static String formatDeliveryDateLocal(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcDeliveryDateFormatter.withZone(DateTimeZone.getDefault()).withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "dd MMM yyyy" (01 Jan 2000) in UTC
   */
  public static String formatTransactionDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcTransactionDateFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted as "dd MMM yyyy" (01 Jan 2000) in the system timezone
   */
  public static String formatTransactionDateLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcTransactionDateFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "dd MMM yyyy HH:mm" (01 Jan 2000 23:59) in UTC
   */
  public static String formatTransactionDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcTransactionDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as "ddd, MMM dd" (Saturday, January 01) in the system timezone
   */
  public static String formatTransactionDateLocal(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcTransactionDateFormatter.withZone(DateTimeZone.getDefault()).withLocale(locale).print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000) in UTC
   */
  public static String formatSmtpDate(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.print(when);
  }

  /**
   * @param when The instant
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000) in the system timezone
   */
  public static String formatSmtpDateLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000) in UTC
   */
  public static String formatSmtpDate(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.withLocale(locale).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted for SMTP as defined in RFC 1123 e.g. "dd MMM yyyy" (01 Jan 2000) in the system timezone
   */
  public static String formatSmtpDateLocal(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcSmtpDateFormatter.withZone(DateTimeZone.getDefault()).withLocale(locale).print(when);
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
   * @param when The instant
   *
   * @return The instant formatted for HTTP as defined in RFC 1123 e.g. "Sat, 01 Jan 2000 23:59:59 GMT" in UTC
   */
  public static String formatHttpDateHeaderLocal(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcHttpDateFormatter.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when The instant in its timezone
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z" in UTC
   */
  public static String formatIso8601(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcIso8601.print(when);
  }

  /**
   * @param when The instant in its timezone
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05" in the system timezone
   */
  public static String formatIso8601Local(ReadableInstant when) {
    if (when == null) {
      return "";
    }
    return utcIso8601.withZone(DateTimeZone.getDefault()).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05Z" in UTC
   */
  public static String formatIso8601(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcIso8601.withLocale(locale).print(when);
  }

  /**
   * @param when   The instant
   * @param locale The required locale
   *
   * @return The instant formatted as ISO8601 e.g. "2000-01-02T03:04:05" in the system timezone
   */
  public static String formatIso8601Local(ReadableInstant when, Locale locale) {
    if (when == null) {
      return "";
    }
    return utcIso8601.withZone(DateTimeZone.getDefault()).withLocale(locale).print(when);
  }

  /**
   * @param text The text representing a date and time in ISO8601 format (e.g. "2000-01-02T03:04:05Z") in UTC
   *
   * @return The DateTime
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseIso8601(String text) {
    return utcIso8601.parseDateTime(text);
  }

  /**
   * @param text The text representing a date and time in SMTP format (e.g. "01 Jan 2000") in UTC
   *
   * @return The DateTime in the UTC timezone for the default locale
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseSmtpUtc(String text) {
    return utcSmtpDateFormatter.parseDateTime(text);
  }

  /**
   * @param text   The text representing a date and time in SMTP format (e.g. "01 Jan 2000") in UTC
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
   * @return The fixed date of the Bitcoin Genesis block (2009-01-03T18:15:00Z) in UTC
   */
  public static DateTime bitcoinGenesis() {

    return new DateTime(2009, 1, 3, 18, 15, 0, 0, DateTimeZone.UTC);

  }

  /**
   * <p>A seed timestamp is the number of days elapsed since Bitcoin genesis block with a modulo 97 checksum appended.
   * This gives a short representation that avoids user error during input and works in all locales.</p>
   *
   * @return Create a new seed timestamp (e.g. "1850/07") in UTC
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
   * @return The DateTime in the UTC timezone for the seed in UTC
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseSeedTimestamp(String text) {

    int separatorIndex = text.indexOf("/");
    Preconditions.checkArgument(separatorIndex > 3, "'" + text + "' does not contain '/' in the correct location");

    try {
      int days = Integer.parseInt(text.substring(0, separatorIndex));
      int checksum = Integer.parseInt(text.substring(separatorIndex + 1));

      Preconditions.checkArgument(days % CHECKSUM_MODULUS == checksum, "'" + text + "' has incorrect checksum. Days=" + days + " checksum=" + checksum);

      return bitcoinGenesis().plusDays(days).toDateMidnight().toDateTime();

    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("'text' does not parse into 2 integers");
    }
  }

  /**
   * @param text The text representing a date and time in backup format (e.g. "20000203235958") in UTC
   *
   * @return The DateTime in the UTC timezone
   *
   * @throws IllegalArgumentException If the text cannot be parsed
   */
  public static DateTime parseBackupDate(String text) {
    return utcBackupFormatter.parseDateTime(text);
  }

  /**
   * <p>Non-blocking call to an SNTP time server.</p>
   *
   * @param sntpHost The SNTP host (e.g. "pool.ntp.org" or "")
   *
   * @return The number of milliseconds of drift from a SNTP timeserver time. Add this figure to local time to become correct. Thus -ve means local clock is ahead of server.
   */
  public static ListenableFuture<Integer> calculateDriftInMillis(final String sntpHost) {

    return systemTimeDriftExecutorService.submit(
      new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {

          log.debug("Checking system time drift against '{}'", sntpHost);

          // Send request
          DatagramSocket socket = new DatagramSocket();
          // Typical response time for SNTP on domestic broadband is around 40ms
          // This very high setting allows for
          // * firewall negotiation where the user manually allows the connection
          // * DNS and UPnP issues at the router
          // * very poor network performance
          socket.setSoTimeout(60_000);
          InetAddress address = InetAddress.getByName(sntpHost);
          byte[] buf = new NtpMessage().toByteArray();

          // Build the SNTP datagram on port 123
          DatagramPacket packet = new DatagramPacket(
            buf,
            buf.length,
            address,
            123
          );

          // Set the transmit timestamp *just* before sending the packet
          NtpMessage.encodeTimestamp(
            packet.getData(),
            40,
            // Offset from 01-01-1900T00:00:00Z
            (System.currentTimeMillis() / 1_000.0) + 2_208_988_800.0
          );

          socket.send(packet);

          // Wait for response response
          packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);

          // Set the receive timestamp *just* after receiving the packet
          double destinationTimestamp =
            // Offset from 01-01-1900T00:00:00Z
            (System.currentTimeMillis() / 1_000.0) + 2_208_988_800.0;

          // Close the socket since we have all the data in the packet buffer
          socket.close();

          // Process response
          NtpMessage msg = new NtpMessage(packet.getData());

          // Calculate local offset in microseconds
          double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp)
            + (msg.transmitTimestamp - destinationTimestamp)) / 2;

          // Provide current time with offset applied
          return (int) (localClockOffset * 1_000);
        }
      });

  }

}
