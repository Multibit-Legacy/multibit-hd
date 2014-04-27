package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Uninterruptibles;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.exceptions.CoreException;
import org.multibit.hd.core.logging.LoggingFactory;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
   * The URL of the live matcher daemon
   */
  public static final String LIVE_MATCHER_URL = "http://localhost:9090/brit";

  // TODO these should point to the multibit.org with the real matcher key
  /**
   * The live matcher PGP public key file
   */
  public static final String LIVE_MATCHER_PUBLIC_KEY_FILE = "multibit-org-matcher-key.asc";

  /**
   * Send or register events to the user interface subscribers
   */
  public static EventBus uiEventBus = new EventBus();

  /**
   * Keep track of selected application events (e.g. exchange rate changes, security alerts etc)
   */
  private static ApplicationEventService applicationEventService;

  /**
   * Keep track of security events (e.g. debugger, file permissions etc) across all wallets
   */
  private static SecurityCheckingService securityCheckingService;

  /**
   * Keep track of shutdown events and ensure the configuration is persisted
   */
  private static ConfigurationService configurationService;

  /**
   * Keep track of the Bitcoin network
   */
  private static BitcoinNetworkService bitcoinNetworkService;

  /**
   * Keeps track of all the contact services against hard and soft wallets
   */
  private static Map<WalletId, ContactService> contactServiceMap = Maps.newHashMap();

  /**
   * Keeps track of all the wallet services against hard and soft wallets
   */
  private static Map<WalletId, WalletService> walletServiceMap = Maps.newHashMap();

  /**
   * Keeps track of all the history services against hard and soft wallets
   */
  private static Map<WalletId, HistoryService> historyServiceMap = Maps.newHashMap();

  static {

    // Order is important here
    applicationEventService = new ApplicationEventService();
    securityCheckingService = new SecurityCheckingService();
    configurationService = new ConfigurationService();

  }

  private static ContactService currentContactService;

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

    log.debug("Loading configuration...");

    Optional<Configuration> configuration;
    try (InputStream is = new FileInputStream(InstallationManager.getConfigurationFile())) {
      // Load configuration (providing a default if none exists)
      configuration = Configurations.readConfiguration(is, Configuration.class);
    } catch (IOException e) {
      configuration = Optional.absent();
    }

    if (configuration.isPresent()) {
      log.warn("Using current configuration");
      Configurations.currentConfiguration = configuration.get();
    } else {
      log.warn("Using default configuration");
      Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    }

    // Configure logging
    new LoggingFactory(Configurations.currentConfiguration.getLogging(), "MBHD").configure();

    if (OSUtils.isDebuggerAttached()) {

      CoreEvents.fireSecurityEvent(SecuritySummary.newDebuggerAttached());

      log.error("************************************************************************");
      log.error("* A debugger is attached. This is a security risk in normal operation. *");
      log.error("************************************************************************");

    }

  }

  /**
   * <p>Typically called directly after a ShutdownEvent is broadcast.</p>
   * <p>Depending on the shutdown type this method will trigger a <code>System.exit(0)</code> to ensure graceful termination.</p></p>
   *
   * @param shutdownType The
   */
  public static synchronized void shutdown(final ShutdownEvent.ShutdownType shutdownType) {

    switch (shutdownType) {
      case HARD:
        SafeExecutors.newFixedThreadPool(1, "hard-shutdown").execute(new Runnable() {
          @Override
          public void run() {

            log.info("Applying hard shutdown. Waiting for processes to clean up...");

            // Provide a short delay while modules deal with the ShutdownEvent
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            log.info("Issuing system exit");
            System.exit(0);
          }
        });
        break;
      case SOFT:
        SafeExecutors.newFixedThreadPool(1, "soft-shutdown").execute(new Runnable() {
          @Override
          public void run() {

            log.info("Applying soft shutdown. Waiting for processes to clean up...");

            // Provide a short delay while modules deal with the ShutdownEvent
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

            log.info("Resetting services and events");

            // Reset the existing services
            bitcoinNetworkService = null;
            contactServiceMap = Maps.newHashMap();
            walletServiceMap = Maps.newHashMap();
            historyServiceMap = Maps.newHashMap();

            // Reset the event handler
            uiEventBus = new EventBus();

            // Suggest a garbage collection
            System.gc();
          }
        });
        break;
      case STANDBY:
        break;
    }


  }

  /**
   * @param bitcoinConfiguration The Bitcoin configuration providing exchange and currency details
   *
   * @return A new exchange service based on the current configuration
   */
  public static ExchangeTickerService newExchangeService(BitcoinConfiguration bitcoinConfiguration) {
    log.debug("Creating new exchange ticker service");
    return new ExchangeTickerService(bitcoinConfiguration);

  }

  /**
   * @return Create a new seed phrase generator
   */
  public static SeedPhraseGenerator newSeedPhraseGenerator() {
    log.debug("Creating new BIP39 seed phrase generator");
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
    log.debug("Get security checking service");
    return securityCheckingService;
  }

  /**
   * @return The Bitcoin network service
   */
  public static synchronized BitcoinNetworkService getOrCreateBitcoinNetworkService() {
    log.debug("Get Bitcoin network service");
    if (bitcoinNetworkService == null) {
      bitcoinNetworkService = new BitcoinNetworkService();
    }
    return bitcoinNetworkService;
  }

  /**
   * <p>Convenience method to log a new history event for the current wallet</p>
   *
   * @param localisedDescription The localised description text
   */
  public static void logHistory(String localisedDescription) {

    // Get the current history service
    HistoryService historyService = CoreServices.getCurrentHistoryService();

    // Create the history entry and persist it
    HistoryEntry historyEntry = historyService.newHistoryEntry(localisedDescription);
    historyService.writeHistory();

    // OK to let everyone else know
    CoreEvents.fireHistoryChangedEvent(historyEntry);

  }

  /**
   * @return The wallet service for the current wallet
   */
  public static WalletService getCurrentWalletService() {

    log.debug("Get current wallet service");

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    Preconditions.checkState(currentWalletSummary.isPresent(), "'currentWalletSummary' must be present. No wallet is open.");

    WalletId walletId = currentWalletSummary.get().getWalletId();

    return getOrCreateWalletService(walletId);

  }

  /**
   * @return The contact service for the current wallet
   */
  public static ContactService getCurrentContactService() {

    log.debug("Get current contact service");

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    Preconditions.checkState(currentWalletSummary.isPresent(), "'currentWalletSummary' must be present. No wallet is open.");

    WalletId walletId = currentWalletSummary.get().getWalletId();

    return getOrCreateContactService(walletId);
  }

  /**
   * @return The history service for the current wallet
   */
  public static HistoryService getCurrentHistoryService() {

    log.debug("Get current history service");

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    Preconditions.checkState(currentWalletSummary.isPresent(), "'currentWalletSummary' must be present. No wallet is open.");

    WalletId walletId = currentWalletSummary.get().getWalletId();

    return getOrCreateHistoryService(walletId);
  }

  /**
   * @return The wallet service for a wallet (single soft, multiple hard)
   */
  public static WalletService getOrCreateWalletService(WalletId walletId) {

    log.debug("Get or create history service");

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    // Check if the wallet service has been created for this wallet ID
    if (!walletServiceMap.containsKey(walletId)) {
      File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      WalletService walletService = new WalletService();
      walletService.initialise(applicationDirectory, walletId);
      walletServiceMap.put(walletId, walletService);
    }

    // Return the existing or new wallet service
    return walletServiceMap.get(walletId);

  }


  /**
   * @return The history service for a wallet (single soft, multiple hard)
   */
  public static HistoryService getOrCreateHistoryService(WalletId walletId) {

    log.debug("Get or create history service");

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    // Check if the history service has been created for this wallet ID
    if (!historyServiceMap.containsKey(walletId)) {
      historyServiceMap.put(walletId, new PersistentHistoryService(walletId));
    }

    // Return the existing or new history service
    return historyServiceMap.get(walletId);

  }

  /**
   * @param walletId The wallet ID for the wallet
   *
   * @return The contact service for a wallet
   */
  public static ContactService getOrCreateContactService(WalletId walletId) {

    log.debug("Get or create contact service");

    Preconditions.checkNotNull(walletId, "'walletId' must be present");

    // Check if the contact service has been created for this wallet ID
    if (!contactServiceMap.containsKey(walletId)) {
      contactServiceMap.put(walletId, new PersistentContactService(walletId));
    }

    // Return the existing or new contact service
    return contactServiceMap.get(walletId);
  }

  /**
   * @return A BRIT fee service pointing to the live Matcher machine
   */
  public static FeeService createFeeService() throws CoreException {
    log.debug("Create fee service");

    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream pgpPublicKeyInputStream = classloader.getResourceAsStream(LIVE_MATCHER_PUBLIC_KEY_FILE);

    try {
      PGPPublicKey matcherPublicKey = PGPUtils.readPublicKey(pgpPublicKeyInputStream);
      URL matcherURL = new URL(LIVE_MATCHER_URL);

      // Return the existing or new fee service
      return new FeeService(matcherPublicKey, matcherURL);
    } catch (Exception e) {
      throw new CoreException(e);
    }
  }
}
