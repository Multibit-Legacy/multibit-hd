package org.multibit.hd.core.events;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;

import java.math.BigInteger;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>A transaction has been seen coming in from the Bitcoin netowork</li>
 *  </ul>
 */
public class TransactionSeenEvent implements CoreEvent {

  private final String transactionId;

  private final TransactionConfidence.ConfidenceType confidenceType;
  private final int depthInBlocks;

  private BigInteger value;

  /**
   * This is the first time this transaction has been seen in the wallet
   */
  private boolean firstAppearanceInWallet = false;

  public static final int DEPTH_IN_BLOCKS_IS_UNDEFINED = -1;

  public TransactionSeenEvent(Transaction transactionSeen) {

    transactionId = transactionSeen.getHashAsString();
    TransactionConfidence confidence = transactionSeen.getConfidence();

    confidenceType = confidence.getConfidenceType();

    if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
      depthInBlocks = confidence.getDepthInBlocks();
    } else {
      depthInBlocks = DEPTH_IN_BLOCKS_IS_UNDEFINED;
    }

  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  public int getDepthInBlocks() {
    return depthInBlocks;
  }

  public BigInteger getValue() {
    return value;
  }

  public void setValue(BigInteger value) {
    this.value = value;
  }

  public boolean isFirstAppearanceInWallet() {
    return firstAppearanceInWallet;
  }

  public void setFirstAppearanceInWallet(boolean firstAppearanceInWallet) {
    this.firstAppearanceInWallet = firstAppearanceInWallet;
  }

  public String getTransactionId() {
    return transactionId;
  }

  @Override
  public String toString() {
    return "TransactionSeenEvent{" +
      "transactionId='" + transactionId + '\'' +
      ", confidenceType=" + confidenceType +
      ", depthInBlocks=" + depthInBlocks +
      '}';
  }
}
