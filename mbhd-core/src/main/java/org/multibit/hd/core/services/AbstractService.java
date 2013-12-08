package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;

/**
 * <p>Abstract base class to provide the following to application services:</p>
 * <ul>
 * <li>Common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public abstract class AbstractService implements ManagedService {

  /**
   * The optional scheduled executor service running this service's activity
   */
  private Optional<ListeningScheduledExecutorService> scheduledService = Optional.absent();

  /**
   * The optional executor service running this service's activity
   */
  private Optional<ListeningExecutorService> service = Optional.absent();

  @Override
  public void start() {
    // Do nothing
  }

  @Override
  public void stopAndWait() {
    if (scheduledService.isPresent()) {

      scheduledService.get().shutdownNow();
    }
    if (service.isPresent()) {
      service.get().shutdownNow();
    }
  }

  /**
   * <p>Provide a single thread scheduled executor</p>
   */
  protected void requireSingleThreadScheduledExecutor() {
    scheduledService = Optional.of(SafeExecutors.newSingleThreadScheduledExecutor());
  }

  /**
   * <p>Provide a single thread executor</p>
   */
  protected void requireSingleThreadExecutor() {
    service = Optional.of(SafeExecutors.newSingleThreadExecutor());
  }

  /**
   * @return The executor service
   */
  protected ListeningExecutorService getExecutorService() {
    return service.get();
  }

  /**
   * @return The executor service
   */
  protected ListeningScheduledExecutorService getScheduledExecutorService() {
    return scheduledService.get();
  }

}
