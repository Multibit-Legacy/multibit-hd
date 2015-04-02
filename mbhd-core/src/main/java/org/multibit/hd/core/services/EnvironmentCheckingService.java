package org.multibit.hd.core.services;

import org.joda.time.DateTime;
import org.multibit.hd.core.dto.EnvironmentSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Periodic environment checking for various issues</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EnvironmentCheckingService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(EnvironmentCheckingService.class);

  public static final int ENVIRONMENT_REFRESH_SECONDS = 2;

  /**
   * Initialise to allow single environment alert for debugger attachment
   */
  private DateTime nextDebuggerAlert = Dates.nowUtc().minusSeconds(1);

  @Override
  public boolean startInternal() {

    log.debug("Starting environment service");

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor("environment");

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(
      new Runnable() {

        public void run() {

          // Check frequently for a Java debugger being attached
          // to get immediate detection
          if (OSUtils.isDebuggerAttached()) {
            handleDebuggerAttached();
          }

        }

        private void handleDebuggerAttached() {

          if (Dates.nowUtc().isAfter(nextDebuggerAlert)) {

            // Prevent lots of repeat alerts
            nextDebuggerAlert = Dates.nowUtc().plusMinutes(5);

            // Issue the alert
            CoreEvents.fireEnvironmentEvent(EnvironmentSummary.newDebuggerAttached());

          }

        }


      }, 0, ENVIRONMENT_REFRESH_SECONDS, TimeUnit.SECONDS);

    return true;

  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {
    // Service can survive a switch
    return preventCleanupOnSwitch(shutdownType);
  }

}