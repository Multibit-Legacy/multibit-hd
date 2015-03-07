package org.multibit.hd.core.events;

import com.google.common.base.Preconditions;
import org.bitcoinj.core.Transaction;

/**
 * <p>Event to provide the following to Core event subscribers</p>
 * <ul>
 * <li>Update of the progress of a broadcast</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BitcoinSendProgressEvent implements CoreEvent {

  private final String transactionId;

  private final double progress; // The progress of the broadcast - a long between 0 (no peers have seen it) and 1 (broadcast complete)


  /**
   * @param transaction The Bitcoinj transaction providing the information
   * @param progress    The broadcast progress of the transaction
   */
  public BitcoinSendProgressEvent(Transaction transaction, double progress) {

    Preconditions.checkNotNull(transaction);

    transactionId = transaction.getHashAsString();
    this.progress = progress;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public double getProgress() {
    return progress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BitcoinSendProgressEvent that = (BitcoinSendProgressEvent) o;

    if (Double.compare(that.progress, progress) != 0) return false;
    if (!transactionId.equals(that.transactionId)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = transactionId.hashCode();
    temp = Double.doubleToLongBits(progress);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "BitcoinSendProgressEvent{" +
            "transactionId='" + transactionId + '\'' +
            ", progress=" + progress +
            '}';
  }
}
