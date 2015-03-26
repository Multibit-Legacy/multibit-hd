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
 */
public class Payments {

  private Collection<MBHDPaymentRequestData> MBHDPaymentRequestDataCollection;

  private Collection<TransactionInfo> transactionInfoCollection;

  private Collection<PaymentRequestData> paymentRequestDataCollection;

  public Payments() {
    this.MBHDPaymentRequestDataCollection = Lists.newArrayList();
    this.transactionInfoCollection = Lists.newArrayList();
  }

  /**
   * @return The MBHD PaymentRequest collection
   */
  public Collection<MBHDPaymentRequestData> getMBHDPaymentRequestDataCollection() {
    return MBHDPaymentRequestDataCollection;
  }

  public void setMBHDPaymentRequestDataCollection(Collection<MBHDPaymentRequestData> MBHDPaymentRequestDataCollection) {
    this.MBHDPaymentRequestDataCollection = MBHDPaymentRequestDataCollection;
  }

  /**
   * @return The BIP70 PaymentRequestData collection
   */
  public Collection<PaymentRequestData> getPaymentRequestDataCollection() {
    return paymentRequestDataCollection;
  }

  public void setPaymentRequestDataCollection(Collection<PaymentRequestData> paymentRequestDataCollection) {
    this.paymentRequestDataCollection = paymentRequestDataCollection;
  }

  /**
   * @return The TransactionInfo collection
   */
  public Collection<TransactionInfo> getTransactionInfoCollection() {
    return transactionInfoCollection;
  }

  public void setTransactionInfoCollection(Collection<TransactionInfo> transactionInfoCollection) {
    this.transactionInfoCollection = transactionInfoCollection;
  }
}
