package org.multibit.hd.core.api;

import com.google.bitcoin.core.TransactionConfidence;
import com.google.common.base.Optional;

import java.math.BigInteger;
import java.util.Date;

/**
 *  <p>Data object to provide the following to Transaction display:<br>
 *  <ul>
 *  <li>Contains transaction relevant data</li>
 *  <li>Immutable</li>
 *  </ul>
 *  
 */
public class TransactionData {

  private String transactionId;

  private BigInteger amountBTC;

  private Optional<BigInteger> feeOnSendBTC;

  private int depth;

  private TransactionConfidence.ConfidenceType confidenceType;

  private Date updateTime;

  public TransactionData(String transactionId, Date updateTime,
                         BigInteger amountBTC, Optional<BigInteger> feeOnSendBTC,
                         TransactionConfidence.ConfidenceType confidenceType, int depth) {
    this.transactionId = transactionId;
    this.updateTime = updateTime;
    this.amountBTC = amountBTC;
    this.feeOnSendBTC = feeOnSendBTC;
    this.confidenceType = confidenceType;
    this.depth = depth;
  }

  /*
  Broadcast ok,
  relayed ok,
  Raw transaction,
  Manual notes,
  automatic notes,
  tags,
  fiat amount,
  exchange rate,
  exchange name,
   */

  @Override
  public String toString() {
    return "TransactionData{" +
            "transactionId='" + transactionId + '\'' +
            ", amountBTC=" + amountBTC +
            ", feeOnSendBTC=" + feeOnSendBTC +
            ", depth=" + depth +
            ", confidenceType=" + confidenceType +
            ", updateTime=" + updateTime +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransactionData that = (TransactionData) o;

    if (depth != that.depth) return false;
    if (!amountBTC.equals(that.amountBTC)) return false;
    if (confidenceType != that.confidenceType) return false;
    if (!feeOnSendBTC.equals(that.feeOnSendBTC)) return false;
    if (!transactionId.equals(that.transactionId)) return false;
    if (!updateTime.equals(that.updateTime)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = transactionId.hashCode();
    result = 31 * result + amountBTC.hashCode();
    result = 31 * result + feeOnSendBTC.hashCode();
    result = 31 * result + depth;
    result = 31 * result + confidenceType.hashCode();
    result = 31 * result + updateTime.hashCode();
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

  public int getDepth() {
    return depth;
  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  public Date getUpdateTime() {
    return updateTime;
  }
}
