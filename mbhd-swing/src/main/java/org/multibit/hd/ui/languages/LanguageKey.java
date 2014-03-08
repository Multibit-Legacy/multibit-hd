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

  AF_AF("af_af"),
  AR_AR("ar_ar"),
  CS_CZ("cs_cz"),
  DA_DK("da_dk"),
  DE_DE("de_de"),
  EL_GR("el_gr"),
  EN_GB("en_gb"),
  EO_ES("eo_es"),
  ES_ES("es_es"),
  FA_IR("fa_ir"),
  FI_FI("fi_fi"),
  FR_FR("fr_fr"),
  HI_IN("hi_in"),
  HR_HR("hr_hr"),
  HU_HU("hu_hu"),
  IN_ID("in_id"), // Legacy form of ID_ID
  IW_IL("iw_il"), // Legacy form of HE_IL
  IT_IT("it_it"),
  JA_JP("ja_jp"),
  KO_KR("ko_kr"),
  LV_LV("lv_lv"),
  NL_NL("nl_nl"),
  NO_NO("no_no"),
  PL_PL("pl_pl"),
  PT_PT("pt_pt"),
  RO_RO("ro_ro"),
  RU_RU("ru_ru"),
  SK_SK("sk_sk"),
  SL_SI("sl_si"),
  SV_SV("sv_sv"),
  SW_KE("sw_ke"),
  TA_LK("ta_lk"),
  TH_TH("th_th"),
  TL_PH("tl_ph"),
  TR_TR("tr_tr"),
  VI_VN("vi_vn"),
  ZH_CN("zh_cn"),
  // End of enum
  ;

  private final String key;
  private final String languageCode;
  private final String languageName;
  private ImageIcon icon;

  private LanguageKey(String key) {

    // The bundle is cached by the JVM
    ResourceBundle rb = ResourceBundle.getBundle(Languages.BASE_NAME);

    this.key = key;
    this.languageCode = key.substring(0, 2);
    this.icon = Images.newLanguageIcon(languageCode);
    this.languageName = rb.getString(key);
  }

  /**
   * <p>Reset the icons after a theme switch</p>
   */
  public static void resetIcons() {
    for (LanguageKey languageKey : values()) {
      languageKey.icon = Images.newLanguageIcon(languageKey.languageCode);
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
