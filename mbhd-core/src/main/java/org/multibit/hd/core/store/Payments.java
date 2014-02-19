package org.multibit.hd.core.store;

import java.util.Collection;

/**
 *  <p>DTO to provide the following to WalletService:<br>
 *  <ul>
 *  <li>Top level encapsulating class around payment requests and transaction info</li>
 *  </p>
 *  
 */

public class Payments {
  private int lastIndexUsed;

  private Collection<PaymentRequest> paymentRequests;

  private Collection<TransactionInfo> transactionInfos;

  public Payments(int lastIndexUsed) {
    this.lastIndexUsed = lastIndexUsed;
  }

  public int getLastIndexUsed() {
    return lastIndexUsed;
  }

  public void setLastIndexUsed(int lastIndexUsed) {
    this.lastIndexUsed = lastIndexUsed;
  }

  public Collection<PaymentRequest> getPaymentRequests() {
    return paymentRequests;
  }

  public void setPaymentRequests(Collection<PaymentRequest> paymentRequests) {
    this.paymentRequests = paymentRequests;
  }

  public Collection<TransactionInfo> getTransactionInfos() {
    return transactionInfos;
  }

  public void setTransactionInfos(Collection<TransactionInfo> transactionInfos) {
    this.transactionInfos = transactionInfos;
  }
}
