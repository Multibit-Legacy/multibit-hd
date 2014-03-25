package org.multibit.hd.core.services;

import org.joda.time.DateTime;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Periodic environment checking for security issues</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SecurityCheckingService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(SecurityCheckingService.class);

  public static final int ENVIRONMENT_REFRESH_SECONDS = 2;

  private final AtomicReference<DateTime> nextAlert=new AtomicReference<>(Dates.nowUtc());

  public SecurityCheckingService() {
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public boolean start() {

    log.debug("Starting security service");

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor();

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {

      public void run() {

        // Check for a Java debugger being attached
        if (OSUtils.isDebuggerAttached()) {

          if (Dates.nowUtc().isAfter(nextAlert.get())) {

          CoreEvents.fireSecurityEvent(SecuritySummary.newDebuggerAttached());
            nextAlert.set(Dates.nowUtc().plusMinutes(5));
          }

        }

      }

    }, 0, ENVIRONMENT_REFRESH_SECONDS, TimeUnit.SECONDS);

    return true;

  }

}