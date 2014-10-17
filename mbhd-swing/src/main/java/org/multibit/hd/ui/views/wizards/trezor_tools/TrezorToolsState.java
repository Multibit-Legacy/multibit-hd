package org.multibit.hd.ui.views.wizards.trezor_tools;

/**
 * <p>Enum to provide the following to "repair wallet" wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum TrezorToolsState {

  SELECT_TREZOR_ACTION,
  VERIFY_DEVICE,
  WIPE_DEVICE,
  TREZOR_ACTION_REPORT

  // End of enum
  ;

}
