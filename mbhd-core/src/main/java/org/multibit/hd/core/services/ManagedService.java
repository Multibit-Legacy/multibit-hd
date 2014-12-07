package org.multibit.hd.core.services;

import org.multibit.hd.core.events.ShutdownEvent;

/**
 * <p>Interface to provide the following to application API services:</p>
 * <ul>
 * <li>Life cycle methods</li>
 * </ul>
 *
 * @since 0.0.1
 */
public interface ManagedService {

  /**
   * <p>Start the service</p>
   *
   * @return True if the service started sufficiently for the application to run, false if an application shutdown is required
   */
  boolean start();

  /**
   * <p>Shut down the service (blocking until terminated) and unregister from CoreEvents</p>
   *
   * @param shutdownType The shutdown type describing why this service is shutting down
   */
  void shutdownNow(ShutdownEvent.ShutdownType shutdownType);

}
