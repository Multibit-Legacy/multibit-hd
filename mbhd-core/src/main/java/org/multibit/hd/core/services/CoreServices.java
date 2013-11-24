package org.multibit.hd.core.services;

import com.google.common.eventbus.EventBus;
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
  public static ExchangeService newExchangeService(String exchangeClassName) {

    // TODO Link this in to the configuration system
    return new ExchangeService(exchangeClassName);

  }

}
