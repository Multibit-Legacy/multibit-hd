package org.multibit.hd.core.events;

import com.google.bitcoin.core.Address;

import java.math.BigInteger;
import java.util.Arrays;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>Success/ failure of send bitcoins</li>
 *  </ul>
 */
public class BitcoinSentEvent implements CoreEvent {

  private final BigInteger amount;

  private final BigInteger feePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  private final boolean sendWasSuccessful;

  private final String sendFailureReasonKey;

  public BitcoinSentEvent(
    Address destinationAddress,
    BigInteger amount,
    Address changeAddress,
    BigInteger feePaid,
    boolean sendWasSuccessful,
    String sendFailureReasonKey,
    String[] sendFailureReasonData
  ) {

    this.amount = amount;
    this.feePaid = feePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
    this.sendWasSuccessful = sendWasSuccessful;
    this.sendFailureReasonKey = sendFailureReasonKey;
    this.sendFailureReasonData = sendFailureReasonData;

  }

  private final String[] sendFailureReasonData;

  public BigInteger getAmount() {
    return amount;
  }

  public BigInteger getFeePaid() {
    return feePaid;
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
            ", feePaid=" + feePaid +
            ", destinationAddress='" + destinationAddress + '\'' +
            ", changeAddress='" + changeAddress + '\'' +
            ", sendWasSuccessful=" + sendWasSuccessful +
            ", sendFailureReasonKey='" + sendFailureReasonKey + '\'' +
            ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
            '}';
  }
}
