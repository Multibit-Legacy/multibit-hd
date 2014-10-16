package org.multibit.hd.core.dto;

import com.google.common.base.Optional;

/**
 * <p>Value object to provide the following to Core API:</p>
 * <ul>
 * <li>Information about an exchange's status</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ExchangeSummary {

  private final ExchangeStatus status;

  private final RAGStatus severity;

  private final Optional<CoreMessageKey> messageKey;
  private final Optional<Object[]> messageData;

  /**
   * <p>The exchange has returned a rate</p>
   *
   * @param exchangeName The exchange name
   *
   * @return A new "exchange OK" summary
   */
  public static ExchangeSummary newExchangeOK(String exchangeName) {
    return new ExchangeSummary(
      ExchangeStatus.OK,
      RAGStatus.GREEN,
      Optional.of(CoreMessageKey.EXCHANGE_OK),
      Optional.<Object[]>fromNullable(new String[]{exchangeName})
    );
  }

  /**
   * <p>The exchange has returned an error. User may want to change their settings.</p>
   *
   * @param exchangeName The exchange name
   * @param message      The message from the exception
   *
   * @return A new "exchange error" summary
   */
  public static ExchangeSummary newExchangeError(String exchangeName, String message) {
    return new ExchangeSummary(
      ExchangeStatus.ERROR,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.EXCHANGE_ERROR),
      Optional.<Object[]>fromNullable(new String[]{exchangeName, message})
    );
  }

  /**
   * <p>The exchange could not be reached. User may want to change their settings.</p>
   *
   * @param exchangeName The exchange name
   * @param message      The message from the exception
   *
   * @return A new "exchange down" summary
   */
  public static ExchangeSummary newExchangeDown(String exchangeName, String message) {
    return new ExchangeSummary(
      ExchangeStatus.DOWN,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.EXCHANGE_DOWN),
      Optional.<Object[]>fromNullable(new String[]{exchangeName, message})
    );
  }

  /**
   * @param status      The network status (e.g. OK)
   * @param severity    The severity (Red, Amber, Green)
   * @param messageKey  The error key to allow localisation
   * @param messageData The error data for insertion into the error message
   */
  public ExchangeSummary(
    ExchangeStatus status,
    RAGStatus severity,
    Optional<CoreMessageKey> messageKey,
    Optional<Object[]> messageData
  ) {

    this.status = status;
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
   * @return The exchange status (e.g. "OK")
   */
  public ExchangeStatus getStatus() {
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
    return "ExchangeSummary{" +
      "status=" + status +
      ", severity=" + severity +
      ", messageKey=" + messageKey +
      ", messageData=" + messageData +
      '}';
  }
}
