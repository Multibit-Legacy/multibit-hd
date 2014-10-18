package org.multibit.hd.ui.views.screens;

/**
 * <p>Enum to provide the following to UI controllers:</p>
 * <ul>
 * <li>References to detail views</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum Screen {

  // Sidebar screens

  /**
   * The send/request screen
   */
  SEND_REQUEST,

  /**
   * The wallet payments screen
   */
  TRANSACTIONS,

  /**
   * The wallet contacts screen
   */
  CONTACTS,

  /**
   * The help screen
   */
  HELP,

  /**
   * The settings screen (language, units, theme etc)
   */
  SETTINGS,

  /**
   * The manage wallet screen (edit, empty, repair wallet etc)
   */
  MANAGE_WALLET,

  /**
   * The tools screen (sign, verify, Trezor etc)
   */
  TOOLS,

  /**
   * The exit screen
   */
  EXIT,

  // Specialised screens from buttons
  /**
   * The history screen
   */
  HISTORY,



  // End of enum
  ;

}
