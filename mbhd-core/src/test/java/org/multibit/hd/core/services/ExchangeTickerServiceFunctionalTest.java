package org.multibit.hd.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeTickerServiceFunctionalTest {

  private boolean receivedTickerUpdate = false;

  private PollingMarketDataService pollingMarketDataService = mock(PollingMarketDataService.class);

  @Before
  public void setUp() throws IOException {

    Ticker ticker = Ticker.TickerBuilder
      .newInstance()
      .withCurrencyPair(CurrencyPair.BTC_USD)
      .withBid(BigDecimal.ZERO)
      .withAsk(BigDecimal.ZERO)
      .withLast(BigDecimal.ZERO)
      .withHigh(BigDecimal.ZERO)
      .withLow(BigDecimal.ZERO)
      .withTimestamp(new Date())
      .withVolume(BigDecimal.ZERO).build();

    when(pollingMarketDataService.getTicker(CurrencyPair.BTC_USD)).thenReturn(ticker);

  }

  @Test
  public void testStart() throws Exception {

    // Use Bitstamp for functional testing
    BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();
    bitcoinConfiguration.setCurrentExchange(ExchangeKey.BITSTAMP.name());
    bitcoinConfiguration.setLocalCurrencyCode("USD");

    ExchangeTickerService testObject = new ExchangeTickerService(bitcoinConfiguration);

    CoreEvents.subscribe(this);

    testObject.start();

    // Allow time for the exchange to respond
    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);

    assertThat(receivedTickerUpdate).isTrue();
  }

  @Subscribe
  public void onTickerUpdateEvent(ExchangeRateChangedEvent exchangeRateChangeEvent) {

    receivedTickerUpdate = true;

  }


}
