package org.multibit.hd.core.api;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Message keys to use for internationalisation of output from the core module</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum CoreMessageKey {

  // Core messages
  START_NETWORK_CONNECTION_ERROR,
  CHAIN_DOWNLOAD,
  PEER_COUNT,

   // Sending bitcoin
  TRANSACTION_CREATED_OK,
  TRANSACTION_CREATION_FAILED,
  SENDING_BITCOIN,
  BITCOIN_SENT_OK,
  BITCOIN_SEND_FAILED,

  // Errors
  THE_ERROR_WAS,
  COULD_NOT_CONNECT_TO_BITCOIN_NETWORK,
  NO_ACTIVE_WALLET

  ;

  /**
   * @return The key for use with the resource bundles
   */
  public String getKey() {
    return "core_"+name().toLowerCase();
  }

}
