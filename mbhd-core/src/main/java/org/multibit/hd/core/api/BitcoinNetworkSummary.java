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
  private final Optional<String[]> errorData;


  /**
   * @return
   */
  public static BitcoinNetworkSummary newNetworkNotInitialised() {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.<String>absent(),
      Optional.<String[]>absent(),
      0
    );
  }
  /**
   * @param errorKey
   * @return
   */
  public static BitcoinNetworkSummary newNetworkStartupFailed(String errorKey, Optional<String[]> errorData) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.of(errorKey),
      errorData,
      0
    );
  }

  public BitcoinNetworkSummary(
    BitcoinNetworkStatus status,
    RAGStatus severity,
    Optional<String> errorKey,
    Optional<String[]> errorData,
    int peerCount) {

    this.status = status;
    this.severity = severity;

    this.errorKey = errorKey;
    this.errorData = errorData;

    this.peerCount = peerCount;
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
      ", errorData=" + errorData +
      ", status=" + status +
      ", peerCount=" + peerCount +
      ", severity=" + severity +
      '}';
  }
}
