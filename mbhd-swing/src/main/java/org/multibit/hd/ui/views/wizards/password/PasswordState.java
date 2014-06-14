package org.multibit.hd.ui.views.wizards.password;

/**
 * <p>Enum to provide the following to "password" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum PasswordState {

  PASSWORD_ENTER_PASSWORD,
  /**
   * Special case state used by the restore button action to trigger
   * a handover to the welcome wizard
   */
  PASSWORD_RESTORE,

  // End of enum
  ;

}
