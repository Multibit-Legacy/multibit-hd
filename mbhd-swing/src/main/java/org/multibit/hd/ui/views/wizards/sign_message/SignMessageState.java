package org.multibit.hd.ui.views.wizards.sign_message;

/**
 * <p>Enum to provide the following to "sign message" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum SignMessageState {

  SIGN_MESSAGE_PASSWORD,
  SIGN_MESSAGE_HARDWARE,
  SIGN_MESSAGE_TREZOR_ENTER_PIN,
  SIGN_MESSAGE_TREZOR_CONFIRM_SIGN,

  // End of enum
  ;

}
