package org.multibit.hd.brit.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Indication of a Matcher store failure</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class MatcherStoreException extends RuntimeException {

  public MatcherStoreException(String s) {
    super(s);
  }

  public MatcherStoreException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
