package org.multibit.hd.core.events;

import com.google.bitcoin.core.Address;
import com.google.common.base.Optional;

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

  /**
   * The total amount paid (including the client fee)
   */
  private final BigInteger amount;

  /**
   * The mining fee that was paid (if available) or Optional.absent() if not available
   */
  private final Optional<BigInteger> miningFeePaid;

  /**
   * The client fee that was paid, or Optional.absent() if no client fee was added to the transaction
   */
  private final Optional<BigInteger> clientFeePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean transactionCreationWasSuccessful;

  private final String transactionId;

  private final String transactionCreationFailureReasonKey;

  private final Optional<String> notes;

  public TransactionCreationEvent(
    String transactionId,
    BigInteger amount,
    Optional<BigInteger> miningFeePaid,
    Optional<BigInteger> clientFeePaid,
    Address destinationAddress,
    Address changeAddress,
    boolean transactionCreationWasSuccessful,
    String transactionCreationFailureReasonKey,
    String[] transactionCreationFailureReasonData,
    Optional<String> notes
  ) {

    this.transactionId = transactionId;
    this.amount = amount;
    this.miningFeePaid = miningFeePaid;
    this.clientFeePaid = clientFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.transactionCreationWasSuccessful = transactionCreationWasSuccessful;
    this.transactionCreationFailureReasonKey = transactionCreationFailureReasonKey;
    this.transactionCreationFailureReasonData = transactionCreationFailureReasonData;
    this.notes = notes;
  }

  private final String[] transactionCreationFailureReasonData;

  public BigInteger getAmount() {
    return amount;
  }


  public Optional<BigInteger> getMiningFeePaid() {
    return miningFeePaid;
  }

  public Optional<BigInteger> getClientFeePaid() {
     return clientFeePaid;
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

  public Optional<String> getNotes() {
    return notes;
  }

  @Override
  public String toString() {
    return "TransactionCreationEvent{" +
      "amount=" + amount +
      ", miningFeePaid=" + miningFeePaid +
      ", clientFeePaid=" + clientFeePaid +
      ", destinationAddress='" + destinationAddress + '\'' +
      ", changeAddress='" + changeAddress + '\'' +
      ", transactionCreationWasSuccessful=" + transactionCreationWasSuccessful +
      ", transactionId='" + transactionId + '\'' +
      ", transactionCreationFailureReasonKey='" + transactionCreationFailureReasonKey + '\'' +
      ", transactionCreationFailureReasonData=" + Arrays.toString(transactionCreationFailureReasonData) +
      ", notes=" + notes +
      '}';
  }
}
