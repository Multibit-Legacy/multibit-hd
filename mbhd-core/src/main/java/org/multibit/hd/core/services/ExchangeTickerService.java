package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.concurrent.Callable;
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
   * This is a hard coded value to avoid hammering the exchanges with the number
   * of instances of MultiBit out there
   * 15 minutes = 900 seconds and is the recommended value
   */
  public static final int TICKER_REFRESH_SECONDS = 900;

  private final ExchangeKey exchangeKey;
  private final CurrencyUnit localCurrencyUnit;
  final Exchange exchange;

  /**
   * <p>Each new instance of the exchange ticker service creates a new independent Exchange</p>
   *
   * @param bitcoinConfiguration The Bitcoin configuration providing exchange and currency information
   */
  public ExchangeTickerService(BitcoinConfiguration bitcoinConfiguration) {

    this.exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getExchangeKey());
    this.localCurrencyUnit = bitcoinConfiguration.getLocalCurrencyUnit();

    // Create a new exchange
    String exchangeClassName = exchangeKey.getExchange().getExchangeSpecification().getExchangeClassName();
    exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

    // Apply the Bitcoin configuration to this exchange
    exchange.getExchangeSpecification().setApiKey(bitcoinConfiguration.getExchangeApiKeys().orNull());

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

        // Get the latest ticker asynchronously to fit in with non-scheduled users of the method
        ListenableFuture<Ticker> futureTicker = latestTicker();

        Futures.addCallback(futureTicker, new FutureCallback<Ticker>() {
          @Override
          public void onSuccess(Ticker ticker) {

            if (previous == null || !ticker.getLast().isEqual(previous)) {

              BigMoney rate = ticker.getLast();

              String exchangeName = exchangeKey.getExchangeName();

              CoreEvents.fireExchangeRateChangedEvent(
                rate,
                Optional.of(exchangeName),
                // Exchange rate will expire just after the next update (with small overlap)
                Dates.nowUtc().plusSeconds(TICKER_REFRESH_SECONDS + 5)
              );

              log.debug("Updated '{}' ticker: '{}'", exchangeName, ticker.getLast());

              previous = ticker.getLast();
            }
          }

          @Override
          public void onFailure(Throwable t) {
            // Keep the logging to a minimum
            log.warn("Exchange rate lookup failed 'BTC' ('{}')", exchangeKey.getExchangeName());

          }
        });

      }

    }, 0, TICKER_REFRESH_SECONDS, TimeUnit.SECONDS);

  }

  /**
   * <p>Asynchronously get a single ticker response from the exchange</p>
   *
   * @return The future ticker for wrapping with <code>Futures.addCallback</code>
   */
  public ListenableFuture<Ticker> latestTicker() {

    // Apply any exchange quirks to the counter code (e.g. ISO "RUB" -> legacy "RUR")
    final String exchangeCounterCode = ExchangeKey.exchangeCode(localCurrencyUnit.getCurrencyCode(), exchangeKey);
    final String exchangeBaseCode = ExchangeKey.exchangeCode("XBT", exchangeKey);

    return SafeExecutors.newFixedThreadPool(1).submit(new Callable<Ticker>() {
      @Override
      public Ticker call() throws Exception {
        // Check for fiat exchange
        if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
          return exchange.getPollingMarketDataService().getTicker(exchangeCounterCode, "USD");
        } else {
          // Crypto-exchange is straightforward
          return exchange.getPollingMarketDataService().getTicker(exchangeBaseCode, exchangeCounterCode);
        }
      }
    });

  }

  /**
   * @return All the currencies supported by the exchange
   */
  public ListenableFuture<String[]> allCurrencies() {

    return SafeExecutors.newFixedThreadPool(1).submit(new Callable<String[]>() {
      @Override
      public String[] call() throws Exception {
        Locale currentLocale = Configurations.currentConfiguration.getLocale();

        // This may involve a call to the exchange or not
        List<CurrencyPair> currencyPairs = exchange.getPollingMarketDataService().getExchangeSymbols();

        if (currencyPairs == null || currencyPairs.isEmpty()) {
          return new String[]{};
        }

        SortedSet<String> allCurrencies = Sets.newTreeSet();
        for (CurrencyPair currencyPair : currencyPairs) {

          // Add the currency (if non-BTC we can triangulate through USD)
          String baseCode = currencyPair.baseCurrency;
          String counterCode = currencyPair.counterCurrency;

          // Ignore any malformed currency pairs
          if (baseCode == null || counterCode == null ) {
            continue;
          }

          // Make any adjustments
          counterCode = CurrencyUtils.isoCandidateFor(counterCode);

          try {
            // Use Joda Money to determine supported currency
            CurrencyUnit base = CurrencyUnit.getInstance(baseCode);
            if (base != null) {
              // Use JVM to provide translated name
              String localName = Currency.getInstance(baseCode).getDisplayName(currentLocale);
              allCurrencies.add(baseCode + " (" + localName + ")");
            }
          } catch (IllegalArgumentException e) {
            // Base code is not in ISO 4217 so attempt to locate counter currency (e.g. BTC/RUR)
            try {
              // Use Joda Money to determine supported currency
              CurrencyUnit counter = CurrencyUnit.getInstance(counterCode);
              if (counter != null) {
                // Use JVM to provide translated name
                String localName = Currency.getInstance(counterCode).getDisplayName(currentLocale);
                allCurrencies.add(counterCode + " (" + localName + ")");
              }
            } catch (IllegalArgumentException e1) {
              // Neither base nor counter code is in ISO 4217 so ignore since we're only working with fiat
            }
          }
        }

        // Return the unique list of currencies
        return allCurrencies.toArray(new String[allCurrencies.size()]);

      }

    });
  }
}