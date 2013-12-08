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
import org.multibit.hd.core.events.ExchangeRateChangeEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeTickerServiceTest {

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

    ExchangeTickerService testObject = new ExchangeTickerService("Mt Gox", pollingMarketDataService);

    CoreServices.uiEventBus.register(this);

    testObject.start();

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    assertThat(receivedTickerUpdate).isTrue();
  }

  @Subscribe
  public void onTickerUpdateEvent(ExchangeRateChangeEvent exchangeRateChangeEvent) {

    receivedTickerUpdate = true;

  }


}
