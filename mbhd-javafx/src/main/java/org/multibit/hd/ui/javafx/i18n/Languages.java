package org.multibit.hd.ui.javafx.i18n;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.multibit.hd.ui.javafx.exceptions.UIException;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class Languages {

  private static final String BASE_NAME = "i18n.viewer";

  private static final String LANGUAGE_CHOICE_TEMPLATE = "showPreferencesPanel.language.%d";

  /**
   * The 2-letter short language code suitable for use with Locale
   * The ordering is consistent with the presentation of the language
   * list within the resource bundle, but this list is 0-based
   */
  private static final String[] LANGUAGE_CODES = new String[]{
    // 0 - 9
    "en", "es", "ru", "sv", "no", "it", "fr", "de", "pt", "hi",
    // 10 - 19
    "th", "nl", "zh", "ja", "ar", "ko", "lv", "tr", "fi", "pl",
    // 20 - 29
    "hr", "hu", "el", "da", "eo", "fa", "he", "sk", "cs", "id",
    // 30 - 35
    "sl", "ta", "ro", "af", "tl", "sw",
  };

  public static List<String> getLanguageNames(ResourceBundle rb, boolean includeCodes) {

    Preconditions.checkNotNull(rb, "'rb' must be present");

    List<String> items = Lists.newArrayList();

    for (int i = 0; i < LANGUAGE_CODES.length; i++) {

      // Use a 1-based lookup for the resource bundle
      String key = String.format(LANGUAGE_CHOICE_TEMPLATE, i + 1);
      String value = rb.getString(key);

      if (includeCodes) {
        items.add(LANGUAGE_CODES[i] + ": " + value);
      } else {
        items.add(value);
      }

    }

    return items;

  }

  /**
   * @param locale The locale
   *
   * @return A new resource bundle based on the locale
   */
  public static ResourceBundle newResourceBundle(Locale locale) {

    Preconditions.checkNotNull(locale, "'locale' must be present");

    return ResourceBundle.getBundle(BASE_NAME, locale);
  }

  /**
   * @param index The 0-based index of the language
   *
   * @return A new resource bundle based on the locale
   */
  public static Locale newLocaleFromIndex(int index) {

    Preconditions.checkElementIndex(index, LANGUAGE_CODES.length);

    return new Locale(LANGUAGE_CODES[index]);
  }

  /**
   * @param locale The locale
   *
   * @return The matching index (0-based)
   *
   * @throws UIException If there is no match
   */
  public static int getIndexFromLocale(Locale locale) {

    Preconditions.checkNotNull(locale, "'locale' must be present");

    String language = locale.getLanguage();

    for (int i = 0; i < LANGUAGE_CODES.length; i++) {

      if (LANGUAGE_CODES[i].equals(language)) {
        return i;
      }

    }

    throw new UIException("Unknown locale: " + locale);
  }
}
