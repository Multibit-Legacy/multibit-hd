package org.multibit.hd.ui.i18n;

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
  UNDO("undo"),
  CANCEL("cancel"),
  EXIT("exit"),
  SEND("send"),
  REQUEST("request"),
  REFRESH("refresh"),
  FINISH("finish"),
  CLOSE("close"),
  SHOW("show"),
  HIDE("hide"),
  ADD("add"),
  EDIT("edit"),
  DELETE("delete"),
  RESTORE("restore"),

  // Nouns

  YES("yes"),
  NO("no"),
  NEXT("next"),
  PREVIOUS("previous"),
  ALL("all"),
  NONE("none"),
  MULTIPLE("multiple"),

  // Field labels

  RECIPIENT("recipient"),
  TRANSACTION_LABEL("transaction_label"),
  QR_CODE_LABEL_LABEL("qr_code_label_label"),
  NOTES("notes"),
  ONE_OF_YOUR_ADDRESSES("one_of_your_addresses"),
  SELECT_FOLDER("select_folder"),
  SELECT_FILE("select_file"),
  ENTER_PASSWORD("enter_password"),
  CONFIRM_PASSWORD("confirm_password"),
  AMOUNT("amount"),
  TRANSACTION_FEE("transaction_fee"),
  DEVELOPER_FEE("developer_fee"),

  NAME("name"),
  NAMES("names"),
  EMAIL_ADDRESS("email_address"),
  BITCOIN_ADDRESS("bitcoin_address"),
  EXTENDED_PUBLIC_KEY("extended_public_key"),
  TAGS("tags"),
  DESCRIPTION("description"),

  // Sidebar

  CONTACTS("contacts"),
  TRANSACTIONS("transactions"),
  HISTORY("history"),
  PREFERENCES("preferences"),
  TOOLS("tools"),

  // Placeholders

  APPROXIMATELY("approximately"),
  PARANTHESES("parantheses"),
  LIST_COMMA("list_comma"),
  LIST_ELLIPSIS("list_ellipsis"),

  // Panels

  NETWORK_CONFIGURATION_ERROR("network_configuration_error"),
  DISPLAY_LANGUAGE("display_language"),
  HELP("help"),
  SETTINGS("settings"),

  // Titles
  APPLICATION_TITLE("application_title"),

  RECEIVE_BITCOIN_TITLE("receive_bitcoin_title"),

  SEND_BITCOIN_TITLE("send_bitcoin_title"),
  CONFIRM_SEND_TITLE("confirm_send_title"),
  SEND_PROGRESS_TITLE("send_progress_title"),

  EXIT_TITLE("exit_title"),

  WELCOME_TITLE("welcome_title"),
  SELECT_WALLET_TITLE("select_wallet_title"),
  CREATE_WALLET_SEED_PHRASE_TITLE("create_wallet_seed_phrase_title"),
  CONFIRM_WALLET_SEED_PHRASE_TITLE("confirm_wallet_seed_phrase_title"),
  CREATE_WALLET_PASSWORD_TITLE("create_wallet_password_title"),
  SELECT_BACKUP_LOCATION_TITLE("select_backup_location_title"),
  CREATE_WALLET_REPORT_TITLE("create_wallet_report_title"),

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
  PASSWORD_ENTER_SEED_PHRASE_TITLE("password_enter_seed_phrase_title"),
  PASSWORD_REPORT_TITLE("password_report_title"),
  PASSWORD_UNLOCK("password_unlock"),

  // Labels
  CONFIRM_SEND_MESSAGE("confirm_send_message"),
  EXCHANGE_RATE_PROVIDER("exchange_rate_provider"),
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
  ALERT_REMAINING("alert_remaining"),
  EXCHANGE_FIAT_RATE("exchange_fiat_rate"),
  EXCHANGE_FIAT_RATE_WITH_PROVIDER("exchange_fiat_rate_with_provider"),
  SELECT_LANGUAGE("select_language"),
  SEED_SIZE("seed_size"),
  TIMESTAMP("timestamp"),
  MULTI_EDIT_NOTE("multi_edit_note"),

  // Tool buttons
  SHOW_WELCOME_WIZARD("show_welcome_wizard"),
  SHOW_ALERT("show_alert"),

  // Radio buttons
  CREATE_WALLET("create_wallet"),
  RESTORE_WALLET("restore_wallet"),
  SWITCH_WALLET("switch_wallet"),
  USE_HARDWARE_WALLET("use_hardware_wallet"),

  RESTORE_FROM_SEED_PHRASE("restore_from_seed_phrase"),
  RESTORE_FROM_BACKUP("restore_from_backup"),

  // Notes (can contain HTML),

  WELCOME_NOTE_1("welcome_note_1"),
  WELCOME_NOTE_2("welcome_note_2"),
  WELCOME_NOTE_3("welcome_note_3"),
  WELCOME_NOTE_4("welcome_note_4"),

  SELECT_BACKUP_LOCATION_NOTE_1("select_backup_location_note_1"),
  SELECT_BACKUP_LOCATION_NOTE_2("select_backup_location_note_2"),
  SELECT_BACKUP_LOCATION_NOTE_3("select_backup_location_note_3"),
  SELECT_BACKUP_LOCATION_NOTE_4("select_backup_location_note_4"),

  SELECT_BACKUP_NOTE_1("select_backup_note_1"),
  SELECT_BACKUP_NOTE_2("select_backup_note_2"),

  SEED_PRINT_NOTE_1("seed_print_note_1"),
  SEED_PRINT_NOTE_2("seed_print_note_2"),
  SEED_PRINT_NOTE_3("seed_print_note_3"),
  SEED_PRINT_NOTE_4("seed_print_note_4"),

  SEED_WARNING_NOTE_1("seed_warning_note_1"),
  SEED_WARNING_NOTE_2("seed_warning_note_2"),
  SEED_WARNING_NOTE_3("seed_warning_note_3"),
  SEED_WARNING_NOTE_4("seed_warning_note_4"),
  SEED_WARNING_NOTE_5("seed_warning_note_5"),

  CONFIRM_SEED_PHRASE_NOTE_1("confirm_seed_phrase_note_1"),
  CONFIRM_SEED_PHRASE_NOTE_2("confirm_seed_phrase_note_2"),
  CONFIRM_SEED_PHRASE_NOTE_3("confirm_seed_phrase_note_3"),
  CONFIRM_SEED_PHRASE_NOTE_4("confirm_seed_phrase_note_4"),

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

  RESTORE_SEED_PHRASE_NOTE_1("restore_seed_phrase_note_1"),
  RESTORE_SEED_PHRASE_NOTE_2("restore_seed_phrase_note_2"),
  RESTORE_SEED_PHRASE_NOTE_3("restore_seed_phrase_note_3"),

  RESTORE_TIMESTAMP_NOTE_1("restore_timestamp_note_1"),
  RESTORE_TIMESTAMP_NOTE_2("restore_timestamp_note_2"),
  RESTORE_TIMESTAMP_NOTE_3("restore_timestamp_note_3"),
  RESTORE_TIMESTAMP_NOTE_4("restore_timestamp_note_4"),

  PASSWORD_NOTE_1("password_note_1"),
  PASSWORD_NOTE_2("password_note_2"),

  RECIPIENT_SUMMARY("recipient_summary"),
  AMOUNT_SUMMARY("amount_summary"),

  // Wallet detail panel
  SUMMARY("summary"),
  APPLICATION_DIRECTORY("application_directory"),
  WALLET_DIRECTORY("wallet_directory")

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
