package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class RadioButtons {

  /**
   * Utilities have no public constructor
   */
  private RadioButtons() {
  }

  /**
   * @param key    The resource key for the language string
   * @param values The values to apply to the string (can be null)
   *
   * @return A new JButton with default styling
   */
  public static JRadioButton newRadioButton(MessageKey key, Object... values) {

    JRadioButton radio = new JRadioButton();
    radio.setText(Languages.safeText(key, values));

    // Ensure it is accessible
    AccessibilityDecorator.apply(radio, key);

    // Apply the current theme
    radio.setForeground(Themes.currentTheme.text());

    // Reinforce the idea of clicking
    radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Ensure we use the correct component orientation
    radio.applyComponentOrientation(Languages.currentComponentOrientation());

    return radio;
  }

  /**
   * @param key    The resource key for the language string
   * @param values The values to apply to the string (can be null)
   *
   * @return A new JButton with default styling
   */
  public static JRadioButton newRadioButton(ActionListener listener, MessageKey key, Object... values) {

    JRadioButton radio = newRadioButton(key, values);

    // Add the listener at the end to avoid early events
    radio.addActionListener(listener);

    return radio;
  }

}
