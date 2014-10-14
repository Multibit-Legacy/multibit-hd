package org.multibit.hd.core.store;

import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;
import org.multibit.hd.core.dto.FiatPayment;

/**
 *  <p>DTO to provide the following to WalletService:</p>
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
   * Boolean indicating the transaction was sent by this wallet
   * true = sent by this wallet (hence will incur BRIT fee)
   * false = not a send or sent by another wallet
   */
  private boolean sentBySelf = false;

  /**
   * On a send, the miner's fee added in satoshi, otherwise Optional.absent()
   */
  private Optional<Coin> minerFee = Optional.absent();

  /**
   * On a send, the MultiBit/ client fee added in satoshi, otherwise Optional.absent()
   */
  private Optional<Coin> clientFee = Optional.absent();

  /**
   * The transaction hash as a String (commonly referred to as transaction id but don't forget about malleability!)
   *
   * @return String transactionHash aka transaction id
   */
  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * @return The amount in fiat
   */
  public FiatPayment getAmountFiat() {
    return amountFiat;
  }

  public void setAmountFiat(FiatPayment amountFiat) {
    this.amountFiat = amountFiat;
  }

  /**
   * @return An associated note
   */
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  /**
   * @return The miner's fee if present
   */
  public Optional<Coin> getMinerFee() {
    return minerFee;
  }

  public void setMinerFee(Optional<Coin> minerFee) {
    this.minerFee = minerFee;
  }

  /**
   *
   * @return The client fee if present
   */
  public Optional<Coin> getClientFee() {
    return clientFee;
  }

  public void setClientFee(Optional<Coin> clientFee) {
    this.clientFee = clientFee;
  }

  public boolean isSentBySelf() {
    return sentBySelf;
  }

  public void setSentBySelf(boolean sentBySelf) {
    this.sentBySelf = sentBySelf;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransactionInfo that = (TransactionInfo) o;

    if (sentBySelf != that.sentBySelf) return false;
    if (amountFiat != null ? !amountFiat.equals(that.amountFiat) : that.amountFiat != null) return false;
    if (clientFee != null ? !clientFee.equals(that.clientFee) : that.clientFee != null) return false;
    if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
    if (minerFee != null ? !minerFee.equals(that.minerFee) : that.minerFee != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = hash != null ? hash.hashCode() : 0;
    result = 31 * result + (amountFiat != null ? amountFiat.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (sentBySelf ? 1 : 0);
    result = 31 * result + (minerFee != null ? minerFee.hashCode() : 0);
    result = 31 * result + (clientFee != null ? clientFee.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TransactionInfo{" +
      "hash='" + hash + '\'' +
      ", amountFiat=" + amountFiat +
      ", note='" + note + '\'' +
      ", minerFee=" + minerFee +
      ", clientFee=" + clientFee +
      ", sentBySelf=" + sentBySelf +
      '}';
  }
}
