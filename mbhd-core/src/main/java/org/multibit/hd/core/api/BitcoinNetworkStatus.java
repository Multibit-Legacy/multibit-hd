package org.multibit.hd.core.api;

/**
 * <p>Value object to provide the following to Core API:</p>
 * <ul>
 * <li>Information about the Bitcoin network status</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum BitcoinNetworkStatus {

  NOT_CONNECTED,

  CONNECTING,

  CONNECTED,

  // End of enum
  ;
}
