package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.protocols.payments.PaymentSession;
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
  private UUID uuid;

  /**
   * The transaction hash of the transaction that paid this payment request.
   * May be absent if the payment request has not been paid yet.
   */
  private Optional<Sha256Hash> transactionHashOptional = Optional.absent();


  /**
   * The fiat payment equivalent of the payment Coin
   */
  private Optional<FiatPayment> fiatPaymentOptional = Optional.absent();

  /**
   * The BIP70 PaymentRequest - stored as a file on the file system
   */
  private Protos.PaymentRequest paymentRequest;

  /**
   * The date of the payment request
   */
  private DateTime date;

  /**
   * The amount in bitcoin of the payment request
   */
  private Coin amountBTC;

  /**
   * The description of the payment request
   */
  private String note = "";

  /**
   * The display name of the PKI identity
   */
  private String identityDisplayName = "";

  /**
   * The trust status - whether the PKI verify was successful
   */
  private PaymentSessionStatus trustStatus;

  /**
   * If the trust status is ERROR or DOWN, a text string describing the problem
   */
  private String trustErrorMessage;

  /**
   * The expiration date for the payment request
   */
  private DateTime expirationDate;


  /**
   * For protobuf - you probably do not want to use this
   */
  public PaymentRequestData() {
  }

  public PaymentRequestData(Protos.PaymentRequest paymentRequest, Optional<Sha256Hash> transactionHashOptional) {
    Preconditions.checkNotNull(paymentRequest);
    Preconditions.checkNotNull(transactionHashOptional);

    this.paymentRequest = paymentRequest;
    this.transactionHashOptional = transactionHashOptional;
    this.uuid = UUID.randomUUID();
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
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

  public void setPaymentRequest(Protos.PaymentRequest paymentRequest) {
    this.paymentRequest = paymentRequest;
  }

  public void setPaymentSessionSummaryOptional(Optional<PaymentSessionSummary> paymentSessionSummaryOptional) {
    // Work out dates and save them
    if (paymentSessionSummaryOptional.isPresent() && paymentSessionSummaryOptional.get().getPaymentSession().isPresent()) {
      PaymentSession paymentSession = paymentSessionSummaryOptional.get().getPaymentSession().get();
      date = new DateTime(paymentSession.getDate());
      amountBTC = paymentSession.getValue();
      note = paymentSession.getMemo();
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PaymentRequestData that = (PaymentRequestData) o;

    if (amountBTC != null ? !amountBTC.equals(that.amountBTC) : that.amountBTC != null) return false;
    if (date != null ? !date.equals(that.date) : that.date != null) return false;
    if (expirationDate != null ? !expirationDate.equals(that.expirationDate) : that.expirationDate != null)
      return false;
    if (fiatPaymentOptional != null ? !fiatPaymentOptional.equals(that.fiatPaymentOptional) : that.fiatPaymentOptional != null)
      return false;
    if (identityDisplayName != null ? !identityDisplayName.equals(that.identityDisplayName) : that.identityDisplayName != null)
      return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;
    if (paymentRequest != null ? !paymentRequest.equals(that.paymentRequest) : that.paymentRequest != null)
      return false;
    if (transactionHashOptional != null ? !transactionHashOptional.equals(that.transactionHashOptional) : that.transactionHashOptional != null)
      return false;
    if (trustErrorMessage != null ? !trustErrorMessage.equals(that.trustErrorMessage) : that.trustErrorMessage != null)
      return false;
    if (trustStatus != that.trustStatus) return false;
    if (!uuid.equals(that.uuid)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = uuid.hashCode();
    result = 31 * result + (transactionHashOptional != null ? transactionHashOptional.hashCode() : 0);
    result = 31 * result + (fiatPaymentOptional != null ? fiatPaymentOptional.hashCode() : 0);
    result = 31 * result + (paymentRequest != null ? paymentRequest.hashCode() : 0);
    result = 31 * result + (date != null ? date.hashCode() : 0);
    result = 31 * result + (amountBTC != null ? amountBTC.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (identityDisplayName != null ? identityDisplayName.hashCode() : 0);
    result = 31 * result + (trustStatus != null ? trustStatus.hashCode() : 0);
    result = 31 * result + (trustErrorMessage != null ? trustErrorMessage.hashCode() : 0);
    result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "PaymentRequestData{" +
            "uuid=" + uuid +
            ", transactionHashOptional=" + transactionHashOptional +
            ", fiatPaymentOptional=" + fiatPaymentOptional +
            ", date=" + date +
            ", amountBTC=" + amountBTC +
            ", note='" + note + '\'' +
            ", identityDisplayName='" + identityDisplayName + '\'' +
            ", trustStatus=" + trustStatus +
            ", trustErrorMessage='" + trustErrorMessage + '\'' +
            ", expirationDate=" + expirationDate +
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
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  @Override
  public Coin getAmountCoin() {
    return amountBTC;
  }

  public void setAmountCoin(Coin amountBTC) {
    this.amountBTC = amountBTC;
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
    return note;
  }

  @Override
  public String getDescription() {
    return getNote();
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public boolean isCoinBase() {
    return false;
  }

  public String getIdentityDisplayName() {
    return identityDisplayName;
  }

  public void setIdentityDisplayName(String identityDisplayName) {
    this.identityDisplayName = identityDisplayName;
  }

  public PaymentSessionStatus getTrustStatus() {
    return trustStatus;
  }

  public void setTrustStatus(PaymentSessionStatus trustStatus) {
    this.trustStatus = trustStatus;
  }

  public String getTrustErrorMessage() {
    return trustErrorMessage;
  }

  public void setTrustErrorMessage(String trustErrorMessage) {
    this.trustErrorMessage = trustErrorMessage;
  }

  public DateTime getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(DateTime expirationDate) {
    this.expirationDate = expirationDate;
  }
}
