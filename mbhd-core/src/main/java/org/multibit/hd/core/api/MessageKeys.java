package org.multibit.hd.core.api;

/**
 * <p>Interface to provide the following to Core API:</p>
 * <ul>
 * <li>Message keys to use for internationalisation of core events</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public interface MessageKeys {

  String PEER_COUNT = "status.peerCount";
  String CHAIN_DOWNLOAD = "status.chainDownload";
  String START_NETWORK_CONNECTION_ERROR = "bitcoin-network.start-network-connection-error";
  String NETWORK_CONFIGURATION_ERROR = "bitcoin-network.configuration-error";
}
