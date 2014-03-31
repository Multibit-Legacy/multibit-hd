package org.multibit.hd.brit.dto;

import java.math.BigInteger;

/**
 * <p>DTO to provide the following to BRIT Payers :</p>
 * <ul>
 * <li>whether the Payer is using the list of hardwired BRIT payment addresses</li>
 * <li>the next Bitcoin address to send fees to</li>
 * <li>the count of sends in the Payers wallet at which to make the payment</li>
 * <li>the amount of bitcoin to send</li>
 * <li>the current fee per send</li>
 * <li>the current fee deficit (the amount allowing from the BRIT Payer to the Redeemer</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class FeeState {

  /**
   * True if the Payer is using the hardwired list of BRIT payment addresses
   * Indicates that the exchange with the BRIT Matcher failed previously.
   */
  private boolean usingHardwiredBRITAddresses;

  /**
   * The Bitcoin address to which the next Payer fee payment should be paid.
   */
  private String nextFeeAddress;

  /**
   * The number of sends in the Payer's wallet at which to send the fee.
   * For instance, if there are 5 sends currently in the Payer's wallet and this figure is 6 then
   * the fee should be paid on the next send.
   */
  private int nextSendCount;

  /**
   * The current fee per send transaction in satoshi
   */
  private BigInteger feeAmount;

  /**
   * The current fee owed
   * The running total of BRIT fees that are due to be paid since the last actual payment in satoshi
   */
  private BigInteger feeOwed;

}
