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
  SYSTEM_TIME_DRIFT("core_system_time_drift"),
  BACKUP_FAILED("core_backup_failed"),
  CERTIFICATE_FAILED("core_certificate_failed"),
  SECURITY_ADVICE("core_security_advice"),
  BACKUP_WALLET_WAS_LOADED("core_backup_wallet_was_loaded"),
  UNSUPPORTED_FIRMWARE_ATTACHED("core_unsupported_firmware_attached"),

  // Connection / Sync
  THE_ERROR_WAS("core_the_error_was"),
  COULD_NOT_CONNECT_TO_BITCOIN_NETWORK("core_could_not_connect_to_bitcoin_network"),
  CONNECTING_TO_BITCOIN_NETWORK("core_connecting_to_bitcoin_network"),
  CONNECTED_TO_BITCOIN_NETWORK("core_connected_to_bitcoin_network"),
  NO_ACTIVE_WALLET("core_no_active_wallet"),
  PREPARING_TO_SYNCHRONISE("core_preparing_to_synchronise"),
  SYNCHRONISING("core_synchronising"),
  SYNCHRONISED("core_synchronised"),
  WALLET_IS_READY_TO_USE("core_wallet_is_ready_to_use"),

  // Transaction/Payment adaption
  PAYMENT_REQUESTED_BY_THEM("core_payment_requested_by_them"),
  PAYMENT_REQUESTED_BY_YOU("core_payment_requested_by_you"),
  PAYMENT_RECEIVED("core_payment_received"),
  PAYMENT_RECEIVING("core_payment_receiving"),
  PAYMENT_SENT("core_payment_sent"),
  PAYMENT_SENDING("core_payment_sending"),
  PAYMENT_PAID("core_payment_paid"),
  PAYMENT_PART_PAID("core_payment_part_paid"),
  CONFIRMED_BY_ONE_BLOCK("core_confirmed_by_one_block"),
  CONFIRMED_BY_SEVERAL_BLOCKS("core_confirmed_by_several_blocks"),
  BROADCAST("core_broadcast"),
  NOT_BROADCAST("core_not_broadcast"),
  DEAD("core_dead"),
  UNKNOWN("core_unknown"),
  NO_PAYMENT_REQUEST("core_no_payment_request"),

  // These appear to be not used
  @Deprecated
  TRANSACTION_TO("core_transaction_to"),
  @Deprecated
  TRANSACTION_BY("core_transaction_by"),

  // Exchange messages
  EXCHANGE_OK("core_exchange_ok"),
  EXCHANGE_ERROR("core_exchange_error"),
  EXCHANGE_DOWN("core_exchange_down"),

  // Payment protocol messages (single parameter)
  PAYMENT_SESSION_OK("core_payment_session_ok"),
  PAYMENT_SESSION_DOWN("core_payment_session_down"),

  // Detailed payment protocol messages (dual parameter)
  PAYMENT_SESSION_ERROR("core_payment_session_error"),
  PAYMENT_SESSION_EXPIRED("core_payment_session_expired"),
  PAYMENT_SESSION_INVALID_NETWORK("core_payment_session_invalid_network"),
  PAYMENT_SESSION_INVALID_OUTPUTS("core_payment_session_invalid_outputs"),
  PAYMENT_SESSION_INVALID_URL("core_payment_session_invalid_url"),
  PAYMENT_SESSION_INVALID_REQUEST_URL("core_payment_session_invalid_request_url"),
  PAYMENT_SESSION_INVALID_PAYMENT_URL("core_payment_session_invalid_payment_url"),
  PAYMENT_SESSION_INVALID_VERSION("core_payment_session_invalid_version"),

  PAYMENT_SESSION_PKI_INVALID("core_payment_session_pki_invalid"),
  PAYMENT_SESSION_PKI_INVALID_TYPE("core_payment_session_pki_invalid_type"),
  PAYMENT_SESSION_PKI_MISSING("core_payment_session_pki_missing"),
  PAYMENT_SESSION_PKI_UNTRUSTED_CA("core_payment_session_pki_untrusted_ca"),

  // Not used
  @Deprecated
  CHANGE_PASSWORD_REPORT_TITLE("core_change_password_report_title"),

  CHANGE_PASSWORD_WORKING("core_change_password_working"),
  CHANGE_PASSWORD_SUCCESS("core_change_password_success"),
  CHANGE_PASSWORD_WRONG_OLD_PASSWORD("core_change_password_wrong_old_password"),
  CHANGE_PASSWORD_ERROR("core_change_password_error"),

  SIGN_MESSAGE_ENTER_ADDRESS("core_sign_message_enter_address"),
  SIGN_MESSAGE_ENTER_MESSAGE("core_sign_message_enter_message"),
  SIGN_MESSAGE_ENTER_PASSWORD("core_sign_message_enter_password"),
  SIGN_MESSAGE_SUCCESS("core_sign_message_success"),
  SIGN_MESSAGE_NO_SIGNING_KEY("core_sign_message_no_signing_key"),
  SIGN_MESSAGE_SIGNING_KEY_NOT_ENCRYPTED("core_sign_message_signing_key_not_encrypted"),
  SIGN_MESSAGE_NO_WALLET("core_sign_message_no_wallet"),
  SIGN_MESSAGE_NO_PASSWORD("core_sign_message_no_password"),
  SIGN_MESSAGE_FAILURE("core_sign_message_failure"),

  VERIFY_MESSAGE_ENTER_ADDRESS("core_verify_message_enter_address"),
  VERIFY_MESSAGE_ENTER_MESSAGE("core_verify_message_enter_message"),
  VERIFY_MESSAGE_ENTER_SIGNATURE("core_verify_message_enter_signature"),
  VERIFY_MESSAGE_FAILURE("core_verify_message_failure"),
  VERIFY_MESSAGE_VERIFY_SUCCESS("core_verify_message_verify_success"),
  VERIFY_MESSAGE_VERIFY_FAILURE("core_verify_message_verify_failure"),

  WALLET_CAPABILITIES("core_wallet_capabilities"),
  WALLET_CAPABILITY_MBHD_SOFT_BETA7("core_wallet_capability_mbhd_soft_beta7"),
  WALLET_CAPABILITY_MBHD_SOFT_BIP32("core_wallet_capability_mbhd_soft_bip32"),
  WALLET_CAPABILITY_TREZOR_HARD("core_wallet_capability_trezor_hard"),
  WALLET_CAPABILITY_TREZOR_SOFT("core_wallet_capability_trezor_soft"),
  WALLET_CAPABILITY_UNKNOWN("core_wallet_capability_unknown"),

  WALLET_LOADING("core_wallet_loading"),
  WALLET_LOADED_OK("core_wallet_loaded_ok"),
  WALLET_FAILED_TO_LOAD("core_wallet_failed_to_load"),
  WALLET_BAD_PASSWORD("core_wallet_bad_password"),

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
