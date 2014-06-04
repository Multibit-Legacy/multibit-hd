package org.multibit.hd.core.events;

import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;

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

  private final int numberOfPeers;

  private final boolean coinbase;

  private Coin amount;

  /**
   * This is the first time this transaction has been seen in the wallet
   */
  private boolean firstAppearanceInWallet = false;

  public static final int DEPTH_IN_BLOCKS_IS_UNDEFINED = -1;

  /**
   * @param transaction The Bitcoinj transaction providing the information
   */
  public TransactionSeenEvent(Transaction transaction) {

    transactionId = transaction.getHashAsString();
    TransactionConfidence confidence = transaction.getConfidence();

    confidenceType = confidence.getConfidenceType();

    if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
      depthInBlocks = confidence.getDepthInBlocks();
    } else {
      depthInBlocks = DEPTH_IN_BLOCKS_IS_UNDEFINED;
    }

    coinbase = transaction.isCoinBase();

    numberOfPeers = confidence.numBroadcastPeers();

  }

  public TransactionConfidence.ConfidenceType getConfidenceType() {
    return confidenceType;
  }

  /**
   * @return The depth in blocks (confirmations), -1 if unknown
   */
  public int getDepthInBlocks() {
    return depthInBlocks;
  }

  /**
   * @return The amount in coins
   */
  public Coin getAmount() {
    return amount;
  }

  public void setAmount(Coin amount) {
    this.amount = amount;
  }

  /**
   * @return True if this is the first time this transaction has appeared in the wallet
   */
  public boolean isFirstAppearanceInWallet() {
    return firstAppearanceInWallet;
  }

  public void setFirstAppearanceInWallet(boolean firstAppearanceInWallet) {
    this.firstAppearanceInWallet = firstAppearanceInWallet;
  }

  /**
   * @return The transaction ID
   */
  public String getTransactionId() {
    return transactionId;
  }

  /**
   * @return True if this transaction is from the coinbase
   */
  public boolean isCoinbase() {
    return coinbase;
  }

  /**
   * @return The number of peers that have broadcast this transaction
   */
  public int getNumberOfPeers() {
    return numberOfPeers;
  }

  @Override
  public String toString() {
    return "TransactionSeenEvent{" +
      "transactionId='" + transactionId + '\'' +
      ", confidenceType=" + confidenceType +
      ", depthInBlocks=" + depthInBlocks +
      ", numberOfPeers=" + numberOfPeers +
      ", coinbase=" + coinbase +
      ", value=" + amount +
      ", firstAppearanceInWallet=" + firstAppearanceInWallet +
      '}';
  }
}
