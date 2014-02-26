package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Message keys to use for internationalisation of output from the core module</li>
 * </ul>
 *
 * <h3>Naming conventions</h3>
 * <p>Message keys are placed in an enum for type safety.</p>
 * <p>Message keys have their resource key so that IDEs can maintain a "where used" reference lookup.</p>
 * <p>Message keys are never concatenated to form larger sentences. Instead use token replacements
 * within a larger sentence structure.</p>
 * <p>Resource keys are simply the message key enum name prefixed with "core_" in lower case.</p>
 *
 * @since 0.0.1
 *  
 */
public enum CoreMessageKey {

  // Core messages
  NOT_INITIALISED("core_not_initialised"),
  START_NETWORK_CONNECTION_ERROR("core_start_network_connection_error"),
  CHAIN_DOWNLOAD("core_chain_download"),
  PEER_COUNT("core_peer_count"),

  // Sending bitcoin
  TRANSACTION_CREATED_OK("core_transaction_created_ok"),
  TRANSACTION_CREATION_FAILED("core_transaction_creation_failed"),
  SENDING_BITCOIN("core_sending_bitcoin"),
  BITCOIN_SENT_OK("core_bitcoin_sent_ok"),
  BITCOIN_SEND_FAILED("core_bitcoin_send_failed"),

  // Security
  DEBUGGER_ATTACHED("core_debugger_attached"),
  BACKUP_FAILED("core_backup_failed"),
  SECURITY_ADVICE("core_security_advice"),

  // Errors
  THE_ERROR_WAS("core_the_error_was"),
  COULD_NOT_CONNECT_TO_BITCOIN_NETWORK("core_could_not_connect_to_bitcoin_network"),
  NO_ACTIVE_WALLET("core_no_active_wallet"),

  // Transaction/Payment adaption
  PAYMENT_REQUESTED("core_payment_requested"),
  PAYMENT_RECEIVED("core_payment_received"),
  PAYMENT_RECEIVING("core_payment_receiving"),
  PAYMENT_SENT("core_payment_sent"),
  PAYMENT_SENDING("core_payment_sending"),

  TRANSACTION_TO("core_transaction_to"),
  TRANSACTION_BY("core_transaction_by")
  // End of enum
  ;

  private final String key;

  private CoreMessageKey(String key) {
    this.key = key;
  }

  /**
   * @return The key for use with the resource bundles
   */

  public String getKey() {
    return key;
  }

}
