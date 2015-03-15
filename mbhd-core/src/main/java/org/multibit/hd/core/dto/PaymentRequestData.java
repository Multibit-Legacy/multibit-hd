package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.joda.time.DateTime;

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
   * The PaymentSessionSummary
   */
  private Optional<PaymentSessionSummary> paymentSessionSummaryOptional = Optional.absent();

  /**
   * BIP 70 PKI identity information
   */
  private Optional<PaymentProtocol.PkiVerificationData> PkiVerificationDataOptional = Optional.absent();


  public PaymentRequestData(Protos.PaymentRequest paymentRequest, Optional<Sha256Hash> transactionHashOptional) {
    Preconditions.checkNotNull(paymentRequest);
    Preconditions.checkNotNull(transactionHashOptional);

    this.paymentRequest = paymentRequest;
    this.transactionHashOptional = transactionHashOptional;
    this.uuid = UUID.randomUUID();
    this.fiatPaymentOptional = Optional.absent();
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

  public Optional<PaymentSessionSummary> getPaymentSessionSummaryOptional() {
    return paymentSessionSummaryOptional;
  }

  public void setPaymentSessionSummaryOptional(Optional<PaymentSessionSummary> paymentSessionSummaryOptional) {
    this.paymentSessionSummaryOptional = paymentSessionSummaryOptional;
  }

  public Optional<PaymentProtocol.PkiVerificationData> getPkiVerificationDataOptional() {
    return PkiVerificationDataOptional;
  }

  public void setPkiVerificationDataOptional(Optional<PaymentProtocol.PkiVerificationData> pkiVerificationDataOptional) {
    PkiVerificationDataOptional = pkiVerificationDataOptional;
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
    if (transactionHashOptional.isPresent()) {
      return PaymentType.PAID;
    } else {
      return PaymentType.THEY_REQUESTED;
    }
  }

  @Override
  public PaymentStatus getStatus() {
    if (transactionHashOptional.isPresent()) {
      return new PaymentStatus(RAGStatus.GREEN, CoreMessageKey.PAYMENT_PAID);
    } else {
      return new PaymentStatus(RAGStatus.PINK, CoreMessageKey.PAYMENT_REQUESTED_BY_THEM);
    }
   }

  @Override
  public DateTime getDate() {
    if (paymentSessionSummaryOptional.isPresent() && paymentSessionSummaryOptional.get().getPaymentSession().isPresent()) {
      return new DateTime(paymentSessionSummaryOptional.get().getPaymentSession().get().getDate());
    } else {
      return null;
    }
  }

  @Override
  public Coin getAmountCoin() {
    if (paymentSessionSummaryOptional.isPresent() && paymentSessionSummaryOptional.get().getPaymentSession().isPresent()) {
      return paymentSessionSummaryOptional.get().getPaymentSession().get().getValue();
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
    if (paymentSessionSummaryOptional.isPresent() && paymentSessionSummaryOptional.get().getPaymentSession().isPresent()) {
      return paymentSessionSummaryOptional.get().getPaymentSession().get().getMemo();
    } else {
      return "";
    }
  }

  @Override
  public boolean isCoinBase() {
    return false;
  }
}
