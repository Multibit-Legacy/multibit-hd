package org.multibit.hd.core.events;

import com.google.bitcoin.core.Address;

import java.math.BigInteger;
import java.util.Arrays;

/**
 *  <p>Event to provide the following to UI event subscribers:</p>
 *  <ul>
 *  <li>Success or failure of creation of transaction sending bitcoins</li>
 *  </ul>
 *
 * <p>Success guarantees that the wallet contains the underlying transaction.</p>
 */
public class TransactionCreationEvent implements CoreEvent {

  private final BigInteger amount;

  private final BigInteger feePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean transactionCreationWasSuccessful;

  private final String transactionId;

  private final String transactionCreationFailureReasonKey;

  public TransactionCreationEvent(
    String transactionId,
    BigInteger amount,
    BigInteger feePaid,
    Address destinationAddress,
    Address changeAddress,
    boolean transactionCreationWasSuccessful,
    String transactionCreationFailureReasonKey,
    String[] transactionCreationFailureReasonData
  ) {

    this.transactionId = transactionId;
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

  public Address getDestinationAddress() {
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

  public String getTransactionId() {
    return transactionId;
  }

  @Override
  public String toString() {
    return "TransactionCreationEvent{" +
      "amount=" + amount +
      ", feePaid=" + feePaid +
      ", destinationAddress='" + destinationAddress + '\'' +
      ", changeAddress='" + changeAddress + '\'' +
      ", transactionCreationWasSuccessful=" + transactionCreationWasSuccessful +
      ", transactionId='" + transactionId + '\'' +
      ", transactionCreationFailureReasonKey='" + transactionCreationFailureReasonKey + '\'' +
      ", transactionCreationFailureReasonData=" + Arrays.toString(transactionCreationFailureReasonData) +
      '}';
  }
}
