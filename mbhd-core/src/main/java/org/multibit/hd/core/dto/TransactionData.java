package org.multibit.hd.core.dto;

import com.google.bitcoin.core.TransactionConfidence;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.Collection;

/**
 *  <p>Data object to provide the following to Payments display:<br>
 *  <ul>
 *  <li>Contains transaction relevant data</li>
 *  <li>Immutable</li>
 *  </ul>
 *  
 */

public class TransactionData implements PaymentData {

  private final PaymentStatus statusWithOrdinal;

  private final String transactionId;

  private final BigInteger amountBTC;

  private FiatPayment amountFiat;

  private final Optional<BigInteger> feeOnSendBTC;

  private final TransactionConfidence.ConfidenceType confidenceType;

  private final DateTime date;

  private final PaymentType type;

  private String description;

  private String note;

  private final boolean coinBase;

  /**
   * A collection of the payment requests that the transaction pays bitcoin to
   */
  private Collection<String> paymentRequestAddresses;


  public TransactionData(String transactionId, DateTime date, PaymentStatus statusWithOrdinal,
                         BigInteger amountBTC, FiatPayment amountFiat, Optional<BigInteger> feeOnSendBTC,
                         TransactionConfidence.ConfidenceType confidenceType, PaymentType type, String description, boolean coinBase) {
    this.transactionId = transactionId;
    this.date = date;
    this.statusWithOrdinal = statusWithOrdinal;
    this.amountBTC = amountBTC;
    this.amountFiat = amountFiat;
    this.feeOnSendBTC = feeOnSendBTC;
    this.confidenceType = confidenceType;
    this.type = type;
    this.description = description;
    this.coinBase = coinBase;
    this.paymentRequestAddresses = Lists.newArrayList();
  }

  @Override
  public String toString() {
    return "TransactionData{" +
            "transactionId='" + transactionId + '\'' +
            "statusWithOrdinal=" + statusWithOrdinal +
            ", amountBTC=" + amountBTC +
            ", amountFiat=" + amountFiat +
            ", feeOnSendBTC=" + feeOnSendBTC +
            ", confidenceType=" + confidenceType +
            ", type=" + type +
            ", date=" + date +
            ", description='" + description + "'" +
            ", note='" + note + "'" +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransactionData that = (TransactionData) o;

    if (statusWithOrdinal != that.statusWithOrdinal) return false;
    if (!amountBTC.equals(that.amountBTC)) return false;
    if (!amountFiat.equals(that.amountFiat)) return false;
    if (confidenceType != that.confidenceType) return false;
    if (!feeOnSendBTC.equals(that.feeOnSendBTC)) return false;
    if (!transactionId.equals(that.transactionId)) return false;
    if (!type.equals(that.type)) return false;
    if (!date.equals(that.date)) return false;
    if (!description.equals(that.description)) return false;
    if (!note.equals(that.note)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = transactionId.hashCode();
    result = 31 * result + statusWithOrdinal.hashCode();
    result = 31 * result + amountBTC.hashCode();
    result = 31 * result + amountFiat.hashCode();
    result = 31 * result + feeOnSendBTC.hashCode();
    result = 31 * result + confidenceType.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + date.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + note.hashCode();
    return result;
  }

  public String getTransactionId() {
    return transactionId;
  }

  @Override
  public BigInteger getAmountBTC() {
    return amountBTC;
  }

  public Optional<BigInteger> getFeeOnSendBTC() {
    return feeOnSendBTC;
  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  @Override
  public PaymentStatus getStatus() {
    return statusWithOrdinal;
  }

  @Override
  public PaymentType getType() {
    return type;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public FiatPayment getAmountFiat() {
    return amountFiat;
  }

  public void setAmountFiat(FiatPayment fiatPayment) {
    this.amountFiat = fiatPayment;
  }

  @Override
  public boolean isCoinBase() {
    return coinBase;
  }

  public Collection<String> getPaymentRequestAddresses() {
    return paymentRequestAddresses;
  }

  public void setPaymentRequestAddresses(Collection<String> paymentRequestAddresses) {
    this.paymentRequestAddresses = paymentRequestAddresses;
  }
}
