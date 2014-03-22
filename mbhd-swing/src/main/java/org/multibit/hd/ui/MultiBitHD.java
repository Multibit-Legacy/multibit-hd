package org.multibit.hd.ui;

import com.google.common.base.Optional;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.exceptions.PaymentsLoadException;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.platform.GenericApplicationFactory;
import org.multibit.hd.ui.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.platform.listener.GenericOpenURIEvent;
import org.multibit.hd.ui.views.MainView;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  private static File applicationDataDirectory;

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

    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        log.debug("MultiBit launched with args[{}]: '{}'", i, args[i]);
      }
    }

    // Prepare the JVM (Nimbus, system properties etc)
    initialiseJVM();

    // Prepare platform-specific integration (protocol handlers, quit events etc)
    initialiseGenericApp();

    // Start core services (security alerts, configuration etc)
    initialiseCore(args);

    // Create a new UI based on the configuration
    initialiseUI(args);

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
   * <p>Depends on nothing - it is first</p>
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
        log.error("No look and feel available. MultiBit HD requires Java 7 or higher.", e1);
        System.exit(-1);
      }
    }

    // Set any bespoke system properties
    try {
      // Fix for Windows / Java 7 / VPN bug
      System.setProperty("java.net.preferIPv4Stack", "true");

      // Fix for version.txt not visible for Java 7
      System.setProperty("jsse.enableSNIExtension", "false");

      // Ensure the correct name is displayed in the application menu
      if (OSUtils.isMac()) {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "multiBit HD");
      }

    } catch (SecurityException se) {
      log.error(se.getClass().getName() + " " + se.getMessage());
    }

  }

  /**
   * <p>Initialise the core services</p>
   * <p>Depends on the JVM and generic application being in place</p>
   *
   * @param args The command line arguments
   */
  private static void initialiseCore(String[] args) {

    // Start the core services
    CoreServices.main(args);

    // Pre-loadContacts sound library
    Sounds.initialise();

    // Locate the application data directory
    applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Start the wallet service
    startWalletService(applicationDataDirectory);

  }

  /**
   * <p>Initialise the UI once all the core services are in place</p>
   * <p>This creates the singleton views and controllers that respond to configuration
   * and theme changes</p>
   */
  private static void initialiseUI(final String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure that we are using the configured theme
        ThemeKey themeKey = ThemeKey.valueOf(Configurations.currentConfiguration.getApplicationConfiguration().getCurrentTheme());
        Themes.switchTheme(themeKey.theme());

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
          log.debug("The current wallet is:\nWallet id = '" + walletData.getWalletId().toString() + "\n" + walletData.getWallet().toString(false, false, false, null));

          mainView.setShowExitingPasswordWizard(true);

        } else {

          // No wallet - cold start
          log.debug("There is no current wallet so showing the 'WelcomeWizard'");
          mainView.setShowExitingWelcomeWizard(true);

        }

        mainView.refresh();

        overlaySecurityAlerts();

        initialiseGenericApp();

        overlayBitcoinUriAlerts(args);
      }

    });
  }

  /**
   * <p>Initialise the platform-specific services</p>
   * <p>Depends on the UI being in place</p>
   */
  private static void initialiseGenericApp() {

    GenericApplicationSpecification specification = new GenericApplicationSpecification();
    specification.getOpenURIEventListeners().add(mainController);
    specification.getPreferencesEventListeners().add(mainController);
    specification.getAboutEventListeners().add(mainController);
    specification.getQuitEventListeners().add(mainController);

    GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);

  }

  /**
   * <p>Initialise the UI support services</p>
   * <p>Depends on the Core services being in place</p>
   */
  private static void initialiseSupport() {

    // Continue building the support services in the background
    SafeExecutors.newFixedThreadPool(1).execute(new Runnable() {
      @Override
      public void run() {

        // Start the Bitcoin network service
        startBitcoinNetworkService();

        // Initialise backup
        BackupManager.INSTANCE.initialise(applicationDataDirectory, null);

      }
    });

  }

  /**
   * <p>Start the wallet service (core)</p>
   */
  private static void startWalletService(File applicationDataDirectory) {

    WalletManager.INSTANCE.initialise(applicationDataDirectory);

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
   * <p>Start the Bitcoin network service (support)</p>
   * <p>Requires the wallet manager and UI to be initialised</p>
   */
  private static void startBitcoinNetworkService() {

    // Start the bitcoin network service
    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();
    bitcoinNetworkService.start();

    //Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

    // If the network starts ok start downloading blocks to catch up with the current blockchain
    if (bitcoinNetworkService.isStartedOk()) {
      bitcoinNetworkService.downloadBlockChain();
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
   * <p>Show any Bitcoin URI alerts (UI)</p>
   */
  private static void overlayBitcoinUriAlerts(String[] args) {

    log.debug("Checking for Bitcoin URI on command line");

    // Check for a valid entry on the command line (protocol handler).
    if (args != null && args.length > 0) {
      processCommandLineURI(args[0]);
    } else {
      log.debug("No Bitcoin URI provided as an argument");
    }

  }

  /**
   * <p>Attempt to detect if the command line URI is valid.</p>
   * <p>Note that this is largely because IE6-8 strip URL encoding when passing in URIs to a protocol handler.
   * However, there is also the chance that anyone could hand-craft a URI and pass it in with non-ASCII character encoding present in the label.</p>
   * <p>This a really limited approach (no consideration of "amount=10.0&label=Black & White")
   * but should be OK for <a href="https://github.com/bitcoin/bips/blob/master/bip-0021.mediawiki">BIP21</a> use cases.</p>
   *
   * @param rawURI The raw URI from the command line (not validated until later)
   */
  private static void processCommandLineURI(String rawURI) {

    try {

      // Basic initial checking for URL encoding
      int queryParamIndex = rawURI.indexOf('?');
      if (queryParamIndex > 0 && !rawURI.contains("%")) {
        // Possibly encoded but more likely not
        String encodedQueryParams = URLEncoder.encode(rawURI.substring(queryParamIndex + 1), "UTF-8");
        rawURI = rawURI.substring(0, queryParamIndex) + "?" + encodedQueryParams;
        rawURI = rawURI.replaceAll("%3D", "=");
        rawURI = rawURI.replaceAll("%26", "&");
      }

      log.debug("Using '{}' to create Bitcoin URI", rawURI);
      final URI uri = URI.create(rawURI);

      // Wrap this in a generic event
      GenericOpenURIEvent event = new GenericOpenURIEvent() {
        @Override
        public URI getURI() {
          return uri;
        }
      };

      // Simulate this coming in from an external source
      mainController.onOpenURIEvent(event);

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    }
  }

}
