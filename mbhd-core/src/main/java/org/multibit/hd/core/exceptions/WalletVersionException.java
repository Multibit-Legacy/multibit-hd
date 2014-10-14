package org.multibit.hd.core.exceptions;

/**
 * <p>Exception to provide the following:</p>
 * <ul>
 * <li>Provision of wallet version incorrect messages</li>
 * </ul>
 */
public class WalletVersionException extends RuntimeException {

    public WalletVersionException(String s) {
        super(s);
    }

    public WalletVersionException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
