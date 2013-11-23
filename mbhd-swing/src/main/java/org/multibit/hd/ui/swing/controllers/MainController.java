package org.multibit.hd.ui.swing.controllers;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.ui.swing.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Controller for the Mmin view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 */
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private final MainView mainView;

  private final BlockingQueue<Ticker> tickerQueue;

  private ScheduledExecutorService executorService;

  public MainController(MainView mainView, BlockingQueue<Ticker> tickerQueue) {

    this.mainView = mainView;
    this.tickerQueue = tickerQueue;

  }

  public void start() {

    log.debug("Starting service");

    this.executorService = SafeExecutors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {

        try {

          log.debug("View ticker check");

          // Wait forever for the ticker
          Ticker ticker = tickerQueue.take();

          log.debug("Updating");

          // Perform an update
          mainView.updateBalance(ticker.getLast().getAmount());

        } catch (InterruptedException e) {
          log.error(e.getMessage(), e);
        }

      }
    }, 1, 1, TimeUnit.SECONDS);

    log.debug("Started");

  }

  public void stopAndWait() {

    log.debug("Stopping service");

    executorService.shutdownNow();

    log.debug("Stopped");
  }

}
