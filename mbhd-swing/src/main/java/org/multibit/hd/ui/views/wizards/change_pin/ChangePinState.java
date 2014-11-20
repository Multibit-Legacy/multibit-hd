package org.multibit.hd.ui.views.wizards.change_pin;

/**
 * <p>Enum to provide the following to "credentials" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum ChangePinState {

  SELECT_OPTION,
  REQUEST_REMOVE_PIN,
  REQUEST_PIN_CHANGE,
  ENTER_CURRENT_PIN,
  ENTER_NEW_PIN,
  CONFIRM_NEW_PIN,
  SHOW_REPORT,

  // End of enum
  ;

}
