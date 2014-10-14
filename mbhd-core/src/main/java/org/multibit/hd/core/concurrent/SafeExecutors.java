package org.multibit.hd.core.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(SafeExecutors.class);

  /**
   * The number of seconds to wait before terminating the thread during a shutdown
   */
  private static final long DURATION_BEFORE_QUIT = 5;

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
  public static ListeningExecutorService newSingleThreadExecutor(String name) {
    return newFixedThreadPool(1, name);
  }

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
   * @param nThreads The number of threads in the pool
   * @param poolName The name of the pool (use lowercase hyphenated)
   *
   * @return the newly created thread pool
   *
   * @throws IllegalArgumentException if {@code nThreads <= 0}
   */
  public static ListeningExecutorService newFixedThreadPool(int nThreads, String poolName) {

    log.debug("New fixed thread pool with {} threads: '{}'", nThreads, poolName);

    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("safe-fixed-"+poolName+"-%d").build();

    return newFixedThreadPool(nThreads, threadFactory);
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
  private static ListeningExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {

    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingExecutorService(
        new SafeThreadPoolExecutor(
          nThreads,
          nThreads,
          0L,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(),
          threadFactory
        ), DURATION_BEFORE_QUIT, TimeUnit.SECONDS
      )
    );
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
   * @param name The name of the thread
   *
   * @return the newly created scheduled executor
   */
  public static ListeningScheduledExecutorService newSingleThreadScheduledExecutor(String name) {
    return newScheduledThreadPool(1, name);
  }

  /**
   * Creates a thread pool that can schedule commands to run after a
   * given delay, or to execute periodically.
   *
   * @param corePoolSize The number of threads to keep in the pool, even if they are idle.
   * @param poolName     The name of the pool (use lowercase hyphenated)
   *
   * @return a newly created scheduled thread pool
   *
   * @throws IllegalArgumentException if {@code corePoolSize < 0}
   */
  public static ListeningScheduledExecutorService newScheduledThreadPool(int corePoolSize, String poolName) {

    log.debug("New scheduled thread pool with {} threads: '{}'", corePoolSize, poolName);

    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("safe-scheduled-"+poolName+"-%d").build();

    return newScheduledThreadPool(corePoolSize, threadFactory);
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
  private static ListeningScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {

    return MoreExecutors.listeningDecorator(
      MoreExecutors.getExitingScheduledExecutorService(
        new SafeScheduledThreadPoolExecutor(corePoolSize, threadFactory),
        DURATION_BEFORE_QUIT, TimeUnit.SECONDS
      )
    );
  }

}
