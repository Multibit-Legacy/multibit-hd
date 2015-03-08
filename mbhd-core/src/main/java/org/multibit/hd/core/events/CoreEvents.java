package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.ExchangeSummary;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast core events</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CoreEvents {

  private static final Logger log = LoggerFactory.getLogger(CoreEvents.class);

  private static boolean waitingToFireSlowTransactionSeenEvent = false;
  private static final Object lockObject = new Object();

  // Provide a CoreEvent thread pool to ensure non-UI events are isolated from the EDT
  private static ListeningExecutorService eventExecutor;

  // Provide a slower transaction seen thread that is isolated from the EDT
  private static ListeningScheduledExecutorService txSeenExecutor;

  /**
   * Use Guava to handle subscribers to events
   */
  private static final EventBus coreEventBus = new EventBus(ExceptionHandler.newSubscriberExceptionHandler());

  /**
   * Keep track of the Guava event bus subscribers for a clean shutdown
   */
  private static final Set<Object> coreEventBusSubscribers = Sets.newHashSet();

  /**
   * Utilities have a private constructor
   */
  private CoreEvents() {
  }

  /**
   * Initialise the executor services as late as possible (requires 800ms)
   */
  public static void initialise() {

    eventExecutor = SafeExecutors.newFixedThreadPool(10, "core-events");
    txSeenExecutor = SafeExecutors.newSingleThreadScheduledExecutor("tx-seen");

  }

  /**
   * <p>Subscribe to events. Repeating a subscribe will not affect the event bus.</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   *
   * @param subscriber The subscriber (use the Guava <code>@Subscribe</code> annotation to subscribe a method)
   */
  public static void subscribe(Object subscriber) {

    Preconditions.checkNotNull(subscriber, "'subscriber' must be present");

    if (coreEventBusSubscribers.add(subscriber)) {
      log.trace("Register: " + subscriber.getClass().getSimpleName());
      try {
        coreEventBus.register(subscriber);
      } catch (IllegalArgumentException e) {
        log.warn("Unexpected failure to register");
      }
    } else {
      log.warn("Subscriber already registered: " + subscriber.getClass().getSimpleName());
    }

  }

  /**
   * <p>Unsubscribe a known subscriber from events. Providing an unknown object will not affect the event bus.</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   *
   * @param subscriber The subscriber (use the Guava <code>@Subscribe</code> annotation to subscribe a method)
   */
  public static void unsubscribe(Object subscriber) {

    Preconditions.checkNotNull(subscriber, "'subscriber' must be present");

    if (coreEventBusSubscribers.contains(subscriber)) {
      log.trace("Unregister: " + subscriber.getClass().getSimpleName());
      try {
        coreEventBus.unregister(subscriber);
      } catch (IllegalArgumentException e) {
        log.warn("Unexpected failure to unregister");
      }
      coreEventBusSubscribers.remove(subscriber);
    } else {
      log.warn("Subscriber already unregistered: " + subscriber.getClass().getSimpleName());
    }

  }

  /**
   * <p>Unsubscribe all subscribers from events</p>
   * <p>This approach ensures all subscribers will be correctly removed during a shutdown or wizard hide event</p>
   */
  @SuppressWarnings("unchecked")
  public static void unsubscribeAll() {

    Set allSubscribers = Sets.newHashSet();
    allSubscribers.addAll(coreEventBusSubscribers);
    for (Object subscriber : allSubscribers) {
      unsubscribe(subscriber);
    }
    allSubscribers.clear();
    log.info("All subscribers removed");

  }

  /**
   * <p>Broadcast a new "exchange rate changed" event</p>
   *
   * @param rate         The rate in the local currency against Bitcoin (e.g. "1000" means 1000 local = 1 bitcoin)
   * @param currency     The local currency
   * @param rateProvider The rate provider (e.g. "Bitstamp" or absent if unknown)
   * @param expires      The expiry timestamp of this rate
   */
  public static void fireExchangeRateChangedEvent(
    final BigDecimal rate,
    final Currency currency,
    final Optional<String> rateProvider,
    final DateTime expires
  ) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          ExchangeRateChangedEvent event = new ExchangeRateChangedEvent(rate, currency, rateProvider, expires);
          coreEventBus.post(event);
          log.debug("Firing 'exchange rate changed' event: {}", event);
        }
      });

  }

  /**
   * <p>Broadcast a new "exchange status changed" event</p>
   *
   * @param exchangeSummary The exchange summary
   */
  public static void fireExchangeStatusChangedEvent(final ExchangeSummary exchangeSummary) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'exchange status changed' event");
          coreEventBus.post(new ExchangeStatusChangedEvent(exchangeSummary));
        }
      });

  }

  /**
   * <p>Broadcast TransactionCreationEvent</p>
   *
   * @param transactionCreationEvent containing transaction creation information
   */
  public static void fireTransactionCreationEvent(final TransactionCreationEvent transactionCreationEvent) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'transactionCreation' event");
          coreEventBus.post(transactionCreationEvent);
        }
      });

  }

  /**
   * <p>Broadcast BitcoinSentEvent</p>
   *
   * @param bitcoinSentEvent containing send information
   */
  public static void fireBitcoinSentEvent(final BitcoinSentEvent bitcoinSentEvent) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'bitcoin sent' event");
          coreEventBus.post(bitcoinSentEvent);
        }
      });
  }
  /**
    * <p>Broadcast BitcoinSendingEvent</p>
    *
    * @param bitcoinSendingEvent containing send information
    */
   public static void fireBitcoinSendingEvent(final BitcoinSendingEvent bitcoinSendingEvent) {

     eventExecutor.submit(
       new Runnable() {
         @Override
         public void run() {
           log.trace("Firing 'bitcoin sending' event");
           coreEventBus.post(bitcoinSendingEvent);
         }
       });
   }

  /**
   * <p>Broadcast WalletLoadEvent</p>
   *
   * @param walletLoadEvent containing walletLoad information
   */
  public static void fireWalletLoadEvent(final WalletLoadEvent walletLoadEvent) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'walletLoadEvent' event");
          coreEventBus.post(walletLoadEvent);
        }
      });
  }

  /**
   * Broadcast ChangePasswordResultEvent
   */
  public static void fireChangePasswordResultEvent(final ChangePasswordResultEvent changePasswordResultEvent) {

    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'change password result' event");
          coreEventBus.post(changePasswordResultEvent);
        }
      });
  }

  /**
   * <p>Broadcast TransactionSeenEvent</p>
   *
   * @param transactionSeenEvent containing transaction information
   */
  public static void fireTransactionSeenEvent(final TransactionSeenEvent transactionSeenEvent) {

    // Use the tx-seen pool
    txSeenExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          coreEventBus.post(transactionSeenEvent);
          consolidateTransactionSeenEvents();
        }
      });
  }
  /**
   * <p>Broadcast BitcoinSendProgressEvent</p>
   *
   * @param bitcoinSendProgressEvent containing transaction broadcast progress information
   */
  public static void fireBitcoinSendProgressEvent(final BitcoinSendProgressEvent bitcoinSendProgressEvent) {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          coreEventBus.post(bitcoinSendProgressEvent);
        }
      });
  }

  /**
   * Consolidate many transactionSeenEvents into a single call per (slow) time interval
   */
  private static void consolidateTransactionSeenEvents() {

    synchronized (lockObject) {
      if (!waitingToFireSlowTransactionSeenEvent) {
        // Fire in the future
        waitingToFireSlowTransactionSeenEvent = true;
        txSeenExecutor.schedule(
          new Callable() {
            @Override
            public Object call() throws Exception {
              coreEventBus.post(new SlowTransactionSeenEvent());
              synchronized (lockObject) {
                waitingToFireSlowTransactionSeenEvent = false;
              }
              return null;
            }
          }, 1, TimeUnit.SECONDS);
      }
    }

  }

  /**
   * <p>Broadcast a new "Bitcoin network changed" event</p>
   *
   * @param bitcoinNetworkSummary The Bitcoin network summary
   */
  public static void fireBitcoinNetworkChangedEvent(final BitcoinNetworkSummary bitcoinNetworkSummary) {

    if (log.isTraceEnabled()) {
      if (bitcoinNetworkSummary.getPercent() > 0) {
        log.trace("Firing 'Bitcoin network changed' event: {}%", bitcoinNetworkSummary.getPercent());
      } else {
        log.trace("Firing 'Bitcoin network changed' event");
      }
    }

    coreEventBus.post(new BitcoinNetworkChangedEvent(bitcoinNetworkSummary));

  }

  /**
   * <p>Broadcast a new "security" event</p>
   *
   * @param securitySummary The security summary
   */
  public static void fireSecurityEvent(final SecuritySummary securitySummary) {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'security' event");
          coreEventBus.post(new SecurityEvent(securitySummary));
        }
      });
  }

  /**
   * <p>Broadcast a new "history changed" event</p>
   *
   * @param historyEntry The history entry from the History service
   */
  public static void fireHistoryChangedEvent(final HistoryEntry historyEntry) {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'history changed' event");
          coreEventBus.post(new HistoryChangedEvent(historyEntry));
        }
      });
  }

  /**
   * <p>Broadcast a new "shutdown" event</p>
   *
   * <p>Typically this is for SOFT shutdowns. A HARD shutdown should call <code>shutdownNow()</code> directly.</p>
   *
   * @param shutdownType The shutdown type
   */
  public static void fireShutdownEvent(final ShutdownEvent.ShutdownType shutdownType) {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.info("Firing 'shutdown' event: {}", shutdownType);
          coreEventBus.post(new ShutdownEvent(shutdownType));
        }
      });
  }

  /**
   * <p>Broadcast a new "configuration changed" event</p>
   */
  public static void fireConfigurationChangedEvent() {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'configuration changed' event");
          coreEventBus.post(new ConfigurationChangedEvent());
        }
      });
  }

  /**
   * <p>Broadcast a new "export performed" event</p>
   *
   * @param exportPerformedEvent The export performed event
   */
  public static void fireExportPerformedEvent(final ExportPerformedEvent exportPerformedEvent) {
    eventExecutor.submit(
      new Runnable() {
        @Override
        public void run() {
          log.trace("Firing 'export performed' event");
          coreEventBus.post(exportPerformedEvent);
        }
      });
  }

}
