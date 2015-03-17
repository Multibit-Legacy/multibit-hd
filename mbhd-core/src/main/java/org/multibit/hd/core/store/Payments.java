package org.multibit.hd.core.store;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.dto.PaymentRequestData;

import java.util.Collection;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>Top level encapsulating class around payment requests and transaction info</li>
 * </p>
 *
 */

public class Payments {

  private Collection<MBHDPaymentRequestData> MBHDPaymentRequestDatas;

  private Collection<TransactionInfo> transactionInfos;

  private Collection<PaymentRequestData> paymentRequestDatas;

  public Payments() {
    this.MBHDPaymentRequestDatas = Lists.newArrayList();
    this.transactionInfos = Lists.newArrayList();
  }

  public Collection<MBHDPaymentRequestData> getMBHDPaymentRequestDatas() {
    return MBHDPaymentRequestDatas;
  }

  public void setMBHDPaymentRequestDatas(Collection<MBHDPaymentRequestData> MBHDPaymentRequestDatas) {
    this.MBHDPaymentRequestDatas = MBHDPaymentRequestDatas;
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
