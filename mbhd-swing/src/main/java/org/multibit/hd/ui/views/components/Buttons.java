package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.NimbusDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.annotation.Nullable;
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
   * @param key    The resource key for the language string for the button text
   * @param tooltipKey The resource key for the tooltip
   * @param values The values to apply to the button language string (can be null)
   *
   * @return A new JButton with default styling
   */
  public static JButton newButton(Action action, MessageKey key, MessageKey tooltipKey, @Nullable Object... values) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, key, tooltipKey);

    button.setText(Languages.safeText(key, values));

    return button;
  }

  /**
   * @param key    The resource key for the button language string
   * @param tooltipKey The resource key for the tooltip
   * @param values The values to apply to the button string (can be null)
   *
   * @return A new JButton with default styling and text arranged below the icon
   */
  public static JButton newLargeButton(Action action, MessageKey key, MessageKey tooltipKey,  @Nullable Object... values) {

    JButton button = newButton(action, key, tooltipKey, values);

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
   * @param messageKey The message key to use for the button
   * @param tooltipKey The resource key for the tooltip
   * @param icon       The awesome icon to use
   *
   * @return A new "alert panel" button with icon
   */
  public static JButton newAlertPanelButton(Action action, MessageKey messageKey, MessageKey tooltipKey, AwesomeIcon icon) {

    JButton button = newButton(action, messageKey, tooltipKey);

    AwesomeDecorator.applyIcon(icon, button, true, MultiBitUI.SMALL_ICON_SIZE);

    return button;

  }

  /**
   * @param action             The click action
   * @param confirmIcon        The icon to place on the confirm button to hint at what will happen
   * @param isConfirmDangerous True if the confirm action will result in data loss without undo
   *
   * @return A new "Yes" button with icon
   */
  public static JButton newYesButton(Action action, AwesomeIcon confirmIcon, boolean isConfirmDangerous) {

    JButton button = newButton(action, MessageKey.YES, MessageKey.YES_TOOLTIP);

    // The icon should trail the text for visual consistency
    AwesomeDecorator.applyIcon(confirmIcon, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    if (isConfirmDangerous) {
      NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);
    }

    return button;

  }

  /**
   * @param action             The click action
   *
   * @return A new "No" button with icon
   */
  public static JButton newNoButton(Action action) {

    JButton button = newButton(action, MessageKey.NO, MessageKey.NO_TOOLTIP);

    // The icon should trail the text for visual consistency
    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Undo" button with icon
   */
  public static JButton newUndoButton(Action action) {

    JButton button = newButton(action, MessageKey.UNDO, MessageKey.UNDO_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNDO, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Export" button with icon
   */
  public static JButton newExportButton(Action action) {

    JButton button = newButton(action, MessageKey.EXPORT,  MessageKey.EXPORT_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.SHARE_SQUARE_O, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Sign message" button with icon
   */
  public static JButton newSignMessageButton(Action action) {

    JButton button = newButton(action, MessageKey.SIGN_MESSAGE, MessageKey.SIGN_MESSAGE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.PENCIL, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Verify message" button with icon
   */
  public static JButton newVerifyMessageButton(Action action) {

    JButton button = newButton(action, MessageKey.VERIFY_MESSAGE, MessageKey.VERIFY_MESSAGE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Clear all" button with icon and text
   */
  public static JButton newClearAllButton(Action action) {

    JButton button = newButton(action, MessageKey.CLEAR_ALL, MessageKey.CLEAR_ALL_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.MINUS_CIRCLE, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Delete Payment Request" button with icon
   */
  public static JButton newDeletePaymentRequestButton(Action action) {

    JButton button = newButton(action, MessageKey.DELETE_PAYMENT_REQUEST, MessageKey.DELETE_PAYMENT_REQUEST_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.TRASH, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "delete" button with icon
   */
  public static JButton newDeleteButton(Action action) {

    JButton button = newButton(action, MessageKey.DELETE, MessageKey.DELETE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.TRASH, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "backspace delete" button with icon
   */
  public static JButton newBackspaceDeleteButton(Action action) {

    JButton button = newButton(action);

    // Ensure FEST can find it
    button.setName(MessageKey.DELETE.getKey());

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ARROW_CIRCLE_O_LEFT, AwesomeIcon.ARROW_CIRCLE_O_RIGHT);
    AwesomeDecorator.applyIcon(icon, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Cancel" button with icon
   */
  public static JButton newCancelButton(Action action) {

    JButton button = newButton(action, MessageKey.CANCEL, MessageKey.CANCEL_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action  The click action
   * @param leading True if the icon should lead the text
   *
   * @return A new "Exit" button with icon
   */
  public static JButton newExitButton(Action action, boolean leading) {

    JButton button = newButton(action, MessageKey.EXIT, MessageKey.EXIT_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.SIGN_OUT, button, leading, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Next" button with icon (not prefixed)
   */
  public static JButton newNextButton(Action action) {

    JButton button = newButton(action, MessageKey.NEXT, MessageKey.NEXT_TOOLTIP);

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

    JButton button = newButton(action, MessageKey.PREVIOUS, MessageKey.PREVIOUS_TOOLTIP);

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

    JButton button = newButton(action, MessageKey.FINISH, MessageKey.FINISH_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.FLAG_CHECKERED, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Unlock" button with icon (this is a finish button but just looks different)
   */
  public static JButton newUnlockButton(Action action) {

    JButton button = newButton(action, MessageKey.PASSWORD_UNLOCK, MessageKey.PASSWORD_UNLOCK_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.UNLOCK, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Apply" button with icon
   */
  public static JButton newApplyButton(Action action) {

    JButton button = newButton(action, MessageKey.APPLY, MessageKey.APPLY_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Send" button with icon
   */
  public static JButton newSendButton(Action action) {

    JButton button = newButton(action, MessageKey.SEND, MessageKey.SEND_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    NimbusDecorator.applyThemeColor(Themes.currentTheme.dangerAlertBackground(), button);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Refresh" button with icon
   */
  public static JButton newRefreshButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.REFRESH, MessageKey.REFRESH_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.REFRESH, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "Home" button with icon
   */
  public static JButton newHomeButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.HOME, MessageKey.HOME_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.HOME, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "show" button with icon
   */
  public static JButton newShowButton(Action action) {

    JButton button = newButton(action);

    ButtonDecorator.applyShow(button);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "hide" button with icon
   */
  public static JButton newHideButton(Action action) {

    JButton button = newButton(action);

    ButtonDecorator.applyHide(button);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "QR code" button with icon
   */
  public static JButton newQRCodeButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.QR_CODE, MessageKey.QR_CODE_TOOLTIP);

    // Require this background color to ensure people can find the QR code icon quickly
    NimbusDecorator.applyThemeColor(Themes.currentTheme.readOnlyBackground(), button);

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

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.COPY, MessageKey.COPY_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.COPY, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "copy all" button with icon and text
   */
  public static JButton newCopyAllButton(Action action) {

    JButton button = newButton(action, MessageKey.COPY_ALL, MessageKey.COPY_ALL_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.COPY, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "paste" button with icon
   */
  public static JButton newPasteButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.PASTE, MessageKey.PASTE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.PASTE, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "paste all" button with icon and text
   */
  public static JButton newPasteAllButton(Action action) {

    JButton button = newButton(action, MessageKey.PASTE_ALL, MessageKey.PASTE_ALL_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.PASTE, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "close" button with icon
   */
  public static JButton newPanelCloseButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.CLOSE, MessageKey.CLOSE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.TIMES, button, true, MultiBitUI.SMALL_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new "select file" button with icon
   */
  public static JButton newSelectFileButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.SELECT_FOLDER, MessageKey.SELECT_FOLDER_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.FOLDER_OPEN, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "add" button with icon
   */
  public static JButton newAddButton(Action action) {

    JButton button = newButton(action, MessageKey.ADD, MessageKey.ADD_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.PLUS, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "edit" button with icon
   */
  public static JButton newEditButton(Action action) {

    JButton button = newButton(action, MessageKey.EDIT, MessageKey.EDIT_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.EDIT, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "details" button with icon
   */
  public static JButton newDetailsButton(Action action) {

    JButton button = newButton(action, MessageKey.DETAILS, MessageKey.DETAILS_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.FILE_TEXT_O, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "donate" button with icon
   */
  public static JButton newDonateNowButton(Action action) {

    JButton button = newButton(action, MessageKey.DONATE_NOW, MessageKey.DONATE_NOW);

    AwesomeDecorator.applyIcon(AwesomeIcon.HEART, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
    * @param action The click action
    *
    * @return A new "Pay this payment request" button with icon
    */
   public static JButton newPayThisPaymentRequestButton(Action action) {

     JButton button = newButton(action, MessageKey.PAY_THIS_PAYMENT_REQUEST, MessageKey.PAY_THIS_PAYMENT_REQUEST_TOOLTIP);

     AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, true, MultiBitUI.NORMAL_ICON_SIZE);

     return button;
   }

  /**
   * @param action The click action
   *
   * @return A new "search" button with icon
   */
  public static JButton newSearchButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.SEARCH, MessageKey.SEARCH_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.SEARCH, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "back" button with icon
   */
  public static JButton newBackButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.BACK, MessageKey.BACK_TOOLTIP);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ARROW_LEFT, AwesomeIcon.ARROW_RIGHT);

    AwesomeDecorator.applyIcon(icon, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "forward" button with icon
   */
  public static JButton newForwardButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.FORWARD, MessageKey.FORWARD_TOOLTIP);

    AwesomeIcon icon = AwesomeDecorator.select(AwesomeIcon.ARROW_RIGHT, AwesomeIcon.ARROW_LEFT);

    AwesomeDecorator.applyIcon(icon, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "launch browser" button with icon only
   */
  public static JButton newLaunchBrowserButton(Action action) {

    JButton button = newButton(action);

    // Ensure it is accessible
    AccessibilityDecorator.apply(button, MessageKey.BROWSE, MessageKey.BROWSE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.EXTERNAL_LINK, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "launch browser" button with icon and message
   */
  public static JButton newLaunchBrowserButton(Action action, MessageKey key, MessageKey tooltipKey) {

    JButton button = newButton(action, key, tooltipKey);

    AwesomeDecorator.applyIcon(AwesomeIcon.EXTERNAL_LINK, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }
  /**
   * @param action The click action
   *
   * @return A new "launch browser" button with icon and message
   */
  public static JButton newLaunchBrowserButton(Action action, MessageKey key, MessageKey tooltipKey, Object blockExplorerId) {

    JButton button = newButton(action, key, tooltipKey, blockExplorerId);

    AwesomeDecorator.applyIcon(AwesomeIcon.EXTERNAL_LINK, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "play sound" button with icon and message
   */
  public static JButton newPlaySoundButton(Action action, MessageKey key, MessageKey tooltipKey) {

    JButton button = newButton(action, key, tooltipKey);

    AwesomeDecorator.applyIcon(AwesomeIcon.PLAY, button, true, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "restore" button with icon
   */
  public static JButton newRestoreButton(Action action) {

    JButton button = newButton(action, MessageKey.RESTORE, MessageKey.RESTORE_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.MAGIC, button, false, MultiBitUI.NORMAL_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "send Bitcoin" wizard button with icon
   */
  public static JButton newSendBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_SEND_WIZARD, MessageKey.SHOW_SEND_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_UPLOAD, button, true, JLabel.BOTTOM, MultiBitUI.LARGE_ICON_SIZE);

    return button;
  }

  /**
   * @param action The click action
   *
   * @return A new "Request Bitcoin" wizard button with icon
   */
  public static JButton newRequestBitcoinWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_REQUEST_WIZARD, MessageKey.SHOW_REQUEST_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(AwesomeIcon.CLOUD_DOWNLOAD, button, true, JLabel.BOTTOM, MultiBitUI.LARGE_ICON_SIZE);

    return button;

  }

  /**
   * @param action The click action
   *
   * @return A new large "sign" wizard button with icon
   */
  public static JButton newLargeShowSignMessageWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_SIGN_WIZARD, MessageKey.SHOW_SIGN_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.PENCIL,
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
   * @return A new "verify" wizard button with icon
   */
  public static JButton newShowVerifyMessageWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_VERIFY_WIZARD, MessageKey.SHOW_VERIFY_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.CHECK,
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
     * @return A new "Trezor tools" wizard button with icon
     */
    public static JButton newShowUseTrezorWizardButton(Action action) {

      JButton button = newLargeButton(action, MessageKey.SHOW_TREZOR_TOOLS_WIZARD, MessageKey.SHOW_TREZOR_TOOLS_WIZARD_TOOLTIP);

      AwesomeDecorator.applyIcon(
        // Cannot use LOCK even though it is the closest icon to the Trezor logo
        // since it affects the established iconography throughout the application
        AwesomeIcon.SHIELD,
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
   * @return A new "language settings" wizard button with icon
   */
  public static JButton newShowLanguageSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_LANGUAGE_WIZARD, MessageKey.SHOW_LANGUAGE_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.GLOBE,
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
   * @return A new "Units settings" wizard button with icon
   */
  public static JButton newShowUnitsSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_UNITS_WIZARD, MessageKey.SHOW_UNITS_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.BITCOIN,
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
   * @return A new "exchange rate provider settings" wizard button with icon
   */
  public static JButton newShowExchangeSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_EXCHANGE_WIZARD, MessageKey.SHOW_EXCHANGE_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.DOLLAR,
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
   * @return A new "Appearance settings" wizard button with icon
   */
  public static JButton newShowApplicationSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_APPEARANCE_WIZARD, MessageKey.SHOW_APPEARANCE_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.DESKTOP,
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
    * @return A new "Fee settings" wizard button with icon
    */
   public static JButton newShowFeeSettingsWizardButton(Action action) {

     JButton button = newLargeButton(action, MessageKey.FEES_SETTINGS_TITLE, MessageKey.FEES_SETTINGS_TITLE);

     AwesomeDecorator.applyIcon(
       AwesomeIcon.TICKET,
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
   * @return A new "Sound settings" wizard button with icon
   */
  public static JButton newShowSoundSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_SOUNDS_WIZARD, MessageKey.SHOW_SOUNDS_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.MUSIC,
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
   * @return A new "Lab settings" wizard button with icon
   */
  public static JButton newShowLabSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_LABS_WIZARD, MessageKey.SHOW_LABS_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.FLASK,
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
   * @return A new "edit wallet" button with icon
   */
  public static JButton newShowEditWalletButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_EDIT_WALLET_WIZARD, MessageKey.SHOW_EDIT_WALLET_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.EDIT,
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
   * @return A new "change credentials" button with icon
   */
  public static JButton newShowChangePasswordButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_CHANGE_PASSWORD_WIZARD, MessageKey.SHOW_CHANGE_PASSWORD_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.LOCK,
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
   * @return A new "change PIN" button with icon
   */
  public static JButton newShowChangePinButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_CHANGE_PIN_WIZARD, MessageKey.SHOW_CHANGE_PIN_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.TH,
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
   * @return A new "verify network" button with icon
   */
  public static JButton newShowVerifyNetworkButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_VERIFY_NETWORK_WIZARD, MessageKey.SHOW_VERIFY_NETWORK_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.SITEMAP,
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
   * @return A new "history" button with icon
   */
  public static JButton newShowHistoryScreenButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.HISTORY, MessageKey.HISTORY_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.HISTORY,
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
   * @return A new "about" button with icon
   */
  public static JButton newShowAboutButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_ABOUT_WIZARD, MessageKey.SHOW_ABOUT_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.SMILE_O,
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
   * @return A new "repair wallet" button with icon
   */
  public static JButton newShowRepairWalletButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_REPAIR_WALLET_WIZARD, MessageKey.SHOW_REPAIR_WALLET_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.MEDKIT,
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
   * @return A new "wallet details" button with icon
   */
  public static JButton newShowWalletDetailsButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_WALLET_DETAILS_WIZARD, MessageKey.SHOW_WALLET_DETAILS_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.DASHBOARD,
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
   * @return A new "empty wallet" button with icon
   */
  public static JButton newShowEmptyWalletButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_EMPTY_WALLET_WIZARD, MessageKey.SHOW_EMPTY_WALLET_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.FIRE,
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
   * @return A new "Payment settings" wizard button with icon
   */
  public static JButton newShowPaymentSettingsWizardButton(Action action) {

    JButton button = newLargeButton(action, MessageKey.SHOW_PAYMENT_SETTINGS_WIZARD, MessageKey.SHOW_PAYMENT_SETTINGS_WIZARD_TOOLTIP);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.MONEY,
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
   * @param festName The FEST name
   * @return A new "PIN matrix" button with icon
   */
  public static JButton newPinMatixButton(Action action, String festName) {

    JButton button = newButton(action);

    // Ensure FEST can find it
    button.setName(festName);

    AwesomeDecorator.applyIcon(
      AwesomeIcon.QUESTION,
      button,
      true,
      JLabel.BOTTOM,
      MultiBitUI.NORMAL_ICON_SIZE
    );

    return button;
  }
}
