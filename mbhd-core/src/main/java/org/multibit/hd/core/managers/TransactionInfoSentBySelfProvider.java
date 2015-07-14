package org.multibit.hd.core.managers;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.multibit.hd.brit.core.services.TransactionConfidenceSentBySelfProvider;
import org.multibit.hd.brit.core.services.TransactionSentBySelfProvider;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Class to provide the following to WalletManager:<br>
 * <ul>
 * <li>Provides sentBySelf information using either Transacitoninfo or the transactionConfidence</li>
 * </ul>
 * Example:<br>
 * <pre>
 * </pre>
 * </p>
 *
 */
public class TransactionInfoSentBySelfProvider implements TransactionSentBySelfProvider {

  private static final Logger log = LoggerFactory.getLogger(TransactionInfoSentBySelfProvider.class);

  private WalletId walletId;

  /**
   * Use a standard TransactionConfidenceSentBySelfProvider to catch the case where the transaction
   * confidence is available but the transaction info was written prior to the extra sentBySelf boolean
   */
  TransactionSentBySelfProvider transactionConfidenceSentBySelfProvider;

  public TransactionInfoSentBySelfProvider(WalletId walletId) {
      this.walletId = walletId;
    transactionConfidenceSentBySelfProvider = new TransactionConfidenceSentBySelfProvider();
  }

  @Override
  public boolean isSentBySelf(Wallet wallet, Transaction transaction) {
    String transactionHashAsString = transaction.getHashAsString();

    // If the transactionConfidence tells us it was sent by self then we are done
    if (transactionConfidenceSentBySelfProvider.isSentBySelf(wallet, transaction)) {
      log.trace("The transaction confidence indicates this tx was sent by self for tx {}", transactionHashAsString);
      return true;
    }

    // Otherwise use sentBySelf on corresponding transactionInfo
    WalletService walletService = CoreServices.getOrCreateWalletService(walletId);

    TransactionInfo transactionInfo = walletService.getTransactionInfoByHash(transactionHashAsString);
    if (transactionInfo == null) {
      log.debug("Could not find a transactionInfo for the transaction {}", transactionHashAsString);
      return false;
    } else {
      log.trace("Found a transactionInfo for the transaction {}, sentBySelf was {}", transactionHashAsString, transactionInfo.isSentBySelf());
      return transactionInfo.isSentBySelf();
    }
  }
}
