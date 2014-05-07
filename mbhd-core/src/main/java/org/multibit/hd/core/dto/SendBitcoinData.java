package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Address;
import com.google.common.base.Optional;
import org.multibit.hd.brit.dto.FeeState;

import java.math.BigInteger;

/**
 * <p>Data object to provide the following to Bitcoin network service:</p>
 * <ul>
 * <li>Contains send bitcoin data</li>
 * <li>Immutable</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitcoinData {

  private final Address destinationAddress;
  private final BigInteger amount;
  private final Address changeAddress;
  private final BigInteger feePerKB;
  private final CharSequence password;
  private final Optional<FeeState> feeStateOptional;

  /**
   * @param destinationAddress The destination address to send to
   * @param amount             The amount to send (in satoshis)
   * @param changeAddress      The change address
   * @param feePerKB           The fee per Kb (in satoshis)
   * @param password           The wallet password
   * @param feeStateOptional   The BRIT fee state
   */
  public SendBitcoinData(Address destinationAddress, BigInteger amount, Address changeAddress, BigInteger feePerKB, CharSequence password, Optional<FeeState> feeStateOptional) {
    this.destinationAddress = destinationAddress;
    this.amount = amount;
    this.changeAddress = changeAddress;
    this.feePerKB = feePerKB;
    this.password = password;
    this.feeStateOptional = feeStateOptional;
  }

  public Address getDestinationAddress() {
    return destinationAddress;
  }

  public BigInteger getAmount() {
    return amount;
  }

  public Address getChangeAddress() {
    return changeAddress;
  }

  public BigInteger getFeePerKB() {
    return feePerKB;
  }

  public CharSequence getPassword() {
    return password;
  }

  public Optional<FeeState> getFeeStateOptional() {
    return feeStateOptional;
  }

  @Override
  public String toString() {
    return "SendBitcoinData{" +
      "destinationAddress=" + destinationAddress +
      ", amount=" + amount +
      ", changeAddress=" + changeAddress +
      ", feePerKB=" + feePerKB +
      ", password=" + password +
      ", feeStateOptional=" + feeStateOptional +
      '}';
  }
}
