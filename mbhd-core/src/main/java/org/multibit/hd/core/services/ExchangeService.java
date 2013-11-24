package org.multibit.hd.core.services;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.multibit.hd.core.api.BalanceChangeEvent;
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
public class ExchangeService extends AbstractService implements ManagedService {

  private static final Logger log = LoggerFactory.getLogger(ExchangeService.class);

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
   * 15 minutes =
   */
  public static final int TICKER_REFRESH_SECONDS = 1;

  public ExchangeService(String exchangeClassName) {

    // Use the factory to get the exchange API using default settings
    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);
    exchangeName = exchange.getExchangeSpecification().getExchangeName();

    // Interested in the public polling market data feed (no authentication)
    this.pollingMarketDataService = exchange.getPollingMarketDataService();

  }

  @Override
  public void initialise() {

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor();

  }

  @Override
  public void start() {

    log.debug("Starting service");

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {

      public void run() {
        // Get the latest ticker data showing BTC to GBP
        Ticker ticker;
        try {
          ticker = pollingMarketDataService.getTicker(Currencies.BTC, Currencies.GBP);

          CoreServices.uiEventBus.post(new BalanceChangeEvent(
            ticker.getLast().getAmount(),
            exchangeName)
          );
        } catch (IOException e) {
          throw new CoreException(e);
        }
      }

    }, 1, TICKER_REFRESH_SECONDS, TimeUnit.SECONDS);

  }

}