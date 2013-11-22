package org.multibit.hd.ui.exceptions;

/**
 * <p>Runtime exception to provide the following to the UI:</p>
 * <ul>
 * <li>Wrapper for all caught exceptions for easier logging</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class UIException extends RuntimeException {

  public UIException(Throwable cause) {
    super(cause.getMessage(), cause);
  }

  public UIException(String message, Throwable cause) {
    super(message, cause);
  }

  public UIException(String message) {
    super(message);
  }
}
