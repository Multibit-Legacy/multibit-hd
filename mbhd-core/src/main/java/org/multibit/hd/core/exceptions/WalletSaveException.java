package org.multibit.hd.core.exceptions;

/**
 * <p>Exception to provide the following to WalletManager:</p>
 * <ul>
 * <li>Provision of wallet saving messages</li>
 * </ul>

 * @since 0.3.6
 */
public class WalletSaveException extends RuntimeException {

    private static final long serialVersionUID = 2372470341301293437L;

    public WalletSaveException(String s) {
        super(s);
    }

    public WalletSaveException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
