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
public class SecuritySummary {

  private final RAGStatus severity;

  private final Optional<CoreMessageKey> messageKey;
  private final Optional<Object[]> messageData;


  /**
   * <p>A debugger is attached - could be an attack in progress</p>
   *
   * @return A new "debugger attached" summary
   */
  public static SecuritySummary newDebuggerAttached() {
    return new SecuritySummary(
      RAGStatus.RED,
      Optional.of(CoreMessageKey.DEBUGGER_ATTACHED),
      Optional.<Object[]>absent()
    );
  }

  /**
   * <p>The network has connected but no synchronization has occurred so show the progress bar with 0%</p>
   *
   * @return A new "backup failed" summary
   */
  public static SecuritySummary newBackupFailed() {
    return new SecuritySummary(
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.CHAIN_DOWNLOAD),
      Optional.of(new Object[]{"0"})
    );
  }


  /**
   * @param severity    The severity (Red, Amber, Green)
   * @param messageKey  The error key to allow localisation
   * @param messageData The error data for insertion into the error message
   */
  public SecuritySummary(
    RAGStatus severity,
    Optional<CoreMessageKey> messageKey,
    Optional<Object[]> messageData
  ) {

    this.severity = severity;

    this.messageKey = messageKey;
    this.messageData = messageData;
  }

  /**
   * @return The severity (e.g. AMBER)
   */
  public RAGStatus getSeverity() {
    return severity;
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
      ", severity=" + severity +
      ", errorKey=" + messageKey +
      '}';
  }
}
