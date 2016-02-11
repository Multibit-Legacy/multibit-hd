package org.multibit.hd.ui.languages;

import org.multibit.hd.ui.views.components.Images;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.multibit.hd.ui.views.fonts.TitleFontDecorator.*;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Language keys to use for supported locales (language,region,variant)</li>
 * </ul>
 *
 * <h3>Naming conventions</h3>
 * <p>Language keys are placed in an enum for type safety.</p>
 * <p>Language keys have their resource key so that IDEs can maintain a "where used" reference lookup.</p>
 * <p>Resource keys are simply the language key enum name in lower case and are only present in the
 * base resource bundle (e.g. "viewer.properties").</p>
 * <p>Eac languages has a preferred title font for presentation</p>
 *
 * @since 0.0.1
 *
 */
public enum LanguageKey {

  /**
   * Afrikaans in Africa
   */
  AF_AF("af_AF", CORBEN_REGULAR),
  /**
   * Arabic
   */
  AR_AR("ar_AR", IMPACT_REGULAR),
  /**
   * Bulgarian
   */
  BG_BG("bg_BG", OPENSANS_SEMIBOLD),
  /**
   * Czech
   */
  CS_CZ("cs_CZ", OPENSANS_SEMIBOLD),
  /**
   * Catalan
   */
  CA_ES("ca_ES", CORBEN_REGULAR),
  /**
   * Danish
   */
  DA_DK("da_DK", CORBEN_REGULAR),
  /**
   * German
   */
  DE_DE("de_DE", CORBEN_REGULAR),
  /**
   * Greek
   */
  EL_GR("el_GR", OPENSANS_SEMIBOLD),
  /**
   * English (United Kingdom)
   */
  EN_GB("en_GB", CORBEN_REGULAR),
  /**
   * English (United States)
   */
  EN_US("en_US", CORBEN_REGULAR),
  /**
   * Esperanto
   */
  EO("eo", CORBEN_REGULAR), // Esperanto has no country
  /**
   * Spanish
   */
  ES_ES("es_ES", CORBEN_REGULAR),
  /**
   * Farsi
   */
  FA_IR("fa_IR", IMPACT_REGULAR),
  /**
   * Finnish
   */
  FI_FI("fi_FI", CORBEN_REGULAR),
  /**
   * French
   */
  FR_FR("fr_FR", CORBEN_REGULAR),
  /**
   * Hindi (should use NotoSans-Bold when proven)
   */
  HI_IN("hi_IN", IMPACT_REGULAR),
  /**
   * Croatian
   */
  HR_HR("hr_HR", OPENSANS_SEMIBOLD),
  /**
   * Hungarian
   */
  HU_HU("hu_HU", OPENSANS_SEMIBOLD),
  /**
   * Indonesian
   */
  IN_ID("in_ID", IMPACT_REGULAR), // Legacy form of "id_ID" for Indonesian in Indonesia
  /**
   * Hebrew (Israel)
   */
  IW_IL("iw_IL", IMPACT_REGULAR), // Legacy form of "he_IL" for Hebrew in Israel
  /**
   * Italian
   */
  IT_IT("it_IT", CORBEN_REGULAR),
  /**
   * Japanese
   */
  JA_JP("ja_JP", IMPACT_REGULAR),
  /**
   * Korean
   */
  KO_KR("ko_KR", IMPACT_REGULAR),
  /**
   * Latvian
   */
  LV_LV("lv_LV", OPENSANS_SEMIBOLD),
  /**
   * Lithuanian
   */
  LT_LT("lt_LT", OPENSANS_SEMIBOLD),
  /**
    * Mongolian
    */
  MN_MN("mn_MN", IMPACT_REGULAR),
  /**
   * Dutch
   */
  NL_NL("nl_NL", CORBEN_REGULAR),
  /**
   * Norwegian
   */
  NO_NO("no_NO", CORBEN_REGULAR),
  /**
   * Polish
   */
  PL_PL("pl_PL", OPENSANS_SEMIBOLD),
  /**
   * Portuguese (Brazil)
   */
  PT_BR("pt_BR", CORBEN_REGULAR),
  /**
   * Portuguese (Portugal)
   */
  PT_PT("pt_PT", CORBEN_REGULAR),
  /**
   * Romanian
   */
  RO_RO("ro_RO", OPENSANS_SEMIBOLD),
  /**
   * Russian
   */
  RU_RU("ru_RU", OPENSANS_SEMIBOLD),
  /**
   * Serbian (Latin)
   */
  SR_CS("sr_CS", OPENSANS_SEMIBOLD),
  /**
   * Slovak
   */
  SK_SK("sk_SK", OPENSANS_SEMIBOLD),
  /**
   * Slovene
   */
  SL_SI("sl_SI", OPENSANS_SEMIBOLD),
  /**
   * Swedish
   */
  SV_SV("sv_SV", CORBEN_REGULAR),
  /**
   * Swahili
   */
  SW_KE("sw_KE", IMPACT_REGULAR),
  /**
   * Tamil
   */
  TA_LK("ta_LK", IMPACT_REGULAR),
  /**
   * Thai
   */
  TH_TH("th_TH", IMPACT_REGULAR),
  /**
   * Tagalog
   */
  TL_PH("tl_PH", IMPACT_REGULAR),
  /**
   * Turkish
   */
  TR_TR("tr_TR", IMPACT_REGULAR),
  /**
   * Vietnamese
   */
  VI_VN("vi_VN", OPENSANS_SEMIBOLD),
  /**
   * Chinese (Simplified)
   */
  ZH_CN("zh_CN", IMPACT_REGULAR),
  /**
   * Chinese (Traditional)
   */
  ZH_TW("zh_TW", IMPACT_REGULAR),
  // End of enum
  ;

  private final String key;
  private final String countryCode;
  private final String languageCode;
  private final String languageName;
  private final Font titleFont;
  private ImageIcon icon;

  private LanguageKey(String key, Font titleFont) {

    this.titleFont = titleFont;

    // The bundle is cached by the JVM
    ResourceBundle rb = ResourceBundle.getBundle(Languages.BASE_NAME);

    this.key = key;
    this.languageCode = key.substring(0, 2);

    if (key.contains("_")) {
      this.countryCode = key.substring(3, 5);
    } else {
      this.countryCode = languageCode;
    }

    this.icon = Images.newLanguageCodeIcon(languageCode);
    this.languageName = rb.getString(key);
  }

  /**
   * <p>Reset the icons after a theme switch</p>
   */
  public static void resetIcons() {
    for (LanguageKey languageKey : values()) {
      languageKey.icon = Images.newLanguageCodeIcon(languageKey.languageCode);
    }
  }

  /**
   * @return The appropriate title font
   */
  public Font getTitleFont() {
    return titleFont;
  }

  /**
   * @return The key for use with the resource bundles
   */
  public String getKey() {
    return key;
  }

  /**
   * @return The icon for use with a list renderer
   */
  public ImageIcon getIcon() {
    return icon;
  }

  /**
   * @return The 2-letter country code (e.g. "UK")
   */
  public String getCountryCode() {
    return countryCode;
  }

  /**
   * @return The 2-letter language code (e.g. "en")
   */
  public String getLanguageCode() {
    return languageCode;
  }

  /**
   * @return The language name (e.g. "English (United Kingdom)")
   */
  public String getLanguageName() {
    return languageName;
  }

  /**
   * @param locale The locale providing at least language and region
   *
   * @return The matching language key, or the default EN_US since it is dominant on the internet
   */
  public static LanguageKey fromLocale(Locale locale) {

    // Ensure we use English rules for uppercase to identify enum keys
    // We use the legacy names for countries for consistency
    String language = locale.getLanguage().toUpperCase(Locale.ENGLISH);
    String country = locale.getCountry().toUpperCase(Locale.ENGLISH);
    String variant = locale.getVariant().toUpperCase(Locale.ENGLISH);

    String matcher1 = language + "_" + country + "_" + variant;
    String matcher2 = language + "_" + country;

    for (LanguageKey languageKey : values()) {

      // Language, country and variant
      if (languageKey.name().equals(matcher1)) {
        return languageKey;
      }

      // Language and country
      if (languageKey.name().equals(matcher2)) {
        return languageKey;
      }

      // At this point we match only on language (e.g. "EO" for Esperanto)
      // so that we don't introduce a country or region bias

      // Language only
      if (languageKey.name().equals(matcher2)) {
        return languageKey;
      }

    }

    // We have an unsupported locale so we use the first entry that matches
    // the supported language

    // Find the first entry with the supported language
    for (LanguageKey languageKey : values()) {

      if (languageKey.name().substring(0, 2).equals(language)) {
        return languageKey;
      }

    }

    // Unsupported language so default to EN_US since it is the dominant locale on the internet
    return LanguageKey.EN_US;

  }

  /**
   * @param languageName The language name (e.g. "English (United Kingdom)") as specified in the primary resource bundle
   *
   * @return The language key matching the language name
   */
  public static LanguageKey fromLanguageName(String languageName) {

    // Use the resource bundle translations
    for (LanguageKey languageKey : values()) {
      if (languageKey.getLanguageName().equalsIgnoreCase(languageName)) {
        return languageKey;
      }
    }

    // Unknown language name so fall back to Java locale lookup for current locale
    for (Locale locale : Locale.getAvailableLocales()) {
      if (locale.getDisplayName().equalsIgnoreCase(languageName)) {
        return fromLocale(locale);
      }
    }

    throw new IllegalArgumentException("'languageName' was not matched for '" + languageName + "'");
  }

  /**
   * @return The localised names of the languages in the order they are declared
   */
  public static String[] localisedNames() {

    String[] languageNames = new String[values().length];

    int i = 0;
    for (LanguageKey languageKey : values()) {
      languageNames[i] = languageKey.getLanguageName();
      i++;
    }

    return languageNames;
  }
}
