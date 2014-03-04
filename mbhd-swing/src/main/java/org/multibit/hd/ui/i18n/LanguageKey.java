package org.multibit.hd.ui.i18n;

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

  EN_GB("en_gb"),
  ES_ES("es_es"),
  RU_RU("ru_ru"),
  SV_SV("sv_sv"),
  NO_NO("no_no"),
  IT_IT("it_it"),
  FR_FR("fr_fr"),
  DE_DE("de_de"),
  PT_PT("pt_pt"),
  HI_IN("hi_in"),
  TH_TH("th_th"),
  NL_NL("nl_nl"),
  ZH_CN("zh_cn"),
  JA_JP("ja_jp"),
  AR_AR("ar_ar"),
  KO_KR("ko_kr"),
  LV_LV("lv_lv"),
  TR_TR("tr_tr"),
  FI_FI("fi_fi"),
  PL_PL("pl_pl"),
  HR_HR("hr_hr"),
  HU_HU("hu_hu"),
  EL_GR("el_gr"),
  DA_DK("da_dk"),
  EO_ES("eo_es"),
  FA_IR("fa_ir"),
  HE_IL("he_il"),
  SK_SK("sk_sk"),
  CS_CZ("cs_cz"),
  ID_ID("id_id"),
  SL_SI("sl_si"),
  TA_LK("ta_lk"),
  RO_RO("ro_ro"),
  AF_AF("af_af"),
  TL_PH("tl_ph"),
  SW_KE("sw_ke"),

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
    this.languageCode = key.substring(0, 2).toUpperCase();
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
}
