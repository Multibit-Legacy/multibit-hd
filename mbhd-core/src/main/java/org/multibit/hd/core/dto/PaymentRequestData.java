package org.multibit.hd.core.dto;

import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.Set;

/**
 *  <p>DTO to provide the following to WalletService:<br>
 *  <ul>
 *  <li>Additional payment request info</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class PaymentRequestData implements PaymentData {

  private String address;
  private String label;
  private BigInteger amountBTC;
  private FiatPayment amountFiat;
  private String note;
  private DateTime date;

  /**
   * The amount of bitcoin actually paid by transactions
   */
  private BigInteger paidAmountBTC;

  private Set<String> payingTransactionHashes;

  public static final String SEPARATOR = ". ";

  public PaymentRequestData() {
    paidAmountBTC = BigInteger.ZERO;
    payingTransactionHashes = Sets.newHashSet();
  }

  public Set<String> getPayingTransactionHashes() {
    return payingTransactionHashes;
  }

  public void setPayingTransactionHashes(Set<String> payingTransactionHashes) {
    this.payingTransactionHashes = payingTransactionHashes;
  }

  public BigInteger getPaidAmountBTC() {
    return paidAmountBTC;
  }

  public void setPaidAmountBTC(BigInteger paidAmountBTC) {
    this.paidAmountBTC = paidAmountBTC;
  }

  public String getAddress() {

    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public BigInteger getAmountBTC() {
    return amountBTC;
  }

  public void setAmountBTC(BigInteger amountBTC) {
    this.amountBTC = amountBTC;
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
  public int getDepth() {
    return  -1; // no depth for payment requests
  }

  @Override
  public String getDescription() {
    // TODO localise
    StringBuilder builder = new StringBuilder();
    boolean appendAddress = true;
    boolean appendSeparator = false;

    builder.append("You requested: ");
    if (getLabel() != null && getLabel().length() >0) {
      builder.append(getLabel());
      appendAddress = false;
      appendSeparator = true;
    }
    if (getNote() != null && getNote().length() >0) {
      if (appendSeparator){
        builder.append(SEPARATOR);
      }
      builder.append(getNote());
      appendAddress = false;
    }
    if (appendAddress) {
      builder.append("To ").append(getAddress());
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
    return PaymentType.REQUESTED;
  }

  @Override
  public RAGStatus getStatus() {
    return RAGStatus.PINK;
  }

  @Override
   public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;

     PaymentRequestData that = (PaymentRequestData) o;

     if (address != null ? !address.equals(that.address) : that.address != null) return false;
     if (amountBTC != null ? !amountBTC.equals(that.amountBTC) : that.amountBTC != null) return false;
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
     result = 31 * result + (amountBTC != null ? amountBTC.hashCode() : 0);
     result = 31 * result + (amountFiat != null ? amountFiat.hashCode() : 0);
     result = 31 * result + (note != null ? note.hashCode() : 0);
     result = 31 * result + (date != null ? date.hashCode() : 0);
     return result;
   }
}
