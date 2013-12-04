package org.multibit.hd.core.exceptions;


/**
 * <p>Exception to provide Wallet load failure information :</p>
 * <ul>
 * <li>Provision of wallet info loading and saving messages</li>
 * </ul>
 * <p>This base exception acts as a general failure mode not attributable to a specific cause (other than
 * that reported in the exception message). Since this is in English, it may not be worth reporting directly
 * to the user other than as part of a "general failure to parse" response.</p>
 *
 * @since 0.3.0
 */
public class WalletLoadException extends RuntimeException {

    public WalletLoadException(String s) {
        super(s);
    }

    public WalletLoadException(String s, Throwable throwable) {
        super(s, throwable);
    }
}