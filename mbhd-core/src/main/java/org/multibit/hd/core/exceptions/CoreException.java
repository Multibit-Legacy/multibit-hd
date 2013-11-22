package org.multibit.hd.core.exceptions;

/**
 * <p>Runtime exception to provide the following to the Core:</p>
 * <ul>
 * <li>Wrapper for all caught exceptions for easier logging</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class CoreException extends RuntimeException {

  public CoreException(Throwable cause) {
    super(cause.getMessage(), cause);
  }

  public CoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public CoreException(String message) {
    super(message);
  }
}
