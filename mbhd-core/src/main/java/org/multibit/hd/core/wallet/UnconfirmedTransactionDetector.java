package org.multibit.hd.core.wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.wallet.WalletTransaction;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *  <p>Class to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Detects unconfirmed transactions in a window of time of interest</li>
 *  <li>Works out a date to replay from when a replay is appropriate</li>
 *  </ul>
 *  </p> 
 */
public class UnconfirmedTransactionDetector {

  private static final Logger log = LoggerFactory.getLogger(UnconfirmedTransactionDetector.class);

  /**
   * The youngest age of an unconfirmed transaction that we are interested in.
   * Unconfirmed transactions younger than this we will just wait to see if they confirm normally
   */
  public static final Hours LOWER_LIMIT_OF_TIME_WINDOW = Hours.FOUR;

  // Make replay happen really quickly for unconfirmed transactions
  // public static final Minutes LOWER_LIMIT_OF_TIME_WINDOW = Minutes.ONE;

  /**
   * The oldest age of an unconfirmed transaction that we are interested in.
   * Unconfirmed transactions older than this most likely have a very low fee
   */
  public static final Days UPPER_LIMIT_OF_TIME_WINDOW = Days.FOUR;

  /**
   * Utility - no public constructor
   */
  private UnconfirmedTransactionDetector() {
  }

  /**
   * Work out if there are any unconfirmed transaction in the wallet that are in the time period of
   * interest (between the LOWER and UPPER time window limits).
   *
   * If there is one or more transactions, the datetime of the oldest one is returned
   * (Replay from the date specified)
   *
   * Otherwise Optional.absent() is returned
   * (No replay is required i.e no unconfirmed transactions in window of interest)
   *
   * @param wallet             The wallet containing the transactions
   * @param compareDate        The date to compare transaction dates against (typically the time now)
   * @return The datetime of the oldest transaction in the time window of interest
   */
  public static Optional<DateTime> calculateReplayDate(Wallet wallet, DateTime compareDate) {
    Preconditions.checkNotNull(wallet);
    Preconditions.checkNotNull(compareDate);

    // Get all the unconfirmed transactions in the wallet with age (compared to the compareDate)
    // between the LOWER_LIMIT_OF_TIME_WINDOW and UPPER_LIMIT_OF_TIME_WINDOW
    Map<Sha256Hash, Transaction> pending = wallet.getTransactionPool(WalletTransaction.Pool.PENDING);

    Optional<DateTime> replayDate = Optional.absent();

    for (Transaction loopTransaction : pending.values()) {
      DateTime loopTransactionTime = new DateTime(loopTransaction.getUpdateTime());
      if (compareDate.minus(LOWER_LIMIT_OF_TIME_WINDOW).isAfter(loopTransactionTime) &&
              compareDate.isBefore(loopTransactionTime.plus(UPPER_LIMIT_OF_TIME_WINDOW))) {
        // Eligible transaction - keep track of the date
        if (replayDate.isPresent()) {
          if (replayDate.get().isAfter(loopTransactionTime)) {
            // This is an earlier unconfirmed transaction in the window of interest
            replayDate = Optional.of(loopTransactionTime);
          }
        } else {
          replayDate = Optional.of(loopTransactionTime);
        }
      }
    }

    log.debug("Calculated replay date for unconfirmed transaction is {}", replayDate);
    return replayDate;
  }
}
