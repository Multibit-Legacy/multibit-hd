package org.multibit.hd.core.concurrency;

import org.multibit.hd.core.exceptions.ExceptionHandler;

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
        if (future.isDone())
          future.get();
      } catch (CancellationException ce) {
        t = ce;
      } catch (ExecutionException ee) {
        t = ee.getCause();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt(); // ignore/reset
      }
    }
    if (t != null) {
      ExceptionHandler.handleThrowable(t);
    }
}
}
