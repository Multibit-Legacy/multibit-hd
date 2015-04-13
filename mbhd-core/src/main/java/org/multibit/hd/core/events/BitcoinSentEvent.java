package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.multibit.hd.core.dto.CoreMessageKey;

import java.util.Arrays;

/**
 * <p>Event to provide the following to UIEventbus subscribers:</p>
 * <ul>
 * <li>Success/ failure of send bitcoins</li>
 * </ul>
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 */
public class BitcoinSentEvent implements CoreEvent {

  private final Optional<Transaction> transaction;

  private final Coin amount;

  private final Optional<Coin> miningFeePaid;

  private final Optional<Coin> clientFeePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean sendWasSuccessful;

  private final CoreMessageKey sendFailureReason;

  /**
   * TODO Consider a List<String> instead
   */
  private final String[] sendFailureReasonData;

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP2"})
  public BitcoinSentEvent(
    Optional<Transaction> transaction,
    Address destinationAddress,
    Coin amount,
    Address changeAddress,
    Optional<Coin> miningFeePaid,
    Optional<Coin> clientFeePaid,
    boolean sendWasSuccessful,
    CoreMessageKey sendFailureReason,
    String[] sendFailureReasonData
  ) {

    this.transaction = transaction;
    this.amount = amount;
    this.miningFeePaid = miningFeePaid;
    this.clientFeePaid = clientFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.sendWasSuccessful = sendWasSuccessful;
    this.sendFailureReason = sendFailureReason;
    this.sendFailureReasonData = sendFailureReasonData;

  }

  /**
   * @return The transaction associated with this event
   */
  public Optional<Transaction> getTransaction() {
    return transaction;
  }

  /**
   * @return The payment amount without fees
   */
  public Coin getAmount() {
    return amount;
  }

  /**
   * @return The mining fee paid
   */
  public Optional<Coin> getMiningFeePaid() {
    return miningFeePaid;
  }

  /**
   * @return The client fee paid
   */
  public Optional<Coin> getClientFeePaid() {
    return clientFeePaid;
  }

  /**
   * @return The destination address
   */
  public Address getDestinationAddress() {
    return destinationAddress;
  }

  /**
   * @return The change address (can also be used for a refund)
   */
  public Address getChangeAddress() {
    return changeAddress;
  }

  /**
   * @return True if the send was successful
   */
  public boolean isSendWasSuccessful() {
    return sendWasSuccessful;
  }

  /**
   * @return The reason for the failure
   */
  public CoreMessageKey getSendFailureReason() {
    return sendFailureReason;
  }

  // The fix for this is more complex than leaving it in place
  @SuppressFBWarnings({"EI_EXPOSE_REP"})
  public String[] getSendFailureReasonData() {
    return sendFailureReasonData;
  }

  @Override
  public String toString() {
    return "BitcoinSentEvent{" +
      "transaction=" + transaction +
      ", amount=" + amount +
      ", destinationAddress=" + destinationAddress +
      ", changeAddress=" + changeAddress +
      ", miningFeePaid=" + miningFeePaid +
      ", clientFeePaid=" + clientFeePaid +
      ", sendWasSuccessful=" + sendWasSuccessful +
      ", sendFailureReason=" + sendFailureReason +
      ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
      '}';
  }
}
