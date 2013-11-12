package org.multibit.hd.ui.javafx.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Uncaught exception handler to provide the following to application:</p>
 * <ul>
 * <li>If a runtime exception is thrown outside the JVM then this will log it</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class UncaughtExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

  public static class UncaughtThreadGroup extends ThreadGroup {

    public UncaughtThreadGroup(String s) {
      super(s);
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
      log.error(throwable.getMessage(), throwable);
    }
  }

}