package org.multibit.hd.core.api;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Message keys to use for internationalisation</li>
 * </ul>
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
  RECEIVE("receive"),
  REFRESH("refresh"),
  FINISH("finish"),
  CLOSE("close"),
  SHOW("show"),
  HIDE("hide"),

  // Nouns
  YES("yes"),
  NO("no"),
  NEXT("next"),
  PREVIOUS("previous"),

  // Field labels
  RECIPIENT("Recipient"),
  NOTES("Notes"),
  ENTER_PASSWORD("Enter password"),
  CONFIRM_PASSWORD("Confirm password"),
  AMOUNT("Amount"),

  // Placeholders
  APPROXIMATELY("placeholder.approximately"),
  PARANTHESES("placeholder.parantheses"),

  // Panels

  PEER_COUNT("status.peerCount"),
  CHAIN_DOWNLOAD("status.chainDownload"),
  START_NETWORK_CONNECTION_ERROR("bitcoin-network.start-network-connection-error"),
  NETWORK_CONFIGURATION_ERROR("bitcoin-network.configuration-error"),
  DISPLAY_LANGUAGE("showPreferencesPanel.useSpecific"),
  HELP("multiBitFrame.helpMenuText"),
  SETTINGS("showPreferencesPanel.title"),
  CREATE_WALLET("Create new wallet"),
  RESTORE_WALLET("Restore from seed or backup"),
  SWITCH_WALLET("Switch to another wallet in the list"),
  USE_HARDWARE_WALLET("Use hardware wallet"),

  // Titles
  APPLICATION_TITLE("MultiBit HD"),
  SEND_BITCOIN_TITLE("sendBitcoinAction.text"),
  CONFIRM_SEND_TITLE("sendBitcoinConfirmView.title"),
  SEND_PROGRESS_TITLE("Send Progress"),
  EXIT_TITLE("Exit application ?"),
  WELCOME_TITLE("Welcome !"),
  SELECT_WALLET_TITLE("Select wallet"),
  CREATE_WALLET_SEED_PHRASE_TITLE("Create wallet"),
  CONFIRM_WALLET_SEED_PHRASE_TITLE("Confirm wallet"),
  CREATE_WALLET_PASSWORD_TITLE("Create wallet password"),
  SELECT_BACKUP_LOCATION_TITLE("Select backup location"),
  CREATE_WALLET_REPORT_TITLE("Create Wallet Report"),
  RESTORE_WALLET_TITLE("Restore wallet"),

  // Labels
  CONFIRM_SEND_MESSAGE("sendBitcoinConfirmView.message"),
  EXCHANGE_RATE_PROVIDER("exchange.rate-provider"),

  // Tool buttons
  SHOW_WELCOME_WIZARD("Welcome wizard"),

  // Statuses
  BROADCAST_STATUS("Broadcast"),
  RELAY_STATUS("Relayed"),
  CONFIRMATION_STATUS("Confirmation count {0}"),
  VERIFICATION_STATUS("Verified"),
  EXCHANGE_RATE_STATUS_OK("Exchange rate OK"),
  EXCHANGE_RATE_STATUS_WARN("Exchange rate is out of date"),

  SEED_PHRASE_CREATED_STATUS("Seed phrase created"),
  WALLET_PASSWORD_CREATED_STATUS("Wallet password created"),
  BACKUP_LOCATION_STATUS("Backup location created"),
  WALLET_CREATED_STATUS("Wallet created"),


  ALERT_REMAINING("alert.remaining"),
  EXCHANGE_FIAT_RATE("exchange.fiat-rate"),
  SELECT_LANGUAGE("showPreferencesPanel.languageTitle"),
  SEED_SIZE("Words in seed"),

  // Notes (can contain HTML)
  WELCOME_NOTE_1("MultiBit HD provides access to the Bitcoin network."),
  WELCOME_NOTE_2("Bitcoin is a digital currency and <strong>requires a secure environment</strong>."),
  WELCOME_NOTE_3("This setup process will guide through each step to help keep your bitcoins safe."),
  WELCOME_NOTE_4("If this is your first time using MultiBit HD you should choose \"Create new wallet\" on the next page."),

  SELECT_BACKUP_LOCATION_NOTE_1("Wallet backups <strong>protect your transactions and contacts</strong> if your computer is damaged or stolen."),
  SELECT_BACKUP_LOCATION_NOTE_2("They are created automatically and are encrypted."),
  SELECT_BACKUP_LOCATION_NOTE_3("Cloud backup services include Spider Oak, Dropbox and Carbonite."),
  SELECT_BACKUP_LOCATION_NOTE_4("Use the folder button to specify your cloud backup folder."),

  SEED_WARNING_NOTE_1("Write down the words below exactly as shown on a <strong>piece of paper</strong> and <strong>keep it safe</strong>."),
  SEED_WARNING_NOTE_2("These words <strong>protect all your bitcoins</strong>. You will never see them again."),
  SEED_WARNING_NOTE_3("Do not copy/paste."),
  SEED_WARNING_NOTE_4("Do not take a screen shot."),

  CONFIRM_SEED_PHRASE_NOTE_1("Enter your seed phrase words below <strong>exactly as they were given</strong>."),
  CONFIRM_SEED_PHRASE_NOTE_2("Include the spaces between words."),
  CONFIRM_SEED_PHRASE_NOTE_3("If you do not have the seed phrase words you must exit and start again."),
  CONFIRM_SEED_PHRASE_NOTE_4("A message will appear when you have confirmed the seed phrase."),

  WALLET_PASSWORD_NOTE_1("This password will be needed whenever you send bitcoins."),
  WALLET_PASSWORD_NOTE_2("You can change it whenever you like from the Tools screen."),
  WALLET_PASSWORD_NOTE_3("You can always recover it using your seed phrase."),

  RECIPIENT_SUMMARY("recipient-summary"),
  AMOUNT_SUMMARY("amount-summary"),

  // End of enum
  CONTACTS("Contacts"), TRANSACTIONS("Transactions"), HISTORY("History"), PREFERENCES("Preferences"), TOOLS("Tools");

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
