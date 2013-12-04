package org.multibit.hd.core.events;

import java.math.BigInteger;
import java.util.Arrays;

/**
 *  <p>Event to provide the following to UIEventbus subscribers
 *  <ul>
 *  <li>Success/ failure of send bitcoins</li>
 *  </ul>
 */
public class BitcoinSentEvent {

  private final BigInteger amount;

  private final BigInteger feePaid;

  private final String sendAddress;

  private final String changeAddress;

  private final boolean sendWasSuccessful;

  private final String sendFailureReasonKey;

  public BitcoinSentEvent(BigInteger amount, BigInteger feePaid, String sendAddress, String changeAddress, boolean sendWasSuccessful, String sendFailureReasonKey, String[] sendFailureReasonData) {
    this.amount = amount;
    this.feePaid = feePaid;
    this.sendAddress = sendAddress;
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

  public String getSendAddress() {
    return sendAddress;
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
            ", sendAddress='" + sendAddress + '\'' +
            ", changeAddress='" + changeAddress + '\'' +
            ", sendWasSuccessful=" + sendWasSuccessful +
            ", sendFailureReasonKey='" + sendFailureReasonKey + '\'' +
            ", sendFailureReasonData=" + Arrays.toString(sendFailureReasonData) +
            '}';
  }
}
