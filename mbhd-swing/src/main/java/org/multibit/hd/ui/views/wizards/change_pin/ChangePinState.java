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

  CHANGE_PIN_SELECT_OPTION,
  CHANGE_PIN_REQUEST_PIN_CHANGE,
  CHANGE_PIN_ENTER_CURRENT_PIN,
  CHANGE_PIN_ENTER_NEW_PIN,
  CHANGE_PIN_CONFIRM_NEW_PIN,
  CHANGE_PIN_REPORT,

  // End of enum
  ;

}
