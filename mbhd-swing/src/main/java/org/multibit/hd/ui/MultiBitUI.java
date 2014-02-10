package org.multibit.hd.ui;

/**
 * <p>Interface to provide the following to Swing UI:</p>
 * <ul>
 * <li>Various constants that are hard-coded into the UI</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface MultiBitUI {



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

  /**
   * Large icon size (e.g. Gravatars)
   */
  int LARGE_ICON_SIZE = 70;
  /**
   * Normal icon size (e.g. standard buttons)
   */
  int NORMAL_ICON_SIZE = 20;
  /**
   * Small icon size (e.g. stars and status)
   */
  int SMALL_ICON_SIZE = 14;
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
  int WIZARD_MIN_WIDTH = 600;
  int WIZARD_MIN_HEIGHT = 450;
}
