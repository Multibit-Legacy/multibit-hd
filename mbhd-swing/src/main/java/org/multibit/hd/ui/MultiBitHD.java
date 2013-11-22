package org.multibit.hd.ui;

import org.multibit.hd.core.concurrency.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.logging.LoggingFactory;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.swing.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  // TODO Implement this
  private static GenericApplication genericApplication = null;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) {

    ExceptionHandler.registerExceptionHandler();

    // Start the logging factory
    LoggingFactory.bootstrap();

    // Load configuration
    Configurations.currentConfiguration = Configurations.readConfiguration();

    // Configure logging
    new LoggingFactory(Configurations.currentConfiguration.getLoggingConfiguration(), "MultiBit HD").configure();

        // Create the views
    final MainView mainView = new MainView();
    mainView.pack();
    mainView.setVisible(true);

    ScheduledExecutorService executorService1 = SafeExecutors.newSingleThreadScheduledExecutor();
    executorService1.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        // Force a failure
        mainView.updateBalance(new BigDecimal(System.currentTimeMillis() / 1000));
      }

    },1, 1, TimeUnit.SECONDS);

    ScheduledExecutorService executorService2 = SafeExecutors.newSingleThreadScheduledExecutor();
    executorService2.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        // Run normally
        mainView.updateBalance(new BigDecimal(((double) System.currentTimeMillis() / 1_000_000)));

        BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();

        String current = bitcoinConfiguration.getBitcoinSymbol();
        int ordinal = BitcoinSymbol.valueOf(current).ordinal();

        BitcoinSymbol[] all = BitcoinSymbol.class.getEnumConstants();
        ordinal++;
        ordinal = ordinal % all.length;

        // Swap the prefixing
        if (ordinal ==0) {
          Configurations.currentConfiguration.getI18NConfiguration().setCurrencySymbolPrefixed(
            !Configurations.currentConfiguration.getI18NConfiguration().isCurrencySymbolPrefixed()
          );
        }

        bitcoinConfiguration.setBitcoinSymbol(all[ordinal].name());

      }

    },5, 1, TimeUnit.SECONDS);

  }

  private void registerEventListeners() {

    // TODO Get this working

    log.info("Configuring native event handling");

  }
}
