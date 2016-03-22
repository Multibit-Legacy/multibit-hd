package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

/**
 * <p>Event to provide the following to UIEventbus subscribers:</p>
 * <ul>
 * <li>Indication that a bitcoin transaction is just about to be sent</li>
 * </ul>
 * <p>This is an infrequent event</p>
 *
 * @since 0.0.1
 */
public class BitcoinSendingEvent implements CoreEvent {

  /**
   * The total amount paid
   */
  private final Coin amount;

  private final Optional<Coin> miningFeePaid;

  private final Address destinationAddress;

  private final Address changeAddress;

  public BitcoinSendingEvent(
          Address destinationAddress,
          Coin amount,
          Address changeAddress,
          Optional<Coin> miningFeePaid
  ) {

    this.amount = amount;
    this.miningFeePaid = miningFeePaid;
    this.destinationAddress = destinationAddress;
    this.changeAddress = changeAddress;
  }

  public Coin getAmount() {
    return amount;
  }


  @Override
  public String toString() {
    return "BitcoinSendingEvent{" +
      "amount=" + amount +
      ", miningFeePaid=" + miningFeePaid +
      ", destinationAddress='" + destinationAddress + '\'' +
      ", changeAddress='" + changeAddress + '\'' +
      '}';
  }
}
