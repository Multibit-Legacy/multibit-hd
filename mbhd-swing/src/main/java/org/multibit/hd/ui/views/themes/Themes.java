package org.multibit.hd.ui.views.themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>utility to provide the following to Views:</p>
 * <ul>
 * <li>Provision of a UI theme</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Themes {

  // TODO Link this to the configuration
  public static Theme currentTheme = new LightTheme();

  /**
   * Utilities do not have public constructors
   */
  private Themes() {
  }

  /**
   * @param font The font to use
   */
  public static void setDefaultFont(Font font) {

    UIManager.getLookAndFeelDefaults().put("defaultFont", font);

  }

}
