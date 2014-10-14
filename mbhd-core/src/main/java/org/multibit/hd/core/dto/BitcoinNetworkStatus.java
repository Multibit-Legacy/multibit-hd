package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to Core API:</p>
 * <ul>
 * <li>Information about the Bitcoin network status</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum BitcoinNetworkStatus {

  /**
   * No connection to the network
   */
  NOT_CONNECTED,

  /**
   * In the process of making a connection (no peers)
   */
  CONNECTING,

  /**
   * In the process of downloading the blockchain (not ready for a send)
   */
  DOWNLOADING_BLOCKCHAIN,

  /**
   * Connected and synchronized (ready to send)
   */
  SYNCHRONIZED,

  // End of enum
  ;
}
