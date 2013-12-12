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
   * @param awesomeIcon The awesome icon reference
   *
   * @return The icon
   */
  public static Icon createIcon(AwesomeIcon awesomeIcon) {

    return new AwesomeSwingIcon(new JLabel(), awesomeIcon.getChar());
  }

  /**
   * @param icon The font awesome icon
   *
   * @return A JLabel containing the icon with blank text using standard prefixing
   */
  public static JLabel createIconLabel(AwesomeIcon icon) {

    return createIconLabel(icon, "", true);

  }

  /**
   * @param icon     The font awesome icon
   * @param text     The text
   * @param prefixed True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   *
   * @return A suitable button
   */
  public static JLabel createIconLabel(AwesomeIcon icon, String text, boolean prefixed) {

    JLabel label = new JLabel();

    Icon swingIcon = new AwesomeSwingIcon(label, icon.getChar());

    label.setText(text);
    label.setIcon(swingIcon);

    if (!prefixed) {
      // Icon presentation is opposite to standard layout
      label.setHorizontalTextPosition(SwingConstants.LEADING);
    }

    return label;
  }

  /**
   * @param icon     The font awesome icon
   * @param text     The text
   * @param prefixed True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   * @param action   The action
   *
   * @return A suitable button
   */
  public static JButton createIconButton(AwesomeIcon icon, String text, boolean prefixed, Action action) {

    JButton button = new JButton(action);

    Icon swingIcon = new AwesomeSwingIcon(button, icon.getChar());

    button.setText(text);
    button.setIcon(swingIcon);

    if (!prefixed) {
      // Icon presentation is opposite to standard layout
      button.setHorizontalTextPosition(SwingConstants.LEADING);
    }

    return button;
  }

  /**
   * <p>Apply an icon to a label. Both icon states (enabled/disabled) will be added.</p>
   *
   * @param icon     The icon reference
   * @param label    The label
   * @param prefixed True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   */
  public static void applyIcon(AwesomeIcon icon, JLabel label, boolean prefixed) {

    Icon enabledIcon = new AwesomeSwingIcon(label, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(label, icon.getChar());

    label.setIcon(enabledIcon);
    label.setDisabledIcon(disabledIcon);

    if (!prefixed) {
      // Icon presentation is opposite to standard layout
      label.setHorizontalTextPosition(SwingConstants.LEADING);
    }

  }

  /**
   * <p>Apply an icon to a button. Both icon states (enabled/disabled) will be added.</p>
   *
   * @param icon     The icon reference
   * @param button   The button
   * @param prefixed True if the icon comes before the text in the reading direction (LTR and RTL is handled automatically)
   */

  public static void applyIcon(AwesomeIcon icon, JButton button, boolean prefixed) {

    Icon enabledIcon = new AwesomeSwingIcon(button, icon.getChar());
    Icon disabledIcon = new AwesomeSwingIcon(button, icon.getChar());

    button.setIcon(enabledIcon);
    button.setDisabledIcon(disabledIcon);

    if (!prefixed) {
      // Icon presentation is opposite to standard layout
      button.setHorizontalTextPosition(SwingConstants.LEADING);
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