package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;
import org.multibit.hd.ui.views.themes.painters.NamedProgressBarRegionPainter;
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
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED_FOCUSED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_DISABLED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED));

    // Add the theme to the component
    pane.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
    pane.putClientProperty("Nimbus.Overrides", tabTheme);

  }

  /**
   * <p>Applies a theme color to a Nimbus button</p>
   *
   * @param button The button to be decorated
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

  /**
   * <p>Applies a theme color to a Nimbus progress bar</p>
   *
   * @param bar The progress bar to be decorated
   */
  public static void applyThemeColor(Color color, JProgressBar bar) {

    UIDefaults progressBarTheme = new UIDefaults();
    progressBarTheme.put("ProgressBar[Disabled+Finished].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.FOREGROUND_DISABLED_FINISHED));
    progressBarTheme.put("ProgressBar[Disabled+Indeterminate].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.FOREGROUND_DISABLED_INDETERMINATE));
    progressBarTheme.put("ProgressBar[Disabled].backgroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.BACKGROUND_DISABLED));
    progressBarTheme.put("ProgressBar[Disabled].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.BACKGROUND_DISABLED));
    progressBarTheme.put("ProgressBar[Enabled+Finished].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.FOREGROUND_ENABLED_FINISHED));
    progressBarTheme.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.FOREGROUND_ENABLED_INDETERMINATE));
    progressBarTheme.put("ProgressBar[Enabled].backgroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.BACKGROUND_ENABLED));
    progressBarTheme.put("ProgressBar[Enabled].foregroundPainter", new NamedProgressBarRegionPainter(color, NamedProgressBarRegionPainter.FOREGROUND_ENABLED));

    // Add the theme to the component
    bar.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
    bar.putClientProperty("Nimbus.Overrides", progressBarTheme);

  }

  /**
   * <p>Disables the background selection rendering in the Nimbus LAF</p>
   *
   * @param tree The JTree with the Nimbus LAF
   */
  public static void disableTreeViewSelection(JTree tree) {

    // Create a null painter
    Painter painter = new Painter() {
      public void paint(Graphics2D g, Object o, int w, int h) {
        // Do nothing
      }
    };

    UIDefaults treeTheme = new UIDefaults();
    treeTheme.put("Tree:TreeCell[Focused+Selected].backgroundPainter", painter);
    treeTheme.put("Tree:TreeCell[Enabled+Selected].backgroundPainter", painter);
    tree.putClientProperty("Nimbus.Overrides", treeTheme);

  }
}