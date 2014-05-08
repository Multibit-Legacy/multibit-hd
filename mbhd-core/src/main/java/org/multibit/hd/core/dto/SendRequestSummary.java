package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Address;
import com.google.common.base.Optional;
import org.multibit.hd.brit.dto.FeeState;
import org.spongycastle.crypto.params.KeyParameter;

import java.math.BigInteger;

/**
 * <p>Data object to provide the following to Bitcoin network service:</p>
 * <ul>
 * <li>Contains send bitcoin data</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendRequestSummary {

  private final Address destinationAddress;
  private final BigInteger amount;
  private final Address changeAddress;
  private final BigInteger feePerKB;
  private final CharSequence password;
  private final Optional<FeeState> feeState;

  // Mutable values
  private Optional<Address> feeAddress = Optional.absent();
  private Optional<KeyParameter> keyParameter = Optional.absent();

  /**
   * @param destinationAddress The destination address to send to
   * @param amount             The amount to send (in satoshis)
   * @param changeAddress      The change address
   * @param feePerKB           The fee per Kb (in satoshis)
   * @param password           The wallet password
   * @param feeState           The BRIT fee state
   */
  public SendRequestSummary(
    Address destinationAddress,
    BigInteger amount,
    Address changeAddress,
    BigInteger feePerKB,
    CharSequence password,
    Optional<FeeState> feeState
  ) {
    this.destinationAddress = destinationAddress;
    this.amount = amount;
    this.changeAddress = changeAddress;
    this.feePerKB = feePerKB;
    this.password = password;
    this.feeState = feeState;
  }

  /**
   * @return The destination address
   */
  public Address getDestinationAddress() {
    return destinationAddress;
  }

  /**
   * @return The amount in satoshis
   */
  public BigInteger getAmount() {
    return amount;
  }

  /**
   * @return The change address
   */
  public Address getChangeAddress() {
    return changeAddress;
  }

  /**
   * @return The fee to pay per Kb
   */
  public BigInteger getFeePerKB() {
    return feePerKB;
  }

  /**
   * @return The wallet password
   */
  public CharSequence getPassword() {
    return password;
  }

  /**
   * @return The fee state to determine if a client fee is required
   */
  public Optional<FeeState> getFeeState() {
    return feeState;
  }

  /**
   * @return The client fee address (from BRIT)
   */
  public Optional<Address> getFeeAddress() {
    return feeAddress;
  }

  public void setFeeAddress(Address feeAddress) {
    this.feeAddress = Optional.fromNullable(feeAddress);
  }

  /**
   * @return The wallet AES key
   */
  public Optional<KeyParameter> getKeyParameter() {
    return keyParameter;
  }

  public void setKeyParameter(KeyParameter keyParameter) {
    this.keyParameter = Optional.fromNullable(keyParameter);
  }

  @Override
  public String toString() {
    return "SendBitcoinData{" +
      "destinationAddress=" + destinationAddress +
      ", amount=" + amount +
      ", changeAddress=" + changeAddress +
      ", feePerKB=" + feePerKB +
      ", password=" + password +
      ", feeStateOptional=" + feeState +
      '}';
  }
}
