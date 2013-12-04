package org.multibit.hd.core.services;

import com.google.common.eventbus.EventBus;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.logging.LoggingFactory;

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

  /**
   * Send or register events to the user interface subscribers
   */
  public static final EventBus uiEventBus = new EventBus();

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
    new LoggingFactory(Configurations.currentConfiguration.getLoggingConfiguration(), "MultiBit HD").configure();



  }

  /**
   * @param exchangeClassName The exchange class name taken from the XChange library
   *
   * @return A new exchange service based on the current configuration
   */
  public static ExchangeTickerService newExchangeService(String exchangeClassName) {

    // Use the factory to get the exchange API using default settings
    final Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);

    return new ExchangeTickerService(exchange.getExchangeSpecification().getExchangeName(), exchange.getPollingMarketDataService());

  }

  /**
   * @return create a new BitcoinNetworkService for access to the Bitcoin network
   */
  public static BitcoinNetworkService newBitcoinNetworkService() {
    return new BitcoinNetworkService();
  }

}
