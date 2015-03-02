package org.multibit.hd.ui;

/**
 * <p>Interface to provide the following to Swing UI:</p>
 * <ul>
 * <li>Various size and layout constants that are hard-coded into the UI</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface MultiBitUI {

  // Panel dimensions

  /**
   * The minimum width for the application UI (900 is the minimum for tables)
   * See #94 for screenshot of minimum resolution on Intel Atom at 1024x600
   */
  int UI_MIN_WIDTH = 900;
  /**
   * The minimum height for the application UI (550 is the minimum)
   */
  int UI_MIN_HEIGHT = 550;

  /**
   * The minimum width for a wizard panel (600 is about right) allowing for popovers
   */
  int WIZARD_MIN_WIDTH = 630;
  /**
   * The minimum height for a standard wizard panel (450 is tight) allowing for popovers
   */
  int WIZARD_MIN_HEIGHT = 450;

  /**
   * The minimum width for a wizard popover (must be less than the PREF defined below)
   */
  int POPOVER_MIN_WIDTH = 300;
  /**
   * The minimum height for a wizard popover (must be less than the PREF defined below)
   */
  int POPOVER_MIN_HEIGHT = 250;
  /**
   * The preferred width for a wizard popover (must be less than the MAX defined below)
   */
  int POPOVER_PREF_WIDTH = 500;
  /**
   * The preferred height for a wizard popover (must be less than the MAX defined below)
   */
  int POPOVER_PREF_HEIGHT = 350;
  /**
   * The maximum width for a wizard popover (500 allows for maximum Bitcoin URI QR code)
   */
  int POPOVER_MAX_WIDTH = 500;
  /**
   * The maximum height for a wizard popover (450 allows for maximum Bitcoin URI QR code)
   */
  int POPOVER_MAX_HEIGHT = 450;

  /**
   * The preferred width for the sidebar
   */
  int SIDEBAR_LHS_PREF_WIDTH = 180;

  /**
   * A width constraint to avoid text overflow in wizards
   */
  String WIZARD_MAX_WIDTH_MIG = "wmax " + (MultiBitUI.WIZARD_MIN_WIDTH - 30);

  /**
   * A width constraint to avoid seed phrase text area overflow in wizards
   * In particular this affects some Linux variants perhaps due to an issue
   * within MiG Layout.
   *
   * See <a href="https://github.com/bitcoin-solutions/multibit-hd/issues/230">Issue #230</a> for details.
   */
  String WIZARD_MAX_WIDTH_SEED_PHRASE_MIG = "width 300:300:" + (MultiBitUI.WIZARD_MIN_WIDTH - 30);

  /**
   * A width constraint to avoid text overflow in wizards
   */
  String COMBO_BOX_WIDTH_MIG = "w min:350:";

  /**
   * A width constraint to avoid text overflow in wizards showing Trezor device screens
   */
  String TREZOR_DISPLAY_MAX_WIDTH_MIG = "w 150";

  // Corners

  /**
   * The corner radius to use for rounded rectangles (e.g. panels, text fields etc)
   */
  int COMPONENT_CORNER_RADIUS = 10;
  /**
   * The corner radius to use for images (e.g. gravatars etc)
   */
  int IMAGE_CORNER_RADIUS = 20;

  // Fonts

  /**
   * Balance header large font
   */
  float BALANCE_HEADER_LARGE_FONT_SIZE = 36.0f;

  /**
    * Wizard title font
    */
   float WIZARD_TITLE_FONT_SIZE = 30.0f;

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
   * Font size for table text
   */
  float TABLE_TEXT_FONT_SIZE = 13.0f;

  /**
   * Font size for combo box text
   */
  float COMBO_BOX_TEXT_FONT_SIZE = 15.0f;

  // Icons

  /**
   * Huge icon size (e.g. detail panel background)
   */
  int HUGE_ICON_SIZE = 270;
  /**
   * Large icon size (e.g. Gravatars)
   */
  int LARGE_ICON_SIZE = 60;
  /**
   * Larger than normal icon size (e.g. buttons needing more attention like QR code)
   */
  int NORMAL_PLUS_ICON_SIZE = 30;
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
   * Needs to be in ratio 5:4 and 150,120 is the comfortable minimum
   */
  String LARGE_BUTTON_MIG = "wmin 150,hmin 120";

  /**
   * Provides the MiG layout information for a medium button
   */
  String MEDIUM_BUTTON_MIG = "wmin 75,hmin 60";

  /**
   * Provides the MiG layout information for a small button
   */
  String SMALL_BUTTON_MIG = "wmin 50,hmin 40";

  /**
   * Provides the MiG layout information for a normal icon
   */
  String NORMAL_ICON_SIZE_MIG = "w " + NORMAL_ICON_SIZE + ",h " + NORMAL_ICON_SIZE;

  /**
   * Provides the MiG layout information for a normal plus icon
   */
  String NORMAL_PLUS_ICON_SIZE_MIG = "w " + (NORMAL_PLUS_ICON_SIZE * 1.5) + ",h " + NORMAL_PLUS_ICON_SIZE;

  // Text fields

  /**
   * The maximum length of a receive address label
   */
  int RECEIVE_ADDRESS_LABEL_LENGTH = 60;

  /**
   * The maximum length of the credentials
   */
  int PASSWORD_LENGTH = 40;

  /**
   * The width of the seed phrase window
   */
  int SEED_PHRASE_WIDTH = 50;

  /**
   * The maximum length of the seed phrase
   */
  int SEED_PHRASE_LENGTH = 240;

  // Alpha composite

  /**
   * The alpha composite to apply to the background image of a detail panel
   * Anything below 0.2 is too faded on some monitors
   */
  float DETAIL_PANEL_BACKGROUND_ALPHA = 0.2f;

  // Combo boxes

  /**
   * The maximum number of rows to show before a slider is introduced
   * 12 is a good value for the standard wizard height
   */
  int COMBOBOX_MAX_ROW_COUNT = 12;

  // Tables

  /**
   * Additional padding for table columns
   */
  int TABLE_SPACER = 10;

  /**
   * Footer minimum height
   */
  int FOOTER_MINIMUM_HEIGHT = 30;

}
