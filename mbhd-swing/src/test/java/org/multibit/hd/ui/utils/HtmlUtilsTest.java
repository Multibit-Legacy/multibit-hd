package org.multibit.hd.ui.utils;

import org.junit.After;
import org.junit.Test;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class HtmlUtilsTest {

  private Locale originalLocale = Locale.getDefault();

  @After
  public void tearDown() throws Exception {

    Locale.setDefault(originalLocale);

  }

  @Test
  public void testLocaliseWithCenteredLineBreaks() throws Exception {

    Locale.setDefault(Locale.UK);

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String[] lines = new String[] {

      "Line 1",
      "Line 2",
      "Line 3",

    };

    String expected = "<html><body style='width: 100%'><div align=center><p>Line 1</p><br><p>Line 2</p><br><p>Line 3</p></div></body></html>";
    assertThat(HtmlUtils.localiseWithCenteredLinedBreaks(lines)).isEqualTo(expected);

  }

  @Test
  public void testLocaliseWithLineBreaks_LTR() throws Exception {

    Locale.setDefault(Locale.UK);

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String[] lines = new String[] {

      "Line 1",
      "Line 2",
      "Line 3",

    };

    String expected = "<html><body style='width: 100%'><div align=left><p>Line 1</p><br><p>Line 2</p><br><p>Line 3</p></div></body></html>";
    assertThat(HtmlUtils.localiseWithLineBreaks(lines)).isEqualTo(expected);

  }

  @Test
  public void testLocaliseWithLineBreaks_RTL() throws Exception {

    Locale.setDefault(new Locale("ar","SA"));

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String[] lines = new String[] {

      "Line 1",
      "Line 2",
      "Line 3",

    };

    String expected = "<html><body style='width: 100%'><div align=right><p>Line 1</p><br><p>Line 2</p><br><p>Line 3</p></div></body></html>";
    assertThat(HtmlUtils.localiseWithLineBreaks(lines)).isEqualTo(expected);

  }

  @Test
  public void testApplyBoldFragments_NoFragment() throws Exception {

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String expected = "<html>hello world</html>";
    assertThat(HtmlUtils.applyBoldFragments("","hello world")).isEqualTo(expected);

  }

  @Test
  public void testApplyBoldFragments_VariableCase() throws Exception {

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String expected = "<html><b>abc</b> <b>ABC</b> <b>Abc</b> <b>AbC</b> <b>abC</b> hello<b>abc</b>world</html>";
    assertThat(HtmlUtils.applyBoldFragments("abc","abc ABC Abc AbC abC helloabcworld")).isEqualTo(expected);

  }

  @Test
  public void testApplyBoldFragments_NoMatch() throws Exception {

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String expected = "<html>hello world</html>";
    assertThat(HtmlUtils.applyBoldFragments("abc","hello world")).isEqualTo(expected);

  }

  @Test
  public void testApplyBoldFragments_MatchAtEnd() throws Exception {

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String expected = "<html>hello world<b>abc</b></html>";
    assertThat(HtmlUtils.applyBoldFragments("abc","hello worldabc")).isEqualTo(expected);

  }

  @Test
  public void testApplyBoldFragments_PartialMatches() throws Exception {

    // New Configuration relies on default locale
    Configurations.currentConfiguration = new Configuration();

    String expected = "<html>ab ab <b>abc</b><b>abc</b><b>abc</b><b>abc</b></html>";
    assertThat(HtmlUtils.applyBoldFragments("abc","ab ab abcabcabcabc")).isEqualTo(expected);

  }

}
