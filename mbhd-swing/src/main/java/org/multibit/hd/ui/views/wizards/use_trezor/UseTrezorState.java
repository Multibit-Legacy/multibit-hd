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
   * Request a cipher key form the Trezor (most likely triggers a PIN request)
   */
  REQUEST_CIPHER_KEY,

  /**
   * Enter a Trezor PIN
   */
   ENTER_PIN,

  /**
   * No Trezor PIN required
   */
   NO_PIN_REQUIRED,

  /**
   * Information panel asking user to press the Trezor Confirm button to the comment 'Encrypt MultiBit HD unlock'
   */
   PRESS_CONFIRM_FOR_UNLOCK,

  /**
   * Report panel
   */
   USE_TREZOR_REPORT_PANEL,

  // End of enum
  ;

}
