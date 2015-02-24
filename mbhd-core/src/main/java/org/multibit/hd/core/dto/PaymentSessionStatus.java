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
   * The payment request does not contain a PKI entry (suspicious)
   */
  OK_PKI_INVALID,

  /**
   * Payment request returned an error (malformed response etc)
   */
  ERROR,

  /**
   * Server could not be reached (SSL error or unknown host etc)
   */
  DOWN,

}
