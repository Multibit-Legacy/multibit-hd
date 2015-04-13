package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to exchange API:</p>
 * <ul>
 * <li>Exchange server status</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum ExchangeStatus {

  /**
   * Successful communication with exchange
   */
  OK,

  /**
   * Exchange returned an error
   */
  ERROR,

  /**
   * Exchange could not be reached (SSL error or unknown host etc)
   */
  DOWN

}
