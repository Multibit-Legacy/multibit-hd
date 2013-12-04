package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

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

  private static final String APPLY_BUTTON = "showPreferencesSubmitAction.text";
  private static final String UNDO_BUTTON = "undoPreferencesChangesSubmitAction.text";
  private static final String CANCEL_BUTTON = "Cancel";
  private static final String NEXT_BUTTON = "Next";
  private static final String PREVIOUS_BUTTON = "Previous";
  private static final String EXIT_BUTTON = "Exit";
  private static final String SEND_BUTTON = "Send";
  private static final String RECEIVE_BUTTON = "Receive";

  /**
   * Utilities have no public constructor
   */
  private Buttons() {
  }

  /**
   * @param action The click action
   *
   * @return A new "Apply" button with icon
   */
  public static JButton newApplyButton(Action action) {

    return AwesomeDecorator.createIconButton(
      AwesomeIcon.ARROW_RIGHT,
      Languages.safeText(APPLY_BUTTON),
      action
    );

  }

  /**
   * @param action The click action
   *
   * @return A new "Undo" button with icon
   */
  public static JButton newUndoButton(Action action) {

    return AwesomeDecorator.createIconButton(
      AwesomeIcon.UNDO,
      Languages.safeText(UNDO_BUTTON),
      action
    );

  }

  /**
   * @param action The click action
   *
   * @return A new "Cancel" button with icon
   */
  public static JButton newCancelButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.TIMES,
      Languages.safeText(CANCEL_BUTTON),
      action
    );
  }

  /**
   * @param action The click action
   *
   * @return A new "Exit" button with icon
   */
  public static JButton newExitButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.SIGN_OUT,
      Languages.safeText(EXIT_BUTTON),
      action
    );
  }

  /**
   * @param action The click action
   *
   * @return A new "Next" button with icon
   */
  public static JButton newNextButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.ANGLE_DOUBLE_RIGHT,
      Languages.safeText(NEXT_BUTTON),
      action
    );
  }

  /**
   * @param action The click action
   *
   * @return A new "Previous" button with icon
   */
  public static JButton newPreviousButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.ANGLE_DOUBLE_LEFT,
      Languages.safeText(PREVIOUS_BUTTON),
      action
    );
  }

  /**
   * @param action The click action
   *
   * @return A new "Send" button with icon
   */
  public static JButton newSendButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.CLOUD_UPLOAD,
      Languages.safeText(SEND_BUTTON),
      action
    );
  }

  /**
   * @param action The click action
   *
   * @return A new "Receive" button with icon
   */
  public static JButton newReceiveButton(Action action) {
    return AwesomeDecorator.createIconButton(
      AwesomeIcon.CLOUD_DOWNLOAD,
      Languages.safeText(RECEIVE_BUTTON),
      action
    );
  }

}
