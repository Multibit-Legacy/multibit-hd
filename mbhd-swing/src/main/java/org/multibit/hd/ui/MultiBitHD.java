package org.multibit.hd.ui;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.platform.GenericApplication;
import org.multibit.hd.ui.views.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  // TODO Implement this
  private static GenericApplication genericApplication = null;

  private static BitcoinNetworkService bitcoinNetworkService;

  private static WalletService walletService;

  private static MainController mainController;
  private static MainView mainView;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) throws InterruptedException, UnsupportedLookAndFeelException {

    // Prepare the JVM (Nimbus etc)
    initialiseJVM();

    // Start core services (wallet manager, security alerts, configuration etc)
    initialiseCore(args);

    // Create a new UI based on the configuration
    initialiseUI();

    // Start supporting services (wizards, exchange, wallet access etc)
    initialiseSupport();

  }

  /**
   * @return The Bitcoin network service for the UI
   */
  public static BitcoinNetworkService getBitcoinNetworkService() {
    return bitcoinNetworkService;
  }

  /**
   * @return The wallet service for the UI
   */
  public static WalletService getWalletService() {
    return walletService;
  }

  public static void setWalletService(WalletService newWalletService) {
    walletService = newWalletService;
  }

  /**
   * <p>Initialise the JVM. This occurs before anything else is called.</p>
   */
  private static void initialiseJVM() {

    // Although we guarantee the JVM through the packager it is possible that
    // a power user will use their own
    try {
      // We guarantee the JVM through the packager so we should try it first
      UIManager.setLookAndFeel(new NimbusLookAndFeel());
    } catch (UnsupportedLookAndFeelException e) {
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
        log.error("No look and feel available.", e1);
        System.exit(-1);
      }
    }

  }

  /**
   * <p>Initialise the core services before the UI fires up</p>
   *
   * @param args The command line arguments
   */
  private static void initialiseCore(String[] args) {

    // Start the core services
    CoreServices.main(args);

    // Pre-loadContacts sound library
    Sounds.initialise();

    if (OSUtils.isMac()) {
      System.getProperties().setProperty("com.apple.mrj.application.apple.menu.about.name", Languages.safeText(MessageKey.APPLICATION_TITLE));
    }

    startWalletService();

  }

  /**
   * <p>Initialise the UI once all the core services are in place</p>
   * <p>This creates the singleton views and controllers that respond to configuration
   * and theme changes</p>
   */
  private static void initialiseUI() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Build the main view
        mainView = new MainView();

        // Create controllers
        mainController = new MainController();
        new HeaderController();
        new SidebarController();

        // Check for a current wallet
        if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {

          // There is a wallet present - warm start
          WalletData walletData = WalletManager.INSTANCE.getCurrentWalletData().get();
          log.debug("The current wallet is:\nWallet id = '" + walletData.getWalletId().toString() + "\n" + walletData.getWallet().toString());

          mainView.setShowExitingPasswordWizard(true);

        } else {

          // No wallet - cold start
          log.debug("There is no current wallet so showing the 'WelcomeWizard'");
          mainView.setShowExitingWelcomeWizard(true);

        }

        mainView.refresh();

        overlaySecurityAlerts();

      }

    });
  }

  /**
   * <p>Initialise the UI support services</p>
   */
  private static void initialiseSupport() {

    // Continue building the support services in the background
    SafeExecutors.newFixedThreadPool(1).execute(new Runnable() {
      @Override
      public void run() {

        startExchangeService();

        startBitcoinNetworkService();

      }
    });

  }

  /**
   * <p>Start the wallet service (core)</p>
   */
  private static void startWalletService() {

    // Initialise the wallet manager, which will load the current wallet if available
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    WalletManager.INSTANCE.initialise(applicationDataDirectory);
    BackupManager.INSTANCE.initialise(applicationDataDirectory, null);

    if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {

      // Initialise the WalletService, which provides transaction information from the wallet
      walletService = CoreServices.newWalletService();
      try {
        walletService.initialise(applicationDataDirectory, WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId());
      } catch (PaymentsLoadException ple) {
        // Payments db did not load  TODO tell user or abort ??
        log.error(ple.getClass().getCanonicalName() + "" + ple.getMessage());
      }

      // Create the history service for this wallet to catch any system events
      CoreServices.getOrCreateHistoryService(Optional.of(WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId()));
    }
  }

  /**
   * <p>Show any security alerts (UI)</p>
   */
  private static void overlaySecurityAlerts() {

    // Catch up with any early security events
    Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();

    if (securityEvent.isPresent()) {
      mainController.onSecurityEvent(securityEvent.get());
    }

  }

  /**
   * <p>Start the exchange service (support)</p>
   */
  private static void startExchangeService() {

    ExchangeKey exchangeKey = ExchangeKey.valueOf(Configurations.currentConfiguration.getBitcoinConfiguration().getExchangeKey());
    ExchangeTickerService exchangeTickerService = CoreServices.newExchangeService(ExchangeKey.BITSTAMP);
    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    // Start up the exchange service
    exchangeTickerService.start();

  }

  /**
   * <p>Start the Bitcoin network service (support)</p>
   */
  private static void startBitcoinNetworkService() {

    // Start the bitcoin network service
    bitcoinNetworkService.start();

    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

    // If the network starts ok start downloading blocks to catch up with the current blockchain
    if (bitcoinNetworkService.isStartedOk()) {
      bitcoinNetworkService.downloadBlockChain();
    }

  }

  /**
   * TODO Integrate the generic application structure for BitcoinURI support
   */
  private void registerEventListeners() {

    log.info("Configuring native event handling");

  }

}
