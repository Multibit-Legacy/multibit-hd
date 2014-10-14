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
   * Enter a Trezor PIN
   */
   CREDENTIALS_ENTER_PIN,

  /**
   * No Trezor PIN required
   */
   CREDENTIALS_NO_PIN_REQUIRED,

   /**
   * Special case state used by the restore button action to trigger
   * a handover to the welcome wizard
   */
  CREDENTIALS_RESTORE,

  // End of enum
  ;

}
