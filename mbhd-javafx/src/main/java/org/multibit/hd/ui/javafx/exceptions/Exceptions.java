package org.multibit.hd.ui.javafx.exceptions;

/**
 * <p>Utility to provide the following to exception handling:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class Exceptions {

  /**
   * Utilities have private constructors
   */
  private Exceptions() {
  }

  public static void rethrow(Exception e) {
    throw new UIException(e.getMessage(), e);
  }
}
