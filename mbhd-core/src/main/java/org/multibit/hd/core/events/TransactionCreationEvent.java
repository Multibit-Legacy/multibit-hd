package org.multibit.hd.core.events;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;
import org.multibit.hd.core.dto.FiatPayment;

import java.util.Arrays;

/**
 * <p>Event to provide the following to UI event subscribers:</p>
 * <ul>
 * <li>Success or failure of creation of transaction sending bitcoins</li>
 * </ul>
 *
 * <p>Success guarantees that the wallet contains the underlying transaction.</p>
 */
public class TransactionCreationEvent implements CoreEvent {

  /**
   * The total amount paid in Bitcoin (including the client fee)
   */
  private final Coin amount;

  /**
   * The fiat equivalent to the Bitcoin payment
   */
  private final Optional<FiatPayment> fiatPayment;

  /**
   * The mining fee that was paid (if available) or Optional.absent() if not available
   */
  private final Optional<Coin> miningFeePaid;

  /**
   * The client fee that was paid, or Optional.absent() if no client fee was added to the transaction
   */
  private final Optional<Coin> clientFeePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean transactionCreationWasSuccessful;

  private final String transactionId;

  private final String transactionCreationFailureReasonKey;

  private final Optional<String> notes;

  private final Boolean sentByMe;

  public TransactionCreationEvent(
    String transactionId,
    Coin amount,
    Optional<FiatPayment> fiatPayment,
    Optional<Coin> miningFeePaid,
    Optional<Coin> clientFeePaid,
    Address destinationAddress,
    Address changeAddress,
    boolean transactionCreationWasSuccessful,
    String transactionCreationFailureReasonKey,
    String[] transactionCreationFailureReasonData,
    Optional<String> notes,
    Boolean sentByMe
  ) {

    this.transactionId = transactionId;
    this.amount = amount;
    this.fiatPayment = fiatPayment;
    this.miningFeePaid = miningFeePaid;
    this.clientFeePaid = clientFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.transactionCreationWasSuccessful = transactionCreationWasSuccessful;
    this.transactionCreationFailureReasonKey = transactionCreationFailureReasonKey;
    this.transactionCreationFailureReasonData = transactionCreationFailureReasonData;
    this.notes = notes;
    this.sentByMe = sentByMe;
  }

  private final String[] transactionCreationFailureReasonData;

  public Coin getAmount() {
    return amount;
  }

  public Optional<FiatPayment> getFiatPayment() {
    return fiatPayment;
  }

  public Optional<Coin> getMiningFeePaid() {
    return miningFeePaid;
  }

  public Optional<Coin> getClientFeePaid() {
     return clientFeePaid;
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

  public Boolean isSentByMe() {
    return sentByMe;
  }

  @Override
  public String toString() {
    return "TransactionCreationEvent{" +
      "amount=" + amount +
      ", fiatPayment=" + fiatPayment +
      ", miningFeePaid=" + miningFeePaid +
      ", clientFeePaid=" + clientFeePaid +
      ", destinationAddress='" + destinationAddress + '\'' +
      ", changeAddress='" + changeAddress + '\'' +
      ", transactionCreationWasSuccessful=" + transactionCreationWasSuccessful +
      ", transactionId='" + transactionId + '\'' +
      ", transactionCreationFailureReasonKey='" + transactionCreationFailureReasonKey + '\'' +
      ", transactionCreationFailureReasonData=" + Arrays.toString(transactionCreationFailureReasonData) +
      ", notes=" + notes +
      ", sentByMe=" +sentByMe +
      '}';
  }
}
