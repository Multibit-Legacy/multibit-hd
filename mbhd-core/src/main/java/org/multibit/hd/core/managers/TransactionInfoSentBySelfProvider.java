package org.multibit.hd.core.managers;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import org.multibit.hd.brit.services.TransactionConfidenceSentBySelfProvider;
import org.multibit.hd.brit.services.TransactionSentBySelfProvider;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;

/**
 *  <p>Class to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Provides sentBySelf information using either Transacitoninfo or the transactionConfidence</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class TransactionInfoSentBySelfProvider implements TransactionSentBySelfProvider {

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
    // If the transactionConfidence tells us it was sent by self then we are done
    if (transactionConfidenceSentBySelfProvider.isSentBySelf(wallet, transaction)) {
      return true;
    }

    // Otherwise use sentBySelf on corresponding transactionInfo
    String transactionHashAsString = transaction.getHashAsString();
    WalletService walletService = CoreServices.getOrCreateWalletService(walletId);

    TransactionInfo transactionInfo = walletService.getTransactionInfoByHash(transactionHashAsString);
    return transactionInfo != null &&  transactionInfo.isSentBySelf();
  }
}
