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
 *         
 */
public interface ManagedService {

  /**
   * Start the service (events are fired)
   */
  void start();

  /**
   * Stop the service (blocking until terminated)
   */
  void stopAndWait();

  /**
   * Subscribe to a "shutdown" event
   */
  @Subscribe
  void onShutdownEvent(ShutdownEvent shutdownEvent);

}
