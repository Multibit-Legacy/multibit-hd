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
  IN_ID("in_ID"), // Legacy form of ID_ID
  IW_IL("iw_IL"), // Legacy form of HE_IL
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
   * @return the matching language key
   */
  public static LanguageKey fromLocale(Locale locale) {
    return valueOf(locale.toString().toUpperCase());
  }

  /**
   * @param languageName The language name (e.g. "English (United Kingdom)")
   *
   * @return The language key matching the language name
   */
  public static LanguageKey fromLanguageName(String languageName) {

    for (LanguageKey languageKey : values()) {
      if (languageKey.getLanguageName().equalsIgnoreCase(languageName)) {
        return languageKey;
      }
    }
    throw new IllegalArgumentException("'languageName' was not matched");
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
