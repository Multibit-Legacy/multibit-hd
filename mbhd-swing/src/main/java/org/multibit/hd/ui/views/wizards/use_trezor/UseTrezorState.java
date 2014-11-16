package org.multibit.hd.ui.views.wizards.use_trezor;

/**
 * <p>Enum to provide the following to "Use Trezor" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum UseTrezorState {

  /**
   * Select the action you want to perform with the Trezor
   */
  SELECT_TREZOR_ACTION,

  /**
   * Use the Trezor wallet
   */
  USE_TREZOR_WALLET,

  /**
   * Buy a Trezor
   */
  BUY_TREZOR,

  /**
   * Verify the Trezor device
   */
  VERIFY_TREZOR,

  /**
   * Wipe the Trezor device
   */
  WIPE_TREZOR,

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
  USE_TREZOR_REPORT_PANEL

  // End of enum
  ;

}
