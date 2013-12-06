package org.multibit.hd.ui.views.themes;

import java.awt.*;

/**
 * <p>Strategy to provide the following to themes:</p>
 * <ul>
 * <li>Various accessor methods</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public interface Theme {

  /**
   * @return The normal font colour
   */
  public Color normalFontColor();

  /**
   * @return A lighter version of the normal font colour
   */
  public Color lightFontColor();
}
