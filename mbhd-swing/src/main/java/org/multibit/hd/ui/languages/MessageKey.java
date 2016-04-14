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

  CREATE("create"),
  CREATE_TOOLTIP("create_tooltip"),

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

  UPLOAD_ERROR_REPORT("upload_error_report"),
  UPLOAD_ERROR_REPORT_TOOLTIP("upload_error_report_tooltip"),

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
  DEFAULT("default"),

  QR_CODE("qr_code"),
  QR_CODE_TOOLTIP("qr_code_tooltip"),

  // Adjectives
  LEADING("leading"),
  TRAILING("trailing"),

  // Adverbs
  WORKING("working"),

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
  AT_GAP_LIMIT("at_gap_limit"),
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
  TRANSACTION_FEE_RATE("transaction_fee_rate"),
  ADJUST_TRANSACTION_FEE("adjust_transaction_fee"),
  EXPLAIN_TRANSACTION_FEE1("explain_transaction_fee1"),
  EXPLAIN_TRANSACTION_FEE2("explain_transaction_fee2"),
  TRANSACTION_FEE_CHOSEN("transaction_fee_chosen"),
  LOWER("lower"),
  HIGHER("higher"),

  DONATE_NOW("donate_now"),

  CLIENT_FEE("client_fee"),
  CLIENT_FEE_NOW("client_fee_now"),
  CLIENT_FEE_LATER_PLURAL("client_fee_later_plural"),
  CLIENT_FEE_LATER_SINGULAR("client_fee_later_singular"),
  CLIENT_FEE_OVERPAID("client_fee_overpaid"),
  CLIENT_FEE_RUNNING_TOTAL("client_fee_running_total"),

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

  SHOW_ATOM_FEED_ALERT("show_atom_feed_alert"),
  SHOW_ATOM_FEED_ALERT_TOOLTIP("show_atom_feed_alert_tooltip"),

  BLOCK_EXPLORER("block_explorer"),

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
  NOT_AVAILABLE_TOOLTIP("not_available_tooltip"),

  NO_CLIENT_FEE_WAS_ADDED("no_client_fee_was_added"),

  ALERT_SOUND("alert_sound"),
  ALERT_SOUND_TOOLTIP("alert_sound_tooltip"),

  RECEIVE_SOUND("receive_sound"),
  RECEIVE_SOUND_TOOLTIP("receive_sound_tooltip"),

  PLAY_SOUND("play_sound"),
  PLAY_SOUND_TOOLTIP("play_sound_tooltip"),

  SELECT_SHOW_RESTORE_BETA7_WALLETS("select_show_restore_beta7_wallets"),
  SELECT_SHOW_RESTORE_BETA7_WALLETSTOOLTIP("select_show_restore_beta7_wallets_tooltip"),

   // Sidebar

  SIDEBAR_TREE("sidebar_tree"),
  WALLET("wallet"),

  BUY_OR_SELL("buy_or_sell"),
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

  // Placeholders
  APPROXIMATELY("approximately"),
  PARENTHESES("parentheses"),
  LIST_COMMA("list_comma"),
  LIST_ELLIPSIS("list_ellipsis"),
  DECIMAL_COMMA("decimal_comma"),
  DECIMAL_POINT("decimal_point"),
  DECIMAL_SPACE("decimal_space"),

  // Payment Protocol messages
  PAYMENT_PROTOCOL_TRUSTED_NOTE("payment_protocol_trusted_note"),
  PAYMENT_PROTOCOL_UNTRUSTED_NOTE("payment_protocol_untrusted_note"),
  PAYMENT_PROTOCOL_ERROR_NOTE("payment_protocol_error_note"),
  PAYMENT_PROTOCOL_TRUSTED_ALERT("payment_protocol_trusted_alert"),
  PAYMENT_PROTOCOL_UNTRUSTED_ALERT("payment_protocol_untrusted_alert"),
  PAYMENT_PROTOCOL_ERROR_ALERT("payment_protocol_error_alert"),

  PAY_THIS_PAYMENT_REQUEST("pay_this_payment_request"),
  PAY_THIS_PAYMENT_REQUEST_TOOLTIP("pay_this_payment_request_tooltip"),

  BITCOIN_URI_ALERT("bitcoin_uri_label"),
  PAYMENT_RECEIVED_ALERT("payment_received_label"),
  PAYMENT_SENT_ALERT("payment_sent_label"),

  PEER_COUNT("peer_count"),
  BLOCKS_LEFT("blocks_left"),

  @Deprecated
  LAST_TRANSACTION_ID("last_transaction_id"),

  @Deprecated
  TRANSACTION_COUNT("transaction_count"),

  // Panels

  GENERAL_NETWORK_CONFIGURATION_ERROR("general_network_configuration_error"),
  BITCOIN_NETWORK_CONFIGURATION_ERROR("bitcoin_network_configuration_error"),
  HELP("help"),

  SETTINGS("settings"),
  SETTINGS_TOOLTIP("settings_tooltip"),

  // Titles

  MULTIBIT_HD_TITLE("multibit_hd_title"),

  ERROR_REPORTING_TITLE("error_reporting_title"),

  ERROR_REPORTING_APOLOGY_NOTE_1("error_reporting_apology_note_1"),
  ERROR_REPORTING_APOLOGY_NOTE_2("error_reporting_apology_note_2"),
  ERROR_REPORTING_APOLOGY_NOTE_3("error_reporting_apology_note_3"),

  ERROR_REPORTING_MANUAL_NOTE_1("error_reporting_manual_note_1"),
  ERROR_REPORTING_MANUAL_NOTE_2("error_reporting_manual_note_2"),

  ERROR_REPORTING_CONTENTS("error_reporting_contents"),
  ERROR_REPORTING_NOTES("error_reporting_notes"),
  ERROR_REPORTING_UPLOADING("error_reporting_uploading"),
  ERROR_REPORTING_UPLOAD_COMPLETE("error_reporting_upload_complete"),
  ERROR_REPORTING_UPLOAD_FAILED("error_reporting_upload_failed"),

  // Buy/sell
  BUY_SELL_TITLE("buy_sell_title"),
  BUY_SELL_REGION_1("buy_sell_region_1"),
  BUY_SELL_REGION_2("buy_sell_region_2"),
  BUY_SELL_REGION_NONE("buy_sell_region_none"),
  BUY_VISIT_GLIDERA("buy_visit_glidera"),
  BUY_VISIT_GLIDERA_TOOLTIP("buy_visit_glidera_tooltip"),
  BUY_VISIT_GLIDERA_EXPLAIN("buy_visit_glidera_explain"),
  SELL_VISIT_GLIDERA("sell_visit_glidera"),
  SELL_VISIT_GLIDERA_TOOLTIP("sell_visit_glidera_tooltip"),

  // Send/request
  REQUEST_BITCOIN_TITLE("request_bitcoin_title"),

  SEND_BITCOIN_TITLE("send_bitcoin_title"),
  DISPLAY_PAYMENT_REQUEST_TITLE("display_payment_request_title"),
  DISPLAY_PAYMENT_REQUEST_MEMO_TITLE("display_payment_request_memo_title"),
  CONFIRM_SEND_TITLE("confirm_send_title"),
  SEND_PROGRESS_TITLE("send_progress_title"),
  SEND_PAYMENT_MEMO_TITLE("send_payment_memo_title"),
  SEND_PAYMENT_MEMO_LABEL("send_payment_memo_label"),
  SEND_PAYMENT_MEMO_LABEL_READ_ONLY("send_payment_memo_label_read_only"),

  SEND_PAYMENT_ACK_MEMO_LABEL("send_payment_ack_memo_label"),
  SEND_PAYMENT_ACK_MEMO_TITLE("send_payment_ack_memo_title"),

  NO_MONEY_TO_SEND("no_money_to_send"),

  EXIT_OR_SWITCH_TITLE("exit_or_switch_title"),

  ABOUT_TITLE("about_title"),

  SECURITY_TITLE("security_title"),
  INFO_TITLE("info_title"),

  WELCOME_TITLE("welcome_title"),
  SELECT_LANGUAGE_TITLE("select_language_title"),
  ATTACH_HARDWARE_WALLET_TITLE("attach_hardware_wallet_title"),
  SELECT_WALLET_TITLE("select_wallet_title"),

  CREATE_WALLET_PREPARATION_TITLE("create_wallet_preparation_title"),
  SELECT_BACKUP_LOCATION_TITLE("select_backup_location_title"),
  CREATE_WALLET_SEED_PHRASE_TITLE("create_wallet_seed_phrase_title"),
  CONFIRM_WALLET_SEED_PHRASE_TITLE("confirm_wallet_seed_phrase_title"),
  CREATE_WALLET_PASSWORD_TITLE("create_wallet_password_title"),
  CREATE_WALLET_REPORT_TITLE("create_wallet_report_title"),

  LOAD_WALLET_REPORT_TITLE("load_wallet_report_title"),

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

  PASSWORD_TITLE("password_title"),

  PASSWORD_UNLOCK("password_unlock"),
  PASSWORD_UNLOCK_TOOLTIP("password_unlock_tooltip"),

  // Credentials
  PIN_TITLE("pin_title"),
  CHANGE_PIN_TITLE("change_pin_title"),
  CHANGE_PIN_REPORT_TITLE("change_pin_report_title"),
  PIN_INTRODUCTION("pin_introduction"),

  PIN_FAILURE("pin_failure"),
  PIN_SUCCESS("pin_success"),

  // Settings titles

  LANGUAGE_SETTINGS_TITLE("language_settings_title"),
  EXCHANGE_SETTINGS_TITLE("exchange_settings_title"),

  @Deprecated
  UNITS_SETTINGS_TITLE("units_settings_title"),

  @Deprecated
  APPEARANCE_SETTINGS_TITLE("appearance_settings_title"),

  @Deprecated
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

  PAYMENT_SETTINGS_TITLE("payment_settings_title"),

  // Tools titles

  SIGN_MESSAGE_TITLE("sign_message_title"),
  VERIFY_MESSAGE_TITLE("verify_message_title"),

  VERIFY_NETWORK_TITLE("verify_network_title"),

  // Labels
  CONFIRM_SEND_MESSAGE("confirm_send_message"),

  EXCHANGE_RATE_PROVIDER("exchange_rate_provider"),
  EXCHANGE_RATE_PROVIDER_TOOLTIP("exchange_rate_provider_tooltip"),

  UUID("uuid"),
  IDENTITY("identity"),

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
  SELECT_WALLET_TYPE_BETA7("select_wallet_type_beta7"),
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

  SHOW_PAYMENT_SETTINGS_WIZARD("show_payment_settings_wizard"),
  SHOW_PAYMENT_SETTINGS_WIZARD_TOOLTIP("show_payment_settings_wizard_tooltip"),

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

  SHOW_ALERT("show_alert"),
  HIDE_ALERT("hide_alert"),

  SHOW_ERROR_REPORTING("show_error_reporting"),
  SHOW_ERROR_REPORTING_TOOLTIP("show_error_reporting_tooltip"),

  // Radio buttons
  CREATE_WALLET("create_wallet"),
  RESTORE_PASSWORD("restore_password"),
  RESTORE_WALLET("restore_wallet"),
  USE_EXISTING_WALLET("use_existing_wallet"),

  RESTORE_FROM_SEED_PHRASE("restore_from_seed_phrase"),
  RESTORE_FROM_BACKUP("restore_from_backup"),

  CLOUD_BACKUP_LOCATION("cloud_backup_location"),

  WALLET_DEFAULT_NOTES("wallet_default_notes"),


  // Notes (can contain HTML),

  LICENCE_NOTE_1("licence_note_1"),
  WELCOME_NOTE_2("welcome_note_2"),
  WELCOME_NOTE_3("welcome_note_3"),

  ABOUT_NOTE_1("about_note_1"),
  ABOUT_NOTE_2("about_note_2"),
  ABOUT_NOTE_3("about_note_3"),

  SELECT_BACKUP_NOTE_1("select_backup_note_1"),
  SELECT_BACKUP_NOTE_1_TOOLTIP("select_backup_note_1_tooltip"),
  SELECT_BACKUP_NOTE_2("select_backup_note_2"),

  ATTACH_HARDWARE_WALLET_NOTE_1("attach_hardware_wallet_note_1"),
  ATTACH_HARDWARE_WALLET_NOTE_2("attach_hardware_wallet_note_2"),
  ATTACH_HARDWARE_WALLET_NOTE_3("attach_hardware_wallet_note_3"),
  ATTACH_HARDWARE_WALLET_NOTE_4("attach_hardware_wallet_note_4"),
  ATTACH_HARDWARE_WALLET_NOTE_5("attach_hardware_wallet_note_5"),

  PREPARATION_NOTE_1("preparation_note_1"),
  PREPARATION_NOTE_2("preparation_note_2"),
  PREPARATION_NOTE_3("preparation_note_3"),
  PREPARATION_NOTE_4("preparation_note_4"),
  PREPARATION_NOTE_5("preparation_note_5"),
  PREPARATION_NOTE_6("preparation_note_6"),

  SELECT_BACKUP_LOCATION_NOTE_1("select_backup_location_note_1"),
  SELECT_BACKUP_LOCATION_NOTE_2("select_backup_location_note_2"),
  SELECT_BACKUP_LOCATION_NOTE_3("select_backup_location_note_3"),
  SELECT_BACKUP_LOCATION_NOTE_4("select_backup_location_note_4"),

  SEED_WARNING_NOTE_1("seed_warning_note_1"),
  SEED_WARNING_NOTE_2("seed_warning_note_2"),
  SEED_WARNING_NOTE_3("seed_warning_note_3"),
  SEED_WARNING_NOTE_4("seed_warning_note_4"),

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
  RESTORE_WALLET_NOTE_2("restore_wallet_note_2"),

  CHANGE_PASSWORD_NOTE_1("change_password_note_1"),
  CHANGE_PASSWORD_NOTE_2("change_password_note_2"),

  CREATE_WALLET_REPORT_NOTE_1("create_wallet_report_note_1"),

  ENTER_CURRENT_PIN("enter_current_pin"),
  ENTER_NEW_PIN("enter_new_pin"),
  CONFIRM_NEW_PIN("confirm_new_pin"),
  ENTER_PIN_LOOK_AT_DEVICE("enter_pin_look_at_device"),

  VERIFY_NETWORK_NOTE_1("verify_network_note_1"),
  VERIFY_NETWORK_NOTE_2("verify_network_note_2"),
  VERIFY_NETWORK_NOTE_3("verify_network_note_3"),
  VERIFY_NETWORK_PEER_COUNT("verify_network_peer_count"),
  VERIFY_NETWORK_BLOCK_COUNT("verify_network_block_count"),

  UNITS_SETTINGS_NOTE_1("units_settings_note_1"),

  EXCHANGE_SETTINGS_NOTE_1("exchange_settings_note_1"),

  LANGUAGE_CHANGE_NOTE_1("language_change_note_1"),
  LOCALISATION_IS_BY_VOLUNTEERS("localisation_is_by_volunteers"),
  I_WOULD_LIKE_TO_HELP("i_would_like_to_help"),

  SOUND_CHANGE_NOTE_1("sound_change_note_1"),

  LAB_CHANGE_NOTE_1("lab_change_note_1"),

  DATA_ENTERED_NOTE_1("data_entered_note_1"),
  DATA_ENTERED_NOTE_2("data_entered_note_2"),

  SIGN_MESSAGE_NOTE_1("sign_message_note_1"),
  SIGN_MESSAGE_NOTE_2("sign_message_note_2"),
  SIGN_MESSAGE_NOTE_3("sign_message_note_3"),

  VERIFY_MESSAGE_NOTE_1("verify_message_note_1"),
  VERIFY_MESSAGE_NOTE_2("verify_message_note_2"),

  BUY_SELL_NOTE_1("buy_sell_note_1"),
  BUY_SELL_NOTE_2("buy_sell_note_2"),
  BUY_SELL_NOTE_3("buy_sell_note_3"),

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
  EXPIRES("expires"),

  DESCRIPTION("description"),
  DESCRIPTION_READ_ONLY("description_read_only"),

  TRANSACTION_HASH("transaction_hash"),
  RAW_TRANSACTION("raw_transaction"),

  VIEW_IN_BLOCK_EXPLORER("view_in_block_explorer"),
  VIEW_IN_BLOCK_EXPLORER_TOOLTIP("view_in_block_explorer_tooltip"),

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
  YOUR_PAYMENT_REQUESTS_WERE_EXPORTED_TO_THE_FILE("your_payment_requests_were_exported_to_the_file"),
  THEIR_PAYMENT_REQUESTS_WERE_EXPORTED_TO_THE_FILE("their_payment_requests_were_exported_to_the_file"),
  COULD_NOT_WRITE_TO_THE_DIRECTORY("could_not_write_to_the_directory"),

  // Repair wallet
  REPAIR_WALLET_NOTE_1("repair_wallet_note_1"),
  REPAIR_WALLET_NOTE_2("repair_wallet_note_2"),
  REPAIR_WALLET_NOTE_3("repair_wallet_note_3"),

  SPENDABLE_BALANCE_IS_LOWER("spendable_balance_is_lower"),
  PLUS_UNCONFIRMED("plus_unconfirmed"),

  // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // Generic hardware wallet entries (branding is applied when used)

  // Trezor-specific entries
  SELECT_HARDWARE_WALLET("select_hardware_wallet"),
  SELECT_HARDWARE_WALLET_TOOLTIP("select_hardware_wallet_tooltip"),

  // Alerts
  HARDWARE_ATTACHED_ALERT("hardware_attached_alert"),
  HARDWARE_DETACHED_ALERT("hardware_detached_alert"),
  HARDWARE_STOPPED_ALERT("hardware_stopped_alert"),
  HARDWARE_FAILURE_ALERT("hardware_failure_alert"),

  CREATE_HARDWARE_WALLET_PREPARATION_TITLE("create_hardware_wallet_preparation_title"),
  CREATE_HARDWARE_WALLET_ENTER_DETAILS_TITLE("create_hardware_wallet_enter_details_title"),
  CREATE_HARDWARE_WALLET_REQUEST_CREATE_WALLET_TITLE("create_hardware_wallet_request_create_wallet_title"),

  // Use hardware
  HARDWARE_CONFIRM_ADD_PIN_TITLE("hardware_confirm_add_pin_title"),
  HARDWARE_CONFIRM_CHANGE_PIN_TITLE("hardware_confirm_change_pin_title"),
  HARDWARE_CONFIRM_REMOVE_PIN_TITLE("hardware_confirm_remove_pin_title"),
  HARDWARE_PRESS_CONFIRM_TITLE("hardware_press_confirm_title"),
  HARDWARE_PRESS_NEXT_TITLE("hardware_press_next_title"),

  // Operations
  HARDWARE_PRESS_CONFIRM_OPERATION("hardware_press_confirm_operation"),
  HARDWARE_PRESS_NEXT_OPERATION("hardware_press_next_operation"),
  HARDWARE_NO_WALLET_OPERATION("hardware_no_wallet_operation"),
  HARDWARE_NO_WALLET_RECOVERY("hardware_no_wallet_recovery"),
  HARDWARE_FAILURE_OPERATION("hardware_failure_operation"),
  HARDWARE_REMOVE_PIN_OPERATION("hardware_remove_pin_operation"),
  SEARCHING_FOR_CONNECTED_HARDWARE_OPERATION("searching_for_connected_hardware_operation"),
  COMMUNICATING_WITH_HARDWARE_OPERATION("communicating_with_hardware_operation"),

  // Recovery
  HARDWARE_FAILURE_RECOVERY("hardware_failure_recovery"),

  // Buttons
  HARDWARE_VERIFY_DEVICE("hardware_verify_device"),
  HARDWARE_VERIFY_DEVICE_TITLE("hardware_verify_device_title"),

  HARDWARE_WIPE_DEVICE("hardware_wipe_device"),
  HARDWARE_WIPE_DEVICE_TITLE("hardware_wipe_device_title"),

  // Reports
  // Success
  HARDWARE_CHANGE_PIN_SUCCESS("hardware_change_pin_success"),
  HARDWARE_REMOVE_PIN_SUCCESS("hardware_remove_pin_success"),
  HARDWARE_WIPE_DEVICE_SUCCESS("hardware_wipe_device_success"),
  USE_HARDWARE_REPORT_MESSAGE_SUCCESS("use_hardware_report_message_success"),

  // Failure
  HARDWARE_INCORRECT_PIN_FAILURE("hardware_incorrect_pin_failure"),
  HARDWARE_ADD_PIN_FAILURE("hardware_add_pin_failure"),
  HARDWARE_CHANGE_PIN_FAILURE("hardware_change_pin_failure"),
  HARDWARE_WIPE_DEVICE_FAILURE("hardware_wipe_device_failure"),
  HARDWARE_SIGN_FAILURE("hardware_sign_failure"),
  USE_HARDWARE_REPORT_MESSAGE_FAILURE("use_hardware_report_message_failure"),

  USE_HARDWARE_REPORT_TITLE("use_hardware_report_title"),

  USE_HARDWARE_WALLET("use_hardware_wallet"),

  HARDWARE_FOUND("hardware_found"),
  NO_HARDWARE_FOUND("no_hardware_found"),

  HARDWARE_TRANSACTION_CREATED_OPERATION("hardware_transaction_created_operation"),

  ENTER_HARDWARE_LABEL("enter_hardware_label"),
  ENTER_HARDWARE_LABEL_TOOLTIP("enter_hardware_label_tooltip"),

  HARDWARE_UNLOCK_TITLE("hardware_unlock_title"),
  HARDWARE_UNLOCK_OPERATION("hardware_unlock_operation"),

  USE_HARDWARE_TITLE("use_hardware_title"),

  SHOW_HARDWARE_TOOLS_WIZARD("show_hardware_tools_wizard"),
  SHOW_HARDWARE_TOOLS_WIZARD_TOOLTIP("show_hardware_tools_wizard_tooltip"),

  HARDWARE_CREATE_WALLET("hardware_create_wallet"),

  HARDWARE_PREPARATION_NOTE_1("hardware_preparation_note_1"),
  HARDWARE_PREPARATION_NOTE_2("hardware_preparation_note_2"),
  HARDWARE_PREPARATION_NOTE_3("hardware_preparation_note_3"),
  HARDWARE_PREPARATION_NOTE_4("hardware_preparation_note_4"),
  HARDWARE_PREPARATION_NOTE_5("hardware_preparation_note_5"),
  HARDWARE_PREPARATION_NOTE_6("hardware_preparation_note_6"),

  // Buy hardware
  BUY_HARDWARE_TITLE("buy_hardware_title"),
  BUY_HARDWARE_COMMENT("buy_hardware_comment"),
  BUY_HARDWARE("buy_hardware"),
  BUY_HARDWARE_TOOLTIP("buy_hardware_tooltip"),

  // //////////////////////////////////////////////////////////// Trezor /////////////////////////////////////////////////////////////
  
  // Trezor-specific display text
  TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY("trezor_encrypt_multibit_hd_unlock_display"),
  TREZOR_ADD_PIN_DISPLAY("trezor_add_pin_display"),
  TREZOR_CHANGE_PIN_DISPLAY("trezor_change_pin_display"),
  TREZOR_REMOVE_PIN_DISPLAY("trezor_remove_pin_display"),
  TREZOR_WORD_DISPLAY("trezor_word_display"),
  TREZOR_CHECK_WORD_DISPLAY("trezor_check_word_display"),
  TREZOR_TRANSACTION_OUTPUT_CONFIRM_DISPLAY("trezor_transaction_output_confirm_display"),
  TREZOR_SIGN_CONFIRM_DISPLAY("trezor_send_confirm_display"),
  TREZOR_SIGN_MESSAGE_CONFIRM_DISPLAY("trezor_sign_message_confirm_display"),
  TREZOR_WIPE_CONFIRM_DISPLAY("trezor_wipe_confirm_display"),
  TREZOR_HIGH_FEE_CONFIRM_DISPLAY("trezor_high_fee_confirm_display"),

  // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // KeepKey-specific display text
  KEEP_KEY_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY("keepkey_encrypt_multibit_hd_unlock_display"),
  KEEP_KEY_ADD_PIN_DISPLAY("keepkey_add_pin_display"),
  KEEP_KEY_CHANGE_PIN_DISPLAY("keepkey_change_pin_display"),
  KEEP_KEY_REMOVE_PIN_DISPLAY("keepkey_remove_pin_display"),
  KEEP_KEY_WALLET_WORDS_DISPLAY_1("keepkey_wallet_words_display_1"),
  KEEP_KEY_WALLET_WORDS_DISPLAY_2("keepkey_wallet_words_display_2"),
  KEEP_KEY_TRANSACTION_OUTPUT_CONFIRM_DISPLAY("keepkey_transaction_output_confirm_display"),
  KEEP_KEY_SIGN_CONFIRM_DISPLAY("keepkey_send_confirm_display"),
  KEEP_KEY_SIGN_MESSAGE_CONFIRM_DISPLAY("keepkey_sign_message_confirm_display"),
  KEEP_KEY_WIPE_CONFIRM_DISPLAY("keepkey_wipe_confirm_display"),
  KEEP_KEY_HIGH_FEE_CONFIRM_DISPLAY("keepkey_high_fee_confirm_display"),

  // End of enum
  ;

  private final String key;

  MessageKey(String key) {
    this.key = key;
  }

  /**
   * @return The key for use with the resource bundles
   */

  public String getKey() {
    return key;
  }

}
