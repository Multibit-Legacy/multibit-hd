package org.multibit.hd.ui.views.themes;

import java.awt.*;

/**
 * <p>Strategy to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class LightTheme implements Theme {

  @Override
  public Color normalFontColor() {
    return new Color(6,6,6);
  }

  @Override
  public Color lightFontColor() {
    return new Color(96,96,96);
  }
}
