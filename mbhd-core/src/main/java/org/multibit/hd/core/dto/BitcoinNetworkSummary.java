package org.multibit.hd.core.dto;

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

  private final Optional<CoreMessageKey> messageKey;
  private final Optional<Object[]> messageData;


  /**
   * <p>The network has not initialised yet so hide the progress bar</p>
   *
   * @return A new "not initialised" summary
   */
  public static BitcoinNetworkSummary newNetworkNotInitialised() {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.of(CoreMessageKey.NOT_INITIALISED),
      Optional.<Object[]>absent(),
      0,
      -1
    );
  }

  /**
   * <p>The network has connected but no synchronization has occurred so show the progress bar with 0%</p>
   *
   * @return A new "downloading blockchain" summary
   */
  public static BitcoinNetworkSummary newChainDownloadStarted() {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.DOWNLOADING_BLOCKCHAIN,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.CHAIN_DOWNLOAD),
      Optional.of(new Object[]{"0"}),
      0,
      0
    );
  }

  /**
   * <p>The network has begun to synchronize so show the progress bar</p>
   *
   * @param percent The percentage of blocks downloaded
   *
   * @return A new "progress update" summary
   */
  public static BitcoinNetworkSummary newChainDownloadProgress(int percent) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.DOWNLOADING_BLOCKCHAIN,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.CHAIN_DOWNLOAD),
      Optional.of(new Object[]{percent}),
      0,
      percent
    );
  }

  /**
   * <p>The network is only ready when 100% synchronization has been achieved</p>
   *
   * @param peerCount The peer count
   *
   * @return A new "network ready with peer count" summary
   */
  public static BitcoinNetworkSummary newNetworkReady(int peerCount) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.SYNCHRONIZED,
      RAGStatus.GREEN,
      Optional.of(CoreMessageKey.PEER_COUNT),
      Optional.of(new Object[]{peerCount}),
      peerCount,
      100
    );
  }

  /**
   * <p>The network has failed to synchronize so hide the progress bar and report the failure</p>
   *
   * @param messageKey The message key to allow localisation
   *
   * @return A new "startup failed" summary
   */
  public static BitcoinNetworkSummary newNetworkStartupFailed(CoreMessageKey messageKey, Optional<Object[]> messageData) {
    return new BitcoinNetworkSummary(
      BitcoinNetworkStatus.NOT_CONNECTED,
      RAGStatus.RED,
      Optional.of(messageKey),
      messageData,
      0,
      -1
    );
  }

  /**
   * @param status      The network status (e.g. NOT_CONNECTED)
   * @param severity    The severity (Red, Amber, Green)
   * @param messageKey  The error key to allow localisation
   * @param messageData The error data for insertion into the error message
   * @param peerCount   The current peer count
   * @param percent     The percentage of blocks downloaded (-1 means hide, 0-99 "in progress", 100 "success")
   */
  public BitcoinNetworkSummary(
    BitcoinNetworkStatus status,
    RAGStatus severity,
    Optional<CoreMessageKey> messageKey,
    Optional<Object[]> messageData,
    int peerCount,
    int percent) {

    this.status = status;
    this.severity = severity;

    this.messageKey = messageKey;
    this.messageData = messageData;

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

  /**
   * @return The severity (e.g. AMBER)
   */
  public RAGStatus getSeverity() {
    return severity;
  }

  /**
   * @return The network status (e.g. "CONNECTING")
   */
  public BitcoinNetworkStatus getStatus() {
    return status;
  }

  /**
   * @return An optional array of arbitrary objects, often for insertion into a resource bundle string
   */
  public Optional<Object[]> getMessageData() {
    return messageData;
  }

  public Optional<CoreMessageKey> getMessageKey() {
    return messageKey;
  }

  @Override
  public String toString() {
    return "BitcoinNetworkSummary{" +
      "errorData=" + messageData +
      ", status=" + status +
      ", peerCount=" + peerCount +
      ", percent=" + percent +
      ", severity=" + severity +
      ", errorKey=" + messageKey +
      '}';
  }
}
