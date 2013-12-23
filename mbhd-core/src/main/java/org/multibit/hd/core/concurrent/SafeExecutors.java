package org.multibit.hd.core.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * <p>Factory that partially wraps the standard Java Executors concurrency to allow any runtime exceptions to be passed
 * to the ExceptionHandler.</p>
 *
 * @since 0.0.1
 *         
 */
public class SafeExecutors {

  /**
   * The number of seconds to wait before terminating the thread during a shutdown
   */
  private static final long DURATION_BEFORE_QUIT = 5;

  /**
   * Creates a thread pool that reuses a fixed number of threads
   * operating off a shared unbounded queue.  At any point, at most
   * <tt>nThreads</tt> threads will be active processing tasks.
   * If additional tasks are submitted when all threads are active,
   * they will wait in the queue until a thread is available.
   * If any thread terminates due to a failure during execution
   * prior to shutdown, a new one will take its place if needed to
   * execute subsequent tasks.  The threads in the pool will exist
   * until it is explicitly {@link java.util.concurrent.ExecutorService#shutdown shutdown}.
   *
   * @param nThreads the number of threads in the pool
   *
   * @return the newly created thread pool
   *
   * @throws IllegalArgumentException if {@code nThreads <= 0}
   */
  public static ListeningExecutorService newFixedThreadPool(int nThreads) {
    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingExecutorService(
        new SafeThreadPoolExecutor(
          nThreads,
          nThreads,
          0L,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>()
        ), 10, TimeUnit.SECONDS));
  }

  /**
   * Creates a thread pool that reuses a fixed number of threads
   * operating off a shared unbounded queue, using the provided
   * ThreadFactory to create new threads when needed.  At any point,
   * at most <tt>nThreads</tt> threads will be active processing
   * tasks.  If additional tasks are submitted when all threads are
   * active, they will wait in the queue until a thread is
   * available.  If any thread terminates due to a failure during
   * execution prior to shutdown, a new one will take its place if
   * needed to execute subsequent tasks.  The threads in the pool will
   * exist until it is explicitly {@link ExecutorService#shutdown
   * shutdown}.
   *
   * @param nThreads      the number of threads in the pool
   * @param threadFactory the factory to use when creating new threads
   *
   * @return the newly created thread pool
   *
   * @throws NullPointerException     if threadFactory is null
   * @throws IllegalArgumentException if {@code nThreads <= 0}
   */
  public static ListeningExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingExecutorService(
        new SafeThreadPoolExecutor(
          nThreads,
          nThreads,
          0L,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(),
          threadFactory
        ), DURATION_BEFORE_QUIT, TimeUnit.SECONDS));
  }

  /**
   * Creates an Executor that uses a single worker thread operating
   * off an unbounded queue. (Note however that if this single
   * thread terminates due to a failure during execution prior to
   * shutdown, a new one will take its place if needed to execute
   * subsequent tasks.)  Tasks are guaranteed to execute
   * sequentially, and no more than one task will be active at any
   * given time. Unlike the otherwise equivalent
   * <tt>newFixedThreadPool(1)</tt> the returned executor is
   * guaranteed not to be reconfigurable to use additional threads.
   *
   * @return the newly created single-threaded Executor
   */
  public static ListeningExecutorService newSingleThreadExecutor() {
    return MoreExecutors.listeningDecorator(newFixedThreadPool(1));
  }

  /**
   * Creates a single-threaded executor that can schedule commands
   * to run after a given delay, or to execute periodically.
   * (Note however that if this single
   * thread terminates due to a failure during execution prior to
   * shutdown, a new one will take its place if needed to execute
   * subsequent tasks.)  Tasks are guaranteed to execute
   * sequentially, and no more than one task will be active at any
   * given time. Unlike the otherwise equivalent
   * <tt>newScheduledThreadPool(1)</tt> the returned executor is
   * guaranteed not to be reconfigurable to use additional threads.
   *
   * @return the newly created scheduled executor
   */
  public static ListeningScheduledExecutorService newSingleThreadScheduledExecutor() {
    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingScheduledExecutorService(
        new SafeScheduledThreadPoolExecutor(1),
        DURATION_BEFORE_QUIT, TimeUnit.SECONDS
      ));
  }

  /**
   * Creates a thread pool that can schedule commands to run after a
   * given delay, or to execute periodically.
   *
   * @param corePoolSize the number of threads to keep in the pool,
   *                     even if they are idle.
   *
   * @return a newly created scheduled thread pool
   *
   * @throws IllegalArgumentException if {@code corePoolSize < 0}
   */
  public static ListeningScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingScheduledExecutorService(
        new SafeScheduledThreadPoolExecutor(corePoolSize),
        DURATION_BEFORE_QUIT, TimeUnit.SECONDS
      ));
  }

  /**
   * Creates a thread pool that can schedule commands to run after a
   * given delay, or to execute periodically.
   *
   * @param corePoolSize  the number of threads to keep in the pool,
   *                      even if they are idle.
   * @param threadFactory the factory to use when the executor
   *                      creates a new thread.
   *
   * @return a newly created scheduled thread pool
   *
   * @throws IllegalArgumentException if {@code corePoolSize < 0}
   * @throws NullPointerException     if threadFactory is null
   */
  public static ListeningScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingScheduledExecutorService(
        new SafeScheduledThreadPoolExecutor(corePoolSize, threadFactory),
        DURATION_BEFORE_QUIT, TimeUnit.SECONDS
      ));
  }

}
