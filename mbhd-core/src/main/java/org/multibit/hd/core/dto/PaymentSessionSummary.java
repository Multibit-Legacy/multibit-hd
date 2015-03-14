package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStoreException;
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

  private static final Logger log = LoggerFactory.getLogger(PaymentSessionSummary.class);

  private final PaymentSessionStatus status;
  private final Optional<PaymentSession> paymentSession;

  /**
   * Payment data including the tx that was used to pay it and a UUID used for persistence
   */
  private Optional<PaymentRequestData> paymentRequestDataOptional = Optional.absent();

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
      Optional.fromNullable(paymentSession),
      PaymentSessionStatus.TRUSTED,
      RAGStatus.GREEN,
      Optional.of(CoreMessageKey.PAYMENT_SESSION_OK),
      Optional.<Object[]>fromNullable(null)
    );
  }

  /**
   * <p>The server has returned a well-formed payment request that has failed PKI validation</p>
   *
   * <p>The user may want to proceed under these circumstances so we cater for it.</p>
   *
   * @param paymentSession The payment session containing meta data
   *
   * @return A new "payment session" summary with appropriate confidence level
   */
  public static PaymentSessionSummary newPaymentSessionAlmostOK(PaymentSession paymentSession, Exception e) {

    if (e instanceof PaymentProtocolException.InvalidPkiData) {
      return new PaymentSessionSummary(
        Optional.of(paymentSession),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID),
        Optional.<Object[]>fromNullable(new String[]{paymentSession.getMemo(), e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPkiType) {
      return new PaymentSessionSummary(
        Optional.of(paymentSession),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID_TYPE),
        Optional.<Object[]>fromNullable(new String[]{paymentSession.getMemo(), e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.PkiVerificationException) {
      return new PaymentSessionSummary(
        Optional.of(paymentSession),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_MISSING),
        Optional.<Object[]>fromNullable(new String[]{paymentSession.getMemo(), e.getMessage()})
      );
    }

    if (e instanceof KeyStoreException) {
      return new PaymentSessionSummary(
        Optional.of(paymentSession),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_UNTRUSTED_CA),
        Optional.<Object[]>fromNullable(new String[]{paymentSession.getMemo(), e.getMessage()})
      );
    }

    // Assume the worst
    return newPaymentSessionFromException(e, paymentSession.getMemo());

  }

  /**
   * @param e The payment protocol exception (either an ERROR or a DOWN)
   *
   * @return A suitable payment session summary
   */
  public static PaymentSessionSummary newPaymentSessionFromException(Exception e, String hostName) {

    log.warn("Failed payment server: Host={} Failure={}", hostName, e.getMessage());

    // Default handling is ERROR

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
   * @param e              The payment protocol exception (specific problem)
   * @param paymentRequest The payment request providing extra information
   * @param hostName  The host name
   * @return A suitable payment session summary
   */
  public static PaymentSessionSummary newPaymentSessionFromException(PaymentProtocolException e, Protos.PaymentRequest paymentRequest, String hostName) {

    log.warn("Failed payment session: Host={} Failure={}", hostName, e.getMessage());

    // Default handling is ERROR

    if (e instanceof PaymentProtocolException.Expired) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_EXPIRED),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidNetwork) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_NETWORK),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidOutputs) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_OUTPUTS),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPaymentRequestURL) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_REQUEST_URL),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPaymentURL) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_PAYMENT_URL),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidVersion) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_INVALID_VERSION),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPkiData) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.InvalidPkiType) {
      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
        RAGStatus.AMBER,
        Optional.of(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID_TYPE),
        Optional.<Object[]>fromNullable(new String[]{hostName, e.getMessage()})
      );
    }
    if (e instanceof PaymentProtocolException.PkiVerificationException) {

      return new PaymentSessionSummary(
        Optional.<PaymentSession>absent(),
        PaymentSessionStatus.UNTRUSTED,
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
   * @return The payment session containing the payment request and other meta data
   */
  public Optional<PaymentSession> getPaymentSession() {
    return paymentSession;
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

  public Optional<PaymentRequestData> getPaymentRequestDataOptional() {
    return paymentRequestDataOptional;
  }

  public void setPaymentRequestDataOptional(Optional<PaymentRequestData> paymentRequestDataOptional) {
    this.paymentRequestDataOptional = paymentRequestDataOptional;
  }

  @Override
  public String toString() {
    return "PaymentSessionSummary{" +
      "messageData=" + messageData +
      ", status=" + status +
      ", paymentSession=" + paymentSession +
      ", severity=" + severity +
      ", messageKey=" + messageKey +
            ", paymentRequestDataOptional=" + paymentRequestDataOptional +
      '}';
  }
}
