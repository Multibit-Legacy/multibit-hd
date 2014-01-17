package org.multibit.hd.core.services;

import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.joda.money.BigMoney;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.exceptions.CoreException;
import org.multibit.hd.core.utils.Dates;
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
public class ExchangeTickerService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(ExchangeTickerService.class);

  /**
   * The polling market data service for tickers
   */
  private final PollingMarketDataService pollingMarketDataService;

  /**
   * The exchange name (not localised)
   */
  private final String exchangeName;

  /**
   * This is a hard coded value to avoid hammering the exchanges with the number
   * of instances of MultiBit out there
   * 15 minutes = 900 seconds and is the recommended value
   */
  public static final int TICKER_REFRESH_SECONDS = 5;

  /**
   * @param exchangeName             The friendly exchange name (e.g. "Bitstamp")
   * @param pollingMarketDataService The polling market data service for this exchange
   */
  public ExchangeTickerService(String exchangeName, PollingMarketDataService pollingMarketDataService) {

    this.exchangeName = exchangeName;
    this.pollingMarketDataService = pollingMarketDataService;

  }

  @Override
  public void start() {
    log.debug("Starting service");
    // Use the provided executor service management
    requireSingleThreadScheduledExecutor();

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {

      private BigMoney previous;

      public void run() {
        // Get the latest ticker data showing BTC to USD
        Ticker ticker;
        try {
          ticker = pollingMarketDataService.getTicker(Currencies.BTC, Currencies.USD);

          if (previous == null || !ticker.getLast().isEqual(previous)) {

            CoreEvents.fireExchangeRateChangedEvent(
              ticker.getLast().getAmount(),
              exchangeName,
              // Exchange rate will expire just after the next update (with small overlap)
              Dates.nowUtc().plusSeconds(TICKER_REFRESH_SECONDS + 5)
            );

            log.debug("Updated {} ticker: {}", exchangeName, ticker.getLast());
          }

          previous = ticker.getLast();

        } catch (IOException e) {
          throw new CoreException(e);
        }
      }

    }, 0, TICKER_REFRESH_SECONDS, TimeUnit.SECONDS);

  }

}