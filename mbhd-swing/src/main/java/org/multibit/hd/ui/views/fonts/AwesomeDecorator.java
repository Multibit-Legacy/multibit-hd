package org.multibit.hd.ui.views.fonts;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.exceptions.UIException;
import org.multibit.hd.ui.i18n.Languages;

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

  public static final int NORMAL_ICON_SIZE = 20;
  public static final int LARGE_ICON_SIZE = 70;

  public static Font AWESOME_FONT;

  static {

    try (InputStream in = AwesomeDecorator.class.getResourceAsStream("/FontAwesome.ttf")) {

      AWESOME_FONT = Font.createFont(Font.TRUETYPE_FONT, in);

      Preconditions.checkNotNull(AWESOME_FONT, "'awesome' font not loaded");

      AWESOME_FONT = AWESOME_FONT.deriveFont(25.0f);

    } catch (FontFormatException | IOException e) {
      throw new UIException(e);
    }

  }

  /**
   * <p>Create an icon of the given size</p>
   *
   * @param awesomeIcon The awesome icon reference
   * @param color       The icon color
   * @param size        The icon size (font metrics)
   *
   * @return The icon
   */
  public static Icon createIcon(AwesomeIcon awesomeIcon, Color color, int size) {

    JLabel iconLabel = new JLabel();
    iconLabel.setFont(iconLabel.getFont().deriveFont((float) size));
    iconLabel.setForeground(color);

    return new AwesomeSwingIcon(iconLabel, awesomeIcon.getChar());
  }

  /**
   * <p>Apply an icon to a label. Both icon states (enabled/disabled) will be added.</p>
   * <p>The icon will use the font and color from the label, but <strong>will not</strong> reflect any subsequent updates.</p>
   *
   * @param icon    The icon reference
   * @param label   The label
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   * @param size    The icon size (font metrics)
   */
  public static void applyIcon(AwesomeIcon icon, JLabel label, boolean leading, int size) {

    JLabel iconLabel = new JLabel();
    iconLabel.setFont(iconLabel.getFont().deriveFont((float) size));
    iconLabel.setForeground(label.getForeground());

    Icon enabledIcon = new AwesomeSwingIcon(iconLabel, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(iconLabel, icon.getChar(), true);

    label.setIcon(enabledIcon);
    label.setDisabledIcon(disabledIcon);

    align(label, leading);

  }

  /**
   * <p>Apply an icon to a label. Both icon states (enabled/disabled) will be added.</p>
   * <p>The icon will use the font and color from the label and will reflect any subsequent updates.</p>
   *
   * @param icon    The icon reference
   * @param label   The label (iconography will change will changes to the label color and font)
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   * @param size    The icon size (font metrics)
   */
  public static void bindIcon(AwesomeIcon icon, JLabel label, boolean leading, int size) {

    Icon enabledIcon = new AwesomeSwingIcon(label, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(label, icon.getChar(), true);

    label.setIcon(enabledIcon);
    label.setDisabledIcon(disabledIcon);

    align(label, leading);

  }

  /**
   * <p>Apply an icon to a button. Both icon states (enabled/disabled) will be added.</p>
   * <p>The icon will use the font and color from the button, but <strong>will not</strong> reflect any subsequent updates.</p>
   *
   * @param icon    The icon reference
   * @param button  The button
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   * @param size    The icon size (font metrics)
   */

  public static void applyIcon(AwesomeIcon icon, JButton button, boolean leading, int size) {

    JButton iconButton = new JButton();
    iconButton.setFont(iconButton.getFont().deriveFont((float) size));
    iconButton.setForeground(button.getForeground());

    Icon enabledIcon = new AwesomeSwingIcon(iconButton, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(iconButton, icon.getChar(), true);

    button.setIcon(enabledIcon);
    button.setDisabledIcon(disabledIcon);

    align(button, leading);

  }

  /**
   * <p>Apply an icon to a button. Both icon states (enabled/disabled) will be added.</p>
   * <p>The icon will use the font and color from the button and will reflect any subsequent updates.</p>
   *
   * @param icon    The icon reference
   * @param button  The button (iconography will change will changes to the label color and font)
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   * @param size    The icon size (font metrics)
   */
  public static void bindIcon(AwesomeIcon icon, JButton button, boolean leading, int size) {

    Icon enabledIcon = new AwesomeSwingIcon(button, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(button, icon.getChar(), true);

    button.setIcon(enabledIcon);
    button.setDisabledIcon(disabledIcon);

    align(button, leading);

  }

  /**
   * <p>Align the icon according to the locale (leading or trailing)</p>
   *
   * @param button  The button (iconography will change will changes to the label color and font)
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   */

  private static void align(JButton button, boolean leading) {

    button.applyComponentOrientation(ComponentOrientation.getOrientation(Languages.currentLocale()));

    if (leading) {
      // Text trails the icon in LTR
      button.setHorizontalTextPosition(SwingConstants.TRAILING);
    } else {
      button.setHorizontalTextPosition(SwingConstants.LEADING);
    }

  }

  /**
   * <p>Align the icon according to the locale (leading or trailing)</p>
   *
   * @param label   The label (iconography will change will changes to the label color and font)
   * @param leading True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   */

  private static void align(JLabel label, boolean leading) {

    label.applyComponentOrientation(ComponentOrientation.getOrientation(Languages.currentLocale()));

    if (leading) {
      // Text trails the icon in LTR
      label.setHorizontalTextPosition(SwingConstants.TRAILING);
    } else {
      label.setHorizontalTextPosition(SwingConstants.LEADING);
    }

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

  /**
   * <p>Select between the LTR and RTL icon forms depending on component orientation</p>
   *
   * @param ltrIcon The left-to-right icon
   * @param rtlIcon The right-to-left icon
   *
   * @return The appropriate icon based on the current locale
   */
  public static AwesomeIcon select(AwesomeIcon ltrIcon, AwesomeIcon rtlIcon) {

    if (ComponentOrientation.getOrientation(Languages.currentLocale()).isLeftToRight()) {
      return ltrIcon;
    }

    return rtlIcon;
  }

}