package org.multibit.hd.ui.views.themes;

import java.awt.*;

/**
 * <p>Abstract base class to provide the following to themes:</p>
 * <ul>
 * <li>Access to common values</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class BaseTheme implements Theme {

  @Override
  public Color creditText() {
    // The status color value provides suitable contrast across all themes
    return statusGreen();
  }

  @Override
  public Color debitText() {
    // The status color value provides suitable contrast across all themes
    return statusRed();
  }

  @Override
  public Color statusRed() {
    return new Color(210, 50, 45);
  }

  @Override
  public Color statusAmber() {
    return new Color(237, 156, 40);
  }

  @Override
  public Color statusGreen() {
    return new Color(71, 164, 71);
  }

  @Override
  public Color tableRowBackground() {
    return sidebarPanelBackground();
  }

  @Override
  public Color tableRowAltBackground() {
    return detailPanelBackground();
  }

  @Override
  public Color tableRowSelectedBackground() {
    return sidebarSelectedText();
  }

}
