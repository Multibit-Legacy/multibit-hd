package org.multibit.hd.core.managers;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import org.multibit.hd.brit.services.TransactionSentBySelfProvider;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;

/**
 *  <p>Class to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Provides sentBySelf information using Transacitoninfo</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class TransactionInfoSentBySelfProvider implements TransactionSentBySelfProvider {

  private WalletId walletId;

  public TransactionInfoSentBySelfProvider(WalletId walletId) {
      this.walletId = walletId;
  }

  @Override
  public boolean isSentBySelf(Wallet wallet, Transaction transaction) {
    // Use sentBySelf on corresponding transactionInfo
    String transactionHashAsString = transaction.getHashAsString();
    WalletService walletService = CoreServices.getOrCreateWalletService(walletId);

    TransactionInfo transactionInfo = walletService.getTransactionInfoByHash(transactionHashAsString);
    return transactionInfo != null &&  transactionInfo.isSentBySelf();
  }
}
