package org.multibit.hd.ui.languages;

import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class LanguageKeyTest {

  @Test
  public void testFromLocale() throws Exception {

    // Supported locale
    Locale locale = Locale.UK;
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("EN_GB");

    // Unsupported country (expect first matching "EN")
    locale = new Locale("en","IE");
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("EN_GB");

    // Unsupported variant (expect first matching "NO")
    locale = new Locale("no","NO","NY");
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("NO_NO");

    // Different uppercase rules
    locale = new Locale("tr");
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("TR_TR");

    // Updated ISO 639 country code
    locale = new Locale("in","ID");
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("IN_ID");

    locale = new Locale("id","ID");
    assertThat(LanguageKey.fromLocale(locale).name()).isEqualTo("IN_ID");

  }

  @Test
  public void testFromLanguageName() throws Exception {

    // Supported locale
    Locale locale = Locale.UK;
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("EN_GB");

    // Unsupported country (expect first matching "EN")
    locale = new Locale("en","IE");
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("EN_GB");

    // Unsupported variant (expect first matching "NO")
    locale = new Locale("no","NO","NY");
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("NO_NO");

    // Different uppercase rules
    locale = new Locale("tr");
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("TR_TR");

    // Updated ISO 639 country code
    locale = new Locale("in","ID");
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("IN_ID");

    locale = new Locale("id","ID");
    assertThat(LanguageKey.fromLanguageName(locale.getDisplayName()).name()).isEqualTo("IN_ID");

  }

}