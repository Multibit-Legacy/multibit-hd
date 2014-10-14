package org.multibit.hd.core.events;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;

import java.util.Arrays;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>Success/ failure of send bitcoins</li>
 *  </ul>
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

  private final String sendFailureReasonKey;

  public BitcoinSentEvent(
    Address destinationAddress,
    Coin amount,
    Address changeAddress,
    Optional<Coin> miningFeePaid,
    Optional<Coin> clientFeePaid,
    boolean sendWasSuccessful,
    String sendFailureReasonKey,
    String[] sendFailureReasonData
  ) {

    this.amount = amount;
    this.miningFeePaid = miningFeePaid;
    this.clientFeePaid = clientFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.sendWasSuccessful = sendWasSuccessful;
    this.sendFailureReasonKey = sendFailureReasonKey;
    this.sendFailureReasonData = sendFailureReasonData;

  }

  private final String[] sendFailureReasonData;

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

  public String getSendFailureReasonKey() {
    return sendFailureReasonKey;
  }

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
            ", sendFailureReasonKey='" + sendFailureReasonKey + '\'' +
            ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
            '}';
  }
}
