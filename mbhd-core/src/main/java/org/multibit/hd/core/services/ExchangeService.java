package org.multibit.hd.core.services;

import com.google.common.collect.Queues;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.exceptions.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Access to exchange rates and market information</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ExchangeService {

  private static final Logger log = LoggerFactory.getLogger(ExchangeService.class);

  private final BlockingQueue<Ticker> tickerQueue = Queues.newArrayBlockingQueue(10);

  public void start() {

    log.debug("Starting service");

    // Use the factory to get the version 2 MtGox exchange API using default settings
    final Exchange mtGoxExchange = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());

    // Interested in the public polling market data feed (no authentication)
    final PollingMarketDataService marketDataService = mtGoxExchange.getPollingMarketDataService();

    final ScheduledExecutorService service = SafeExecutors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {

        // Get the latest ticker data showing BTC to GBP
        Ticker ticker;
        try {

          log.debug("Ticker check...");

          // Get the latest ticker and add it to the queue
          ticker = marketDataService.getTicker(Currencies.BTC, Currencies.GBP);
          tickerQueue.put(ticker);

        } catch (IOException | InterruptedException e) {
          throw new CoreException(e);
        }

      }

    }, 1, 5, TimeUnit.SECONDS);


  }

  /**
   * @return The ticker queue
   */
  public BlockingQueue<Ticker> getTickerQueue() {
    return tickerQueue;
  }
}