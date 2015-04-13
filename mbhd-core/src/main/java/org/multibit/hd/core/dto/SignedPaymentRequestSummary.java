package org.multibit.hd.core.dto;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;

/**
 * <p>Parameter object to provide the following to Payment Protocol service:</p>
 * <ul>
 * <li>Contains all data to build a signed PaymentRequest</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SignedPaymentRequestSummary {

  // Mandatory values
  private final Address paymentAddress;
  private final Coin amount;
  private final String memo;
  private final URL paymentUrl;
  private final KeyStore keyStore;
  private final String keyAlias;
  private final char[] keyStorePassword;
  private final byte[] merchantData;

  /**
   * @param paymentAddress   The address that will receive payment
   * @param amount           The amount required in payment
   * @param memo             The reason for this payment (seen by user so should be localised)
   * @param paymentUrl       The endpoint that will acknowledge the payment
   * @param merchantData     Extra data to assist payment process (reflected by client)
   * @param keyStore         The key store containing X509 certificates and the signing key
   * @param keyAlias         The key alias to identify the signing key
   * @param keyStorePassword The key store password
   */
  public SignedPaymentRequestSummary(
    Address paymentAddress,
    Coin amount,
    String memo,
    URL paymentUrl,
    byte[] merchantData,
    KeyStore keyStore,
    String keyAlias,
    char[] keyStorePassword) {

    // Payment request fields
    this.paymentAddress = paymentAddress;
    this.amount = amount;
    this.memo = memo;
    this.paymentUrl = paymentUrl;
    this.merchantData = Arrays.copyOf(merchantData, merchantData.length);

    // Signing fields
    this.keyStore = keyStore;
    this.keyAlias = keyAlias;
    this.keyStorePassword = Arrays.copyOf(keyStorePassword, keyStorePassword.length);

  }

  /**
   * @return the address that will receive payment
   */
  public Address getPaymentAddress() {
    return paymentAddress;
  }

  /**
   * @return The amount required in payment
   */
  public Coin getAmount() {
    return amount;
  }

  /**
   * @return The reason for this payment (seen by user so should be localised)
   */
  public String getMemo() {
    return memo;
  }

  /**
   * @return The endpoint that will acknowledge the payment
   */
  public URL getPaymentUrl() {
    return paymentUrl;
  }

  /**
   * @return Extra data to assist payment process (reflected by client)
   */
  public byte[] getMerchantData() {
    return Arrays.copyOf(merchantData, merchantData.length);
  }

  /**
   * @return The key store containing X509 certificates and the signing key
   */
  public KeyStore getKeyStore() {
    return keyStore;
  }

  /**
   * @return The key alias to identify the signing key
   */
  public String getKeyAlias() {
    return keyAlias;
  }

  /**
   * @return The key store password
   */
  public char[] getKeyStorePassword() {
    return Arrays.copyOf(keyStorePassword, keyStorePassword.length);
  }

}
