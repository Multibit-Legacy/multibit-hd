package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
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

    // Ensure borders render smoothly
    button.setOpaque(false);

    // Reinforce the idea of clicking
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Ensure we use the correct component orientation
    button.applyComponentOrientation(Languages.currentComponentOrientation());

    // Apply default theme (do not set foreground color)
    NimbusDecorator.applyThemeColor(Themes.currentTheme.buttonBackground(), button);

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

    // TODO Accessibility API - append _ACCESSIBILITY to .name() ?

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

    // Apply default theme
    NimbusDecorator.applyThemeColor(Themes.currentTheme.buttonBackground(), button);

    return button;
  }

  /**
   * <p>Create a new alert panel button</p>
   *
   * @param action     The click action
   * @param messageKey The message key to use
   * @param icon       The awesome icon to use
   *
   * @return A new "alert panel" button with icon
   */
  public static JButton newAlertPanelButton(Action action, MessageKey messageKey, AwesomeIcon icon) {

    JButton button = newButton(action, messageKey);
    button.setAction(action);

    AwesomeDecorator.applyIcon(icon, button, true, MultiBitUI.SMALL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, MultiBitUI.NORMAL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, true, MultiBitUI.NORMAL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, MultiBitUI.NORMAL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.SIGN_OUT, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);

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

    AwesomeDecorator.applyIcon(icon, button, false, MultiBitUI.NORMAL_ICON_SIZE);


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

    AwesomeDecorator.applyIcon(icon, button, true, MultiBitUI.NORMAL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.FLAG_CHECKERED, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
    * @param action The click action
    *
    * @return A new "Unlock" button with icon (this is a finish button but just looks differently)
    */
   public static JButton newUnlockButton(Action action) {

     JButton button = newButton(action, MessageKey.PASSWORD_UNLOCK);
     button.setAction(action);

     AwesomeDecorator.applyIcon(AwesomeIcon.KEY, button, false, MultiBitUI.NORMAL_ICON_SIZE);

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

    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Send" button with icon
   */
  public static JButton newSendButton(Action action) {

    JButton button = newButton(action, MessageKey.SEND);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Request" button with icon
   */
  public static JButton newRequestButton(Action action) {

    JButton button = newButton(action, MessageKey.REQUEST);
    button.setAction(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.infoAlertBackground(), button);

    return button;
  }


  /**
   * @param action The click action
   *
   * @return A new "Refresh" button with icon
   */
  public static JButton newRefreshButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Print" button with icon
   */
  public static JButton newPrintButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.PRINT, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "show" button with icon
   */
  public static JButton newShowButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.EYE, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "hide" button with icon
   */
  public static JButton newHideButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.EYE_SLASH, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "QR code" button with icon
   */
  public static JButton newQRCodeButton(Action action) {

    JButton button = newButton(action);
    NimbusDecorator.applyThemeColor(
      Themes.currentTheme.readOnlyBackground(),
      button
    );

    Icon enabledIcon = Images.newQRCodeIcon();

    button.setIcon(enabledIcon);
    button.setDisabledIcon(enabledIcon);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "copy" button with icon
   */
  public static JButton newCopyButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.COPY, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "paste" button with icon
   */
  public static JButton newPasteButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.PASTE, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "close" button with icon
   */
  public static JButton newPanelCloseButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "select file" button with icon
   */
  public static JButton newSelectFileButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.FOLDER_OPEN, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "add" button with icon
   */
  public static JButton newAddButton(Action action) {

    JButton button = newButton(action, MessageKey.ADD);

    AwesomeDecorator.applyIcon(AwesomeIcon.PLUS, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "edit" button with icon
   */
  public static JButton newEditButton(Action action) {

    JButton button = newButton(action, MessageKey.EDIT);

    AwesomeDecorator.applyIcon(AwesomeIcon.EDIT, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "delete" button with icon
   */
  public static JButton newDeleteButton(Action action) {

    JButton button = newButton(action, MessageKey.DELETE);

    AwesomeDecorator.applyIcon(AwesomeIcon.TRASH_ALT, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "search" button with icon
   */
  public static JButton newSearchButton(Action action) {

    JButton button = newButton(action);

    AwesomeDecorator.applyIcon(AwesomeIcon.SEARCH, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "restore" button with icon
   */
  public static JButton newRestoreButton(Action action) {

    JButton button = newButton(action, MessageKey.RESTORE);

    AwesomeDecorator.applyIcon(AwesomeIcon.MAGIC, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "send Bitcoin" wizard button with icon
   */
  public static JButton newSendBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SEND);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, true, JLabel.BOTTOM, MultiBitUI.LARGE_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Request Bitcoin" wizard button with icon
   */
  public static JButton newRequestBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.REQUEST);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true, JLabel.BOTTOM, MultiBitUI.LARGE_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Welcome" wizard button with icon
   */
  public static JButton newShowWelcomeWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_WELCOME_WIZARD);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.WRENCH,
      button,
      true,
      JLabel.BOTTOM,
      MultiBitUI.LARGE_ICON_SIZE
    );

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Alert" button with icon
   */
  public static JButton newAddAlertButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_ALERT);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.EXCLAMATION_CIRCLE,
      button,
      true,
      JLabel.BOTTOM,
      MultiBitUI.LARGE_ICON_SIZE
    );

    return button;
  }

}
