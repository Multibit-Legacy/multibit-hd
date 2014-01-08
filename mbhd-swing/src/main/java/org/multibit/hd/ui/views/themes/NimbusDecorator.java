package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;
import org.multibit.hd.ui.views.themes.painters.NamedTabbedPaneTabAreaPainter;
import org.multibit.hd.ui.views.themes.painters.NamedTabbedPaneTabPainter;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Decorator to provide the following to panels:</p>
 * <ul>
 * <li>Application of various themed styles to panels</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class NimbusDecorator {

  /**
   * Utilities have a private constructor
   */
  private NimbusDecorator() {
  }

  /**
   * <p>Applies a theme color to a Nimbus tabbed pane</p>
   *
   * @param pane The tabbed pane to be decorated
   */
  public static void applyThemeColor(Color color, JTabbedPane pane) {

    UIDefaults tabTheme = new UIDefaults();
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_ENABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color,NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color,NamedTabbedPaneTabAreaPainter.BACKGROUND_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color,NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color,NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color,NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED));

    // Add the theme to the component
    pane.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
    pane.putClientProperty("Nimbus.Overrides", tabTheme);

  }

  /**
   * <p>Applies a theme color to a Nimbus button</p>
   *
   * @param button The tabbed pane to be decorated
   */
  public static void applyThemeColor(Color color, JButton button) {

    UIDefaults buttonTheme = new UIDefaults();
    buttonTheme.put("Button[Disabled].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_DISABLED));
    buttonTheme.put("Button[Enabled].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_ENABLED));
    buttonTheme.put("Button[Focused].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_FOCUSED));
    buttonTheme.put("Button[MouseOver].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_MOUSEOVER));
    buttonTheme.put("Button[Focused+MouseOver].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_MOUSEOVER_FOCUSED));
    buttonTheme.put("Button[Pressed].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_PRESSED));
    buttonTheme.put("Button[Focused+Pressed].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_PRESSED_FOCUSED));

    // Add the theme to the component
    button.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
    button.putClientProperty("Nimbus.Overrides", buttonTheme);

  }

}