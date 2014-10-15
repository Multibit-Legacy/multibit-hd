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
   * Special case state used by the restore button action to trigger
   * a handover to the welcome wizard
   */
  CREDENTIALS_RESTORE,

  // End of enum
  ;

}
