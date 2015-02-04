package org.multibit.hd.core.dto;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
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
