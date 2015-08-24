package org.multibit.hd.ui.views.fonts;

import org.multibit.hd.ui.languages.LanguageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * <p>Decorator to provide the following to Swing components:</p>
 * <ul>
 * <li>Application of a generic title font</li>
 * <li>Series of fonts for different languages</li>
 * <li>Final fallback font is supported on Windows, Mac and Linux in all languages</li>
 * </ul>
 *
 * <p>More fonts can be sourced from Google Fonts and extracted from a ZIP file as a TTF</p>
 *
 * @since 0.0.1
 */
public class TitleFontDecorator {

  private static final Logger log = LoggerFactory.getLogger(TitleFontDecorator.class);

  /**
   * The Corben Regular font is good for a reduced set of Latin languages (North America, Western Europe)
   * and makes the application look attractive
   *
   * Supports Windows anti-aliasing
   */
  public static final Font CORBEN_REGULAR;

  /**
   * The OpenSans Regular font is good for all Latin languages (North America, Europe)
   * and is a good fallback from Corben
   *
   * Supports Windows anti-aliasing
   */
  public static final Font OPENSANS_SEMIBOLD;

  /**
   * The NotoSans Bold font is good for all Devangari languages (India, Nepal)
   * Currently elided due to its 415Kb payload
   */
  //public static final Font NOTOSANS_BOLD;

  /**
   * The Impact font is found on Windows, Mac and Linux variants
   * It is a good fall back position when other fonts are not suitable (such as Chinese, Tamil etc)
   *
   * DOES NOT SUPPORT ANTI-ALIASING ON WINDOWS
   */
  public static final Font IMPACT_REGULAR;

  /**
   * The currently selected font for the given locale
   */
  private static Font TITLE_FONT;

  static {
    Font font;
    try {
      // If "Impact" does not exist the "Dialog" font family will be returned
      font = Font.decode("Impact").deriveFont(Font.PLAIN);
    } catch (RuntimeException e) {
      // Something strange is going on so try a different approach
      log.warn("Could not load 'Impact' font. Using system default as a fallback.", e);
      // If this call fails with another RTE then the environment is too messed up to function
      // and we should rightly show an error report
      font = Font.decode(null);
    }
    // Impact regular must be defined first since it is used in assignFont
    IMPACT_REGULAR = font;

    // Register built-in fonts
    CORBEN_REGULAR = assignFont("Corben-Regular.ttf");
    OPENSANS_SEMIBOLD = assignFont("OpenSans-Semibold.ttf");
    //NOTOSANS_BOLD = assignFont("NotoSans-Bold.ttf");

    // Set default to Impact Regular in case of problems
    TITLE_FONT = IMPACT_REGULAR;
  }

  /**
   * @param fontName The font name (e.g. "Corben-Regular.ttf") as found in <code>/assets/fonts</code>
   *
   * @return The derived font after registration
   */
  private static Font assignFont(String fontName) {

    try (InputStream in = TitleFontDecorator.class.getResourceAsStream("/assets/fonts/" + fontName)) {

      // We'll either get a font or a failure
      Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, in);

      // Always stick with plain for best effect
      Font derivedFont = loadedFont.deriveFont(Font.PLAIN);

      // HTML tags won't use the font unless the graphics environment has registered it
      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .registerFont(derivedFont);

      return derivedFont;

    } catch (FontFormatException | IOException e) {
      log.warn("Failed to load font: '{}'", fontName, e);
      // Fall back to Impact Regular which is guaranteed to have something set to be here
      return IMPACT_REGULAR;
    }

  }

  /**
   * @param component The component to which the plain default Corben font will be applied
   * @param size      The size required (usually from MultiBitUI)
   */
  public static void apply(JComponent component, float size) {

    Font font = TITLE_FONT.deriveFont(size);
    component.setFont(font);

  }

  /**
   * Set the title font based on the given locale
   */
  public static synchronized void refresh(Locale locale) {

    TITLE_FONT = LanguageKey.fromLocale(locale).getTitleFont();

  }
}
