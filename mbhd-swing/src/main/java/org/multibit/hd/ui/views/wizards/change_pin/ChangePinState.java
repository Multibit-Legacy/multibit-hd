package org.multibit.hd.ui.views.wizards.change_pin;

/**
 * <p>Enum to provide the following to "credentials" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public enum ChangePinState {

  SELECT_OPTION,
  REQUEST_CHANGE_PIN,
  REQUEST_REMOVE_PIN,
  CONFIRM_REMOVE_PIN,
  ENTER_CURRENT_PIN,
  ENTER_NEW_PIN,
  CONFIRM_NEW_PIN,
  SHOW_REPORT,

  // End of enum
  ;

}
