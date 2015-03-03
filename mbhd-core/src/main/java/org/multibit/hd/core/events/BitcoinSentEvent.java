package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
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

  /**
   * The total amount paid (including the client fee)
   */
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
    Address destinationAddress,
    Coin amount,
    Address changeAddress,
    Optional<Coin> miningFeePaid,
    Optional<Coin> clientFeePaid,
    boolean sendWasSuccessful,
    CoreMessageKey sendFailureReason,
    String[] sendFailureReasonData
  ) {

    this.amount = amount;
    this.miningFeePaid = miningFeePaid;
    this.clientFeePaid = clientFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.sendWasSuccessful = sendWasSuccessful;
    this.sendFailureReason = sendFailureReason;
    this.sendFailureReasonData = sendFailureReasonData;

  }

  public Coin getAmount() {
    return amount;
  }

  public Optional<Coin> getMiningFeePaid() {
    return miningFeePaid;
  }

  public Optional<Coin> getClientFeePaid() {
    return clientFeePaid;
  }

  public Address getDestinationAddress() {
    return destinationAddress;
  }

  public boolean isSendWasSuccessful() {
    return sendWasSuccessful;
  }

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
      "amount=" + amount +
      ", miningFeePaid=" + miningFeePaid +
      ", clientFeePaid=" + clientFeePaid +
      ", destinationAddress='" + destinationAddress + '\'' +
      ", changeAddress='" + changeAddress + '\'' +
      ", sendWasSuccessful=" + sendWasSuccessful +
      ", sendFailureReasonKey='" + sendFailureReason + '\'' +
      ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
      '}';
  }
}
