package org.multibit.hd.core.events;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>A transaction has been seen coming in from the Bitcoin netowork</li>
 *  </ul>
 *
 */
public class TransactionSeenEvent {
  private final String transactionId;

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  public int getDepthInBlocks() {
    return depthInBlocks;
  }

  private final TransactionConfidence.ConfidenceType confidenceType;
  private final int depthInBlocks;

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
