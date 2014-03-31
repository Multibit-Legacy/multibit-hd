package org.multibit.hd.brit.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Indication of a Payer request failure</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PayerRequestException extends RuntimeException {

  public PayerRequestException(String s) {
    super(s);
  }

  public PayerRequestException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
