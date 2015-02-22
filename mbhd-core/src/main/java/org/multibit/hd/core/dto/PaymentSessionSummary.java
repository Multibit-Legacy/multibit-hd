package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Value object to provide the following to Core API:</p>
 * <ul>
 * <li>Information about a payment session's status</li>
 * </ul>
 *
 * @since 0.0.7
 */
public class PaymentSessionSummary {

  private final PaymentSessionStatus status;
  private final Optional<PaymentSession> paymentSession;

  private final RAGStatus severity;

  private final Optional<CoreMessageKey> messageKey;
  private final Optional<Object[]> messageData;

  /**
   * <p>The server has returned a well-formed payment request</p>
   *
   * @param paymentSession The payment session containing meta data
   *
   * @return A new "payment session OK" summary
   */
  public static PaymentSessionSummary newPaymentSessionOK(PaymentSession paymentSession) {
    return new PaymentSessionSummary(
      Optional.<PaymentSession>absent(),
      PaymentSessionStatus.OK,
      RAGStatus.GREEN,
      Optional.of(CoreMessageKey.PAYMENT_SESSION_OK),
      Optional.<Object[]>fromNullable(null)
    );
  }

  /**
   * @param e The payment protocol exception (either an ERROR or a DOWN)
   *
   * @return A suitable payment session summary
   */
  public static PaymentSessionSummary newPaymentSessionFromException(Exception e, String hostName) {

    // Default handling is ERROR

    if (e instanceof ExecutionException) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.DOWN,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_DOWN),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof InterruptedException) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.DOWN,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_DOWN),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof TimeoutException) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.DOWN,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_DOWN),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }

    // Use default response
    return new PaymentSessionSummary(
      Optional.<PaymentSession>absent(),
      PaymentSessionStatus.ERROR,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.PAYMENT_SESSION_ERROR),
      Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
    );

  }

  /**
   * @param e The payment protocol exception (specific problem)
   *
   * @return A suitable payment session summary
   */
  public static PaymentSessionSummary newPaymentSessionFromException(PaymentProtocolException e, String hostName) {

    if (e instanceof PaymentProtocolException.Expired) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_EXPIRED),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidNetwork) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_NETWORK),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidOutputs) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_OUTPUTS),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPaymentRequestURL) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_REQUEST_URL),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPaymentURL) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_PAYMENT_URL),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidVersion) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_VERSION),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPkiData) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPkiType) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID_TYPE),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.PkiVerificationException) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.PKI_MISSING,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_MISSING),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }

    // Unknown
    return new PaymentSessionSummary(
      Optional.<PaymentSession>absent(),
      PaymentSessionStatus.ERROR,
      RAGStatus.AMBER,
      Optional.of(CoreMessageKey.PAYMENT_SESSION_ERROR),
      Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
    );

  }

  /**
   * <p>See the utility factory methods for standard situations</p>
   *
   * @param paymentSession The optional payment session
   * @param status         The payment session status (e.g. OK)
   * @param severity       The severity (Red, Amber, Green)
   * @param messageKey     The error key to allow localisation
   * @param messageData    The error data for insertion into the error message
   */
  public PaymentSessionSummary(
    Optional<PaymentSession> paymentSession,
    PaymentSessionStatus status,
    RAGStatus severity,
    Optional<CoreMessageKey> messageKey,
    Optional<Object[]> messageData
  ) {

    this.paymentSession = paymentSession;

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
   * @return The payment session status (e.g. "OK")
   */
  public PaymentSessionStatus getStatus() {
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
    return "PaymentSessionSummary{" +
      "messageData=" + messageData +
      ", status=" + status +
      ", paymentSession=" + paymentSession +
      ", severity=" + severity +
      ", messageKey=" + messageKey +
      '}';
  }
}
