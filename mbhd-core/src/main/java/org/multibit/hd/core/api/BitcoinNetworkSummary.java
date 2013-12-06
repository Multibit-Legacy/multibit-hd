package org.multibit.hd.core.api;

import com.google.common.base.Optional;

/**
 * <p>Value object to provide the following to Core API:</p>
 * <ul>
 * <li>Information about the Bitcoin network status</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinNetworkSummary {

  private final BitcoinNetworkStatus status;

  private final int peerCount;

  private final RAGStatus severity;

  private final Optional<String> errorKey;


  /**
   * TODO Is this structure useful?
   * @param errorKey
   * @return
   */
  public static BitcoinNetworkSummary newNetworkStartupFailed(String errorKey) {

    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.of(errorKey),
      0
    );

  }

  public BitcoinNetworkSummary(
    BitcoinNetworkStatus status,
    RAGStatus severity,
    Optional<String> errorKey, int peerCount) {

    this.peerCount = peerCount;
    this.status = status;
    this.severity = severity;

    this.errorKey = errorKey;
  }


  public int getPeerCount() {
    return peerCount;
  }

  public RAGStatus getSeverity() {
    return severity;
  }

  public BitcoinNetworkStatus getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "BitcoinNetworkSummary{" +
      "errorKey=" + errorKey +
      ", status=" + status +
      ", peerCount=" + peerCount +
      ", severity=" + severity +
      '}';
  }
}
