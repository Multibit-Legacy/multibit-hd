package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Coin;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import java.util.Set;

/**
 *  <p>DTO to provide the following to WalletService:</p>
 *  <ul>
 *  <li>Additional payment request info</li>
 *  </ul>
 *  
 */
public class PaymentRequestData implements PaymentData {

  private String address;
  private String label;
  private Coin amountCoin;
  private FiatPayment amountFiat;
  private String note;
  private DateTime date;

  /**
   * The amount of bitcoin actually paid by transactions
   */
  private Coin paidAmountCoin;

  private final Set<String> payingTransactionHashes;

  public static final String SEPARATOR = ". ";

  public PaymentRequestData() {
    paidAmountCoin = Coin.ZERO;
    payingTransactionHashes = Sets.newHashSet();
  }

  public Set<String> getPayingTransactionHashes() {
    return payingTransactionHashes;
  }

  /**
   * @return The amount paid so far in coins
   */
  public Coin getPaidAmountCoin() {
    return paidAmountCoin;
  }

  public void setPaidAmountCoin(Coin paidAmountCoin) {
    this.paidAmountCoin = paidAmountCoin;
  }

  /**
   * @return The Bitcoin address
   */
  public String getAddress() {

    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return The transaction label (often for a QR code)
   */
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public Coin getAmountCoin() {
    return amountCoin;
  }

  public void setAmountCoin(Coin amountBTC) {
    this.amountCoin = amountBTC;
  }

  @Override
  public FiatPayment getAmountFiat() {
    return amountFiat;
  }

  public void setAmountFiat(FiatPayment amountFiat) {
    this.amountFiat = amountFiat;
  }

  @Override
  public String getNote() {
    return note;
  }

  @Override
  public boolean isCoinBase() {
    return false;
  }

  @Override
  public String getDescription() {

    StringBuilder builder = new StringBuilder();
    boolean appendAddress = true;
    boolean appendSeparator = false;

    if (!Strings.isNullOrEmpty(getLabel())) {
      builder.append(getLabel());
      appendAddress = false;
      appendSeparator = true;
    }

    if (!Strings.isNullOrEmpty(getNote())) {
      if (appendSeparator) {
        builder.append(SEPARATOR);
      }
      builder.append(getNote());
      appendAddress = false;
    }

    if (appendAddress) {
      builder
        .append(getAddress());
    }

    return builder.toString();
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  @Override
  public PaymentType getType() {
    PaymentType type = PaymentType.REQUESTED;
    // Work out if it is requested, partly paid or fully paid
    if (paidAmountCoin != null && amountCoin != null) {
      if (paidAmountCoin.compareTo(Coin.ZERO) > 0) {
        // bitcoin has been paid to this payment request
        if (paidAmountCoin.compareTo(amountCoin) >= 0) {
          // fully paid
          type = PaymentType.PAID;
        } else {
          // partly paid
          type = PaymentType.PART_PAID;
        }
      }
    }
    return type;
  }

  @Override
  public PaymentStatus getStatus() {

    final PaymentStatus paymentStatus;

    // Work out if it is requested, part paid or fully paid
    if (paidAmountCoin != null && amountCoin != null) {
      if (paidAmountCoin.compareTo(Coin.ZERO) > 0) {
        // Bitcoin has been paid to this payment request
        if (paidAmountCoin.compareTo(amountCoin) >= 0) {
          // Fully paid (or overpaid)
          return new PaymentStatus(RAGStatus.GREEN, CoreMessageKey.PAYMENT_PAID);
        } else {
          // Part paid
          return new PaymentStatus(RAGStatus.PINK, CoreMessageKey.PAYMENT_PART_PAID);
        }
      }
    }

    // Must be payment requested to be here
    paymentStatus = new PaymentStatus(RAGStatus.PINK, CoreMessageKey.PAYMENT_REQUESTED);

    return paymentStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PaymentRequestData that = (PaymentRequestData) o;

    if (address != null ? !address.equals(that.address) : that.address != null) return false;
    if (amountCoin != null ? !amountCoin.equals(that.amountCoin) : that.amountCoin != null) return false;
    if (date != null ? !date.equals(that.date) : that.date != null) return false;
    if (amountFiat != null ? !amountFiat.equals(that.amountFiat) : that.amountFiat != null) return false;
    if (label != null ? !label.equals(that.label) : that.label != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = address != null ? address.hashCode() : 0;
    result = 31 * result + (label != null ? label.hashCode() : 0);
    result = 31 * result + (amountCoin != null ? amountCoin.hashCode() : 0);
    result = 31 * result + (amountFiat != null ? amountFiat.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (date != null ? date.hashCode() : 0);
    return result;
  }
}
