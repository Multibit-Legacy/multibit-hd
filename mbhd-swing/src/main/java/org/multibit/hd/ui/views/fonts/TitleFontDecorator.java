package org.multibit.hd.ui.views.fonts;

import javax.swing.*;
import java.awt.*;

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
   * The Impact font is found on Windows, Mac and Linux variants
   */
  public static Font FONT = Font.decode("Impact").deriveFont(Font.BOLD);

  /**
   * @param component The component to which the plain default Corben font will be applied
   * @param size      The size required (usually from MultiBitUI)
   */
  public static void apply(JComponent component, float size) {

    Font font = FONT.deriveFont(size);
    component.setFont(font);

  }

}
