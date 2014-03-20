package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.events.ShutdownEvent;

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

  protected AbstractService() {
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public void start() {
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
   * <p>Provide a fixed thread pool executor</p>
   */
  protected void requireFixedThreadPoolExecutor(int threadCount) {
    service = Optional.of(SafeExecutors.newFixedThreadPool(threadCount));
  }

  /**
   * @return The executor service
   */
  protected ListeningExecutorService getExecutorService() {
    return service.get();
  }

  /**
    * @return The executor service optional
    */
   protected Optional<ListeningExecutorService> getExecutorServiceOptional() {
     return service;
   }

   /**
   * @return The executor service
   */
  protected ListeningScheduledExecutorService getScheduledExecutorService() {
    return scheduledService.get();
  }

  @Override
  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    stopAndWait();

  }
}
