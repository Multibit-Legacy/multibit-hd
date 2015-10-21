package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

/**
 * <p>Enum to provide the following to "Use Trezor" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum UseHardwareWalletState {

  /**
   * Select the action you want to perform with the Trezor
   */
  SELECT_HARDWARE_ACTION,

  /**
   * Use the Trezor wallet
   */
  USE_TREZOR_WALLET,

  /**
   * Buy a Trezor
   */
  BUY_DEVICE,

  /**
   * Verify the Trezor device
   */
  VERIFY_DEVICE,

  /**
   * Request a wipe of the Trezor device
   */
  REQUEST_WIPE_DEVICE,

  /**
   * Confirm a wipe of the Trezor device
   */
  CONFIRM_WIPE_DEVICE,

  /**
   * Enter a Trezor PIN
   */
  ENTER_PIN,

  /**
   * No Trezor PIN required
   */
  NO_PIN_REQUIRED,

  /**
   * Report panel
   */
  USE_HARDWARE_WALLET_REPORT_PANEL

  // End of enum
  ;

}
