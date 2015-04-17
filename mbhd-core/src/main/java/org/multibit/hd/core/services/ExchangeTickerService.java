package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.ExchangeSummary;
import org.multibit.hd.core.dto.EnvironmentSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.utils.CurrencyUtils;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Access to exchange rates and market information</li>
 * </ul>
 *
 * @since 0.0.1
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
  private final Currency localCurrency;

  private final Optional<Exchange> exchange;

  /**
   * The executor service for managing one off dynamic "all currency" lookups against exchanges
   */
  private ListeningExecutorService allCurrenciesExecutorService = null;
  private ListeningExecutorService latestTickerExecutorService = SafeExecutors.newSingleThreadExecutor("latest-ticker");

  /**
   * <p>Each new instance of the exchange ticker service creates a new independent Exchange</p>
   *
   * @param bitcoinConfiguration The Bitcoin configuration providing exchange and currency information
   */
  public ExchangeTickerService(BitcoinConfiguration bitcoinConfiguration) {

    super();

    this.exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());
    this.localCurrency = Currency.getInstance(bitcoinConfiguration.getLocalCurrencyCode());

    // Check for a real exchange
    if (ExchangeKey.NONE.equals(exchangeKey)) {

      this.exchange = Optional.absent();

    } else {

      // Create a new exchange
      String exchangeClassName = exchangeKey.getExchange().get().getExchangeSpecification().getExchangeClassName();
      exchange = Optional.of(ExchangeFactory.INSTANCE.createExchange(exchangeClassName));

      // Apply the Bitcoin configuration to this exchange
      Map<String, String> exchangeApiKeys = bitcoinConfiguration.getExchangeApiKeys();
      if (exchangeApiKeys.containsKey(exchangeKey.name())) {
        exchange.get().getExchangeSpecification().setApiKey(exchangeApiKeys.get(exchangeKey.name()));
      }

    }

  }

  @Override
  public boolean startInternal() {

    log.debug("Starting service");

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor("exchange-ticker");

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(
      new Runnable() {

        private BigDecimal previous;

        public void run() {

          // Get the latest ticker asynchronously to fit in with non-scheduled users of the method
          ListenableFuture<Ticker> futureTicker = latestTicker();

          Futures.addCallback(
            futureTicker, new FutureCallback<Ticker>() {

              @Override
              public void onSuccess(Ticker ticker) {

                // Network or exchange might be down
                if (ticker == null) {
                  CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeDown(exchangeKey.getExchangeName(), ""));
                  return;
                }

                // Fire the event in case the exchange is restored (or a new exchange comes online from a settings change)
                CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeOK(exchangeKey.getExchangeName()));

                if (previous == null || !ticker.getLast().equals(previous)) {

                  BigDecimal rate = ticker.getLast();

                  String exchangeName = exchangeKey.getExchangeName();

                  CoreEvents.fireExchangeRateChangedEvent(
                    rate,
                    localCurrency,
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

                if (t instanceof IllegalArgumentException) {
                  // The exchange may have changed their currency offerings
                  log.warn("Exchange '{}' reported a currency error: {}", exchangeKey.getExchangeName(), t.getMessage());
                  CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeError(exchangeKey.getExchangeName(), t.getMessage()));
                }

                if (t instanceof NotAvailableFromExchangeException) {
                  // The exchange is unable to service this request
                  log.warn("Exchange '{}' reported a 'not available from exchange' error: {}", exchangeKey.getExchangeName(), t.getMessage());
                  CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeError(exchangeKey.getExchangeName(), t.getMessage()));
                }

                if (t instanceof UnknownHostException) {
                  // The exchange is either down or we have no network connection
                  log.warn("Exchange '{}' reported an unknown host error: {}", exchangeKey.getExchangeName(), t.getMessage());
                  CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeDown(exchangeKey.getExchangeName(), t.getMessage()));
                }

                if (t instanceof SSLHandshakeException) {
                  // The exchange is not presenting a valid SSL certificate - treat as down
                  log.warn("Exchange '{}' reported an SSL error: {}", exchangeKey.getExchangeName(), t.getMessage());
                  CoreEvents.fireExchangeStatusChangedEvent(ExchangeSummary.newExchangeDown(exchangeKey.getExchangeName(), t.getMessage()));
                }

              }
            }
          );

        }

      }

      , 0, TICKER_REFRESH_SECONDS, TimeUnit.SECONDS);

    return true;

  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    switch (shutdownType) {

      case HARD:
      case SOFT:
        if (allCurrenciesExecutorService != null) {
          allCurrenciesExecutorService.shutdownNow();
        }
        latestTickerExecutorService.shutdownNow();

        // Allow ongoing cleanup
        return true;
      case SWITCH:
        // Avoid ongoing cleanup
        return false;
      default:
        throw new IllegalStateException("Unsupported state: " + shutdownType.name());
    }
  }

  /**
   * <p>Asynchronously get a single ticker response from the exchange</p>
   *
   * @return The future ticker for wrapping with <code>Futures.addCallback</code>
   */
  public ListenableFuture<Ticker> latestTicker() {
    // Apply any exchange quirks to the counter code (e.g. ISO "RUB" -> legacy "RUR")
    final String exchangeCounterCode = ExchangeKey.exchangeCode(localCurrency.getCurrencyCode(), exchangeKey);
    final String exchangeBaseCode = ExchangeKey.exchangeCode("XBT", exchangeKey);

    // Perform an asynchronous call to the exchange
    return latestTickerExecutorService.submit(
      new Callable<Ticker>() {

        @Override
        public Ticker call() throws Exception {

          if (ExchangeKey.NONE.equals(exchangeKey)) {

            return getEmptyTicker();
          }

          if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {

            // Triangulate through USD to reach exchange rate
            return getTriangulatedTicker();

          } else {

            // Crypto-exchange is straightforward
            return getDirectTicker();
          }
        }

        private Ticker getDirectTicker() throws IOException {

          log.debug("Direct ticker");

          CurrencyPair directPair = new CurrencyPair(exchangeBaseCode, exchangeCounterCode);
          return exchange.get().getPollingMarketDataService().getTicker(directPair);

        }

        private Ticker getTriangulatedTicker() throws IOException {

          log.debug("OER triangulated ticker");

          CurrencyPair localToUsdPair = new CurrencyPair(exchangeCounterCode, "USD");
          CurrencyPair bitcoinToUsdPair = new CurrencyPair("BTC", "USD");

          // Need to triangulate through USD
          Ticker inverseLocalToUsdTicker = exchange.get().getPollingMarketDataService().getTicker(localToUsdPair);
          Ticker inverseBitcoinToUsdTicker = exchange.get().getPollingMarketDataService().getTicker(bitcoinToUsdPair);

          // OER gives inverse values to reduce number of calculations
          BigDecimal inverseLocalToUsd = inverseLocalToUsdTicker.getLast();
          BigDecimal inverseBitcoinToUsd = inverseBitcoinToUsdTicker.getLast();

          // Conversion rate is inverse local divided by inverse Bitcoin
          BigDecimal conversionRate = inverseLocalToUsd.divide(inverseBitcoinToUsd, RoundingMode.HALF_EVEN);

          // Infer the ticker
          return Ticker.TickerBuilder.newInstance()
            .withLast(conversionRate)
              // All others are zero
            .withAsk(BigDecimal.ZERO)
            .withBid(BigDecimal.ZERO)
            .withHigh(BigDecimal.ZERO)
            .withLow(BigDecimal.ZERO)
            .withCurrencyPair(bitcoinToUsdPair)
            .withVolume(BigDecimal.ONE)
            .build();
        }

        private Ticker getEmptyTicker() throws IOException {

          log.debug("Empty ticker");

          CurrencyPair directPair = new CurrencyPair(exchangeBaseCode, exchangeCounterCode);

          // Infer the ticker
          return Ticker.TickerBuilder.newInstance()
            .withLast(BigDecimal.ZERO)
              // All others are zero
            .withAsk(BigDecimal.ZERO)
            .withBid(BigDecimal.ZERO)
            .withHigh(BigDecimal.ZERO)
            .withLow(BigDecimal.ZERO)
            .withCurrencyPair(directPair)
            .withVolume(BigDecimal.ONE)
            .build();
        }

      });

  }

  /**
   * @return All the currencies supported by the exchange
   */
  public ListenableFuture<String[]> allCurrencies() {
    if (allCurrenciesExecutorService == null) {
      allCurrenciesExecutorService = SafeExecutors.newSingleThreadExecutor("all-currencies");
    }

    return allCurrenciesExecutorService.submit(
      new Callable<String[]>() {
        @Override
        public String[] call() throws Exception {

          if (ExchangeKey.NONE.equals(exchangeKey)) {
            return new String[]{"BTC"};
          }

          Locale currentLocale = Configurations.currentConfiguration.getLocale();

          // This may involve a call to the exchange or not
          Collection<CurrencyPair> currencyPairs;
          try {
            // Use dynamic lookup (may result in null or SSL failures)
            currencyPairs = exchange.get().getPollingMarketDataService().getExchangeSymbols();
          } catch (SSLHandshakeException e) {
            // Inform the user of a serious problem with current certificates
            CoreEvents.fireEnvironmentEvent(EnvironmentSummary.newCertificateFailed());
            // Trigger the failure handler
            throw new IllegalStateException(e.getMessage(), e);
          }

          // Fail fast
          if (currencyPairs == null || currencyPairs.isEmpty()) {
            return new String[]{};
          }

          // Must have at least one currency pair to be here

          SortedSet<String> allCurrencies = Sets.newTreeSet();
          for (CurrencyPair currencyPair : currencyPairs) {

            // Add the currency (if non-BTC we can triangulate through USD)
            String baseCode = currencyPair.baseSymbol;
            String counterCode = currencyPair.counterSymbol;

            // Ignore any malformed currency pairs
            if (baseCode == null || counterCode == null) {
              continue;
            }

            // Make any adjustments
            counterCode = CurrencyUtils.isoCandidateFor(counterCode);

            try {
              // Use JVM to determine if currency is in ISO 4217
              Currency base = Currency.getInstance(baseCode);
              if (base != null) {
                // Use JVM to provide translated name
                String localName = Currency.getInstance(baseCode).getDisplayName(currentLocale);
                allCurrencies.add(baseCode + " (" + localName + ")");
              }
            } catch (IllegalArgumentException e) {
              // Base code is not in ISO 4217 so attempt to locate counter currency (e.g. BTC/RUR)
              try {
                // Use JVM to determine supported currency
                Currency counter = Currency.getInstance(counterCode);
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