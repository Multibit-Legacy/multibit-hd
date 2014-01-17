package org.multibit.hd.core.utils;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.multibit.hd.core.config.Configurations;

import java.text.NumberFormat;
import java.text.ParsePosition;
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
   * <p>Locale aware method of validating numbers</p>
   *
   * @param text The text representing a number (null or empty is not a number)
   *
   * @return True if the text can be converted into a number
   */
  public static boolean isNumeric(String text) {

    Locale locale = Configurations.currentConfiguration.getLocale();

    if (!Strings.isNullOrEmpty(text)) {

      // Ensure spaces are converted to non-breaking spaces (ASCII 160)
      text = text.replace(" ","\u00a0");

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
   * <p>Locale aware method of parsing numbers including separators</p>
   *
   * @param text The text representing a number (null or empty is not a number, can include grouping symbols)
   *
   * @return A Double representing the parsed number
   */
  public static Optional<Double> parseDouble(String text) {

    Locale locale = Configurations.currentConfiguration.getLocale();

    if (!Strings.isNullOrEmpty(text)) {

      // Ensure spaces are converted to non-breaking spaces (ASCII 160)
      text = text.replace(" ","\u00a0");

      NumberFormat formatter = NumberFormat.getNumberInstance(locale);

      // Keep track of the parse position
      ParsePosition pos = new ParsePosition(0);
      Number value = formatter.parse(text, pos);

      // If position is unchanged then no number was found
      if (text.length() != pos.getIndex()) {
        return Optional.absent();
      }

      return Optional.of(value.doubleValue());

    } else {
      return Optional.absent();
    }
  }

}
