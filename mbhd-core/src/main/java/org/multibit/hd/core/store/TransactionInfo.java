package org.multibit.hd.core.store;

import org.multibit.hd.core.dto.FiatPayment;

import java.util.Collection;

/**
 *  <p>DTOto provide the following to WalletService:<br>
 *  <ul>
 *  <li>Additional information related to a transaction</li>
 *  </ul>
 *  </p>
 *  
 */
public class TransactionInfo {
  private String hash;
  private Collection<String> requestAddresses;
  private FiatPayment amountFiat;
  private String note;

  /**
   * The transaction hash as a String (commonly referred to as transaction id but don't forget about malleability!)
   * @return
   */
  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public Collection<String> getRequestAddresses() {
    return requestAddresses;
  }

  public void setRequestAddresses(Collection<String> requestAddresses) {
    this.requestAddresses = requestAddresses;
  }

  public FiatPayment getAmountFiat() {
    return amountFiat;
  }

  public void setAmountFiat(FiatPayment amountFiat) {
    this.amountFiat = amountFiat;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransactionInfo that = (TransactionInfo) o;

    if (amountFiat != null ? !amountFiat.equals(that.amountFiat) : that.amountFiat != null) return false;
    if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;
    if (requestAddresses != null ? !requestAddresses.equals(that.requestAddresses) : that.requestAddresses != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = hash.hashCode();
    result = 31 * result + (requestAddresses != null ? requestAddresses.hashCode() : 0);
    result = 31 * result + (amountFiat != null ? amountFiat.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    return result;
  }
}
