package org.multibit.hd.core.exceptions;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Exception handler to provide the following to application:</p>
 * <ul>
 * <li>Displays a critical failure dialog to the user and handles process of bug reporting</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ExceptionHandler extends EventQueue implements Thread.UncaughtExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * <p>Set this as the default uncaught exception handler</p>
   */
  public static void registerExceptionHandler() {

    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());

  }

  /**
   * <p>The handler of last resort when a programming error has caused an uncaught exception to occur</p>
   *
   * @param t The cause of the problem
   */
  public static void handleThrowable(Throwable t) {

    log.error("Uncaught exception", t);

    final String message;
    if (t.getMessage() == null || t.getMessage().length() == 0) {
      message = "Fatal: " + t.getClass();
    } else {
      message = t.getMessage();
    }

    // TODO Replace this with a full-on reporting system
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

        // Safest option at this point is to shut down
        CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);
      }
    });

  }

  @Override
  protected void dispatchEvent(AWTEvent newEvent) {
    try {
      super.dispatchEvent(newEvent);
    } catch (Throwable t) {
      handleThrowable(t);
    }
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    handleThrowable(e);
  }

  /**
   * @return A subscriber exception handler for the Guava event bus
   */
  public static SubscriberExceptionHandler newSubscriberExceptionHandler() {

    return new SubscriberExceptionHandler() {
      @Override
      public void handleException(Throwable exception, SubscriberExceptionContext context) {

        log.error(exception.getMessage(), exception);

      }
    };
  }
}

