package org.multibit.hd.ui.views.themes;

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
  public static Theme theme = new LightTheme();

  /**
   * Utilities do not have public constructors
   */
  private Themes() {
  }

  public static class H1 {

    public static Color foreground = theme.fontColor();
  }

  public static class LIGHT_BOX {

    public static Color foreground = theme.fontColor();

  }
}
