package org.multibit.hd.ui.languages;

import org.multibit.hd.ui.views.components.Images;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

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
 *
 * @since 0.0.1
 *  
 */
public enum LanguageKey {

  AF_AF("af_AF"),
  AR_AR("ar_AR"),
  CS_CZ("cs_CZ"),
  DA_DK("da_DK"),
  DE_DE("de_DE"),
  EL_GR("el_GR"),
  EN_GB("en_GB"),
  EN_US("en_US"),
  EO("eo"), // Esperanto has no country
  ES_ES("es_ES"),
  FA_IR("fa_IR"),
  FI_FI("fi_FI"),
  FR_FR("fr_FR"),
  HI_IN("hi_IN"),
  HR_HR("hr_HR"),
  HU_HU("hu_HU"),
  IN_ID("in_ID"), // Legacy form of "id_ID" for Indonesian in Indonesia
  IW_IL("iw_IL"), // Legacy form of "he_IL" for Hebrew in Israel
  IT_IT("it_IT"),
  JA_JP("ja_JP"),
  KO_KR("ko_KR"),
  LV_LV("lv_LV"),
  NL_NL("nl_NL"),
  NO_NO("no_NO"),
  PL_PL("pl_PL"),
  PT_PT("pt_PT"),
  RO_RO("ro_RO"),
  RU_RU("ru_RU"),
  SK_SK("sk_SK"),
  SL_SI("sl_SI"),
  SV_SV("sv_SV"),
  SW_KE("sw_KE"),
  TA_LK("ta_LK"),
  TH_TH("th_TH"),
  TL_PH("tl_PH"),
  TR_TR("tr_TR"),
  VI_VN("vi_VN"),
  ZH_CN("zh_CN"),
  // End of enum
  ;

  private final String key;
  private final String countryCode;
  private final String languageCode;
  private final String languageName;
  private ImageIcon icon;

  private LanguageKey(String key) {

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

    for (LanguageKey languageKey : LanguageKey.values()) {

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
    for (LanguageKey languageKey : LanguageKey.values()) {

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

    String[] languageNames = new String[LanguageKey.values().length];

    int i = 0;
    for (LanguageKey languageKey : LanguageKey.values()) {
      languageNames[i] = languageKey.getLanguageName();
      i++;
    }

    return languageNames;
  }
}
