package org.multibit.hd.ui.views.fonts;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.exceptions.UIException;
import org.multibit.hd.ui.languages.LanguageKey;

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
 *
 */
public class TitleFontDecorator {

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
  public static final Font IMPACT_REGULAR = Font.decode("Impact").deriveFont(Font.PLAIN);

  /**
   * The currently selected font for the given locale (default is Impact Regular in case of problems)
   */
  private static Font TITLE_FONT = IMPACT_REGULAR;

  static {

    CORBEN_REGULAR = assignFont("Corben-Regular.ttf");
    OPENSANS_SEMIBOLD = assignFont("OpenSans-Semibold.ttf");
    //NOTOSANS_BOLD = assignFont("NotoSans-Bold.ttf");

  }

  /**
   * @param fontName The font name (e.g. "Corben-Regular.ttf") as found in <code>/assets/fonts</code>
   *
   * @return The derived font after registration
   */
  private static Font assignFont(String fontName) {

    try (InputStream in = TitleFontDecorator.class.getResourceAsStream("/assets/fonts/" + fontName)) {

      Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, in);

      Preconditions.checkNotNull(loadedFont, fontName + " not loaded");

      // Always stick with plain for best effect
      Font derivedFont = loadedFont.deriveFont(Font.PLAIN);

      // HTML tags won't use the font unless the graphics environment has registered it
      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .registerFont(derivedFont);

      return derivedFont;

    } catch (FontFormatException | IOException e) {
      throw new UIException(e);
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
