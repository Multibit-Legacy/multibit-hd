package org.multibit.hd.core.store;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.FiatPayment;

import java.math.BigInteger;

/**
 *  <p>DTOto provide the following to WalletService:</p>
 *  <ul>
 *  <li>Additional information related to a transaction that is not stored in the bitcoinj transaction</li>
 *  </ul>
 *  </p>
 *  
 */
public class TransactionInfo {

  private String hash;
  private FiatPayment amountFiat;
  private String note = "";

  /**
   * On a send, the miner's fee added in satoshi, otherwise Optional.absent()
   */
  private Optional<BigInteger> minerFee;

  /**
   * On a send, the MultiBit/ client fee added in satoshi, otherwise Optional.absent()
   */
  private Optional<BigInteger> clientFee;

  /**
   * The transaction hash as a String (commonly referred to as transaction id but don't forget about malleability!)
   * @return String transactionHash aka transaction id
   */
  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
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

  public Optional<BigInteger> getMinerFee() {
    return minerFee;
  }

  public void setMinerFee(Optional<BigInteger> minerFee) {
    this.minerFee = minerFee;
  }

  public Optional<BigInteger> getClientFee() {
    return clientFee;
  }

  public void setClientFee(Optional<BigInteger> clientFee) {
    this.clientFee = clientFee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransactionInfo that = (TransactionInfo) o;

    if (amountFiat != null ? !amountFiat.equals(that.amountFiat) : that.amountFiat != null) return false;
    if (clientFee != null ? !clientFee.equals(that.clientFee) : that.clientFee != null) return false;
    if (!hash.equals(that.hash)) return false;
    if (minerFee != null ? !minerFee.equals(that.minerFee) : that.minerFee != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = hash.hashCode();
    result = 31 * result + (amountFiat != null ? amountFiat.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (minerFee != null ? minerFee.hashCode() : 0);
    result = 31 * result + (clientFee != null ? clientFee.hashCode() : 0);
    return result;
  }
}
