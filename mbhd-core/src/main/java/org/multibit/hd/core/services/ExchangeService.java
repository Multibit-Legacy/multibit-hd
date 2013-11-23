package org.multibit.hd.core.services;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.multibit.hd.core.api.BalanceChangeEvent;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.exceptions.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

  /**
   * This is a hard coded value to avoid hammering the exchanges with the number
   * of instances of MultiBit out there
   */
  public static final int TICKER_REFRESH_MINUTES = 1;

  public void start() {

    log.debug("Starting service");

    // Use the factory to get the version 2 MtGox exchange API using default settings
    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());
    final String exchangeName = exchange.getExchangeSpecification().getExchangeName();

    // Interested in the public polling market data feed (no authentication)
    final PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();

    final ListeningScheduledExecutorService service = SafeExecutors.newSingleThreadScheduledExecutor();

    service.scheduleAtFixedRate(new Runnable() {

      public void run() {
        // Get the latest ticker data showing BTC to GBP
        Ticker ticker;
        try {
          ticker = marketDataService.getTicker(Currencies.BTC, Currencies.GBP);

          CoreServices.postEvent(new BalanceChangeEvent(
              ticker.getLast().getAmount(),
              exchangeName)
          );
        } catch (IOException e) {
          throw new CoreException(e);
        }
      }

    }, 1, 1, TimeUnit.SECONDS);

  }

}