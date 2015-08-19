package org.multibit.hd.ui;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.commons.concurrent.SafeExecutors;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.logging.LoggingFactory;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.controllers.HeaderController;
import org.multibit.hd.ui.controllers.MainController;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.platform.GenericApplicationFactory;
import org.multibit.hd.ui.platform.GenericApplicationSpecification;
import org.multibit.hd.ui.services.ExternalDataListeningService;
import org.multibit.hd.ui.views.MainView;
import org.multibit.hd.ui.views.SplashScreen;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>Main entry point to the application</p>
 */
public class MultiBitHD {

  //////////////////////////// NO STATIC VARIABLES ALLOWED IN THIS CLASS! ////////////////////////

  // See initialiseSystemProperties for explanation

  // Special case logger that is not static
  private final Logger log = LoggerFactory.getLogger(MultiBitHD.class);
  private MainController mainController;
  private SplashScreen splashScreen;

  ////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * <p>Main entry point to the application</p>
   *
   * @param args None specified
   */
  public static void main(String[] args) throws Exception {

    // These are called at the first line to avoid any other class loading
    // interfering with them
    initialiseSystemProperties();

    // Hand over to an instance to simplify FEST tests
    final MultiBitHD multiBitHD = new MultiBitHD();
    if (!multiBitHD.start(args)) {
      // Failed to start so issue a hard shutdown
      CoreServices.shutdownNow(ShutdownEvent.ShutdownType.HARD);
    } else {
      // Initialise the UI views in the EDT, Nimbus etc
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            multiBitHD.initialiseUIViews();
          }
        });
    }
  }

  /**
   * Initialise any system properties that need to be in place before any other
   * classes are loaded
   */
  private static void initialiseSystemProperties() {

    // Fix for "TimSort" clipboard failure - https://github.com/bitcoin-solutions/multibit-hd/issues/645
    // Suggested by https://www.java.net/node/700601
    // See also http://stackoverflow.com/a/26829874/396747 for more details on ordering at startup
    // Verified that java.util.Arrays has not been loaded at this stage
    System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

    // Fix for Windows / Java 7 / VPN bug
    System.setProperty("java.net.preferIPv4Stack", "true");

    // Fix for version.txt not visible for Java 7
    System.setProperty("jsse.enableSNIExtension", "false");

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

    // Start the logging factory (see later for instance) to get console logging up fast
    LoggingFactory.bootstrap();

    // Get the configuration fast (Bitcoin URI processing relies on it)
    CoreServices.bootstrap();

    // Analyse the command line
    if (args != null && args.length > 0) {
      // Show the command line arguments
      for (int i = 0; i < args.length; i++) {
        log.info("MultiBit launched with args[{}]: '{}'", i, args[i]);
      }
    } else {
      // Provide empty arguments to avoid potential NPEs
      log.info("No command line arguments");
      args = new String[]{};
    }

    // Check for another instance as soon as possible
    Optional<ExternalDataListeningService> externalDataListeningService = initialiseListeningService(args);
    if (!externalDataListeningService.isPresent()) {
      return false;
    }

    log.info("This is the primary instance so showing splash screen.");
    // Provide an AWT splash screen to ensure faster initial rendering
    splashScreen = new SplashScreen();
    // Provide a short thread sleep to allow the AWT time to take over
    // and fill the Frame with the splash screen image
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // Do nothing
    }

    // Prepare the CA certs (run on a separate thread)
    initialiseCaCerts();

    // Start core services (logging, environment alerts, configuration, Bitcoin URI handling etc)
    initialiseCore(args);

    // Create controllers so that the generic app can access listeners
    if (!initialiseUIControllers()) {

      // Required to shut down
      return false;

    }

    // Must be OK to be here
    return true;
  }

  public void stop() {
    log.debug("Stopping MultiBit HD");

    mainController = null;

    // final purge in case anything gets missed
    ViewEvents.unsubscribeAll();
    ControllerEvents.unsubscribeAll();
    CoreEvents.unsubscribeAll();
  }

  /**
   * @param args The command line arguments
   *
   * @return The external data listening service if it could be started successfully, absent implies another instance
   */
  private Optional<ExternalDataListeningService> initialiseListeningService(String[] args) {
    // Determine if another instance is running and shutdown if this is the case
    ExternalDataListeningService externalDataListeningService = new ExternalDataListeningService(args);

    if (externalDataListeningService.start()) {
      return Optional.of(externalDataListeningService);
    }

    // Must have failed to be here
    return Optional.absent();
  }

  /**
   * <p>Initialise the CA certs</p>
   */
  @SuppressFBWarnings({"DM_EXIT"})
  private void initialiseCaCerts() throws Exception {

    log.debug("Initialising CA certs...");

    try {

      // Execute the CA certificates download on a separate thread to avoid slowing
      // the startup time
      SafeExecutors.newSingleThreadExecutor("install-cacerts").submit(
        new Runnable() {
          @Override
          public void run() {
            HttpsManager.INSTANCE.installCACertificates(
              InstallationManager.getOrCreateApplicationDataDirectory(),
              InstallationManager.CA_CERTS_NAME,
              null, // Use default host list
              false // Do not force loading if they are already present
            );
          }
        });
    } catch (SecurityException se) {
      log.error("Security exception: {} {}", se.getClass().getName(), se.getMessage());
    }
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
  private boolean initialiseUIControllers() {
    if (OSUtils.isWindowsXPOrEarlier()) {
      log.error("Windows XP or earlier detected. Forcing shutdown.");
      JOptionPane.showMessageDialog(
        null, "This version of Windows is not supported for security reasons.\nPlease upgrade.", "Error",
        JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // Including the other controllers avoids dangling references during a soft shutdown
    mainController = new MainController(
      new HeaderController()
    );

    // Start the hardware wallet support to allow credentials screen to be selected
    mainController.handleHardwareWallets();

    // Set the tooltip delay to be slightly longer
    ToolTipManager.sharedInstance().setInitialDelay(1000);

    // Must be OK to be here
    return true;
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
  @SuppressFBWarnings({"DM_EXIT"})
  public MainView initialiseUIViews() {
    log.debug("Initialising UI...");

    Preconditions.checkNotNull(mainController, "'mainController' must be present. FEST will cause this if another instance is running.");
    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must execute on EDT. Check calling environment.");

    // Give MultiBit Hardware a chance to process any attached hardware wallet
    // and for MainController to subsequently process the events
    // The delay observed in reality and FEST tests ranges from 1400-2200ms and if
    // not included results in wiped hardware wallets being missed on startup
    final Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
    log.debug("Starting the clock for hardware wallet initialisation");
    long hardwareInitialisationTime = System.currentTimeMillis();

    // Perform time consuming tasks to use the hardware initialisation time to best effect
    // Prepare platform-specific integration (protocol handlers, quit events etc)
    initialiseGenericApp();

    // Pre-load sound library
    Sounds.initialise();

    try {
      // Set look and feel (expect ~1000ms to perform this)
      log.debug("Loading Nimbus LaF...");
      UIManager.setLookAndFeel(new NimbusLookAndFeel() {

        // Require configurable error feedback through beeps
        @Override
        public void provideErrorFeedback(Component component) {

          if (Configurations.currentConfiguration.getSound().isAlertSound()) {
            super.provideErrorFeedback(component);
          }

        }
      });
    } catch (UnsupportedLookAndFeelException e) {
      try {
        log.warn("Falling back to cross platform LaF...");
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
        log.error("No look and feel available. MultiBit HD requires Java 7 or higher.", e1);
        System.exit(-1);
      }

    }
    log.debug("LaF loaded OK");

    // This must be performed immediately after the LaF has been set
    if (OSUtils.isMac()) {
      log.debug("Applying OSX key bindings...");
      // Ensure the correct name is displayed in the application menu
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "multiBit HD");

      // Ensure OSX key bindings are used for copy, paste etc
      // Use the Nimbus keys and ensure this occurs before any component creation
      addOSXKeyStrokes((InputMap) UIManager.get("EditorPane.focusInputMap"));
      addOSXKeyStrokes((InputMap) UIManager.get("FormattedTextField.focusInputMap"));
      addOSXKeyStrokes((InputMap) UIManager.get("PasswordField.focusInputMap"));
      addOSXKeyStrokes((InputMap) UIManager.get("TextField.focusInputMap"));
      addOSXKeyStrokes((InputMap) UIManager.get("TextPane.focusInputMap"));
      addOSXKeyStrokes((InputMap) UIManager.get("TextArea.focusInputMap"));
    }

    // Ensure that we are using the configured theme (must be after key bindings)
    log.debug("Switching theme...");
    ThemeKey themeKey = ThemeKey.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentTheme());
    Themes.switchTheme(themeKey.theme());

    log.debug("Building MainView...");

    // Build a new MainView
    final MainView mainView = new MainView();
    mainController.setMainView(mainView);

    log.debug("Checking for pre-existing wallets...");

    // Check for any pre-existing wallets in the application directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    List<File> walletDirectories = WalletManager.findWalletDirectories(applicationDataDirectory);
    List<WalletSummary> softWalletSummaries = WalletManager.getSoftWalletSummaries(Optional.of(Configurations.currentConfiguration.getLocale()));

    // Check for fresh install
    boolean noWallets = walletDirectories.isEmpty();
    boolean noSoftWallets = softWalletSummaries.isEmpty();
    boolean unacceptedLicence = !Configurations.currentConfiguration.isLicenceAccepted();

    // HardwareWalletService needs HARDWARE_INITIALISATION_TIME milliseconds to initialise so sleep the rest
    conditionallySleep(hardwareInitialisationTime);

    boolean deviceAttached = false;
    boolean deviceWiped = false;

    // Check hardware wallet situation after initialisation
    if (hardwareWalletService.isPresent()) {

      if (hardwareWalletService.get().isDeviceReady()) {

        deviceAttached = true;

        if (!hardwareWalletService.get().isWalletPresent()) {

          log.debug("Wiped hardware wallet detected");

          // Must show the welcome wizard in hardware wallet mode
          // regardless of wallet or licence situation
          // MainController should have handled the events
          deviceWiped = true;
        }
      }
    }

    // Determine if welcome wizard should show
    boolean showWelcomeWizard = unacceptedLicence // Always prompt for a licence
      || (deviceAttached && deviceWiped) // We have a wiped hardware wallet so need to initialise
      || (noSoftWallets && !deviceAttached) // No soft wallets and no hardware wallet so need to create/restore one
      || noWallets; // No wallets at all so need to create/restore one (either hard or soft)

    if (showWelcomeWizard) {
      log.debug("Showing the welcome wizard");
      mainView.setShowExitingWelcomeWizard(true);
      mainView.setShowExitingCredentialsWizard(false);
    } else {
      log.debug("Showing the credentials wizard");
      mainView.setShowExitingCredentialsWizard(true);
      mainView.setShowExitingWelcomeWizard(false);
    }

    // Provide a backdrop to the user and trigger the showing of the wizard
    mainView.refresh(false);

    log.debug("MainView is ready - hide the splash screen");
    if (splashScreen != null) {
      splashScreen.dispose();
    }

    // See the MainController wizard hide event for the next stage

    return mainView;
  }

  /**
   * <p>Initialise the platform-specific services</p>
   */
  private void initialiseGenericApp() {
    GenericApplicationSpecification specification = new GenericApplicationSpecification();
    specification.getOpenURIEventListeners().add(mainController);
    specification.getOpenFilesEventListeners().add(mainController);
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
  }

  /**
   * <p>Apply OSX key strokes to input map for consistent UX</p>
   *
   * @param inputMap The input map for the Swing component
   */
  private void addOSXKeyStrokes(InputMap inputMap) {
    // Undo and redo require more complex handling
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK), DefaultEditorKit.copyAction);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
  }

  /**
   * Allow a delay of HARDWARE_INITIALISATION_TIME from the startTime
   *
   * @param startTime The reference time from which to measure the amount of sleep from
   */
  private void conditionallySleep(long startTime) {

    // Check if Trezor is required
    if (!Configurations.currentConfiguration.isTrezor()) {
      return;
    }

    final long HARDWARE_INITIALISATION_TIME = 2000;  // milliseconds
    long currentTime = System.currentTimeMillis();
    long timeSpent = currentTime - startTime;

    if (timeSpent < HARDWARE_INITIALISATION_TIME) {
      long sleepFor = HARDWARE_INITIALISATION_TIME - timeSpent;
      log.debug("Sleep for an extra {} milliseconds to allow hardwareWalletService to initialise", sleepFor);
      Uninterruptibles.sleepUninterruptibly(sleepFor, TimeUnit.MILLISECONDS);
      log.debug("Finished sleep");
    } else {
      log.debug("No need for extra sleep time to allow hardwareWalletService to initialise");
    }

  }
}
