package org.multibit.hd.brit.services;

import org.bitcoinj.core.*;

/**
 * <p>Class to provide the following to FeeService:<br>
 * <ul>
 * <li>Provide 'isSentByMe' information using the transaction confidence</li>
 * </ul>
 * Example:<br>
 * <pre>
 * </pre>
 * </p>
 *
 */
public class TransactionConfidenceSentBySelfProvider implements TransactionSentBySelfProvider {
  @Override
  public boolean isSentBySelf(Wallet wallet, Transaction transaction) {
    return (transaction.getValueSentFromMe(wallet).compareTo(Coin.ZERO) > 0) && (
            transaction.getConfidence() != null && TransactionConfidence.Source.SELF.equals(transaction.getConfidence().getSource())
    );
  }
}
