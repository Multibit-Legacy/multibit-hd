package org.multibit.hd.core.events;

import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast core events</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class CoreEvents {

  private static final Logger log = LoggerFactory.getLogger(CoreEvents.class);

  /**
   * Utilities have a private constructor
   */
  private CoreEvents() {
  }

  /**
   * <p>Broadcast a new "exchange rate changed" event</p>
   *
   * @param rate         The rate in the local currency (e.g. USD)
   * @param exchangeName The exchange name (e.g. "Bitstamp")
   */
  public static void fireExchangeRateChangedEvent(BigDecimal rate, String exchangeName) {

    log.debug("Firing 'exchange rate changed' event");
    CoreServices.uiEventBus.post(new ExchangeRateChangedEvent(rate, exchangeName));

  }

  /**
   * <p>Broadcast a new "Bitcoin network changed" event</p>
   *
   * @param bitcoinNetworkSummary The Bitcoin network summary
   */
  public static void fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary bitcoinNetworkSummary) {

    log.debug("Firing 'Bitcoin network changed' event : " + bitcoinNetworkSummary.getPercent() + "%.");
    CoreServices.uiEventBus.post(new BitcoinNetworkChangedEvent(bitcoinNetworkSummary));

  }

  /**
   * <p>Broadcast a new "shutdown" event</p>
   */
  public static void fireShutdownEvent() {

    log.debug("Firing 'shutdown' event");
    CoreServices.uiEventBus.post(new ShutdownEvent());

  }
}
