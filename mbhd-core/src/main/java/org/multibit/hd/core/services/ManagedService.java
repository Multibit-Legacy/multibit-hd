package org.multibit.hd.core.services;

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
   * Stop the service blocking until terminated
   */
  void stopAndWait();

}
