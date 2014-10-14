package org.multibit.hd.core.concurrent;

import java.util.concurrent.*;

/**
 * <p>Wrapper to provide standard exception handling</p>
 *
 * @since 0.0.1
 *  
 */
public class SafeScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

  public SafeScheduledThreadPoolExecutor(int corePoolSize) {
    super(corePoolSize);
  }

  public SafeScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
    super(corePoolSize, handler);
  }

  public SafeScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
    super(corePoolSize, threadFactory);
  }

  public SafeScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
    super(corePoolSize, threadFactory, handler);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);

    if (t == null && r instanceof Future<?>) {
      try {
        Future<?> future = (Future<?>) r;
        if (future.isDone()) {
          future.get();
        }
      } catch (CancellationException ce) {
        // Do nothing - deliberately cancelled
      } catch (ExecutionException ee) {
        // Exception generated
        t = ee.getCause();
      } catch (InterruptedException ie) {
        // Shutdown occurring
        Thread.currentThread().interrupt();
      }
    }

    // We rely on ListenableFuture to handle exceptions from executors

  }
}
