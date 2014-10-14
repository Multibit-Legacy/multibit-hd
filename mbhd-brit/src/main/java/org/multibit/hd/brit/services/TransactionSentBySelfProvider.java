package org.multibit.hd.brit.services;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;

/**
 * <p>Interfeace to provide the following to FeeService:<br>
 * <ul>
 * <li>Given a transaction, return a boolean to indicate whether it has been sent by the active wallet (true) or not (false)</li>
 * </ul>
This is an interface because the Core implementation of it depends on various Core classes that BRIT does not know about
 * </p>
 *
 */
public interface TransactionSentBySelfProvider {

  /**
   * Return whether the transaction is sent by the active wallet (set up in implementation construction)
   * @param wallet the wallet the 'sent by self' check is to be performed against
   * @param transaction the transaction
   * @return true if transaction was sent by self, false if not a send or not sent by self
   */
  public boolean isSentBySelf(Wallet wallet, Transaction transaction);
}
