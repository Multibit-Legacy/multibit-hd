package org.multibit.hd.core.error_reporting;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.io.Files;
import org.apache.commons.lang3.text.WordUtils;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.logging.LogbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * <p>Exception handler to provide the following to application:</p>
 * <ul>
 * <li>Displays a critical failure dialog to the user and handles process of bug reporting</li>
 * </ul>
 *
 * @since 0.0.1
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
  @SuppressWarnings("unchecked")
  public static void handleThrowable(Throwable t) {

    log.error("Uncaught exception", t);

    final String message;
    if (t.getLocalizedMessage() == null || t.getLocalizedMessage().length() == 0) {
      message = "Fatal: " + t.getClass();
    } else {
      message = t.getLocalizedMessage();
    }

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          try {
            // Internationalisation and layout issues require a dynamic invocation
            // of a class present in the Swing module
            Class nativeApplicationClass = Class.forName("org.multibit.hd.ui.error_reporting.ErrorReportingDialog");
            // Instantiate through newInstance() and supply constructor arguments
            // Dialog will show and handle all further requirements including hard shutdown
            nativeApplicationClass.getDeclaredConstructor(String.class).newInstance(message);
          } catch (Throwable t1) {
            log.error("Unable to use standard error reporting dialog.", t1);
            try {
              // This should never happen due to static binding of the Swing library
              JOptionPane.showMessageDialog(
                null, "Oh Snap!\n\nA serious error has occurred with the following message:\n" + WordUtils.wrap(message, 30),
                "Error", JOptionPane
                  .ERROR_MESSAGE);
              // Fire a hard shutdown after dialog closes with emergency fallback
              CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);
            } catch (Throwable t2) {
              log.error("Unable to use fallback error reporting dialog. Forcing immediate shutdown.", t2);
              // Safest option at this point is an emergency shutdown
              System.exit(-1);
            }
          }

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

        // This is not serious enough to concern the user
        log.error(exception.getMessage(), exception);

      }
    };
  }

  /**
   * Reads the current logging file (obtained through Logback) as a String
   */
  public static String readCurrentLogfile() {

    Optional<File> currentLoggingFile = LogbackFactory.getCurrentLoggingFile();

    if (currentLoggingFile.isPresent()) {
      // Read it
      try {
        return Files.toString(currentLoggingFile.get(), Charsets.UTF_8);
      } catch (IOException e) {
        return "Current log file not available";
      }
    }

    return "Current log file not available";

  }

  /**
   * @param userNotes The additional user notes to upload
   */
  public static void handleErrorReportUpload(String userNotes) {

    Optional<File> currentLoggingFile = LogbackFactory.getCurrentLoggingFile();

    if (currentLoggingFile.isPresent()) {
      // Read and encrypt it as ASCII Armor
    }

  }
}

