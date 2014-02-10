package org.multibit.hd.core.events;

import org.joda.money.BigMoney;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
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

    log.trace("Firing 'exchange rate changed' event");
    CoreServices.uiEventBus.post(new ExchangeRateChangedEvent(rate, exchangeName, expires));

  }

  /**
   * <p>Broadcast TransactionCreationEvent</p>
   *
   * @param transactionCreationEvent containing transaction creation information
   */
  public static void fireTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {

    log.trace("Firing 'transactionCreation' event");
    CoreServices.uiEventBus.post(transactionCreationEvent);

  }

  /**
   * <p>Broadcast BitcoinSentEvent</p>
   *
   * @param bitcoinSentEvent containing send information
   */
  public static void fireBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {

    log.trace("Firing 'bitcoin sent' event");
    CoreServices.uiEventBus.post(bitcoinSentEvent);
  }

  /**
   * <p>Broadcast TransactionSeenEvent</p>
   *
   * @param transactionSeenEvent containing transaction information
   */
  public static void fireTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {

    CoreServices.uiEventBus.post(transactionSeenEvent);

  }

  /**
   * <p>Broadcast a new "Bitcoin network changed" event</p>
   *
   * @param bitcoinNetworkSummary The Bitcoin network summary
   */
  public static void fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary bitcoinNetworkSummary) {

    if (bitcoinNetworkSummary.getPercent() > 0) {
      log.trace("Firing 'Bitcoin network changed' event: {}%", bitcoinNetworkSummary.getPercent());
    } else {
      log.trace("Firing 'Bitcoin network changed' event");
    }

    CoreServices.uiEventBus.post(new BitcoinNetworkChangedEvent(bitcoinNetworkSummary));
  }

  /**
   * <p>Broadcast a new "shutdown" event</p>
   */
  public static void fireShutdownEvent() {

    log.trace("Firing 'shutdown' event");
    CoreServices.uiEventBus.post(new ShutdownEvent());

  }
}
