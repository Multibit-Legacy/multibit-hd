package org.multibit.hd.ui.views.components;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Decorator to provide the following to panels:</p>
 * <ul>
 * <li>Application of various themed styles</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class PanelDecorator {

  /**
   * <p>Create the standard "wizard" theme</p>
   *
   * @param panel The panel to decorate (contains wizard components at the top and buttons at the bottom)
   * @param wizardComponents  The wizard components arranged in a panel
   */
  public static void applyWizardTheme(JPanel panel, JPanel wizardComponents) {

    // Standard wizard layout
    MigLayout layout = new MigLayout(
      "fill", // Layout constrains
      "[]", // Column constraints
      "[grow]10[shrink]" // Row constraints
    );

    panel.setLayout(layout);

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Add the wizard components
    panel.add(wizardComponents, "span 4,grow,wrap");

  }

  /**
   * <p>Make the panel have the "danger" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyDangerTheme(JPanel panel) {

    Color background = Themes.currentTheme.dangerBackground();
    Color border = Themes.currentTheme.dangerBorder();
    Color text = Themes.currentTheme.dangerText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "warning" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyWarningTheme(JPanel panel) {

    Color background = Themes.currentTheme.warningBackground();
    Color border = Themes.currentTheme.warningBorder();
    Color text = Themes.currentTheme.warningText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "success" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applySuccessTheme(JPanel panel) {

    Color background = Themes.currentTheme.successBackground();
    Color border = Themes.currentTheme.successBorder();
    Color text = Themes.currentTheme.successText();

    applyTheme(panel, background, border, text);

  }

  /**
   * <p>Apply panel colours</p>
   *
   * @param panel      The target panel
   * @param background The background colour
   * @param border     The border colour
   * @param text       The text colour
   */
  private static void applyTheme(JPanel panel, Color background, Color border, Color text) {

    panel.setBackground(background);
    panel.setForeground(text);

    // Use a simple rounded border
    panel.setBorder(new TextBubbleBorder(border));

    for (Component component : panel.getComponents()) {
      if (component instanceof JLabel) {
        component.setForeground(text);
      }
    }

  }
}
