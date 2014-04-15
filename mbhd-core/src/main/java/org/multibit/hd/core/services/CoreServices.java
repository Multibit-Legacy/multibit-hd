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
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.events.CoreEvents;
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

    Optional<Configuration> configuration;
    try (InputStream is = new FileInputStream(InstallationManager.getOrCreateConfigurationFile())) {
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
   * <p>Typically called after a ShutdownEvent is broadcast, this method waits a short time and then issues
   * the <code>System.exit()</code> call to finalise all threads.</p>
   */
  public static void shutdown() {

    SafeExecutors.newFixedThreadPool(1, "shutdown").execute(new Runnable() {
      @Override
      public void run() {

        // Provide a short delay while modules deal with the ShutdownEvent
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

        log.info("Issuing system exit");
        System.exit(0);

      }
    });

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
    //log.debug("Get application service");
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

    // Always expect a current wallet for a history entry
    WalletData walletData = WalletManager.INSTANCE.getCurrentWalletData().get();

    HistoryService historyService = CoreServices.getOrCreateHistoryService(walletData.getWalletId());

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

    log.debug("Get or create history service");

    Optional<WalletData> walletData = WalletManager.INSTANCE.getCurrentWalletData();

    Preconditions.checkState(walletData.isPresent(), "'walletData' must be present");

    WalletId walletId = walletData.get().getWalletId();

    return getOrCreateWalletService(walletId);

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
