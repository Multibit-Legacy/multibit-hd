package org.multibit.hd.core.exceptions;

/**
 * <p>Exception to provide the following to WalletManager:</p>
 * <ul>
 * <li>Provision of wallet saving messages</li>
 * </ul>

 * @since 0.3.6
 */
public class ContactsSaveException extends RuntimeException {

    private static final long serialVersionUID = 2372470341301293437L;

    public ContactsSaveException(String s) {
        super(s);
    }

    public ContactsSaveException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
