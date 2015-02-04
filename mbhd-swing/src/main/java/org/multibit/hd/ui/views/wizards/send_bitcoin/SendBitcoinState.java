package org.multibit.hd.ui.views.wizards.send_bitcoin;

/**
 * <p>Enum to provide the following to "send bitcoin" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum SendBitcoinState {

  SEND_ENTER_AMOUNT,
  SEND_CONFIRM_AMOUNT,
  SEND_CONFIRM_TREZOR,
  SEND_REPORT,

  // End of enum
  ;

}
