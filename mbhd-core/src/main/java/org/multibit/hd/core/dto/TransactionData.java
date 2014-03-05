package org.multibit.hd.core.dto;

import com.google.bitcoin.core.TransactionConfidence;
import com.google.common.base.Optional;
import org.joda.time.DateTime;

import java.math.BigInteger;

/**
 *  <p>Data object to provide the following to Payments display:<br>
 *  <ul>
 *  <li>Contains transaction relevant data</li>
 *  <li>Immutable</li>
 *  </ul>
 *  
 */

public class TransactionData implements PaymentData {

  private final RAGStatus status;

  private final String transactionId;

  private final BigInteger amountBTC;

  private FiatPayment amountFiat;

  private final Optional<BigInteger> feeOnSendBTC;

  private final int depth;

  private final TransactionConfidence.ConfidenceType confidenceType;

  private final DateTime date;

  private final PaymentType type;

  private String description;

  private String note;

  private final boolean coinBase;

  public TransactionData(String transactionId, DateTime date, RAGStatus status,
                         BigInteger amountBTC, FiatPayment amountFiat, Optional<BigInteger> feeOnSendBTC,
                         TransactionConfidence.ConfidenceType confidenceType, PaymentType type, int depth, String description, boolean coinBase) {
    this.transactionId = transactionId;
    this.date = date;
    this.status = status;
    this.amountBTC = amountBTC;
    this.amountFiat = amountFiat;
    this.feeOnSendBTC = feeOnSendBTC;
    this.confidenceType = confidenceType;
    this.type = type;
    this.depth = depth;
    this.description = description;
    this.coinBase = coinBase;
  }

  @Override
  public String toString() {
    return "TransactionData{" +
            "transactionId='" + transactionId + '\'' +
            "status=" + status +
            ", amountBTC=" + amountBTC +
            ", amountFiat=" + amountFiat +
            ", feeOnSendBTC=" + feeOnSendBTC +
            ", depth=" + depth +
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

    if (depth != that.depth) return false;
    if (status != that.status) return false;
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
    result = 31 * result + status.hashCode();
    result = 31 * result + amountBTC.hashCode();
    result = 31 * result + amountFiat.hashCode();
    result = 31 * result + feeOnSendBTC.hashCode();
    result = 31 * result + depth;
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

  public BigInteger getAmountBTC() {
    return amountBTC;
  }

  public Optional<BigInteger> getFeeOnSendBTC() {
    return feeOnSendBTC;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  public DateTime getDate() {
    return date;
  }

  @Override
  public RAGStatus getStatus() {
    return status;
  }

  public PaymentType getType() {
    return type;
  }

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
}
