package org.multibit.hd.ui.views.wizards.credentials;

/**
 * <p>Enum to provide the following to "credentials" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum CredentialsState {

  /**
   * Enter a password
   */
  CREDENTIALS_ENTER_PASSWORD,

  /**
   * Request the master public key from the Trezor (does not trigger PIN request)
   */
  CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY,

  /**
   * Request a cipher key from the Trezor (most likely triggers a PIN request)
   */
  CREDENTIALS_REQUEST_CIPHER_KEY,

  /**
   * Enter a PIN
   */
  CREDENTIALS_ENTER_PIN,

  /**
   * Information panel asking user to press the Trezor "confirm" button
   */
  CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK,

   /**
   * Special case state used by the restore button action to trigger
   * a handover to the welcome wizard
   */
  CREDENTIALS_RESTORE,

  // End of enum
  ;

}
