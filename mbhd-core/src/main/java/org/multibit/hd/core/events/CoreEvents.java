package org.multibit.hd.core.events;

import org.joda.money.BigMoney;
import org.joda.time.DateTime;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * @param rate         The rate in the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   * @param exchangeName The exchange name (e.g. "Bitstamp")
   * @param expires      The expiry timestamp of this rate
   */
  public static void fireExchangeRateChangedEvent(BigMoney rate, String exchangeName, DateTime expires) {

    log.debug("Firing 'exchange rate changed' event");
    CoreServices.uiEventBus.post(new ExchangeRateChangedEvent(rate, exchangeName, expires));

  }

  /**
   * <p>Broadcast BitcoinSentEvent</p>
   *
   * @param bitcoinSentEvent containing send information
   */
  public static void fireBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {

    log.debug("Firing 'bitcoin sent event' event");
    CoreServices.uiEventBus.post(bitcoinSentEvent);

  }
  /**
   * <p>Broadcast a new "Bitcoin network changed" event</p>
   *
   * @param bitcoinNetworkSummary The Bitcoin network summary
   */
  public static void fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary bitcoinNetworkSummary) {
    String message = "Firing 'Bitcoin network changed' event";
    if (bitcoinNetworkSummary.getPercent() > 0) {
      message = message + " : " + bitcoinNetworkSummary.getPercent() + "%.";
    }
    log.debug(message);
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
