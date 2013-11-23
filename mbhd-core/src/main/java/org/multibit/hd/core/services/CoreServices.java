package org.multibit.hd.core.services;

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
   * @return The currently selected exchange service
   */
  public static ExchangeService newExchangeService() {

    // TODO Link this in to the configuration system
    return new ExchangeService();

  }


}
