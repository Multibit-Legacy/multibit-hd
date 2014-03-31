package org.multibit.hd.brit.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Indication of an incorrect seed phrase</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SeedPhraseException extends RuntimeException {

  public SeedPhraseException(String s) {
    super(s);
  }

  public SeedPhraseException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
