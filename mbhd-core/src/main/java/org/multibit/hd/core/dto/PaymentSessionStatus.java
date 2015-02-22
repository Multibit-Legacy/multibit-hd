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
   * Successful communication with payment request server (no user action required)
   */
  OK,

  /**
   * Payment request returned an error (malformed response etc)
   */
  ERROR,

  /**
   * Server could not be reached (SSL error or unknown host etc)
   */
  DOWN,

  /**
   * The payment request does not contain a PKI entry (suspicious)
   */
  PKI_INVALID,

  /**
   * The payment request contains an untrusted CA (user must import)
   */
  PKI_UNTRUSTED_CA

}
