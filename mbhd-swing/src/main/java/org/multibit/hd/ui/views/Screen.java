package org.multibit.hd.ui.views;

/**
 * <p>Enum to provide the following to UI controllers:</p>
 * <ul>
 * <li>References to Views and their FXML</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum Screen {

  /**
   * The welcome stage (present during startup and major configuration)
   */
  WELCOME_STAGE,

  /**
   * The login screen
   */
  WELCOME_LOGIN,

  WELCOME_PROVIDE_INITIAL_SEED,

  // Main stage screens

  /**
   * The main stage (ongoing interactions)
   */
  MAIN_STAGE,

  /**
   * The home screen within the main stage
   */
  MAIN_HOME,

  /**
   * The contacts screen within the main stage
   */
  MAIN_CONTACTS,

  /**
   * The settings screen within the main stage
   */
  MAIN_SETTINGS,

  /**
   * The help screen within the main stage
   */
  MAIN_HELP,

  /**
   * The wallet screen within the main stage
   */
  MAIN_WALLET,

  /**
   * The error reporting screen within the main stage
   */
  MAIN_ERROR,

  // End of enum
  ;

}
