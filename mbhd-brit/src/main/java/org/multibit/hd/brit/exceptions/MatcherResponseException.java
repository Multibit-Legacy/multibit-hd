package org.multibit.hd.brit.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Indication of a Payer request failure</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class MatcherResponseException extends RuntimeException {

  public MatcherResponseException(String s) {
    super(s);
  }

  public MatcherResponseException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
