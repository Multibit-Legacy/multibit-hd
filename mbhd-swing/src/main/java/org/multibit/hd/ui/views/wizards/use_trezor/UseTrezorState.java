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
   * Ask for a cipher key value from the device (may trigger a PIN request)
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
   * Information panel asking user to press the Trezor OK button to the comment 'Encrypt MultiBit HD unlock'
   */
   PRESS_OK_FOR_UNLOCK,

  // End of enum
  ;

}
