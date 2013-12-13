package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

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

  /**
   * Utilities have no public constructor
   */
  private Buttons() {
  }

  /**
   * @param key    The resource key for the i18n string
   * @param values The values to apply to the string (can be null)
   *
   * @return A new JButton with default styling
   */
  public static JButton newButton(MessageKey key, Object... values) {
    return new JButton(Languages.safeText(key, values));
  }

  /**
   * @param action The click action
   *
   * @return A new "Apply" button with icon
   */
  public static JButton newApplyButton(Action action) {

    JButton button = newButton(MessageKey.APPLY_BUTTON);
    button.setAction(action);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Undo" button with icon
   */
  public static JButton newUndoButton(Action action) {

    JButton button = newButton(MessageKey.UNDO_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, true);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Cancel" button with icon
   */
  public static JButton newCancelButton(Action action) {

    JButton button = newButton(MessageKey.CANCEL_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Exit" button with icon
   */
  public static JButton newExitButton(Action action) {

    JButton button = newButton(MessageKey.EXIT_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.SIGN_OUT, button, true);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Next" button with icon (not prefixed)
   */
  public static JButton newNextButton(Action action) {

    JButton button = newButton(MessageKey.NEXT_BUTTON);
    button.setAction(action);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ANGLE_DOUBLE_RIGHT, AwesomeIcon.ANGLE_DOUBLE_LEFT);

    AwesomeDecorator.applyIcon(icon, button, true);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Previous" button with icon
   */
  public static JButton newPreviousButton(Action action) {

    JButton button = newButton(MessageKey.PREVIOUS_BUTTON);
    button.setAction(action);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ANGLE_DOUBLE_LEFT, AwesomeIcon.ANGLE_DOUBLE_RIGHT);

    AwesomeDecorator.applyIcon(icon, button, true);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Finish" button with icon
   */
  public static JButton newFinishButton(Action action) {

    JButton button = newButton(MessageKey.FINISH_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.FLAG_CHECKERED, button, false);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Send" button with icon
   */
  public static JButton newSendButton(Action action) {

    // TODO New Danger button
    JButton button = newButton(MessageKey.SEND_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, false);

    button.setBackground(Themes.currentTheme.dangerBackground());

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Receive" button with icon
   */
  public static JButton newReceiveButton(Action action) {

    // TODO New Info button
    JButton button = newButton(MessageKey.RECEIVE_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true);

    button.setBackground(Themes.currentTheme.infoBackground());

    return button;
  }


  /**
   * @param action The click action
   *
   * @return A new "Send Bitcoin" wizard button with icon
   */
  public static JButton newSendBitcoinWizardButton(Action action) {

    // TODO New tool button
    JButton button = newButton(MessageKey.SEND_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, true);

    button.setBackground(Themes.currentTheme.dangerBackground());

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Receive Bitcoin" wizard button with icon
   */
  public static JButton newReceiveBitcoinWizardButton(Action action) {

    // TODO New tool button
    JButton button = newButton(MessageKey.RECEIVE_BUTTON);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true);

    button.setBackground(Themes.currentTheme.infoBackground());

    return button;

  }

}
