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

  private static final EventBus eventBus = new EventBus();

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


  /**
   * @param event The event to post
   */
  public static void postEvent(Object event) {
    eventBus.post(event);
  }

  /**
   * @param subscriber An event subscriber with zero or more annotated methods
   */
  public static void registerEventSubscriber(Object subscriber) {
    eventBus.register(subscriber);
  }
}
