package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.bitcoinj.core.Coin;
import org.joda.time.DateTime;

/**
 * <p>Interface to provide the following to payments view:</p>
 * <ul>
 * <li>Unified common interface for TransactionData and PaymentRequestData </li>
 * </ul>
 *
 */
public interface PaymentData {

  /**
   * @return The payment type (e.g. "requested" etc)
   */
  public PaymentType getType();

  /**
   * @return The payment status
   */
  public PaymentStatus getStatus();

  /**
   * @return The creation date
   */
  public DateTime getDate();

  /**
   * @return The amount requested in Bitcoin
   */
  public Optional<Coin> getAmountCoin();

  /**
   * @return The amount request in fiat
   */
  public FiatPayment getAmountFiat();

  /**
   * @return A private note
   */
  public String getNote();

  /**
   * @return A localised description containing the label and note
   */
  public String getDescription();

  /**
   * @return True if the payment transaction is from the Bitcoin coinbase, false if not coinbase or not a transaction
   */
  public boolean isCoinBase();
}
