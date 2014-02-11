package org.multibit.hd.ui;

/**
 * <p>Interface to provide the following to Swing UI:</p>
 * <ul>
 * <li>Various size and layout constants that are hard-coded into the UI</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface MultiBitUI {

  // Panel dimensions

  /**
   * The minimum width for the application UI (900 is about right)
   */
  int UI_MIN_WIDTH = 900;
  /**
   * The minimum height for the application UI (600 is about right)
   */
  int UI_MIN_HEIGHT = 600;

  /**
   * The minimum width for a wizard panel (600 is about right)
   */
  int WIZARD_MIN_WIDTH = 600;
  /**
   * The minimum height for a wizard panel (450 is tight)
   */
  int WIZARD_MIN_HEIGHT = 450;

  // Corners

  /**
   * The corner radius to use for rounded rectangles (e.g. panels, text fields etc)
   */
  int CORNER_RADIUS = 10;

  // Fonts

  /**
   * Balance header large font
   */
  float BALANCE_HEADER_LARGE_FONT_SIZE = 36.0f;
  /**
   * Balance header normal font (decimals etc)
   */
  float BALANCE_HEADER_NORMAL_FONT_SIZE = 20.0f;

  /**
   * Transaction large font (e.g. send bitcoins)
   */
  float BALANCE_TRANSACTION_LARGE_FONT_SIZE = 18.0f;
  /**
   * Transaction normal font (e.g. send bitcoins decimals etc)
   */
  float BALANCE_TRANSACTION_NORMAL_FONT_SIZE = 14.0f;

  /**
   * Fee large font (e.g. send bitcoins wizard)
   */
  float BALANCE_FEE_LARGE_FONT_SIZE = 14.0f;
  /**
   * Fee normal font (e.g. send bitcoins wizard)
   */
  float BALANCE_FEE_NORMAL_FONT_SIZE = 12.0f;

  /**
   * Font for the "panel close" button
   */
  float PANEL_CLOSE_FONT_SIZE = 28.0f;

  // Icons

  /**
   * Large icon size (e.g. Gravatars)
   */
  int LARGE_ICON_SIZE = 60;
  /**
   * Normal icon size (e.g. standard buttons)
   */
  int NORMAL_ICON_SIZE = 20;
  /**
   * Small icon size (e.g. stars and status)
   */
  int SMALL_ICON_SIZE = 16;

  // Buttons

  /**
   * Provides the MiG layout information for a large button
   */
  String LARGE_BUTTON_MIG = "w 200,h 160";

  // Text fields

  /**
   * The maximum length of a receive address label
   */
  int RECEIVE_ADDRESS_LABEL_LENGTH = 60;

  /**
   * The maximum length of the password
   */
  int PASSWORD_LENGTH = 40;
  /**
   * The maximum length of the seed phrase
   */
  int SEED_PHRASE_LENGTH = 240;

}
