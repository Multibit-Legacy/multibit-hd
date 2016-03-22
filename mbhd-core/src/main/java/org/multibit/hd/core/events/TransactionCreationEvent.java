package org.multibit.hd.core.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean transactionCreationWasSuccessful;

  private final String transactionId;

  private final String transactionCreationFailureReasonKey;

  private final Optional<String> notes;

  private final Boolean sentByMe;

  /**
   * TODO Consider List<String> instead
   */
  private final String[] transactionCreationFailureReasonData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public TransactionCreationEvent(
    String transactionId,
    Coin amount,
    Optional<FiatPayment> fiatPayment,
    Optional<Coin> miningFeePaid,
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
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.transactionCreationWasSuccessful = transactionCreationWasSuccessful;
    this.transactionCreationFailureReasonKey = transactionCreationFailureReasonKey;
    this.transactionCreationFailureReasonData = transactionCreationFailureReasonData;
    this.notes = notes;
    this.sentByMe = sentByMe;
  }

  public Coin getAmount() {
    return amount;
  }

  public Optional<FiatPayment> getFiatPayment() {
    return fiatPayment;
  }

  public Optional<Coin> getMiningFeePaid() {
    return miningFeePaid;
  }

  public boolean isTransactionCreationWasSuccessful() {
    return transactionCreationWasSuccessful;
  }

  public String getTransactionCreationFailureReasonKey() {
    return transactionCreationFailureReasonKey;
  }

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
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
