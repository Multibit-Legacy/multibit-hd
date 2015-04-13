package org.multibit.hd.ui.views.wizards.empty_wallet;

/**
 * <p>Enum to provide the following to "empty wallet" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum EmptyWalletState {

  EMPTY_WALLET_ENTER_DETAILS,
  EMPTY_WALLET_CONFIRM,
  EMPTY_WALLET_ENTER_PIN_FROM_CONFIRM_TREZOR,
  EMPTY_WALLET_CONFIRM_TREZOR,
  EMPTY_WALLET_REPORT,

  // End of enum
  ;

}
