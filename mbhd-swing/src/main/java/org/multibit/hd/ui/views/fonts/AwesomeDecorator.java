package org.multibit.hd.ui.views.fonts;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.exceptions.UIException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Decorator to provide the following to UI controllers:</p>
 * <ul>
 * <li>Apply Font Awesome iconography to various Swing components</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AwesomeDecorator {

  private final static String DEFAULT_ICON_SIZE = "20.0";
  private final static String DEFAULT_FONT_SIZE = "1em";

  public static Font AWESOME_FONT;

  static {

    try (InputStream in = AwesomeDecorator.class.getResourceAsStream("/FontAwesome.ttf")) {

      AWESOME_FONT = Font.createFont(Font.TRUETYPE_FONT, in);

      Preconditions.checkNotNull(AWESOME_FONT, "'awesome' font not loaded");

      AWESOME_FONT = AWESOME_FONT.deriveFont(16.0f);

    } catch (FontFormatException | IOException e) {
      throw new UIException(e);
    }

  }

  /**
   * @param icon The font awesome icon
   *
   * @return A JLabel containing the icon with blank text
   */
  public static JLabel createIconLabel(AwesomeIcon icon) {

    return createIconLabel(icon, "");

  }

  /**
   * @param icon The font awesome icon
   * @param text The text
   *
   * @return A suitable button
   */
  public static JLabel createIconLabel(AwesomeIcon icon, String text) {

    JLabel label = new JLabel();

    Icon swingIcon = new AwesomeSwingIcon(label, icon.getChar());

    label.setText(text);
    label.setIcon(swingIcon);

    return label;
  }

  /**
   * @param icon   The font awesome icon
   * @param text   The text
   * @param action The action
   *
   * @return A suitable button
   */
  public static JButton createIconButton(AwesomeIcon icon, String text, Action action) {

    JButton button = new JButton(action);

    Icon swingIcon = new AwesomeSwingIcon(button, icon.getChar());

    button.setText(text);
    button.setIcon(swingIcon);

    return button;
  }

  /**
   * <p>Apply an icon to a label. Both icon states (enabled/disabled) will be added.</p>
   *
   * @param icon  The icon reference
   * @param label The label
   */
  public static void applyIcon(AwesomeIcon icon, JLabel label) {

    Icon enabledIcon = new AwesomeSwingIcon(label, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(label, icon.getChar());

    label.setIcon(enabledIcon);
    label.setDisabledIcon(disabledIcon);

  }

  /**
   * <p>Apply an icon to a button. Both icon states (enabled/disabled) will be added.</p>
   *
   * @param icon   The icon reference
   * @param button The button
   */
  public static void applyIcon(AwesomeIcon icon, JButton button) {

    Icon enabledIcon = new AwesomeSwingIcon(button, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(button, icon.getChar());

    button.setIcon(enabledIcon);
    button.setDisabledIcon(disabledIcon);

  }

  /**
   * <p>Remove an icon from a label. Both icon states (enabled/disabled) will be removed.</p>
   *
   * @param label The label
   */
  public static void removeIcon(JLabel label) {
    label.setIcon(null);
    label.setDisabledIcon(null);
  }

  /**
   * <p>Remove an icon from a button. Both icon states (enabled/disabled) will be removed.</p>
   *
   * @param button The label
   */
  public static void removeIcon(JButton button) {
    button.setIcon(null);
    button.setDisabledIcon(null);
  }
}