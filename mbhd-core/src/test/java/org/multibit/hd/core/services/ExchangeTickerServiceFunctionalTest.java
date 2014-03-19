package org.multibit.hd.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeTickerServiceFunctionalTest {

  private boolean receivedTickerUpdate = false;

  private PollingMarketDataService pollingMarketDataService = mock(PollingMarketDataService.class);

  @Before
  public void setUp() throws IOException {

    Ticker ticker = Ticker.TickerBuilder
      .newInstance()
      .withTradableIdentifier("BTC")
      .withBid(BigMoney.zero(CurrencyUnit.USD))
      .withAsk(BigMoney.zero(CurrencyUnit.USD))
      .withLast(BigMoney.zero(CurrencyUnit.USD))
      .withHigh(BigMoney.zero(CurrencyUnit.USD))
      .withLow(BigMoney.zero(CurrencyUnit.USD))
      .withTimestamp(new Date())
      .withVolume(BigDecimal.ZERO).build();

    when(pollingMarketDataService.getTicker(Currencies.BTC, Currencies.USD)).thenReturn(ticker);

  }

  @Test
  public void testStart() throws Exception {

    // Use Bitstamp for functional testing
    BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();
    bitcoinConfiguration.setCurrentExchange(ExchangeKey.BITSTAMP.name());
    bitcoinConfiguration.setLocalCurrencyUnit(CurrencyUnit.USD);

    ExchangeTickerService testObject = new ExchangeTickerService(bitcoinConfiguration);

    CoreServices.uiEventBus.register(this);

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
