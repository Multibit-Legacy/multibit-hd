package org.multibit.hd.core.exceptions;

/**
 * <p>Exception to provide the following to WalletManager:</p>
 * <ul>
 * <li>Provision of wallet saving messages</li>
 * </ul>

 * @since 0.3.6
 */
public class WalletSaveException extends RuntimeException {

    public WalletSaveException(String s) {
        super(s);
    }

    public WalletSaveException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
