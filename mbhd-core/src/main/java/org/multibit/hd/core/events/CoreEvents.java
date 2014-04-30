package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import org.joda.money.BigMoney;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.ExchangeSummary;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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

  private static final long CONSOLIDATION_INTERVAL = 1000; // milliseconds
  private static boolean waitingToFireSlowTransactionSeenEvent = false;
  private static final Object lockObject = new Object();

  /**
   * Utilities have a private constructor
   */
  private CoreEvents() {
  }

  /**
   * <p>Broadcast a new "exchange rate changed" event</p>
   *
   * @param rate         The rate in the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   * @param rateProvider The rate provider (e.g. "Bitstamp" or absent if unknown)
   * @param expires      The expiry timestamp of this rate
   */
  public static void fireExchangeRateChangedEvent(BigMoney rate, Optional<String> rateProvider, DateTime expires) {
    log.trace("Firing 'exchange rate changed' event");
    CoreServices.uiEventBus.post(new ExchangeRateChangedEvent(rate, rateProvider, expires));
  }

  /**
   * <p>Broadcast a new "exchange status changed" event</p>
   *
   * @param exchangeSummary The exchange summary
   */
  public static void fireExchangeStatusChangedEvent(ExchangeSummary exchangeSummary) {
    log.trace("Firing 'exchange status changed' event");
    CoreServices.uiEventBus.post(new ExchangeStatusChangedEvent(exchangeSummary));
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
    consolidateTransactionSeenEvents();
  }

  /**
   * Consolidate many transactionSeenEvents into a single call per (slow)time interval
   */
  private static void consolidateTransactionSeenEvents() {
    synchronized (lockObject) {
      if (!waitingToFireSlowTransactionSeenEvent) {
        // Fire in the future
        waitingToFireSlowTransactionSeenEvent = true;
        SafeExecutors.newSingleThreadScheduledExecutor("tx-seen").schedule(new Callable() {
          @Override
          public Object call() throws Exception {
            CoreServices.uiEventBus.post(new SlowTransactionSeenEvent());
            synchronized (lockObject) {
              waitingToFireSlowTransactionSeenEvent = false;
            }
            return null;
          }
        }, CONSOLIDATION_INTERVAL, TimeUnit.MILLISECONDS);
      }
    }
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
   * <p>Broadcast a new "security" event</p>
   *
   * @param securitySummary The security summary
   */
  public static void fireSecurityEvent(SecuritySummary securitySummary) {
    log.trace("Firing 'security' event");
    CoreServices.uiEventBus.post(new SecurityEvent(securitySummary));
  }

  /**
   * <p>Broadcast a new "history changed" event</p>
   *
   * @param historyEntry The history entry from the History service
   */
  public static void fireHistoryChangedEvent(HistoryEntry historyEntry) {
    log.trace("Firing 'history changed' event");
    CoreServices.uiEventBus.post(new HistoryChangedEvent(historyEntry));
  }

  /**
   * <p>Broadcast a new "shutdown" event</p>
   *
   * @param shutdownType The shutdown type
   */
  public static void fireShutdownEvent(ShutdownEvent.ShutdownType shutdownType) {
    log.info("Firing 'shutdown' event: {}", shutdownType);
    CoreServices.uiEventBus.post(new ShutdownEvent(shutdownType));

    // Use Core services to handle any finalisation
    CoreServices.shutdown(shutdownType);
  }

  /**
   * <p>Broadcast a new "configuration changed" event</p>
   */
  public static void fireConfigurationChangedEvent() {
    log.trace("Firing 'configuration changed' event");
    CoreServices.uiEventBus.post(new ConfigurationChangedEvent());
  }

  public static void fireExportPerformedEvent(ExportPerformedEvent exportPerformedEvent) {
    log.trace("Firing 'export performed' event");
    CoreServices.uiEventBus.post(exportPerformedEvent);
  }

}
