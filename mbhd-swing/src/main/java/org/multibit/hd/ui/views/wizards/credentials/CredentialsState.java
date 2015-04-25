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
   * Request the master public key from the Trezor (may trigger PIN request)
   */
  CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY,

  /**
   * Master public key has requested a PIN
   */
  CREDENTIALS_ENTER_PIN_FROM_MASTER_PUBLIC_KEY,

  /**
   * Request a cipher key from the Trezor (may trigger a PIN request)
   */
  CREDENTIALS_REQUEST_CIPHER_KEY,

  /**
   * Cipher key has requested a PIN
   */
  CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY,

  /**
   * Information panel asking user to press the Trezor "confirm" button
   */
  CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK,

  /**
   * Report on whether the wallet loaded successfully or not
   */
  CREDENTIALS_LOAD_WALLET_REPORT,

  /**
   * Special case state used by the restore button action to trigger
   * a handover to the welcome wizard
   */
  CREDENTIALS_RESTORE,

  /**
   * Special case state used by the create button action to trigger
   * a handover to the welcome wizard
   */
  CREDENTIALS_CREATE,

   // End of enum
  ;

}
