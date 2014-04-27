package org.multibit.hd.ui;

import com.google.common.base.Optional;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.controllers.SidebarController;
import org.multibit.hd.ui.platform.GenericApplicationFactory;
import org.multibit.hd.ui.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.services.BitcoinURIListeningService;
import org.multibit.hd.ui.views.MainView;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.File;
import java.util.List;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  private static final Logger log = LoggerFactory.getLogger(MultiBitHD.class);

  private MainController mainController;

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(final String[] args) throws Exception {

    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        log.debug("MultiBit launched with args[{}]: '{}'", i, args[i]);
      }
    }

    // Hand over to an instance to simplify FEST tests
    final MultiBitHD multiBitHD = new MultiBitHD();
    if (!multiBitHD.start(args)) {

      // Failed to start so issue a hard shutdown
      multiBitHD.stop(ShutdownEvent.ShutdownType.HARD);

    } else {

      // Initialise the UI views in the EDT
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          multiBitHD.initialiseUIViews();

        }
      });

    }


  }

  /**
   * <p>Start this instance of MultiBit HD</p>
   *
   * @param args The command line arguments
   *
   * @return True if the application started successfully, false if a shutdown is required
   *
   * @throws Exception If something goes wrong
   */
  public boolean start(String[] args) throws Exception {

    // Prepare the JVM (Nimbus, system properties etc)
    initialiseJVM();

    // Create controllers so that the generic app can access listeners
    if (!initialiseUIControllers(args)) {

      // Required to shut down
      return false;

    }

    // Prepare platform-specific integration (protocol handlers, quit events etc)
    initialiseGenericApp();

    // Start core services (logging, security alerts, configuration, Bitcoin URI handling etc)
    initialiseCore(args);

    // Must be OK to be here
    return true;
  }

  /**
   * @param shutdownType The shutdown type to use
   */
  public void stop(ShutdownEvent.ShutdownType shutdownType) {

    CoreEvents.fireShutdownEvent(shutdownType);

  }

  /**
   * <p>Initialise the UI controllers once all the core services are in place</p>
   * <p>This creates the singleton controllers that respond to generic events</p>
   * <p>At this stage none of the following will be running:</p>
   * <ul>
   * <li>Themes or views</li>
   * <li>Wallet service</li>
   * <li>Backup service</li>
   * <li>Bitcoin network service</li>
   * </ul>
   */
  public boolean initialiseUIControllers(String[] args) {

    // Determine if another instance is running and shutdown if this is the case
    BitcoinURIListeningService bitcoinURIListeningService = new BitcoinURIListeningService(args);
    if (!bitcoinURIListeningService.start()) {
      return false;
    }

    if (OSUtils.isWindowsXPOrEarlier()) {
      log.error("Windows XP or earlier detected. Forcing shutdown.");
      JOptionPane.showMessageDialog(null, "This version of Windows is not supported for security reasons.\nPlease upgrade.", "Error",
        JOptionPane.ERROR_MESSAGE);
      return false;
    }

    mainController = new MainController(bitcoinURIListeningService);
    new HeaderController();
    new SidebarController();

    // Must be OK to be here
    return true;

  }

  /**
   * <p>Initialise the JVM. This occurs before anything else is called.</p>
   */
  private void initialiseJVM() throws Exception {

    log.debug("Initialising JVM...");

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

    // Configure SSL certificates without forcing
    final File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    SSLManager.INSTANCE.installMultiBitSSLCertificate(applicationDirectory, "multibit-cacerts", false);

  }

  /**
   * <p>Initialise the platform-specific services</p>
   */
  private void initialiseGenericApp() {

    GenericApplicationSpecification specification = new GenericApplicationSpecification();
    specification.getOpenURIEventListeners().add(mainController);
    specification.getPreferencesEventListeners().add(mainController);
    specification.getAboutEventListeners().add(mainController);
    specification.getQuitEventListeners().add(mainController);

    GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);

  }

  /**
   * <p>Initialise the core services</p>
   *
   * @param args The command line arguments
   */
  private void initialiseCore(String[] args) {

    log.debug("Initialising Core...");

    // Start the core services
    CoreServices.main(args);

    // Pre-loadContacts sound library
    Sounds.initialise();

  }

  /**
   * <p>Initialise the UI once all the core services are in place</p>
   * <p>This creates the singleton views and controllers that respond to configuration
   * and theme changes</p>
   * <p>At this stage none of the following will be running:</p>
   * <ul>
   * <li>Wallet service</li>
   * <li>Backup service</li>
   * <li>Bitcoin network service</li>
   * </ul>
   * <p>Once the UI renders, control passes to the <code>MainController</code> to
   * respond to the wizard close event which will trigger ongoing initialisation.</p>
   */
  public MainView initialiseUIViews() {

    log.debug("Initialising UI...");

    // Ensure that we are using the configured theme
    ThemeKey themeKey = ThemeKey.valueOf(Configurations.currentConfiguration.getApplication().getCurrentTheme());
    Themes.switchTheme(themeKey.theme());

    // Build the main view
    MainView mainView = new MainView();
    mainController.setMainView(mainView);

    // Check for any pre-existing wallets in the application directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    List<File> walletDirectories = WalletManager.findWalletDirectories(applicationDataDirectory);

    if (walletDirectories.isEmpty()) {

      log.debug("No wallets in the directory - showing the welcome wizard");
      mainView.setShowExitingWelcomeWizard(true);
      mainView.setShowExitingPasswordWizard(false);

    } else {

      log.debug("Wallets are present - showing the password wizard");
      mainView.setShowExitingPasswordWizard(true);
      mainView.setShowExitingWelcomeWizard(false);

    }

    // Provide a backdrop to the user and trigger the showing of the wizard
    mainView.refresh();

    // Catch up with any early security events
    Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();

    if (securityEvent.isPresent()) {
      mainController.onSecurityEvent(securityEvent.get());
    }

    // See the MainController wizard hide event for the next stage

    return mainView;

  }
}
