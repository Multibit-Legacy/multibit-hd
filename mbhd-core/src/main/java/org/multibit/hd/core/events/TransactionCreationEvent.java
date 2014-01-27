package org.multibit.hd.core.events;

import java.math.BigInteger;
import java.util.Arrays;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>Success/ failure of creation of transaction sending bitcoins</li>
 *  </ul>
 *
 *  If the transaction creation succeeds it will be committed to the wallet, if not it will not be.
 */
public class TransactionCreationEvent {

  private final BigInteger amount;

  private final BigInteger feePaid;

  private final String destinationAddress;

  private final String changeAddress;

  private final boolean transactionCreationWasSuccessful;

  private final String transactionCreationFailureReasonKey;

  public TransactionCreationEvent(BigInteger amount, BigInteger feePaid, String destinationAddress, String changeAddress, boolean transactionCreationWasSuccessful,
                                  String transactionCreationFailureReasonKey, String[] transactionCreationFailureReasonData) {
    this.amount = amount;
    this.feePaid = feePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.transactionCreationWasSuccessful = transactionCreationWasSuccessful;
    this.transactionCreationFailureReasonKey = transactionCreationFailureReasonKey;
    this.transactionCreationFailureReasonData = transactionCreationFailureReasonData;
  }

  private final String[] transactionCreationFailureReasonData;

  public BigInteger getAmount() {
    return amount;
  }

  public BigInteger getFeePaid() {
    return feePaid;
  }

  public String getDestinationAddress() {
    return destinationAddress;
  }

  public boolean isTransactionCreationWasSuccessful() {
    return transactionCreationWasSuccessful;
  }

  public String getTransactionCreationFailureReasonKey() {
    return transactionCreationFailureReasonKey;
  }

  public String[] getTransactionCreationFailureReasonData() {
    return transactionCreationFailureReasonData;
  }

  @Override
  public String toString() {
    return "TransactionCreationEvent{" +
            "amount=" + amount +
            ", feePaid=" + feePaid +
            ", destinationAddress='" + destinationAddress + '\'' +
            ", changeAddress='" + changeAddress + '\'' +
            ", transactionCreationWasSuccessful=" + transactionCreationWasSuccessful +
            ", transactionCreationFailureReasonKey='" + transactionCreationFailureReasonKey + '\'' +
            ", transactionCreationFailureReasonData=" + Arrays.toString(transactionCreationFailureReasonData) +
            '}';
  }
}
