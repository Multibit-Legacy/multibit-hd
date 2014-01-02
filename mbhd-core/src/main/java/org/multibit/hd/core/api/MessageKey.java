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
  USE_HARDWARE_WALLET("Use hardware wallet"),

  // Titles
  SEND_BITCOIN_TITLE("sendBitcoinAction.text"),
  CONFIRM_SEND_TITLE("sendBitcoinConfirmView.title"),
  CONFIRM_SEND_MESSAGE("sendBitcoinConfirmView.message"),
  SEND_PROGRESS_TITLE("Send Progress"),
  EXIT_TITLE("Exit application ?"),
  WELCOME_TITLE("Welcome !"),
  SELECT_WALLET_TITLE("Select wallet"),
  CREATE_WALLET_TITLE("Create wallet"),
  CREATE_WALLET_PASSWORD_TITLE("Create wallet password"),
  RESTORE_WALLET_TITLE("Restore wallet"),

  // Labels
  ENTER_PASSWORD("Enter password"),
  CONFIRM_PASSWORD("Confirm password"),

  // Tool buttons
  SHOW_WELCOME_WIZARD("Welcome wizard"),

  // Statuses
  BROADCAST_STATUS_OK("Broadcast OK"),
  RELAY_STATUS_OK("Relayed OK"),
  CONFIRMATION_STATUS_OK("Confirmations 6+"),

  ALERT_REMAINING("alert.remaining"),
  EXCHANGE_FIAT_RATE("exchange.fiat-rate"),
  SELECT_LANGUAGE("showPreferencesPanel.languageTitle"),
  SEED_SIZE("Words in seed"),

  // Notes
  WELCOME_NOTE("MultiBit HD provides secure access to the Bitcoin network. Click next to get started."),
  SEED_WARNING_NOTE("You must write down the words shown above on a piece of paper. You will never see them again and they protect all your bitcoins."),
  WALLET_PASSWORD_NOTE("This password will be needed whenever you send bitcoins. You can always recover it using your seed phrase."),

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
