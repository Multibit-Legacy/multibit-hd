package org.multibit.hd.ui.views.components;

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
   * <p>Make the panel have the "danger" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyDanger(JPanel panel) {

    Color background = Themes.currentTheme.dangerBackground();
    Color border = Themes.currentTheme.dangerBorder();
    Color text = Themes.currentTheme.dangerText();

    apply(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "warning" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyWarning(JPanel panel) {

    Color background = Themes.currentTheme.warningBackground();
    Color border = Themes.currentTheme.warningBorder();
    Color text = Themes.currentTheme.warningText();

    apply(panel, background, border, text);

  }

  /**
   * <p>Make the panel have the "success" theme</p>
   *
   * @param panel The panel to decorate
   */
  public static void applySuccess(JPanel panel) {

    Color background = Themes.currentTheme.successBackground();
    Color border = Themes.currentTheme.successBorder();
    Color text = Themes.currentTheme.successText();

    apply(panel, background, border, text);

  }

  /**
   * <p>Make the panel invisible (invalidates errant pixels)</p>
   *
   * @param panel The panel to decorate
   */
  public static void applyHide(JPanel panel) {

    Color background = Themes.currentTheme.panelBackground();
    Color border = Themes.currentTheme.panelBackground();
    Color text = Themes.currentTheme.panelBackground();

    apply(panel, background, border, text);
  }

  /**
   * <p>Apply panel colours</p>
   *
   * @param panel      The target panel
   * @param background The background colour
   * @param border     The border colour
   * @param text       The text colour
   */
  private static void apply(JPanel panel, Color background, Color border, Color text) {

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
