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
  private final int percent;

  private final RAGStatus severity;

  private final Optional<String> errorKey;
  private final Optional<String[]> errorData;


  /**
   * @return A new "not initialised" summary
   */
  public static BitcoinNetworkSummary newNetworkNotInitialised() {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.<String>absent(),
      Optional.<String[]>absent(),
      0,
      0
    );
  }

  /**
   * @return A new "downloading blockchain" summary
   */
  public static BitcoinNetworkSummary newChainDownloadStarted() {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.DOWNLOADING_BLOCKCHAIN,
      RAGStatus.AMBER,
      Optional.of(MessageKeys.CHAIN_DOWNLOAD),
      Optional.of(new String[]{"0"}),
      0,
      0
    );
  }

  /**
   * @param percent The percentage of blocks downloaded
   *
   * @return A new "progress update" summary
   */
  public static BitcoinNetworkSummary newChainDownloadProgress(int percent) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.DOWNLOADING_BLOCKCHAIN,
      RAGStatus.AMBER,
      Optional.of(MessageKeys.CHAIN_DOWNLOAD),
      Optional.of(new String[]{String.valueOf(percent)}),
      0,
      percent
    );
  }

  /**
   * @param peerCount The peer count
   *
   * @return A new "network ready with peer count" summary
   */
  public static BitcoinNetworkSummary newNetworkReady(int peerCount) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.SYNCHRONIZED,
      RAGStatus.GREEN,
      Optional.of(MessageKeys.PEER_COUNT),
      Optional.of(new String[]{String.valueOf(peerCount)}),
      peerCount,
      0
    );
  }

  /**
   * @param messageKey The message key to allow localisation
   *
   * @return A new "startup failed" summary
   */
  public static BitcoinNetworkSummary newNetworkStartupFailed(String messageKey, Optional<String[]> messageData) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.of(messageKey),
      messageData,
      0,
      0
    );
  }

  /**
   * @param status    The network status (e.g. NOT_CONNECTED)
   * @param severity  The severity (Red, Amber, Green)
   * @param errorKey  The error key to allow localisation
   * @param errorData The error data for insertion into the error message
   * @param peerCount The current peer count
   */
  public BitcoinNetworkSummary(
    BitcoinNetworkStatus status,
    RAGStatus severity,
    Optional<String> errorKey,
    Optional<String[]> errorData,
    int peerCount,
    int percent) {

    this.status = status;
    this.severity = severity;

    this.errorKey = errorKey;
    this.errorData = errorData;

    this.peerCount = peerCount;
    this.percent = percent;
  }

  public int getPeerCount() {
    return peerCount;
  }

  /**
   * @return A percent value for a progress bar (such as blockchain download progress)
   */
  public int getPercent() {
    return percent;
  }

  public RAGStatus getSeverity() {
    return severity;
  }

  public BitcoinNetworkStatus getStatus() {
    return status;
  }

  public Optional<String[]> getErrorData() {
    return errorData;
  }

  public Optional<String> getErrorKey() {
    return errorKey;
  }

  @Override
  public String toString() {
    return "BitcoinNetworkSummary{" +
      "errorData=" + errorData +
      ", status=" + status +
      ", peerCount=" + peerCount +
      ", percent=" + percent +
      ", severity=" + severity +
      ", errorKey=" + errorKey +
      '}';
  }
}
