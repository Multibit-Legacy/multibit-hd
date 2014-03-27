package org.multibit.hd.brit.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Provision of wallet version incorrect messages</li>
 * </ul>
 */
public class SeedPhraseException extends RuntimeException {

    public SeedPhraseException(String s) {
        super(s);
    }

    public SeedPhraseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
