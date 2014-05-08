package org.multibit.hd.brit.dto;

import com.google.bitcoin.core.Address;

import java.math.BigInteger;

/**
 * <p>DTO to provide the following to BRIT Payers :</p>
 * <ul>
 * <li>Management of the client fee payment schedule</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class FeeState {

  /**
   * @param usingHardwiredBRITAddresses True if the Payer is using the list of hardwired BRIT payment addresses
   * @param nextFeeAddress              The next Bitcoin address to send fees to
   * @param currentNumberOfSends        The current count of send in the Payer's wallet
   * @param nextFeeSendCount            The count of sends in the Payer's wallet at which to make the payment
   * @param feePerSendSatoshi           The current fee per send (in Satoshis)
   * @param feeOwed                     The current deficit (the amount allowing from the BRIT Payer to the Redeemer)
   */
  public FeeState(boolean usingHardwiredBRITAddresses,
                  Address nextFeeAddress,
                  int currentNumberOfSends,
                  int nextFeeSendCount,
                  BigInteger feePerSendSatoshi,
                  BigInteger feeOwed) {

    this.usingHardwiredBRITAddresses = usingHardwiredBRITAddresses;
    this.nextFeeAddress = nextFeeAddress;
    this.currentNumberOfSends = currentNumberOfSends;
    this.nextFeeSendCount = nextFeeSendCount;
    this.feePerSendSatoshi = feePerSendSatoshi;
    this.feeOwed = feeOwed;

  }

  /**
   * True if the Payer is using the hardwired list of BRIT payment addresses
   * Indicates that the exchange with the BRIT Matcher failed previously.
   */
  private boolean usingHardwiredBRITAddresses;

  /**
   * The Bitcoin address to which the next Payer fee payment should be paid.
   */
  private Address nextFeeAddress;

  /**
   * The total number of sends in the Payer's wallet.
   */
  private int currentNumberOfSends;

  /**
   * The number of sends in the Payer's wallet at which to send the fee.
   * For instance, if there are 5 sends currently in the Payer's wallet and this figure is 6 then
   * the fee should be paid on the next send.
   */
  private int nextFeeSendCount;

  /**
   * The current fee per send transaction in satoshi
   */
  private BigInteger feePerSendSatoshi;

  /**
   * The current fee owed
   * The running total of BRIT fees that are due to be paid since the last actual payment in satoshi
   */
  private BigInteger feeOwed;

  public boolean isUsingHardwiredBRITAddresses() {
    return usingHardwiredBRITAddresses;
  }

  public Address getNextFeeAddress() {
    return nextFeeAddress;
  }

  public int getCurrentNumberOfSends() {
    return currentNumberOfSends;
  }

  public int getNextFeeSendCount() {
    return nextFeeSendCount;
  }

  public BigInteger getFeePerSendSatoshi() {
    return feePerSendSatoshi;
  }

  public BigInteger getFeeOwed() {
    return feeOwed;
  }
}
