package org.multibit.hd.core.utils;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.multibit.hd.core.config.Configurations;

import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.text.*;
import java.util.Locale;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Parsing numeric values</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class Numbers {

  /**
   * Utilities have private constructor
   */
  private Numbers() {
  }

  /**
   * <p>Locale aware method of validating numbers - uses the currentConfiguration locale </p>
   *
   * @param text The text representing a number (null or empty is not a number)
   *
   * @return True if the text can be converted into a number
   */
  public static boolean isNumeric(String text) {
    return isNumeric(text, Configurations.currentConfiguration.getLocale());

  }

  /**
   * <p>Locale aware method of validating numbers</p>
   *
   * @param text   The text representing a number (null or empty is not a number)
   * @param locale Locale to use for number conversion
   *
   * @return True if the text can be converted into a number
   */
  public static boolean isNumeric(String text, Locale locale) {
    if (!Strings.isNullOrEmpty(text)) {

      // Ensure spaces are converted to non-breaking spaces (ASCII 160)
      text = text.replace(" ", "\u00a0");

      // The current configuration will provide the locale
      NumberFormat formatter = NumberFormat.getInstance(locale);

      // Keep track of the parse position
      ParsePosition pos = new ParsePosition(0);
      formatter.parse(text, pos);

      // If position is unchanged then no number was found
      return text.length() == pos.getIndex();

    } else {
      return false;
    }
  }

  /**
   * <p>Configuration aware method of parsing numbers including separators</p>
   *
   * @param text The text representing a number (null or empty is not a number, can include grouping symbols)
   *
   * @return A BigDecimal representing the parsed number using the String
   */
  public static Optional<BigDecimal> parseBigDecimal(String text) {

    if (!Strings.isNullOrEmpty(text)) {

      // Ensure spaces are converted to non-breaking spaces (ASCII 160)
      text = text.replace(" ", "\u00a0");

      DecimalFormat format = newDecimalFormat("#,##0.000000000000000", 8);

      // Keep track of the parse position
      ParsePosition pos = new ParsePosition(0);
      Number value = format.parse(text, pos);

      // If position is unchanged then no number was found
      if (text.length() != pos.getIndex()) {
        return Optional.absent();
      }

      return Optional.of(new BigDecimal(value.toString()));

    } else {
      return Optional.absent();
    }
  }

  /**
   * @param decimalPlaces The number of decimal places to allow
   * @param maxLength     The overall maximum length
   *
   * @return A number formatter for editing numbers based on the current configuration
   */
  public static NumberFormatter newEditFormatter(int decimalPlaces, int maxLength) {

    return newNumberFormatter("#,###.###############", decimalPlaces, maxLength);

  }

  /**
   * @param decimalPlaces The number of decimal places to allow
   * @param maxLength     The overall maximum length
   *
   * @return A number formatter for displaying numbers based on the current configuration
   */
  public static NumberFormatter newDisplayFormatter(int decimalPlaces, int maxLength) {

    return newNumberFormatter("#,##0.000000000000000", decimalPlaces, maxLength);

  }

  /**
   * @param pattern       The pattern to use (e.g. "#,##0.000000000000000")
   * @param decimalPlaces The number of decimal places to allow
   * @param maxLength     The overall maximum length
   *
   * @return A number formatter configured for insert mode and enforced maximum length length
   */
  private static NumberFormatter newNumberFormatter(String pattern, int decimalPlaces, int maxLength) {

    DecimalFormat decimalFormat = newDecimalFormat(pattern, decimalPlaces);

    NumberFormatter numberFormatter = newNumberFormatter(decimalFormat, maxLength);

    // Ensure we keep insert mode
    numberFormatter.setOverwriteMode(false);

    return numberFormatter;

  }

  /**
   * @param pattern       The pattern to use
   * @param decimalPlaces The number of decimal places to allow
   *
   * @return A decimal format based on the current configuration
   */
  private static DecimalFormat newDecimalFormat(String pattern, int decimalPlaces) {

    // Adjust edit/display formats to the current configuration
    String groupingSeparator = Configurations.currentConfiguration.getBitcoin().getGroupingSeparator();
    String decimalSeparator = Configurations.currentConfiguration.getBitcoin().getDecimalSeparator();

    // Use locale decimal formatting then override with current configuration
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(decimalSeparator.charAt(0));
    symbols.setGroupingSeparator(groupingSeparator.charAt(0));
    symbols.setMonetaryDecimalSeparator(decimalSeparator.charAt(0));

    // Identify the location of the decimal in the template before locale adjustments
    int decimalIndex = pattern.indexOf('.');

    // Adjust patterns to accommodate the required decimal places
    if (decimalPlaces > 0) {
      pattern = pattern.substring(0, decimalIndex + decimalPlaces + 1);
    } else {
      pattern = pattern.substring(0, decimalIndex);
    }

    // Create a decimal format using the configured symbols for edit/display
    return new DecimalFormat(pattern, symbols);
  }

  /**
   * @param decimalFormat The decimal format appropriate for this locale
   *
   * @return A number formatter that is locale-aware and configured for doubles
   */
  private static NumberFormatter newNumberFormatter(final DecimalFormat decimalFormat, final int maxEditLength) {

    // Create the number formatter with local-sensitive adjustments
    NumberFormatter displayFormatter = new NumberFormatter(decimalFormat) {

      // The max input length for the given symbol
      DocumentFilter documentFilter = new DocumentMaxLengthFilter(maxEditLength);

      @Override
      public Object stringToValue(String text) throws ParseException {
        // RU locale (and others) requires a non-breaking space for a grouping separator
        text = text.replace(' ', '\u00a0');
        return super.stringToValue(text);
      }

      @Override
      protected DocumentFilter getDocumentFilter() {
        return documentFilter;
      }

    };

    // Use a BigDecimal for widest value handling
    displayFormatter.setValueClass(BigDecimal.class);

    return displayFormatter;
  }

}
