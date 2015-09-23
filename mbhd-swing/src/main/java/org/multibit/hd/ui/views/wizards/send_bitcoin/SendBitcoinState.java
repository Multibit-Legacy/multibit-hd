package org.multibit.hd.ui.views.wizards.send_bitcoin;

/**
 * <p>Enum to provide the following to "send bitcoin" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 */
public enum SendBitcoinState {

  SEND_DISPLAY_PAYMENT_REQUEST,
  SEND_ENTER_AMOUNT,
  SEND_CONFIRM_AMOUNT,
  SEND_CONFIRM_HARDWARE,
  SEND_ENTER_PIN_FROM_CONFIRM_HARDWARE,
  SEND_REPORT,
  SEND_BIP70_PAYMENT_MEMO,
  SEND_BIP70_PAYMENT_ACK_MEMO,

  // End of enum
  ;

}
