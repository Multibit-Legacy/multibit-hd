package org.multibit.hd.core.services;

import com.google.common.eventbus.Subscribe;
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
   * <p>Start the service (events are fired)</p>
   *
   * @return True if the service started sufficiently for the application to run, false if a shutdown is required
   */
  boolean start();

  /**
   * <p>Stop the service (blocking until terminated) and unregister from CoreEvents</p>
   */
  void stopAndUnregister();

  /**
   * <p>Subscribe to a "shutdown" event</p>
   *
   * <p>Implementers may choose to call <code>stopAndUnregister</code> as part of this operation</p>
   */
  @Subscribe
  void onShutdownEvent(ShutdownEvent shutdownEvent);

}
