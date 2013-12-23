package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Buttons {

  public static final int NORMAL_ICON_SIZE = 20;
  public static final int LARGE_ICON_SIZE = 70;

  /**
   * Utilities have no public constructor
   */
  private Buttons() {
  }

  /**
   * @return A new JButton with default styling
   */
  public static JButton newButton(Action action) {

    // The action resets all text
    JButton button = new JButton(action);

    // Apply the current theme
    button.setForeground(Themes.currentTheme.text());
    button.setOpaque(true);

    // Reinforce the idea of clicking
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Create a flat appearance
    Border line = new LineBorder(Themes.currentTheme.infoAlertBorder());
    Border margin = new EmptyBorder(5, 15, 5, 15);
    Border compound = new CompoundBorder(line, margin);
    button.setBorder(compound);

    // Ensure we use the correct component orientation
    button.applyComponentOrientation(Languages.currentComponentOrientation());

    return button;
  }

  /**
   * @param key    The resource key for the i18n string
   * @param values The values to apply to the string (can be null)
   *
   * @return A new JButton with default styling
   */
  public static JButton newButton(Action action, MessageKey key, Object... values) {

    // The action resets all text
    JButton button = newButton(action);

    button.setText(Languages.safeText(key, values));

    return button;
  }

  /**
   * @param key    The resource key for the i18n string
   * @param values The values to apply to the string (can be null)
   *
   * @return A new JButton with default styling and text arranged below the icon
   */
  public static JButton newLargeButton(Action action, MessageKey key, Object... values) {

    JButton button = newButton(action, key, values);

    button.setVerticalTextPosition(SwingConstants.BOTTOM);
    button.setHorizontalTextPosition(SwingConstants.CENTER);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Yes" button with icon
   */
  public static JButton newYesButton(Action action) {

    JButton button = newButton(action, MessageKey.YES);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "No" button with icon
   */
  public static JButton newNoButton(Action action) {

    JButton button = newButton(action, MessageKey.NO);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, NORMAL_ICON_SIZE);

    return button;

  }


  /**
   * @param action The click action
   *
   * @return A new "Apply" button with icon
   */
  public static JButton newApplyButton(Action action) {

    JButton button = newButton(action, MessageKey.APPLY);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.EDIT, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Undo" button with icon
   */
  public static JButton newUndoButton(Action action) {

    JButton button = newButton(action, MessageKey.UNDO);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Cancel" button with icon
   */
  public static JButton newCancelButton(Action action) {

    JButton button = newButton(action, MessageKey.CANCEL);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Exit" button with icon
   */
  public static JButton newExitButton(Action action) {

    JButton button = newButton(action, MessageKey.EXIT);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.SIGN_OUT, button, true, NORMAL_ICON_SIZE);

    button.setBackground(Themes.currentTheme.dangerAlertBackground());

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Next" button with icon (not prefixed)
   */
  public static JButton newNextButton(Action action) {

    JButton button = newButton(action, MessageKey.NEXT);
    button.setAction(action);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ANGLE_DOUBLE_RIGHT, AwesomeIcon.ANGLE_DOUBLE_LEFT);

    AwesomeDecorator.applyIcon(icon, button, false, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Previous" button with icon
   */
  public static JButton newPreviousButton(Action action) {

    JButton button = newButton(action, MessageKey.PREVIOUS);
    button.setAction(action);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ANGLE_DOUBLE_LEFT, AwesomeIcon.ANGLE_DOUBLE_RIGHT);

    AwesomeDecorator.applyIcon(icon, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Finish" button with icon
   */
  public static JButton newFinishButton(Action action) {

    JButton button = newButton(action, MessageKey.FINISH);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.FLAG_CHECKERED, button, false, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Send" button with icon
   */
  public static JButton newSendButton(Action action) {

    // TODO New Danger button
    JButton button = newButton(action, MessageKey.SEND);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, false, NORMAL_ICON_SIZE);

    button.setBackground(Themes.currentTheme.dangerAlertBackground());

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Receive" button with icon
   */
  public static JButton newReceiveButton(Action action) {

    // TODO New Info button
    JButton button = newButton(action, MessageKey.RECEIVE);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true, NORMAL_ICON_SIZE);

    button.setBackground(Themes.currentTheme.infoAlertBackground());

    return button;
  }


  /**
   * @param action The click action
   *
   * @return A new "Refresh" button with icon
   */
  public static JButton newRefreshButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "show" button with icon
   */
  public static JButton newShowButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.EYE, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "hide" button with icon
   */
  public static JButton newHideButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.EYE_SLASH, button, true, NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Send Bitcoin" wizard button with icon
   */
  public static JButton newSendBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SEND);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, true, LARGE_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Receive Bitcoin" wizard button with icon
   */
  public static JButton newReceiveBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.RECEIVE);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true, LARGE_ICON_SIZE);

    return button;

  }
}
