package org.multibit.hd.ui.views.fonts;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.exceptions.UIException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Decorator to provide the following to Swing components:</p>
 * <ul>
 * <li>Application of Corben font</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CorbenDecorator {

  public static Font CORBEN_FONT;

  static {

    try (InputStream in = CorbenDecorator.class.getResourceAsStream("/Corben-Regular.ttf")) {

      CORBEN_FONT = Font.createFont(Font.TRUETYPE_FONT, in);

      Preconditions.checkNotNull(CORBEN_FONT, "Corben not loaded");

      CORBEN_FONT = CORBEN_FONT.deriveFont(Font.PLAIN, MultiBitUI.NORMAL_ICON_SIZE);

    } catch (FontFormatException | IOException e) {
      throw new UIException(e);
    }

  }

  /**
   * @param component The component to which the plain default Corben font will be applied
   * @param size      The size required (usually from MultiBitUI)
   */
  public static void apply(JComponent component, float size) {

    Font font = CORBEN_FONT.deriveFont(size);
    component.setFont(font);

  }

}
