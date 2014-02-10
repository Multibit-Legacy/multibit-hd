package org.multibit.hd.core.services;

import com.google.common.eventbus.EventBus;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.logging.LoggingFactory;
import org.multibit.hd.core.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * Keep track of selected application events (e.g. exchange rate changes etc)
   */
  public static final ApplicationEventService applicationEventService;

  static {
    applicationEventService = new ApplicationEventService();
    uiEventBus.register(applicationEventService);
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

    // Load configuration
    Configurations.currentConfiguration = Configurations.readConfiguration();

    // Configure logging
    new LoggingFactory(Configurations.currentConfiguration.getLoggingConfiguration(), "MBHD").configure();

    if (OSUtils.isDebuggerAttached()) {
      // TODO Inform the user of the problem
      log.error("************************************************************************");
      log.error("* A debugger is attached. This is a security risk in normal operation. *");
      log.error("************************************************************************");
    }

  }

  /**
   * @param exchangeClassName The exchange class name taken from the XChange library
   *
   * @return A new exchange service based on the current configuration
   */
  public static ExchangeTickerService newExchangeService(String exchangeClassName) {

    // Use the factory to get the exchange API using default settings
    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

    // Update the configuration with the current exchange name
    Configurations
      .currentConfiguration
      .getBitcoinConfiguration()
      .setExchangeName(exchange.getExchangeSpecification().getExchangeName());

    return new ExchangeTickerService(exchange.getExchangeSpecification().getExchangeName(), exchange.getPollingMarketDataService());

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
   * @return The contact service for a wallet
   */
  public static ContactService getContactService(WalletId walletId) {

    return new ContactService(walletId);

  }

}
