package org.multibit.hd.core.exceptions;

/**
 * <p>Exception to provide Contacts save failure information :</p>
 *
 * <p>This base exception acts as a general failure mode not attributable to a specific cause (other than
 * that reported in the exception message). Since this is in English, it may not be worth reporting directly
 * to the user other than as part of a "general failure to parse" response.</p>
 *
 * @since 0.0.1
 */
public class ContactsSaveException extends RuntimeException {

    public ContactsSaveException(String s) {
        super(s);
    }

    public ContactsSaveException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
