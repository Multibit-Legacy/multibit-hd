package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.xeiam.xchange.Exchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.logging.LoggingFactory;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to configured instances of Core services</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CoreServices {

  private static final Logger log = LoggerFactory.getLogger(CoreServices.class);

  /**
   * Send or register events to the user interface subscribers
   */
  public static final EventBus uiEventBus = new EventBus();

  /**
   * Keep track of selected application events (e.g. exchange rate changes, security alerts etc)
   */
  public static final ApplicationEventService applicationEventService;

  /**
   * Keep track of security events (e.g. debugger, file permissions etc) across all wallets
   */
  public static final SecurityCheckingService securityCheckingService;

  static {

    // Order is important here
    applicationEventService = new ApplicationEventService();
    securityCheckingService = new SecurityCheckingService();

  }

  /**
   * Keeps track of all the contact services against hard and soft wallets
   */
  private static final Map<WalletId, ContactService> contactServiceMap = Maps.newHashMap();

  /**
   * Keeps track of all the history services against hard and soft wallets
   */
  private static final Map<WalletId, HistoryService> historyServiceMap = Maps.newHashMap();

  /**
   * Utilities have a private constructor
   */
  private CoreServices() {
  }

  /**
   * <p>Initialises the core services, and can act as an independent starting point for headless operations</p>
   *
   * @param args Any command line arguments
   */
  public static void main(String[] args) {

    // Start the logging factory
    LoggingFactory.bootstrap();

    // Load configuration
    Configurations.currentConfiguration = Configurations.readConfiguration();

    // Configure logging
    new LoggingFactory(Configurations.currentConfiguration.getLoggingConfiguration(), "MBHD").configure();

    if (OSUtils.isDebuggerAttached()) {

      CoreEvents.fireSecurityEvent(SecuritySummary.newDebuggerAttached());

      log.error("************************************************************************");
      log.error("* A debugger is attached. This is a security risk in normal operation. *");
      log.error("************************************************************************");

    }

  }

  /**
   * @param exchangeKey The exchange key providing the required information
   *
   * @return A new exchange service based on the current configuration
   */
  public static ExchangeTickerService newExchangeService(ExchangeKey exchangeKey) {

    // Use the factory to get the exchange API using default settings
    final Exchange exchange = exchangeKey.getExchange();

    return new ExchangeTickerService(exchangeKey.getExchangeName(), exchange.getPollingMarketDataService());

  }

  /**
   * @return Create a new BitcoinNetworkService for access to the Bitcoin network
   */
  public static BitcoinNetworkService newBitcoinNetworkService() {
    return new BitcoinNetworkService();
  }

  /**
   * @return Create a new WalletService for wallet specific functionality
   */
  public static WalletService newWalletService() {
    return new WalletService();
  }

  /**
   * @return Create a new seed phrase generator
   */
  public static SeedPhraseGenerator newSeedPhraseGenerator() {
    return new Bip39SeedPhraseGenerator();
  }

  /**
   * @return The application event service singleton
   */
  public static ApplicationEventService getApplicationEventService() {

    return applicationEventService;

  }

  /**
   * @return The security checking service singleton
   */
  public static SecurityCheckingService getSecurityCheckingService() {
    return securityCheckingService;
  }

  /**
   * @param walletIdOptional The optional wallet ID, if absent an EmptyHistoryService will be returned (usually for testing)
   *
   * @return The history service for a wallet
   */
  public static HistoryService getOrCreateHistoryService(Optional<WalletId> walletIdOptional) {

    Preconditions.checkNotNull(walletIdOptional, "'walletIdOptional' must be present");

    if (!walletIdOptional.isPresent()) {
      // No walletId loaded yet - return an EmptyContactService
      return new EmptyHistoryService();
    } else {
      WalletId walletId = walletIdOptional.get();
      // Check if the history service has been created for this wallet ID
      if (!historyServiceMap.containsKey(walletId)) {
        historyServiceMap.put(walletId, new PersistentHistoryService(walletId));
      }

      // Return the existing or new history service
      return historyServiceMap.get(walletId);
    }
  }

  /**
   * @param walletIdOptional The optional wallet ID, if absent an EmptyContactService will be returned
   *
   * @return The contact service for a wallet
   */
  public static ContactService getOrCreateContactService(Optional<WalletId> walletIdOptional) {

    Preconditions.checkNotNull(walletIdOptional, "'walletIdOptional' must be present");

    if (!walletIdOptional.isPresent()) {
      // No walletId loaded yet - return an EmptyContactService
      return new EmptyContactService();
    } else {
      WalletId walletId = walletIdOptional.get();
      // Check if the contact service has been created for this wallet ID
      if (!contactServiceMap.containsKey(walletId)) {
        contactServiceMap.put(walletId, new PersistentContactService(walletId));
      }

      // Return the existing or new contact service
      return contactServiceMap.get(walletId);
    }
  }

  /**
   * <p>Convenience method to log a new history event for the current wallet</p>
   *
   * @param localisedDescription The localised description text
   */
  public static void logHistory(String localisedDescription) {

    HistoryService historyService = CoreServices.getOrCreateHistoryService(
      Optional.of(WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId())
    );

    // Create the history entry and persist it
    HistoryEntry historyEntry = historyService.newHistoryEntry(localisedDescription);
    historyService.writeHistory();

    // OK to let everyone else know
    CoreEvents.fireHistoryChangedEvent(historyEntry);

  }
}
