package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Coin;
import org.joda.time.DateTime;

/**
 *  <p>Interface to provide the following to payments view:</p>
 *  <ul>
 *  <li>Unified common interface for TransactionData and PaymentRequestData </li>
 *  </ul>
 *  
 */
public interface PaymentData {

  public PaymentType getType();

  public PaymentStatus getStatus();

  public DateTime getDate();

  public Coin getAmountBTC();

  public FiatPayment getAmountFiat();

  public String getNote();

  public String getDescription();

  /**
   * @return true if transaction is a coinbase, false if not coinbase or not a transaction
   */
  public boolean isCoinBase();
}
