package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Sha256Hash;

import java.util.UUID;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>Additional payment protocol (BIP70) payment request info</li>
 * </ul>
 *
 */
public class PaymentRequestData{

  /**
   * A UUID used in persisting the BIP70 payment request and as a foreign key to find data from the transaction hash
   */
  private final UUID uuid;

  /**
   * The transaction hash of the transaction that paid this payment request.
   * May be absent if the payment request has not been paid yet.
   */
  private final Optional<Sha256Hash> transactionHashOptional;

  /**
   * The BIP70 PaymentRequest
   */
  private final Protos.PaymentRequest paymentRequest;


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

  public Optional<Sha256Hash> getTransactionHashOptional() {
    return transactionHashOptional;
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
}
