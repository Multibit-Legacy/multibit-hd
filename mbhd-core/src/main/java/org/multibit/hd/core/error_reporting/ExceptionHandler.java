package org.multibit.hd.core.error_reporting;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.text.WordUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.services.BRITServices;
import org.multibit.hd.brit.utils.HttpsUtils;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.logging.LogbackFactory;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchProviderException;

/**
 * <p>Exception handler to provide the following to application:</p>
 * <ul>
 * <li>Displays a critical failure dialog to the user and handles process of bug reporting</li>
 * </ul>
 *
 * @since 0.0.1
 */
@SuppressFBWarnings({"DM_EXIT"})
public class ExceptionHandler extends EventQueue implements Thread.UncaughtExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * The URL of the live error reporting daemon
   */
  public static final String LIVE_ERROR_REPORTING_URL = "https://multibit.org/error";

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
            // Instantiate through newInstance() and supply constructor arguments to indicate an apology is required
            // Dialog will show and handle all further requirements including hard shutdown
            nativeApplicationClass.getDeclaredConstructor(boolean.class).newInstance(true);
          } catch (Throwable t1) {
            log.error("Unable to use standard error reporting dialog.", t1);
            try {
              // This should never happen due to static binding of the Swing library
              JOptionPane.showMessageDialog(
                null, "Oh Snap!\n\nA serious error has occurred with the following message:\n" + WordUtils.wrap(message, 30),
                "Error",
                JOptionPane.ERROR_MESSAGE);
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

  /**
   * Allow the user to manually enter a problem
   */
  @SuppressWarnings("unchecked")
  public static void handleManualErrorReport() {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          try {
            // Internationalisation and layout issues require a dynamic invocation
            // of a class present in the Swing module
            Class nativeApplicationClass = Class.forName("org.multibit.hd.ui.error_reporting.ErrorReportingDialog");
            // Instantiate through newInstance() and supply constructor arguments to thank the user for their feedback
            // Dialog will show and handle all further requirements
            nativeApplicationClass.getDeclaredConstructor(boolean.class).newInstance(false);
          } catch (Throwable t1) {
            log.error("Unable to use standard error reporting dialog.", t1);
            try {
              // This should never happen due to static binding of the Swing library
              JOptionPane.showMessageDialog(
                null, "Oh Snap!\n\nA serious error has occurred with the reporting system",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
   * @param errorReportingUrl The error reporting URL to use
   *
   * @return The error report result
   */
  public static ErrorReportResult handleErrorReportUpload(String userNotes, URL errorReportingUrl) {

    final PGPPublicKey multibitPublicKey;
    try {
      multibitPublicKey = BRITServices.getMatcherPublicKey();
    } catch (IOException | PGPException e) {
      log.error("Failed to load MultiBit public key", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Attempt to load the current logging file
    Optional<File> currentLoggingFile = LogbackFactory.getCurrentLoggingFile();
    if (!currentLoggingFile.isPresent()) {
      log.error("No current logging file.");
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Have a chance of getting a result

    // Create a formatted payload for the server
    String errorReport = "----- BEGIN USER NOTES -----\n" + userNotes + "\n----- BEGIN LOG -----\n" + readCurrentLogfile() + "----- END LOG -----\n";

    // Write this to the disk (it's already known to the system)
    final File errorReportFile = new File(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath() + "/logs/error-report.txt");
    try {
      log.debug("Writing error report file to\n'{}'", errorReportFile.getAbsolutePath());
      Files.write(errorReport.getBytes(Charsets.UTF_8), errorReportFile);
    } catch (IOException e) {
      log.error("Failed to write error-report.txt", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Prepare the ASCII Armor output stream
    final FileOutputStream armoredErrorReportFOS;
    try {
      log.debug("Preparing armored error report file");
      armoredErrorReportFOS = new FileOutputStream(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath() + "/logs/error-report.txt.asc");
    } catch (FileNotFoundException e) {
      log.error("Failed to prepare error-report.txt.asc", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Encrypt with the MultiBit public key
    try {
      log.debug("Writing armored error report file");
      PGPUtils.encryptFile(armoredErrorReportFOS, errorReportFile, multibitPublicKey);
    } catch (IOException | NoSuchProviderException | PGPException e) {
      log.error("Failed to write error-report.txt.asc", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Delete the plain text report
    try {
      log.debug("Deleting error report file");
      SecureFiles.secureDelete(errorReportFile);
    } catch (IOException e) {
      log.error("Failed to delete error-report.txt", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Load the armored payload
    final byte[] armoredErrorReport;
    try {
      log.debug("Reading armored error report file");
      File armoredErrorReportFile = new File(InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath() + "/logs/error-report.txt.asc");
      armoredErrorReport = Files.toByteArray(armoredErrorReportFile);
    } catch (IOException e) {
      log.error("Failed to read error-report.txt.asc", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    final byte[] response;
    try {
      log.debug("POSTing armored error report file to '{}'", errorReportingUrl);
      response = HttpsUtils.doPost(errorReportingUrl, armoredErrorReport);
    } catch (IOException e) {
      log.error("Failed to POST error-report.txt.asc", e);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // At the moment all responses are "unknown" unless empty
    if (response == null || response.length == 0) {
      log.error("Empty response from '{}'", errorReportingUrl);
      return ErrorReportResult.UPLOAD_FAILED;
    }

    // Must be OK to be here
    return ErrorReportResult.UPLOAD_OK_UNKNOWN;

  }
}

