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
 * <li>Includes glyphs for extended Latin, Hebrew, Arabic and Hindi</li>
 * <li>Font name is supported on Windows, Mac and Linux</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TitleFontDecorator {

  /**
   * The Corben Regular font is good for Latin languages (North America, Western Europe)
   * and makes the application look attractive
   */
  public static final Font CORBEN_REGULAR;

  /**
   * The Impact font is found on Windows, Mac and Linux variants
   * It is a good fall back position when other fonts are not suitable
   */
  public static final Font IMPACT_REGULAR = Font.decode("Impact").deriveFont(Font.PLAIN);

  /**
   * The currently selected font for the given locale (default is Impact Regular in case of problems)
   */
  private static Font TITLE_FONT = IMPACT_REGULAR;

  static {

    try (InputStream in = TitleFontDecorator.class.getResourceAsStream("/assets/fonts/Corben-Regular.ttf")) {

      Font corbenRegular = Font.createFont(Font.TRUETYPE_FONT, in);

      Preconditions.checkNotNull(corbenRegular, "Corben Regular not loaded");

     CORBEN_REGULAR = corbenRegular.deriveFont(Font.PLAIN);

      // HTML tags won't use the font unless the graphics environment has registered it
     GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .registerFont(CORBEN_REGULAR);

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
