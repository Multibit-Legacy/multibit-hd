package org.multibit.hd.ui.views.screens;

/**
 * <p>Enum to provide the following to UI controllers:</p>
 * <ul>
 * <li>References to detail views</li>
 * </ul>
 *
 * <p>Note that configuration uses the name of the enum to avoid ordering issues</p>
 *
 * @since 0.0.1
 */
public enum Screen {

  // Sidebar screens

  /**
   * The buy/sell screen
   */
  BUY_SELL,

  /**
   * The send/request screen
   */
  SEND_REQUEST,

  /**
   * The shapeshift exchange screen
   */
  SHAPE_SHIFT,

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
   * The history screen - do not delete in case it is referenced in old configuration files
   *
   * @deprecated
   */
  @Deprecated
  HISTORY,

  // End of enum
  ;

}
