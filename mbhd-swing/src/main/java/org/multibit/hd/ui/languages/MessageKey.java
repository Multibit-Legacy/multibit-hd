package org.multibit.hd.ui.languages;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Message keys to use for internationalisation</li>
 * </ul>
 *
 * <h3>Naming conventions</h3>
 * <p>Message keys are placed in an enum for type safety.</p>
 * <p>Message keys have their resource key so that IDEs can maintain a "where used" reference lookup.</p>
 * <p>Message keys are never concatenated to form larger sentences. Instead use token replacements
 * within a larger sentence structure.</p>
 * <p>Resource keys are simply the message key enum name in lower case.</p>
 *
 * @since 0.0.1
 *
 */
public enum MessageKey {

  // Verbs
  APPLY("apply"),
  APPLY_TOOLTIP("apply_tooltip"),

  UNDO("undo"),
  UNDO_TOOLTIP("undo_tooltip"),

  CANCEL("cancel"),
  CANCEL_TOOLTIP("cancel_tooltip"),

  EXIT("exit"),
  EXIT_TOOLTIP("exit_tooltip"),

  SWITCH("switch"),
  SWITCH_TOOLTIP("switch_tooltip"),

  SEND("send"),
  SEND_TOOLTIP("send_tooltip"),

  REQUEST("request"),
  REQUEST_TOOLTIP("request_tooltip"),

  REFRESH("refresh"),
  REFRESH_TOOLTIP("refresh_tooltip"),

  HOME("home"),
  HOME_TOOLTIP("home_tooltip"),

  FINISH("finish"),
  FINISH_TOOLTIP("finish_tooltip"),

  CLOSE("close"),
  CLOSE_TOOLTIP("close_tooltip"),

  SHOW("show"),
  HIDE("hide"),

  ADD("add"),
  ADD_TOOLTIP("add_tooltip"),

  EDIT("edit"),
  EDIT_TOOLTIP("edit_tooltip"),

  DELETE("delete"),
  DELETE_TOOLTIP("delete_tooltip"),

  RESTORE("restore"),
  RESTORE_TOOLTIP("restore_tooltip"),

  SEARCH("search"),
  SEARCH_TOOLTIP("search_tooltip"),

  BACK("back"),
  BACK_TOOLTIP("back_tooltip"),

  FORWARD("forward"),
  FORWARD_TOOLTIP("forward_tooltip"),

  BROWSE("browse"),
  BROWSE_TOOLTIP("browse_tooltip"),

  PASTE("paste"),
  PASTE_TOOLTIP("paste_tooltip"),

  PASTE_ALL("paste_all"),
  PASTE_ALL_TOOLTIP("paste_all_tooltip"),

  COPY("copy"),
  COPY_TOOLTIP("copy_tooltip"),

  REPAIR("repair"),
  REPAIR_TOOLTIP("repair_tooltip"),

  CLEAR_ALL("clear_all"),
  CLEAR_ALL_TOOLTIP("clear_all_tooltip"),

  COPY_ALL("copy_all"),
  COPY_ALL_TOOLTIP("copy_all_tooltip"),

  SIGN_MESSAGE("sign_message"),
  SIGN_MESSAGE_TOOLTIP("sign_message_tooltip"),

  VERIFY_MESSAGE("verify_message"),
  VERIFY_MESSAGE_TOOLTIP("verify_message_tooltip"),

  // Nouns

  YES("yes"),
  YES_TOOLTIP("yes_tooltip"),

  NO("no"),
  NO_TOOLTIP("no_tooltip"),

  NEXT("next"),
  NEXT_TOOLTIP("next_tooltip"),

  PREVIOUS("previous"),
  PREVIOUS_TOOLTIP("previous_tooltip"),

  ALL("all"),
  NONE("none"),
  ERROR("error"),
  @Deprecated
  DEFAULT("default"),

  QR_CODE("qr_code"),
  QR_CODE_TOOLTIP("qr_code_tooltip"),

  // Adjectives
  LEADING("leading"),
  TRAILING("trailing"),

  // Field labels

  RECIPIENT("recipient"),
  RECIPIENT_TOOLTIP("recipient_tooltip"),

  TRANSACTION_LABEL("transaction_label"),
  TRANSACTION_LABEL_TOOLTIP("transaction_label_tooltip"),
  QR_CODE_LABEL("qr_code_label"),
  QR_CODE_LABEL_TOOLTIP("qr_code_label_tooltip"),
  QR_CODE_NOTE_1("qr_code_note_1"),
  QR_CODE_NOTE_2("qr_code_note_2"),

  PRIVATE_NOTES("private_notes"),
  PRIVATE_NOTES_TOOLTIP("private_notes_tooltip"),

  NOTES("notes"),
  NOTES_TOOLTIP("notes_tooltip"),

  // Sign/verify message

  MESSAGE("message"),
  MESSAGE_TOOLTIP("message_tooltip"),

  SIGNATURE("signature"),
  SIGNATURE_TOOLTIP("signature_tooltip"),

  CONTACT_IMAGE("contact_image"),

  ONE_OF_YOUR_ADDRESSES("one_of_your_addresses"),
  THIS_BITCOIN_WAS_SENT_TO_YOU("this_bitcoin_was_sent_to_you"),

  SELECT_FOLDER("select_folder"),
  SELECT_FOLDER_TOOLTIP("select_folder_tooltip"),

  SELECT_FILE("select_file"),
  SELECT_FILE_TOOLTIP("select_file_tooltip"),

  SELECT_WALLET("select_wallet"),
  SELECT_WALLET_TOOLTIP("select_wallet_tooltip"),

  ENTER_PASSWORD("enter_password"),
  ENTER_PASSWORD_TOOLTIP("enter_password_tooltip"),

  ENTER_NEW_PASSWORD("enter_new_password"),

  RETYPE_NEW_PASSWORD("retype_new_password"),

  PASSWORD_VERIFIED("password_verified"),
  PASSWORD_FAILED("password_failed"),

  BITCOIN_AMOUNT("bitcoin_amount"),
  BITCOIN_AMOUNT_TOOLTIP("bitcoin_amount_tooltip"),
  LOCAL_AMOUNT("local_amount"),
  LOCAL_AMOUNT_TOOLTIP("local_amount_tooltip"),
  LOCAL_AMOUNT_INCLUDING_FEES("local_amount_including_fees"),
  AMOUNT_PAID("amount_paid"),
  TRANSACTION_FEE("transaction_fee"),

  CLIENT_FEE("client_fee"),
  CLIENT_FEE_NOW("client_fee_now"),
  CLIENT_FEE_LATER_PLURAL("client_fee_later_plural"),
  CLIENT_FEE_LATER_SINGULAR("client_fee_later_singular"),
  CLIENT_FEE_OVERPAID("client_fee_overpaid"),

  COINBASE("coinbase"),

  NAME("name"),
  NAME_TOOLTIP("name_tooltip"),
  NAMES("names"),
  EMAIL_ADDRESS("email_address"),
  EMAIL_ADDRESS_TOOLTIP("email_address_tooltip"),
  BITCOIN_ADDRESS("bitcoin_address"),
  BITCOIN_ADDRESS_TOOLTIP("bitcoin_address_tooltip"),
  EXTENDED_PUBLIC_KEY("extended_public_key"),
  EXTENDED_PUBLIC_KEY_TOOLTIP("extended_public_key_tooltip"),
  TAGS("tags"),
  TAGS_TOOLTIP("tags_tooltip"),
  EXAMPLE("example"),
  VERSION("version"),

  VISIT_WEBSITE("visit_website"),
  VISIT_WEBSITE_TOOLTIP("visit_website_tooltip"),

  DISPLAY_LANGUAGE("display_language"),

  SHOW_BALANCE("show_balance"),
  SHOW_BALANCE_TOOLTIP("show_balance_tooltip"),

  SELECT_THEME("select_theme"),
  SELECT_THEME_TOOLTIP("select_theme_tooltip"),

  SELECT_DECIMAL_SEPARATOR("select_decimal_separator"),
  SELECT_DECIMAL_SEPARATOR_TOOLTIP("select_decimal_separator_tooltip"),

  SELECT_GROUPING_SEPARATOR("select_grouping_separator"),
  SELECT_GROUPING_SEPARATOR_TOOLTIP("select_grouping_separator_tooltip"),

  SELECT_LOCAL_SYMBOL("select_local_symbol"),
  SELECT_LOCAL_SYMBOL_TOOLTIP("select_local_symbol_tooltip"),

  SELECT_BITCOIN_SYMBOL("select_bitcoin_symbol"),
  SELECT_BITCOIN_SYMBOL_TOOLTIP("select_bitcoin_symbol_tooltip"),

  SELECT_PLACEMENT("select_placement"),
  SELECT_PLACEMENT_TOOLTIP("select_placement_tooltip"),

  SELECT_EXCHANGE_RATE_PROVIDER("select_exchange_rate_provider"),
  SELECT_EXCHANGE_RATE_PROVIDER_TOOLTIP("select_exchange_rate_provider_tooltip"),

  SELECT_LOCAL_CURRENCY("select_local_currency"),
  SELECT_LOCAL_CURRENCY_TOOLTIP("select_local_currency_tooltip"),

  ENTER_ACCESS_CODE("enter_access_code"),
  ENTER_ACCESS_CODE_TOOLTIP("enter_access_code_tooltip"),
  EXCHANGE_RATE_LABEL("exchange_rate_label"),

  NOT_AVAILABLE("not_available"),

  NO_CLIENT_FEE_WAS_ADDED("no_client_fee_was_added"),

  ALERT_SOUND("alert_sound"),
  ALERT_SOUND_TOOLTIP("alert_sound_tooltip"),

  RECEIVE_SOUND("receive_sound"),
  RECEIVE_SOUND_TOOLTIP("receive_sound_tooltip"),

  PLAY_SOUND("play_sound"),
  PLAY_SOUND_TOOLTIP("play_sound_tooltip"),

  SELECT_TOR("select_tor"),
  SELECT_TOR_TOOLTIP("select_tor_tooltip"),

  SELECT_TREZOR("select_trezor"),
  SELECT_TREZOR_TOOLTIP("select_trezor_tooltip"),

  // Sidebar

  SIDEBAR_TREE("sidebar_tree"),
  WALLET("wallet"),
  SEND_OR_REQUEST("send_or_request"),

  CONTACTS("contacts"),
  CONTACTS_TOOLTIP("contacts_tooltip"),

  PAYMENTS("payments"),

  PREFERENCES("preferences"),
  MANAGE_WALLET("manage_wallet"),
  TOOLS("tools"),

  EXIT_OR_SWITCH("exit_or_switch"),
  EXIT_WALLET("exit_wallet"),
  SWITCH_WALLET("switch_wallet"),

  // Tools
  HISTORY("history"),
  HISTORY_TOOLTIP("history_tooltip"),

  // Placeholders
  APPROXIMATELY("approximately"),
  PARENTHESES("parentheses"),
  LIST_COMMA("list_comma"),
  LIST_ELLIPSIS("list_ellipsis"),
  DECIMAL_COMMA("decimal_comma"),
  DECIMAL_POINT("decimal_point"),
  DECIMAL_SPACE("decimal_space"),

  BITCOIN_URI_ALERT("bitcoin_uri_label"),
  PAYMENT_RECEIVED_ALERT("payment_received_label"),
  PAYMENT_SENT_ALERT("payment_sent_label"),

  // Trezor alerts
  TREZOR_ATTACHED_ALERT("trezor_attached_alert"),
  TREZOR_DETACHED_ALERT("trezor_detached_alert"),
  TREZOR_STOPPED_ALERT("trezor_stopped_alert"),
  TREZOR_FAILURE_ALERT("trezor_failure_alert"),

  PEER_COUNT("peer_count"),
  BLOCKS_LEFT("blocks_left"),

  @Deprecated
  LAST_TRANSACTION_ID("last_transaction_id"),

  @Deprecated
  TRANSACTION_COUNT("transaction_count"),

  // Panels

  NETWORK_CONFIGURATION_ERROR("network_configuration_error"),
  HELP("help"),

  SETTINGS("settings"),
  SETTINGS_TOOLTIP("settings_tooltip"),

  // Titles

  MULTIBIT_HD_TITLE("multibit_hd_title"),

  // Send/request
  REQUEST_BITCOIN_TITLE("request_bitcoin_title"),

  SEND_BITCOIN_TITLE("send_bitcoin_title"),
  CONFIRM_SEND_TITLE("confirm_send_title"),
  SEND_PROGRESS_TITLE("send_progress_title"),

  EXIT_OR_SWITCH_TITLE("exit_or_switch_title"),

  ABOUT_TITLE("about_title"),

  SECURITY_TITLE("security_title"),

  WELCOME_TITLE("welcome_title"),
  SELECT_LANGUAGE_TITLE("select_language_title"),
  SELECT_WALLET_TITLE("select_wallet_title"),

  CREATE_WALLET_PREPARATION_TITLE("create_wallet_preparation_title"),
  SELECT_BACKUP_LOCATION_TITLE("select_backup_location_title"),
  CREATE_WALLET_SEED_PHRASE_TITLE("create_wallet_seed_phrase_title"),
  CONFIRM_WALLET_SEED_PHRASE_TITLE("confirm_wallet_seed_phrase_title"),
  CREATE_WALLET_PASSWORD_TITLE("create_wallet_password_title"),
  CREATE_WALLET_REPORT_TITLE("create_wallet_report_title"),

  // Trezor
  CREATE_TREZOR_WALLET_PREPARATION_TITLE("create_trezor_wallet_preparation_title"),
  CREATE_TREZOR_WALLET_ENTER_DETAILS_TITLE("create_trezor_wallet_enter_details_title"),
  CREATE_TREZOR_WALLET_REQUEST_CREATE_WALLET_TITLE("create_trezor_wallet_request_create_wallet_title"),

  RESTORE_PASSWORD_SEED_PHRASE_TITLE("restore_password_seed_phrase_title"),
  RESTORE_PASSWORD_BACKUP_LOCATION_TITLE("restore_password_backup_location_title"),
  RESTORE_PASSWORD_SELECT_BACKUP_TITLE("restore_password_select_backup_title"),
  RESTORE_PASSWORD_TIMESTAMP_TITLE("restore_password_timestamp_title"),
  RESTORE_PASSWORD_REPORT_TITLE("restore_password_report_title"),
  RESTORE_PASSWORD_REPORT_MESSAGE_FAIL("restore_password_report_message_fail"),
  RESTORE_PASSWORD_REPORT_MESSAGE_SUCCESS("restore_password_report_message_success"),

  RESTORE_WALLET_SEED_PHRASE_TITLE("restore_wallet_seed_phrase_title"),
  RESTORE_WALLET_BACKUP_LOCATION_TITLE("restore_wallet_backup_location_title"),
  RESTORE_WALLET_SELECT_BACKUP_TITLE("restore_wallet_select_backup_title"),
  RESTORE_WALLET_TIMESTAMP_TITLE("restore_wallet_timestamp_title"),
  RESTORE_WALLET_REPORT_TITLE("restore_wallet_report_title"),

  NEW_CONTACT_TITLE("new_contact_title"),
  EDIT_CONTACT_TITLE("edit_contact_title"),
  EDIT_CONTACTS_TITLE("edit_contacts_title"),

  NEW_HISTORY_ENTRY_TITLE("new_history_entry_title"),
  EDIT_HISTORY_ENTRY_TITLE("edit_history_entry_title"),
  EDIT_HISTORY_ENTRIES_TITLE("edit_history_entries_title"),

  PASSWORD_TITLE("password_title"),

  PASSWORD_UNLOCK("password_unlock"),
  PASSWORD_UNLOCK_TOOLTIP("password_unlock_tooltip"),

  // Credentials
  TREZOR_UNLOCK_TITLE("trezor_unlock_title"),
  PIN_TITLE("pin_title"),
  CHANGE_PIN_TITLE("change_pin_title"),
  CHANGE_PIN_REPORT_TITLE("change_pin_report_title"),
  PIN_INTRODUCTION("pin_introduction"),

  PIN_FAILURE("pin_failure"),
  PIN_SUCCESS("pin_success"),

  // Use Trezor
  TREZOR_CONFIRM_ADD_PIN_TITLE("trezor_confirm_add_pin_title"),
  TREZOR_CONFIRM_CHANGE_PIN_TITLE("trezor_confirm_change_pin_title"),
  TREZOR_CONFIRM_REMOVE_PIN_TITLE("trezor_confirm_remove_pin_title"),
  TREZOR_PRESS_CONFIRM_TITLE("trezor_press_confirm_title"),
  TREZOR_PRESS_NEXT_TITLE("trezor_press_next_title"),

  // Operations
  TREZOR_PRESS_CONFIRM_OPERATION("trezor_press_confirm_operation"),
  TREZOR_PRESS_NEXT_OPERATION("trezor_press_next_operation"),
  TREZOR_NO_WALLET_OPERATION("trezor_no_wallet_operation"),
  TREZOR_FAILURE_OPERATION("trezor_failure_operation"),
  TREZOR_REMOVE_PIN_OPERATION("trezor_remove_pin_operation"),
  SEARCHING_FOR_A_CONNECTED_TREZOR_OPERATION("searching_for_a_connected_trezor_operation"),
  COMMUNICATING_WITH_TREZOR_OPERATION("communicating_with_trezor_operation"),

  // Recovery
  TREZOR_FAILURE_RECOVERY("trezor_failure_recovery"),

  // Reports
  TREZOR_CHANGE_PIN_SUCCESS("trezor_change_pin_success"),
  TREZOR_REMOVE_PIN_SUCCESS("trezor_remove_pin_success"),
  TREZOR_WIPE_DEVICE_SUCCESS("trezor_wipe_device_success"),

  TREZOR_INCORRECT_PIN_FAILURE("trezor_incorrect_pin_failure"),
  TREZOR_WIPE_DEVICE_FAILURE("trezor_wipe_device_failure"),

  // Trezor display text
  TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY("trezor_encrypt_multibit_hd_unlock_display"),
  TREZOR_ADD_PIN_DISPLAY("trezor_add_pin_display"),
  TREZOR_CHANGE_PIN_DISPLAY("trezor_change_pin_display"),
  TREZOR_REMOVE_PIN_DISPLAY("trezor_remove_pin_display"),
  TREZOR_WORD_DISPLAY("trezor_word_display"),
  TREZOR_CHECK_WORD_DISPLAY("trezor_check_word_display"),
  TREZOR_TRANSACTION_OUTPUT_CONFIRM_DISPLAY("trezor_transaction_output_confirm_display"),
  TREZOR_SIGN_CONFIRM_DISPLAY("trezor_sign_confirm_display"),
  TREZOR_WIPE_CONFIRM_DISPLAY("trezor_wipe_confirm_display"),

  USE_TREZOR_REPORT_TITLE("use_trezor_report_title"),
  USE_TREZOR_REPORT_MESSAGE_SUCCESS("use_trezor_report_message_success"),
  USE_TREZOR_REPORT_MESSAGE_FAILURE("use_trezor_report_message_failure"),

  USE_TREZOR_WALLET("use_trezor_wallet"),

  VERIFY_DEVICE("verify_device"),
  VERIFY_DEVICE_TITLE("verify_device_title"),
  WIPE_DEVICE("wipe_device"),
  WIPE_DEVICE_TITLE("wipe_device_title"),

  BUY_TREZOR_TITLE("buy_trezor_title"),
  BUY_TREZOR_COMMENT("buy_trezor_comment"),
  BUY_TREZOR("buy_trezor"),
  BUY_TREZOR_TOOLTIP("buy_trezor_tooltip"),

  TREZOR_FOUND("trezor_found"),
  NO_TREZOR_FOUND("no_trezor_found"),

  TREZOR_TRANSACTION_CREATED_OPERATION("trezor_transaction_created"),

  ENTER_TREZOR_LABEL("enter_trezor_label"),
  ENTER_TREZOR_LABEL_TOOLTIP("enter_trezor_label_tooltip"),

  // Settings titles

  LANGUAGE_SETTINGS_TITLE("language_settings_title"),
  EXCHANGE_SETTINGS_TITLE("exchange_settings_title"),
  UNITS_SETTINGS_TITLE("units_settings_title"),
  APPEARANCE_SETTINGS_TITLE("appearance_settings_title"),
  SOUNDS_SETTINGS_TITLE("sounds_settings_title"),
  FEES_SETTINGS_TITLE("fees_settings_title"),
  LABS_SETTINGS_TITLE("labs_settings_title"),

  // Manage wallet titles

  EDIT_WALLET_TITLE("edit_wallet_title"),
  WALLET_DETAILS_TITLE("wallet_details_title"),

  CHANGE_PASSWORD_TITLE("change_password_title"),

  CHANGE_PIN_SELECT_OPTION_TITLE("change_pin_select_option_title"),
  CHANGE_PIN_OPTION("change_pin_option"),
  REMOVE_PIN_OPTION("remove_pin_option"),
  CHANGE_PIN_ENTER_CURRENT_PIN_TITLE("change_pin_enter_current_pin_title"),
  CHANGE_PIN_ENTER_NEW_PIN_TITLE("change_pin_enter_new_pin_title"),
  CHANGE_PIN_CONFIRM_NEW_PIN_TITLE("change_pin_confirm_new_pin_title"),
  CHANGE_PIN_CONFIRM_CHANGE_PIN_TITLE("change_pin_confirm_change_pin_title"),

  REPAIR_WALLET_TITLE("repair_wallet_title"),
  REPAIR_WALLET_PROGRESS_TITLE("repair_wallet_progress_title"),

  EMPTY_WALLET_TITLE("empty_wallet_title"),
  EMPTY_WALLET_CONFIRM_TITLE("empty_wallet_confirm_title"),
  EMPTY_WALLET_PROGRESS_TITLE("empty_wallet_progress_title"),

  // Tools titles

  SIGN_MESSAGE_TITLE("sign_message_title"),
  VERIFY_MESSAGE_TITLE("verify_message_title"),

  VERIFY_NETWORK_TITLE("verify_network_title"),
  USE_TREZOR_TITLE("use_trezor_title"),

  // Labels
  CONFIRM_SEND_MESSAGE("confirm_send_message"),

  EXCHANGE_RATE_PROVIDER("exchange_rate_provider"),
  EXCHANGE_RATE_PROVIDER_TOOLTIP("exchange_rate_provider_tooltip"),

  BROADCAST_STATUS("broadcast_status"),
  RELAY_STATUS("relay_status"),
  CONFIRMATION_STATUS("confirmation_status"),
  VERIFICATION_STATUS("verification_status"),
  EXCHANGE_RATE_STATUS_OK("exchange_rate_status_ok"),
  EXCHANGE_RATE_STATUS_WARN("exchange_rate_status_warn"),
  SEED_PHRASE_CREATED_STATUS("seed_phrase_created_status"),
  WALLET_PASSWORD_CREATED_STATUS("wallet_password_created_status"),
  BACKUP_LOCATION_STATUS("backup_location_status"),
  SELECT_BACKUP("select_backup"),
  WALLET_CREATED_STATUS("wallet_created_status"),
  CACERTS_INSTALLED_STATUS("cacerts_installed_status"),
  WALLET_REPAIRED_STATUS("wallet_repaired_status"),
  SYNCHRONIZING_STATUS("synchronizing_status"),

  ADDRESS_IS_MINE_STATUS("address_is_mine_status"),

  PASSWORD_CHANGED_STATUS("password_changed_status"),

  HISTORY_WALLET_CREATED("history_wallet_created"),
  HISTORY_WALLET_OPENED("history_wallet_opened"),

  ACCEPT_LICENCE("accept_licence"),
  REJECT_LICENCE("reject_licence"),

  ALERT_REMAINING("alert_remaining"),
  EXCHANGE_FIAT_RATE("exchange_fiat_rate"),
  EXCHANGE_FIAT_RATE_WITH_PROVIDER("exchange_fiat_rate_with_provider"),

  CLICK_NEXT_TO_CONTINUE("click_next_to_continue"),
  CLICK_FINISH_TO_CONTINUE("click_finish_to_continue"),

  SELECT_LANGUAGE("select_language"),
  SELECT_LANGUAGE_TOOLTIP("select_language_tooltip"),

  SELECT_WALLET_TYPE("select_wallet_type"),
  SELECT_WALLET_TYPE_TOOLTIP("select_wallet_type_tooltip"),
  SELECT_WALLET_TYPE_BIP32("select_wallet_type_bip32"),
  SELECT_WALLET_TYPE_BIP44("select_wallet_type_bip44"),

  SEED_SIZE("seed_size"),
  SEED_SIZE_TOOLTIP("seed_size_tooltip"),

  SEED_PHRASE("seed_phrase"),
  SEED_PHRASE_TOOLTIP("seed_phrase_tooltip"),

  TIMESTAMP("timestamp"),
  TIMESTAMP_TOOLTIP("timestamp_tooltip"),

  TIMESTAMP_NOTE_1("timestamp_note_1"),
  TIMESTAMP_NOTE_2("timestamp_note_2"),

  MULTI_EDIT_NOTE("multi_edit_note"),

  TRANSACTION_CONSTRUCTION_STATUS_SUMMARY("transaction_construction_status_summary"),
  TRANSACTION_CONSTRUCTION_STATUS_DETAIL("transaction_construction_status_detail"),
  TRANSACTION_BROADCAST_STATUS_SUMMARY("transaction_broadcast_status_summary"),
  TRANSACTION_BROADCAST_STATUS_DETAIL("transaction_broadcast_status_detail"),
  TRANSACTION_CONFIRMATION_STATUS("transaction_confirmation_status"),

  // Tool buttons
  SHOW_WELCOME_WIZARD("show_welcome_wizard"),
  SHOW_WELCOME_WIZARD_TOOLTIP("show_welcome_wizard_tooltip"),

  SHOW_SEND_WIZARD("show_send_wizard"),
  SHOW_SEND_WIZARD_TOOLTIP("show_send_wizard_tooltip"),

  SHOW_REQUEST_WIZARD("show_request_wizard"),
  SHOW_REQUEST_WIZARD_TOOLTIP("show_request_wizard_tooltip"),

  SHOW_SOUNDS_WIZARD("show_sounds_wizard"),
  SHOW_SOUNDS_WIZARD_TOOLTIP("show_sounds_wizard_tooltip"),

  SHOW_LABS_WIZARD("show_labs_wizard"),
  SHOW_LABS_WIZARD_TOOLTIP("show_labs_wizard_tooltip"),

  SHOW_UNITS_WIZARD("show_units_wizard"),
  SHOW_UNITS_WIZARD_TOOLTIP("show_units_wizard_tooltip"),

  SHOW_EXCHANGE_WIZARD("show_exchange_wizard"),
  SHOW_EXCHANGE_WIZARD_TOOLTIP("show_exchange_wizard_tooltip"),

  SHOW_APPEARANCE_WIZARD("show_appearance_wizard"),
  SHOW_APPEARANCE_WIZARD_TOOLTIP("show_appearance_wizard_tooltip"),

  SHOW_LANGUAGE_WIZARD("show_language_wizard"),
  SHOW_LANGUAGE_WIZARD_TOOLTIP("show_language_wizard_tooltip"),

  SHOW_EDIT_WALLET_WIZARD("show_edit_wallet_wizard"),
  SHOW_EDIT_WALLET_WIZARD_TOOLTIP("show_edit_wallet_wizard_tooltip"),

  SHOW_CHANGE_PASSWORD_WIZARD("show_change_password_wizard"),
  SHOW_CHANGE_PASSWORD_WIZARD_TOOLTIP("show_change_password_wizard_tooltip"),

  SHOW_CHANGE_PIN_WIZARD("show_change_pin_wizard"),
  SHOW_CHANGE_PIN_WIZARD_TOOLTIP("show_change_pin_wizard_tooltip"),

  SHOW_VERIFY_NETWORK_WIZARD("show_verify_network_wizard"),
  SHOW_VERIFY_NETWORK_WIZARD_TOOLTIP("show_verify_network_wizard_tooltip"),

  SHOW_REPAIR_WALLET_WIZARD("show_repair_wallet_wizard"),
  SHOW_REPAIR_WALLET_WIZARD_TOOLTIP("show_repair_wallet_wizard_tooltip"),

  SHOW_WALLET_DETAILS_WIZARD("show_wallet_details_wizard"),
  SHOW_WALLET_DETAILS_WIZARD_TOOLTIP("show_wallet_details_wizard_tooltip"),

  SHOW_EMPTY_WALLET_WIZARD("show_empty_wallet_wizard"),
  SHOW_EMPTY_WALLET_WIZARD_TOOLTIP("show_empty_wallet_wizard_tooltip"),

  SHOW_SIGN_WIZARD("show_sign_wizard"),
  SHOW_SIGN_WIZARD_TOOLTIP("show_sign_wizard_tooltip"),

  SHOW_VERIFY_WIZARD("show_verify_wizard"),
  SHOW_VERIFY_WIZARD_TOOLTIP("show_verify_wizard_tooltip"),

  SHOW_ABOUT_WIZARD("show_about_wizard"),
  SHOW_ABOUT_WIZARD_TOOLTIP("show_about_wizard_tooltip"),

  SHOW_TREZOR_TOOLS_WIZARD("show_trezor_tools_wizard"),
  SHOW_TREZOR_TOOLS_WIZARD_TOOLTIP("show_trezor_tools_wizard_tooltip"),

  SHOW_ALERT("show_alert"),
  HIDE_ALERT("hide_alert"),

  // Radio buttons
  CREATE_WALLET("create_wallet"),
  RESTORE_PASSWORD("restore_password"),
  RESTORE_WALLET("restore_wallet"),
  USE_EXISTING_WALLET("use_existing_wallet"),
  TREZOR_CREATE_WALLET("trezor_create_wallet"),

  RESTORE_FROM_SEED_PHRASE("restore_from_seed_phrase"),
  RESTORE_FROM_BACKUP("restore_from_backup"),

  CLOUD_BACKUP_LOCATION("cloud_backup_location"),

  WALLET_DEFAULT_NOTES("wallet_default_notes"),


  // Notes (can contain HTML),

  WELCOME_NOTE_1("welcome_note_1"),
  WELCOME_NOTE_2("welcome_note_2"),
  WELCOME_NOTE_3("welcome_note_3"),

  ABOUT_NOTE_1("about_note_1"),
  ABOUT_NOTE_2("about_note_2"),
  ABOUT_NOTE_3("about_note_3"),

  SELECT_BACKUP_NOTE_1("select_backup_note_1"),
  SELECT_BACKUP_NOTE_1_TOOLTIP("select_backup_note_1_tooltip"),
  SELECT_BACKUP_NOTE_2("select_backup_note_2"),

  PREPARATION_NOTE_1("preparation_note_1"),
  PREPARATION_NOTE_2("preparation_note_2"),
  PREPARATION_NOTE_3("preparation_note_3"),
  PREPARATION_NOTE_4("preparation_note_4"),
  PREPARATION_NOTE_5("preparation_note_5"),
  PREPARATION_NOTE_6("preparation_note_6"),

  TREZOR_PREPARATION_NOTE_1("trezor_preparation_note_1"),
  TREZOR_PREPARATION_NOTE_2("trezor_preparation_note_2"),
  TREZOR_PREPARATION_NOTE_3("trezor_preparation_note_3"),
  TREZOR_PREPARATION_NOTE_4("trezor_preparation_note_4"),
  TREZOR_PREPARATION_NOTE_5("trezor_preparation_note_5"),
  TREZOR_PREPARATION_NOTE_6("trezor_preparation_note_6"),

  SELECT_BACKUP_LOCATION_NOTE_1("select_backup_location_note_1"),
  SELECT_BACKUP_LOCATION_NOTE_2("select_backup_location_note_2"),
  SELECT_BACKUP_LOCATION_NOTE_3("select_backup_location_note_3"),
  SELECT_BACKUP_LOCATION_NOTE_4("select_backup_location_note_4"),

  SEED_WARNING_NOTE_1("seed_warning_note_1"),
  SEED_WARNING_NOTE_2("seed_warning_note_2"),
  SEED_WARNING_NOTE_3("seed_warning_note_3"),
  SEED_WARNING_NOTE_4("seed_warning_note_4"),
  SEED_WARNING_NOTE_5("seed_warning_note_5"),

  CONFIRM_SEED_PHRASE_NOTE_1("confirm_seed_phrase_note_1"),
  CONFIRM_SEED_PHRASE_NOTE_2("confirm_seed_phrase_note_2"),
  CONFIRM_SEED_PHRASE_NOTE_3("confirm_seed_phrase_note_3"),

  WALLET_PASSWORD_NOTE_1("wallet_password_note_1"),
  WALLET_PASSWORD_NOTE_2("wallet_password_note_2"),
  WALLET_PASSWORD_NOTE_3("wallet_password_note_3"),

  RESTORE_METHOD_NOTE_1("restore_method_note_1"),
  RESTORE_METHOD_NOTE_2("restore_method_note_2"),
  RESTORE_METHOD_NOTE_3("restore_method_note_3"),
  RESTORE_METHOD_NOTE_4("restore_method_note_4"),

  RESTORE_BACKUP_NOTE_1("restore_backup_note_1"),
  RESTORE_BACKUP_NOTE_2("restore_backup_note_2"),
  RESTORE_BACKUP_NOTE_3("restore_backup_note_3"),

  RESTORE_FROM_SEED_PHRASE_NOTE_1("restore_from_seed_phrase_note_1"),
  RESTORE_FROM_SEED_PHRASE_NOTE_2("restore_from_seed_phrase_note_2"),
  RESTORE_FROM_SEED_PHRASE_NOTE_3("restore_from_seed_phrase_note_3"),

  RESTORE_TIMESTAMP_NOTE_1("restore_timestamp_note_1"),
  RESTORE_TIMESTAMP_NOTE_2("restore_timestamp_note_2"),
  RESTORE_TIMESTAMP_NOTE_3("restore_timestamp_note_3"),

  RESTORE_PASSWORD_NOTE_1("restore_password_note_1"),

  PASSWORD_NOTE_1("password_note_1"),

  SELECT_WALLET_NOTE_1("select_wallet_note_1"),

  RESTORE_WALLET_NOTE_1("restore_wallet_note_1"),

  CHANGE_PASSWORD_NOTE_1("change_password_note_1"),
  CHANGE_PASSWORD_NOTE_2("change_password_note_2"),

  ENTER_CURRENT_PIN("enter_current_pin"),
  ENTER_NEW_PIN("enter_new_pin"),
  CONFIRM_NEW_PIN("confirm_new_pin"),
  ENTER_PIN_LOOK_AT_DEVICE("enter_pin_look_at_device"),

  VERIFY_NETWORK_NOTE_1("verify_network_note_1"),
  VERIFY_NETWORK_NOTE_2("verify_network_note_2"),

  UNITS_SETTINGS_NOTE_1("units_settings_note_1"),

  EXCHANGE_SETTINGS_NOTE_1("exchange_settings_note_1"),

  LANGUAGE_CHANGE_NOTE_1("language_change_note_1"),

  THEME_CHANGE_NOTE_1("theme_change_note_1"),

  SOUND_CHANGE_NOTE_1("sound_change_note_1"),

  LAB_CHANGE_NOTE_1("lab_change_note_1"),

  DATA_ENTERED_NOTE_1("data_entered_note_1"),
  DATA_ENTERED_NOTE_2("data_entered_note_2"),

  SIGN_MESSAGE_NOTE_1("sign_message_note_1"),
  SIGN_MESSAGE_NOTE_2("sign_message_note_2"),
  SIGN_MESSAGE_NOTE_3("sign_message_note_3"),

  VERIFY_MESSAGE_NOTE_1("verify_message_note_1"),
  VERIFY_MESSAGE_NOTE_2("verify_message_note_2"),

  RECIPIENT_SUMMARY("recipient_summary"),

  AMOUNT_SUMMARY_WITH_RATE("amount_summary_with_rate"),
  AMOUNT_SUMMARY_NO_RATE("amount_summary_no_rate"),

  // Wallet detail panel
  APPLICATION_DIRECTORY("application_directory"),
  WALLET_DIRECTORY("wallet_directory"),

  // Payments panel and wizard
  DETAILS("details"),
  DETAILS_TOOLTIP("details_tooltip"),

  EXPORT("export"),
  EXPORT_TOOLTIP("export_tooltip"),

  DELETE_PAYMENT_REQUEST("delete_payment_request"),
  DELETE_PAYMENT_REQUEST_TOOLTIP("delete_payment_request_tooltip"),

  TRANSACTION_OVERVIEW("transaction_overview"),
  TRANSACTION_AMOUNT("transaction_amount"),
  TRANSACTION_DETAIL("transaction_detail"),

  CHOOSE_PAYMENT_REQUEST("choose_payment_request"),
  CHOOSE_PAYMENT_REQUEST_TOOLTIP("choose_payment_request_tooltip"),

  CHOOSE_PAYMENT_REQUEST_LABEL("choose_payment_request_label"),
  PAYMENT_REQUEST_INFO_SINGULAR("payment_request_info_singular"),
  PAYMENT_REQUEST_INFO_PLURAL("payment_request_info_plural"),
  MATCHING_PAYMENT_REQUEST("matching_payment_request"),
  PAYMENT_REQUEST("payment_request"),
  DATE("date"),
  STATUS("status"),
  TYPE("type"),
  FIAT_AMOUNT("fiat_amount"),
  FIAT_CURRENCY("fiat_currency"),

  DESCRIPTION("description"),
  DESCRIPTION_READ_ONLY("description_read_only"),

  TRANSACTION_HASH("transaction_hash"),
  RAW_TRANSACTION("raw_transaction"),

  VIEW_IN_BLOCKCHAIN_INFO("view_in_blockchain_info"),
  VIEW_IN_BLOCKCHAIN_INFO_TOOLTIP("view_in_blockchain_info_tooltip"),

  VIEW_IN_EXTERNAL_BROWSER("view_in_external_browser"),
  VIEW_IN_EXTERNAL_BROWSER_TOOLTIP("view_in_external_browser_tooltip"),

  SIZE("size"),
  SIZE_VALUE("size_value"),


  // Themes
  LIGHT_THEME("light_theme"),
  DARK_THEME("dark_theme"),
  BOOTSTRAP_THEME("bootstrap_theme"),

  // Date rendering
  TODAY("today"),
  YESTERDAY("yesterday"),

  // Export payments
  SELECT_EXPORT_PAYMENTS_LOCATION("select_export_payments_location"),
  EXPORT_PAYMENTS_REPORT("export_payments_report"),
  SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_1("select_export_payments_location_note_1"),
  SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_2("select_export_payments_location_note_2"),
  SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_3("select_export_payments_location_note_3"),
  SELECT_EXPORT_PAYMENTS_LOCATION_NOTE_4("select_export_payments_location_note_4"),
  EXPORT_TRANSACTIONS_STEM("export_transactions_stem"),
  EXPORT_PAYMENT_REQUESTS_STEM("export_payment_requests_stem"),
  EXPORT_WAS_SUCCESSFUL("export_was_successful"),
  TRANSACTIONS_WERE_EXPORTED_TO_THE_FILE("transactions_were_exported_to_the_file"),
  PAYMENT_REQUESTS_WERE_EXPORTED_TO_THE_FILE("payment_requests_were_exported_to_the_file"),
  COULD_NOT_WRITE_TO_THE_DIRECTORY("could_not_write_to_the_directory"),

  // Repair wallet
  REPAIR_WALLET_NOTE_1("repair_wallet_note_1"),
  REPAIR_WALLET_NOTE_2("repair_wallet_note_2"),
  REPAIR_WALLET_NOTE_3("repair_wallet_note_3"),

  SPENDABLE_BALANCE_IS_LOWER("spendable_balance_is_lower"),

  // End of enum
  ;

  private final String key;

  private MessageKey(String key) {
    this.key = key;
  }

  /**
   * @return The key for use with the resource bundles
   */

  public String getKey() {
    return key;
  }

}
