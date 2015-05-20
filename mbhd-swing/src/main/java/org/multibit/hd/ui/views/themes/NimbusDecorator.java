package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.*;

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
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Disabled].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_DISABLED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Disabled+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_DISABLED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Focused+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_FOCUSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Focused+MouseOver+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"[Focused+Pressed+Selected].backgroundPainter", new NamedTabbedPaneTabPainter(color, NamedTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED_FOCUSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"Area[Disabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_DISABLED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"Area[Enabled+MouseOver].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_MOUSEOVER));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"Area[Enabled+Pressed].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_PRESSED));
    tabTheme.put("ComboBox:\"ComboBox.arrowButton\"Area[Enabled].backgroundPainter", new NamedTabbedPaneTabAreaPainter(color, NamedTabbedPaneTabAreaPainter.BACKGROUND_ENABLED));

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

    buttonTheme.put("Button[Default].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_DEFAULT));
    buttonTheme.put("Button[Default+Focused].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_DEFAULT_FOCUSED));
    buttonTheme.put("Button[Default+MouseOver].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_MOUSEOVER_DEFAULT));
    buttonTheme.put("Button[Default+Focused+MouseOver].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED));
    buttonTheme.put("Button[Default+Pressed].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_PRESSED_DEFAULT));
    buttonTheme.put("Button[Default+Focused+Pressed].backgroundPainter", new NamedButtonRegionPainter(color, NamedButtonRegionPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED));

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
   * <p>Applies a theme color to a Nimbus combo box</p>
   *
   * @param comboBox The combo box to be decorated
   */
  public static void applyThemeColor(Color color, JComboBox comboBox) {

    UIDefaults comboBoxButtonTheme = new UIDefaults();
    comboBoxButtonTheme.put("ComboBox[Disabled].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_DISABLED));
    comboBoxButtonTheme.put("ComboBox[Disabled+Pressed].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_DISABLED_PRESSED));
    comboBoxButtonTheme.put("ComboBox[Enabled].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_ENABLED));
    comboBoxButtonTheme.put("ComboBox[Focused].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_FOCUSED));
    comboBoxButtonTheme.put("ComboBox[Focused+MouseOver].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_MOUSEOVER_FOCUSED));
    comboBoxButtonTheme.put("ComboBox[MouseOver].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_MOUSEOVER));
    comboBoxButtonTheme.put("ComboBox[Focused+Pressed].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_PRESSED_FOCUSED));
    comboBoxButtonTheme.put("ComboBox[Pressed].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_PRESSED));
    comboBoxButtonTheme.put("ComboBox[Enabled+Selected].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_ENABLED_SELECTED));
    comboBoxButtonTheme.put("ComboBox[Disabled+Editable].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_DISABLED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox[Editable+Enabled].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_ENABLED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox[Editable+Focused].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_FOCUSED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox[Editable+MouseOver].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_MOUSEOVER_EDITABLE));
    comboBoxButtonTheme.put("ComboBox[Editable+Pressed].backgroundPainter", new NamedComboBoxPainter(color, NamedComboBoxPainter.BACKGROUND_PRESSED_EDITABLE));

    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Disabled+Editable].backgroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.BACKGROUND_DISABLED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.BACKGROUND_ENABLED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.BACKGROUND_MOUSEOVER_EDITABLE));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.BACKGROUND_PRESSED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.BACKGROUND_SELECTED_EDITABLE));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.FOREGROUND_ENABLED));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.FOREGROUND_MOUSEOVER));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.FOREGROUND_DISABLED));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.FOREGROUND_PRESSED));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter", new NamedComboBoxArrowButtonPainter(color, NamedComboBoxArrowButtonPainter.FOREGROUND_SELECTED));

    comboBoxButtonTheme.put("ComboBox:\"ComboBox.textField\"[Disabled].backgroundPainter", new NamedComboBoxTextFieldPainter(color, NamedComboBoxTextFieldPainter.BACKGROUND_DISABLED));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", new NamedComboBoxTextFieldPainter(color, NamedComboBoxTextFieldPainter.BACKGROUND_ENABLED));
    comboBoxButtonTheme.put("ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", new NamedComboBoxTextFieldPainter(color, NamedComboBoxTextFieldPainter.BACKGROUND_SELECTED));

    // Add the theme to the component
    comboBox.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
    comboBox.putClientProperty("Nimbus.Overrides", comboBoxButtonTheme);

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