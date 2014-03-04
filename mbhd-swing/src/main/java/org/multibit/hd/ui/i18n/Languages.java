package org.multibit.hd.ui.i18n;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.CoreMessageKey;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Utility to provide the following to Views:</p>
 * <ul>
 * <li>Access to internationalised text strings</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Languages {

  public static final String BASE_NAME = "i18n.viewer";

  /**
   * Utilities have private constructors
   */
  private Languages() {
  }

  /**
   * <p>Provide an array of the available amount separators. A hard space '\u00a0' is needed to
   * ensure that values do not wrap.</p>
   *
   * @return The array
   */
  public static String[] getAmountSeparators() {

    return new String[]{
      Languages.safeText(MessageKey.DECIMAL_COMMA),
      Languages.safeText(MessageKey.DECIMAL_POINT),
      Languages.safeText(MessageKey.DECIMAL_SPACE),
    };

  }

  /**
   * @return Current locale
   */
  public static Locale currentLocale() {

    Preconditions.checkNotNull(Configurations.currentConfiguration, "'currentConfiguration' must be present");

    return Configurations.currentConfiguration.getLocale();
  }

  /**
   * @param value The encoding of the locale (e.g. "ll", "ll_rr", "ll_rr_vv")
   *
   * @return A new resource bundle based on the locale
   */
  public static Locale newLocaleFromCode(String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    String[] parameters = value.split("_");

    Preconditions.checkState(parameters.length > 0, "'value' must not be empty");

    final Locale newLocale;

    switch (parameters.length) {
      case 1:
        newLocale = new Locale(parameters[0]);
        break;
      case 2:
        newLocale = new Locale(parameters[0], parameters[1]);
        break;
      case 3:
        newLocale = new Locale(parameters[0], parameters[1], parameters[2]);
        break;
      default:
        throw new IllegalArgumentException("Unknown locale descriptor: " + value);
    }

    return newLocale;

  }

  /**
   * @param key    The key (treated as a direct format string if not present)
   * @param values An optional collection of value substitutions for {@link MessageFormat}
   *
   * @return The localised text with any substitutions made
   */
  public static String safeText(MessageKey key, Object... values) {

    ResourceBundle rb = currentResourceBundle();

    final String message;

    if (!rb.containsKey(key.getKey())) {
      // If no key is present then use it direct
      message = key.getKey();
    } else {
      // Must have the key to be here
      message = rb.getString(key.getKey());
    }

    return MessageFormat.format(message, values);
  }

  /**
   * @param key    The key (treated as a direct format string if not present)
   * @param values An optional collection of value substitutions for {@link MessageFormat}
   *
   * @return The localised text with any substitutions made
   */
  public static String safeText(CoreMessageKey key, Object... values) {

    ResourceBundle rb = currentResourceBundle();

    final String message;

    if (!rb.containsKey(key.getKey())) {
      // If no key is present then use it direct
      message = key.getKey();
    } else {
      // Must have the key to be here
      message = rb.getString(key.getKey());
    }

    return MessageFormat.format(message, values);
  }

  /**
   * @param key    The key (must be present in the bundle)
   * @param values An optional collection of value substitutions for {@link MessageFormat}
   *
   * @return The localised text with any substitutions made
   */
  public static String safeText(String key, Object... values) {

    ResourceBundle rb = currentResourceBundle();

    final String message;

    if (!rb.containsKey(key)) {
      // If no key is present then use it direct
      message = "Key '" + key + "' is not localised!";
    } else {
      // Must have the key to be here
      message = rb.getString(key);
    }

    return MessageFormat.format(message, values);
  }

  /**
   * @param contents  The contents to join into a localised comma-separated list
   * @param maxLength The maximum length of the result single string
   *
   * @return The localised comma-separated list with ellipsis appended if truncated
   */
  public static String truncatedList(Collection<String> contents, int maxLength) {

    String joinedContents = Joiner
      .on(Languages.safeText(MessageKey.LIST_COMMA) + " ")
      .join(contents);

    String ellipsis = Languages.safeText(MessageKey.LIST_ELLIPSIS);

    // Determine the truncation point (if required)
    int maxIndex = Math.min(joinedContents.length() - 1, maxLength - ellipsis.length() - 1);

    if (maxIndex == joinedContents.length() - 1) {
      // No truncation
      return joinedContents;
    } else {
      // Apply truncation (with ellipsis)
      return joinedContents.substring(0, maxIndex) + ellipsis;
    }

  }

  /**
   * <p>Internal access only - external consumers should use safeText()</p>
   *
   * @return The resource bundle based on the current locale
   */
  private static ResourceBundle currentResourceBundle() {

    return ResourceBundle.getBundle(BASE_NAME, currentLocale());
  }

  /**
   * @return The component orientation based on the current locale
   */
  public static ComponentOrientation currentComponentOrientation() {
    return ComponentOrientation.getOrientation(Languages.currentLocale());
  }

  /**
   * @return True if text is to be placed left to right (standard Western language presentation)
   */
  public static boolean isLeftToRight() {
    return ComponentOrientation.getOrientation(currentLocale()).isLeftToRight();
  }

}
