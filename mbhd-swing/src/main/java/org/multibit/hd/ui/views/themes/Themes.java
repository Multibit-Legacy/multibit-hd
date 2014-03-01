package org.multibit.hd.ui.views.themes;

import com.google.common.base.Preconditions;

import javax.swing.*;

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

  public static Theme currentTheme;

  static {

    // TODO Link this to the configuration
    // Always switch into the configured theme at startup
    switchTheme(new LightTheme());
    //switchTheme(new DarkTheme());

  }

  /**
   * Utilities do not have public constructors
   */
  private Themes() {
  }

  public static synchronized void switchTheme(Theme newTheme) {

    Preconditions.checkNotNull(newTheme, "'newTheme' must be present");

    currentTheme = newTheme;

    // Gets used in combo box borders and provides the basis for a "default button"
    UIManager.put("nimbusBase", currentTheme.readOnlyComboBox());

    // Provides basis for text
    UIManager.put("nimbusBlueGrey", currentTheme.fadedText());

    UIManager.put("nimbusBorder", currentTheme.text());
    UIManager.put("nimbusDisabledText", currentTheme.fadedText());

    UIManager.put("nimbusLightBackground", currentTheme.sidebarPanelBackground());

    UIManager.put("nimbusFocus", currentTheme.infoAlertBorder());

    UIManager.put("nimbusSelectedText", currentTheme.infoAlertText());
    UIManager.put("nimbusSelection", currentTheme.infoAlertBackground());
    UIManager.put("nimbusSelectionBackground", currentTheme.infoAlertBackground());

  }

}
