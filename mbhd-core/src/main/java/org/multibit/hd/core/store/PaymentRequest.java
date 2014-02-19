package org.multibit.hd.core.store;

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
public class PaymentRequest {

  private String address;
  private String label;
  private BigInteger amount_btc;
  private FiatPayment fiatPayment;
  private String note;
  private DateTime date;

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

  public BigInteger getAmount_btc() {
    return amount_btc;
  }

  public void setAmount_btc(BigInteger amount_btc) {
    this.amount_btc = amount_btc;
  }

  public FiatPayment getFiatPayment() {
    return fiatPayment;
  }

  public void setFiatPayment(FiatPayment fiatPayment) {
    this.fiatPayment = fiatPayment;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  @Override
   public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;

     PaymentRequest that = (PaymentRequest) o;

     if (address != null ? !address.equals(that.address) : that.address != null) return false;
     if (amount_btc != null ? !amount_btc.equals(that.amount_btc) : that.amount_btc != null) return false;
     if (date != null ? !date.equals(that.date) : that.date != null) return false;
     if (fiatPayment != null ? !fiatPayment.equals(that.fiatPayment) : that.fiatPayment != null) return false;
     if (label != null ? !label.equals(that.label) : that.label != null) return false;
     if (note != null ? !note.equals(that.note) : that.note != null) return false;

     return true;
   }

   @Override
   public int hashCode() {
     int result = address != null ? address.hashCode() : 0;
     result = 31 * result + (label != null ? label.hashCode() : 0);
     result = 31 * result + (amount_btc != null ? amount_btc.hashCode() : 0);
     result = 31 * result + (fiatPayment != null ? fiatPayment.hashCode() : 0);
     result = 31 * result + (note != null ? note.hashCode() : 0);
     result = 31 * result + (date != null ? date.hashCode() : 0);
     return result;
   }
}
