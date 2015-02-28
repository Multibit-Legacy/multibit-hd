package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to payment protocol API:</p>
 * <ul>
 * <li>Payment session status</li>
 * </ul>
 *
 * @since 0.0.7
 */
public enum PaymentSessionStatus {

  /**
   * Successful communication with payment request server resulting
   * in a trusted Payment Request
   */
  TRUSTED,

  /**
   * Successful communication with payment request server resulting
   * in an  untrusted Payment Request
   *
   * The payment request may not contain a PKI entry or it may simply
   * not have a certificate that is in the trust store
   */
  UNTRUSTED,

  /**
   * Payment request returned an error (malformed response etc)
   */
  ERROR,

  /**
   * Server could not be reached (SSL error or unknown host etc)
   */
  DOWN,

}
