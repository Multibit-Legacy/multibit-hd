package org.multibit.hd.core.dto;

import org.joda.time.DateTime;

import java.math.BigInteger;

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

  public static final String SEPARATOR = ". ";

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
  public String getDescription() {
    // TODO localise
    StringBuffer buffer = new StringBuffer();
    buffer.append("by you");
    if (getLabel() != null && getLabel().length() >0) {
      buffer.append(SEPARATOR).append(getLabel());
    }
    if (getNote() != null && getNote().length() >0) {
      buffer.append(SEPARATOR).append(getNote());
    }
    buffer.append(SEPARATOR).append("To ").append(getAddress());
    return buffer.toString();
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
