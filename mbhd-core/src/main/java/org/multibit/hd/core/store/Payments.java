package org.multibit.hd.core.store;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.PaymentRequestData;

import java.util.Collection;

/**
 *  <p>DTO to provide the following to WalletService:</p>
 *  <ul>
 *  <li>Top level encapsulating class around payment requests and transaction info</li>
 *  </p>
 *  
 */

public class Payments {

  private Collection<PaymentRequestData> paymentRequestDatas;

  private Collection<TransactionInfo> transactionInfos;

  public Payments() {
    this.paymentRequestDatas = Lists.newArrayList();
    this.transactionInfos = Lists.newArrayList();
  }

  public Collection<PaymentRequestData> getPaymentRequestDatas() {
    return paymentRequestDatas;
  }

  public void setPaymentRequestDatas(Collection<PaymentRequestData> paymentRequestDatas) {
    this.paymentRequestDatas = paymentRequestDatas;
  }

  public Collection<TransactionInfo> getTransactionInfos() {
    return transactionInfos;
  }

  public void setTransactionInfos(Collection<TransactionInfo> transactionInfos) {
    this.transactionInfos = transactionInfos;
  }
}
