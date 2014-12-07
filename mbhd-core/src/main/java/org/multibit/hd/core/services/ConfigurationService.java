package org.multibit.hd.core.services;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Configuration persistence after a shutdown hook</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ConfigurationService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

  @Override
  protected boolean startInternal() {

    // Do nothing
    return true;
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    log.debug("Persisting current configuration after shutdown");

    // We may be in a partial startup situation
    if (Configurations.currentConfiguration != null) {

      Configurations.persistCurrentConfiguration();

    }

    // Service can survive a switch
    return preventCleanupOnSwitch(shutdownType);

  }

}