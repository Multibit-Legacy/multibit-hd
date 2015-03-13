package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>Additional payment protocol (BIP70) payment request info</li>
 * </ul>
 */
public class PaymentRequestData implements PaymentData {

  /**
   * A UUID used in persisting the BIP70 payment request and as a foreign key to find data from the transaction hash
   */
  private final UUID uuid;

  /**
   * The transaction hash of the transaction that paid this payment request.
   * May be absent if the payment request has not been paid yet.
   */
  private Optional<Sha256Hash> transactionHashOptional;


  /**
   * The fiat payment equivalent of the payment Coin (bitcoin
   */
  private Optional<FiatPayment> fiatPaymentOptional;

  /**
   * The BIP70 PaymentRequest
   */
  private final Protos.PaymentRequest paymentRequest;

  /**
   * The parsed PaymentSession (parsed from the PaymentRequest)
   */
  private Optional<PaymentSession> paymentSessionOptional;


  private static final Logger log = LoggerFactory.getLogger(PaymentRequestData.class);


  public PaymentRequestData(Protos.PaymentRequest paymentRequest, Optional<Sha256Hash> transactionHashOptional) {
    Preconditions.checkNotNull(paymentRequest);
    Preconditions.checkNotNull(transactionHashOptional);

    this.paymentRequest = paymentRequest;
    this.transactionHashOptional = transactionHashOptional;
    this.uuid = UUID.randomUUID();
    this.fiatPaymentOptional = Optional.absent();

    try {
      this.paymentSessionOptional = Optional.of(PaymentProtocol.parsePaymentRequest(paymentRequest));
    } catch (PaymentProtocolException ppe) {
      log.error("Cannot parse PaymentRequest into a PaymentSession", ppe);
      this.paymentSessionOptional = Optional.absent();
    }
  }

  public UUID getUuid() {
    return uuid;
  }

  public Optional<Sha256Hash> getTransactionHashOptional() {
    return transactionHashOptional;
  }

  public void setTransactionHashOptional(Optional<Sha256Hash> transactionHashOptional) {
    this.transactionHashOptional = transactionHashOptional;
  }

  public Protos.PaymentRequest getPaymentRequest() {
    return paymentRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PaymentRequestData that = (PaymentRequestData) o;

    if (!paymentRequest.equals(that.paymentRequest)) return false;
    if (!transactionHashOptional.equals(that.transactionHashOptional)) return false;
    if (!uuid.equals(that.uuid)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = uuid.hashCode();
    result = 31 * result + transactionHashOptional.hashCode();
    result = 31 * result + paymentRequest.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "PaymentRequestData{" +
            "uuid=" + uuid +
            ", transactionHashOptional=" + transactionHashOptional +
            ", paymentRequest=" + paymentRequest +
            '}';
  }

  @Override
  public PaymentType getType() {
    // TODO refine - can be part paid or paid
    return PaymentType.BIP70_REQUESTED;
  }

  @Override
  public PaymentStatus getStatus() {
    // TODO refine - can be part paid or paid
    return new PaymentStatus(RAGStatus.PINK, CoreMessageKey.PAYMENT_REQUESTED);
  }

  @Override
  public DateTime getDate() {
    if (paymentSessionOptional.isPresent()) {
      return new DateTime(paymentSessionOptional.get().getDate());
    } else {
      return null;
    }
  }

  @Override
  public Coin getAmountCoin() {
    if (paymentSessionOptional.isPresent()) {
      return paymentSessionOptional.get().getValue();
    } else {
      return null;
    }
  }

  public void setAmountFiat(FiatPayment fiatPayment) {
    fiatPaymentOptional = Optional.of(fiatPayment);
  }

  @Override
  public FiatPayment getAmountFiat() {
    if (fiatPaymentOptional.isPresent()) {
      return fiatPaymentOptional.get();
    } else {
      return new FiatPayment();
    }
  }

  @Override
  public String getNote() {
    return "";
  }

  @Override
  public String getDescription() {
    if (paymentSessionOptional.isPresent()) {
      return paymentSessionOptional.get().getMemo();
    } else {
      return "";
    }
  }


  @Override
  public boolean isCoinBase() {
    return false;
  }
}
