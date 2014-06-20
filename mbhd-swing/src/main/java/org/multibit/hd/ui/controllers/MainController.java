package org.multibit.hd.ui.controllers;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.bitcoin.uri.BitcoinURIParseException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.*;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BackupService;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.*;
import org.multibit.hd.ui.services.BitcoinURIListeningService;
import org.multibit.hd.ui.views.MainView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Theme;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletState;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizardModel;
import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.password.PasswordState;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * <p>Controller for the main view </p>
 * <ul>
 * <li>Handles interaction between the model and the view</li>
 * </ul>
 * <p>To allow complete separation between Model, View and Controller all interactions are handled using application events</p>
 */
public class MainController extends AbstractController implements
  GenericOpenURIEventListener,
  GenericPreferencesEventListener,
  GenericAboutEventListener,
  GenericQuitEventListener {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private Optional<ExchangeTickerService> exchangeTickerService = Optional.absent();

  private Optional<BitcoinNetworkService> bitcoinNetworkService = Optional.absent();

  private final BitcoinURIListeningService bitcoinURIListeningService;

  final ListeningExecutorService handoverExecutorService = SafeExecutors.newSingleThreadExecutor("wizard-handover");

  // Keep track of other controllers for use after a preferences change
  private final HeaderController headerController;
  private final SidebarController sidebarController;

  // Main view may be replaced during a soft shutdown
  private MainView mainView;

  // Start with the assumption that it is fine to avoid annoying "everything is OK" alert
  private RAGStatus lastExchangeSeverity = RAGStatus.GREEN;

  /**
   * @param bitcoinURIListeningService The Bitcoin URI listening service (must be present to permit a UI)
   * @param headerController           The header controller
   * @param sidebarController          The sidebar controller
   */
  public MainController(
    BitcoinURIListeningService bitcoinURIListeningService,
    HeaderController headerController,
    SidebarController sidebarController
  ) {

    super();

    Preconditions.checkNotNull(bitcoinURIListeningService, "'bitcoinURIListeningService' must be present");
    Preconditions.checkNotNull(headerController, "'headerController' must be present");
    Preconditions.checkNotNull(sidebarController, "'sidebarController' must be present");

    this.bitcoinURIListeningService = bitcoinURIListeningService;
    this.headerController = headerController;
    this.sidebarController = sidebarController;

  }

  /**
   * @param mainView The current main view
   */
  public void setMainView(MainView mainView) {
    this.mainView = mainView;
  }

  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    switch (shutdownEvent.getShutdownType()) {
      case HARD:
      case SOFT:
        log.debug("Informing singletons (wallet, backup, installation)");
        if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
          CoreServices.getOrCreateWalletService(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId()).onShutdownEvent(shutdownEvent);
        }
        WalletManager.INSTANCE.onShutdownEvent(shutdownEvent);
        BackupManager.INSTANCE.onShutdownEvent(shutdownEvent);
        InstallationManager.onShutdownEvent(shutdownEvent);

        // Dispose of the main view and all its attendant references
        log.debug("Disposing of MainView");
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Panels.hideLightBoxIfPresent();
            Panels.applicationFrame.dispose();
          }
        });
        mainView = null;
        System.gc();

        break;
      case STANDBY:
        log.debug("Keeping application frame (standby).");
        Panels.hideLightBoxIfPresent();
        break;
    }

  }

  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    log.debug("Wizard hide: '{}'", event.getPanelName());

    if (!event.isExitCancel()) {

      // Successful wizard interaction

      if (WelcomeWizardState.CREATE_WALLET_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.RESTORE_WALLET_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.RESTORE_PASSWORD_REPORT.name().equals(event.getPanelName())
        ) {

        // Need to hand over to the password wizard
        handoverToPasswordWizard();

      }

      if (PasswordState.PASSWORD_ENTER_PASSWORD.name().equals(event.getPanelName())) {

        // Perform final initialisation
        hidePasswordWizard();

      }

      if (PasswordState.PASSWORD_RESTORE.name().equals(event.getPanelName())) {

        // Need to hand over to the welcome wizard
        handoverToWelcomeWizard();

      }

      if (EditWalletState.EDIT_WALLET.name().equals(event.getPanelName())) {

        // Update the sidebar name
        String walletName = ((EditWalletWizardModel) event.getWizardModel()).getWalletSummary().getName();
        hideEditWalletWizard(walletName);

      }

    } else {

      // Shift focus depending on what was cancelled
      hideAsExitCancel(event.getPanelName());

    }
  }

  /**
   * <p>Update all views to use the current configuration</p>
   *
   * @param event The change configuration event
   */
  @Subscribe
  public synchronized void onConfigurationChangedEvent(ConfigurationChangedEvent event) {

    log.debug("Received 'configuration changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");

    if (mainView.isShowExitingWelcomeWizard()) {

      log.debug("Using simplified view refresh (language change)");

      // Ensure the Swing thread can perform a complete refresh
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          // Switch the theme before any other UI building takes place
          handleTheme();

          // Rebuild MainView contents
          handleLocale();

          // Force a frame redraw
          Panels.applicationFrame.invalidate();

          // Rebuild the detail views and alert panels
          mainView.refresh();

        }
      });

    } else {

      log.debug("Using full view refresh (configuration change)");

      // Switch the exchange ticker service before the UI to ensure the
      // exchange rate provider is rendered correctly
      handleExchange();

      // Ensure the Swing thread can perform a complete refresh
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          // Switch the theme before any other UI building takes place
          handleTheme();

          // Rebuild MainView contents
          handleLocale();

          // Force a frame redraw
          Panels.applicationFrame.invalidate();

          // Rebuild the detail views and alert panels
          mainView.refresh();

          // Show the current detail screen
          Screen screen = Screen.valueOf(Configurations.currentConfiguration.getApplication().getCurrentScreen());
          ControllerEvents.fireShowDetailScreenEvent(screen);

          // Trigger the alert panels to refresh
          headerController.refresh();

        }
      });

      // Restart the Bitcoin network (may have switched parameters)
      handleBitcoinNetwork();
    }

  }

  @Subscribe
  public void onBitcoinNetworkChangeEvent(BitcoinNetworkChangedEvent event) {

    log.trace("Received 'Bitcoin network changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");
    Preconditions.checkNotNull(summary.getMessageKey(), "'errorKey' must be present");
    Preconditions.checkNotNull(summary.getMessageData(), "'errorData' must be present");

    final String localisedMessage;
    if (summary.getMessageKey().isPresent() && summary.getMessageData().isPresent()) {
      // There is a message key with data
      localisedMessage = Languages.safeText(summary.getMessageKey().get(), summary.getMessageData().get());
    } else if (summary.getMessageKey().isPresent()) {
      // There is a message key only
      localisedMessage = Languages.safeText(summary.getMessageKey().get());
    } else {
      // There is no message key so use the status only
      localisedMessage = summary.getStatus().name();
    }

    ViewEvents.fireProgressChangedEvent(localisedMessage, summary.getPercent());

    // Ensure everyone is aware of the update
    ViewEvents.fireSystemStatusChangedEvent(localisedMessage, summary.getSeverity());
  }

  @Subscribe
  public void onBackupWalletLoadedEvent(BackupWalletLoadedEvent event) {
    log.trace("Received 'backup wallet loaded' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getBackupLoaded(), "backup file must be present");

    final String localisedMessage = Languages.safeText(CoreMessageKey.BACKUP_WALLET_WAS_LOADED);

    ControllerEvents.fireAddAlertEvent(
      Models.newAlertModel(
        localisedMessage,
        RAGStatus.AMBER)
    );
  }

  @Subscribe
  public void onExchangeStatusChangeEvent(ExchangeStatusChangedEvent event) {

    log.trace("Received 'Exchange status changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    ExchangeSummary summary = event.getSummary();

    if (!lastExchangeSeverity.equals(summary.getSeverity())) {

      log.debug("Event severity has changed");

      Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");
      Preconditions.checkNotNull(summary.getMessageKey(), "'errorKey' must be present");
      Preconditions.checkNotNull(summary.getMessageData(), "'errorData' must be present");

      final String localisedMessage;
      if (summary.getMessageKey().isPresent() && summary.getMessageData().isPresent()) {
        // There is a message key with data
        localisedMessage = Languages.safeText(summary.getMessageKey().get(), summary.getMessageData().get());
      } else if (summary.getMessageKey().isPresent()) {
        // There is a message key only
        localisedMessage = Languages.safeText(summary.getMessageKey().get());
      } else {
        // There is no message key so use the status only
        localisedMessage = summary.getStatus().name();
      }

      // Action to show the "exchange settings" wizard
      AbstractAction action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

          ControllerEvents.fireRemoveAlertEvent();
          Panels.showLightBox(Wizards.newExchangeSettingsWizard().getWizardScreenHolder());

        }
      };
      JButton button = Buttons.newAlertPanelButton(action, MessageKey.SETTINGS, AwesomeIcon.CHECK);

      // Provide the alert
      ControllerEvents.fireAddAlertEvent(
        Models.newAlertModel(
          localisedMessage,
          summary.getSeverity(), button)
      );
    }

  }

  @Subscribe
  public void onSecurityEvent(SecurityEvent event) {

    log.trace("Received 'security' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    SecuritySummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");
    Preconditions.checkNotNull(summary.getMessageKey(), "'errorKey' must be present");
    Preconditions.checkNotNull(summary.getMessageData(), "'errorData' must be present");

    final String localisedMessage;
    if (summary.getMessageKey().isPresent() && summary.getMessageData().isPresent()) {
      // There is a message key with data
      localisedMessage = Languages.safeText(summary.getMessageKey().get(), summary.getMessageData().get());
    } else if (summary.getMessageKey().isPresent()) {
      // There is a message key only
      localisedMessage = Languages.safeText(summary.getMessageKey().get());
    } else {
      // There is no message key so use the status only
      localisedMessage = summary.getSeverity().name();
    }

    switch (summary.getAlertType()) {
      case DEBUGGER_ATTACHED:
      case BACKUP_FAILED:
        // Append general security advice allowing for LTR/RTL
        ControllerEvents.fireAddAlertEvent(
          Models.newAlertModel(
            localisedMessage + " " + Languages.safeText(CoreMessageKey.SECURITY_ADVICE),
            summary.getSeverity())
        );
        break;
      case CERTIFICATE_FAILED:
        // Create a button to the repair wallet tool
        JButton button = Buttons.newAlertPanelButton(getShowRepairWalletAction(), MessageKey.REPAIR, AwesomeIcon.MEDKIT);

        // Append general security advice allowing for LTR/RTL
        ControllerEvents.fireAddAlertEvent(
          Models.newAlertModel(
            localisedMessage + "\n" + Languages.safeText(CoreMessageKey.SECURITY_ADVICE),
            summary.getSeverity(),
            button)
        );
        break;
      default:
        throw new IllegalStateException("Unknown alert type: " + summary.getAlertType());
    }
  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    // Show the About screen
    Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());

  }

  @Subscribe
  @Override
  public void onOpenURIEvent(GenericOpenURIEvent event) {

    // Validate the data
    final BitcoinURI bitcoinURI;
    try {
      bitcoinURI = new BitcoinURI(event.getURI().toString());
    } catch (BitcoinURIParseException e) {
      // Quietly ignore
      return;
    }

    Optional<AlertModel> alertModel = Models.newBitcoinURIAlertModel(bitcoinURI);

    // If there is sufficient information in the Bitcoin URI display it to the user as an alert
    if (alertModel.isPresent()) {

      // Add the alert
      ControllerEvents.fireAddAlertEvent(alertModel.get());
    }

  }

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    // Show the Preferences screen
    ControllerEvents.fireShowDetailScreenEvent(Screen.SETTINGS);

  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    // Immediately shutdown without requesting confirmation
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);

  }

  /**
   * @return An action to show the "repair wallet" tool
   */
  private AbstractAction getShowRepairWalletAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRepairWalletWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * Handles the changes to the exchange ticker service
   */
  private void handleExchange() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();
    ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());

    if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
      if (bitcoinConfiguration.getExchangeApiKeys().containsKey(ExchangeKey.OPEN_EXCHANGE_RATES.name())) {
        String apiKey = Configurations.currentConfiguration.getBitcoin().getExchangeApiKeys().get(ExchangeKey.OPEN_EXCHANGE_RATES.name());
        exchangeKey.getExchange().get().getExchangeSpecification().setApiKey(apiKey);
      }
    }

    // Stop (with block) any existing exchange ticker service
    if (exchangeTickerService.isPresent()) {
      exchangeTickerService.get().stopAndWait();
    }

    // Create and start the exchange ticker service
    exchangeTickerService = Optional.of(CoreServices.newExchangeService(bitcoinConfiguration));
    exchangeTickerService.get().start();

  }

  /**
   * Handles the changes to the theme
   */
  private void handleTheme() {

    Theme newTheme = ThemeKey.valueOf(Configurations.currentConfiguration.getApplication().getCurrentTheme()).theme();
    Themes.switchTheme(newTheme);

  }

  /**
   * Handles the changes to the locale (resource bundle change, frame locale, component orientation etc)
   */
  private void handleLocale() {

    // Get the current locale
    Locale locale = Configurations.currentConfiguration.getLocale();

    log.debug("Setting application frame to locale '{}'", locale);

    // Ensure the resource bundle is reset
    ResourceBundle.clearCache();

    // Update the frame to allow for LTR or RTL transition
    Panels.applicationFrame.setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    Panels.applicationFrame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

  }

  /**
   * <p>Start the backup manager</p>
   */
  private void handleBackupManager() {

    // Locate the installation directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Initialise backup (must be before Bitcoin network starts and on the main thread)
    Optional<File> cloudBackupLocation = Optional.absent();
    if (Configurations.currentConfiguration != null) {
      String cloudBackupLocationString = Configurations.currentConfiguration.getApplication().getCloudBackupLocation();
      if (cloudBackupLocationString != null && !"".equals(cloudBackupLocationString)) {
        File cloudBackupLocationAsFile = new File(cloudBackupLocationString);
        if (cloudBackupLocationAsFile.exists()) {
          cloudBackupLocation = Optional.of(cloudBackupLocationAsFile);
        }
      }
    }

    BackupManager.INSTANCE.initialise(applicationDataDirectory, cloudBackupLocation);
    BackupService backupService = CoreServices.getOrCreateBackupService();
    backupService.start();

  }

  /**
   * <p>Show any command line Bitcoin URI alerts (UI)</p>
   */
  private void handleBitcoinURIAlert() {

    // Check for Bitcoin URI on the command line
    Optional<BitcoinURI> bitcoinURI = bitcoinURIListeningService.getBitcoinURI();

    if (bitcoinURI.isPresent()) {

      // Attempt to create an alert model from the Bitcoin URI
      Optional<AlertModel> alertModel = Models.newBitcoinURIAlertModel(bitcoinURI.get());

      // If successful the fire the event
      if (alertModel.isPresent()) {
        ControllerEvents.fireAddAlertEvent(alertModel.get());
      }

    }
  }

  /**
   * <p>Restart the Bitcoin network</p>
   */
  private void handleBitcoinNetwork() {

    // Only start the network once
    if (bitcoinNetworkService.isPresent()) {
      bitcoinNetworkService.get().stopAndWait();
    }

    bitcoinNetworkService = Optional.of(CoreServices.getOrCreateBitcoinNetworkService());

    // Start the network now that the password has been validated
    bitcoinNetworkService.get().start();

    if (bitcoinNetworkService.get().isStartedOk()) {
      bitcoinNetworkService.get().downloadBlockChainInBackground();
    }

  }

  private void hideAsExitCancel(String panelName) {

    // The exit dialog has no detail screen so focus defers to the sidebar
    if (ExitState.EXIT_CONFIRM.name().equals(panelName)) {
      mainView.sidebarRequestFocus();
    }

    // The detail screens do not have an intuitive way to capture focus
    // we rely on CTRL+TAB to relocate the focus with keyboard

  }

  private void hideEditWalletWizard(String walletName) {

    mainView.sidebarWalletName(walletName);

  }

  /**
   * Welcome wizard has created a new wallet so hand over to the password wizard for access
   */
  private void handoverToPasswordWizard() {

    log.debug("Hand over to password wizard");

    // Handover
    mainView.setShowExitingWelcomeWizard(false);
    mainView.setShowExitingPasswordWizard(true);

    // Use a new thread to handle the new wizard so that the handover can complete
    SafeExecutors.newSingleThreadExecutor("password-handover").execute(new Runnable() {
      @Override
      public void run() {

        // Allow time for the other wizard to finish hiding (200ms is sufficient)
        Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

        // Must execute on the EDT
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            Panels.hideLightBoxIfPresent();

            log.debug("Showing exiting password wizard after handover");
            Panels.showLightBox(Wizards.newExitingPasswordWizard().getWizardScreenHolder());

          }
        });

      }
    });

  }

  /**
   * Password wizard needs to perform a restore so hand over to the welcome wizard
   */
  private void handoverToWelcomeWizard() {

    log.debug("Hand over to welcome wizard");

    // Handover
    mainView.setShowExitingWelcomeWizard(true);
    mainView.setShowExitingPasswordWizard(false);

    // Use a new thread to handle the new wizard so that the handover can complete
    handoverExecutorService.execute(new Runnable() {
      @Override
      public void run() {

        // Allow time for the other wizard to finish hiding (200ms is sufficient)
        Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

        // Must execute on the EDT
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {

            Panels.hideLightBoxIfPresent();

            log.debug("Showing exiting welcome wizard after handover");
            Panels.showLightBox(Wizards.newExitingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_WALLET).getWizardScreenHolder());

          }
        });

      }
    });

  }

  /**
   * Password wizard has hidden
   */
  private void hidePasswordWizard() {

    log.debug("Wallet unlocked. Starting services...");

    // No wizards on further refreshes
    mainView.setShowExitingWelcomeWizard(false);
    mainView.setShowExitingPasswordWizard(false);

    // Start the main view refresh on the EDT
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mainView.refresh();
      }
    });

    // Allow time for MainView to refresh
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

    // Start the backup manager
    log.debug("Starting backup manager...");
    handleBackupManager();

    // Get the current wallet summary
    Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    mainView.sidebarWalletName(walletSummary.get().getName());

    // Start the wallet service
    log.debug("Starting wallet service...");
    CoreServices.getOrCreateWalletService(walletSummary.get().getWalletId());

    // Record this in the history
    CoreServices.logHistory(Languages.safeText(MessageKey.HISTORY_WALLET_OPENED, walletSummary.get().getName()));

    // Show the initial detail screen
    Screen screen = Screen.valueOf(Configurations.currentConfiguration.getApplication().getCurrentScreen());
    ControllerEvents.fireShowDetailScreenEvent(screen);

    // Don't hold up the UI thread with these background operations
    SafeExecutors.newSingleThreadExecutor("wallet-services").submit(new Runnable() {
      @Override
      public void run() {
        try {

          // Get a ticker going
          log.debug("Starting exchange...");
          handleExchange();

          // Check for Bitcoin URIs
          log.debug("Check for Bitcoin URIs...");
          handleBitcoinURIAlert();

          // Lastly start the Bitcoin network
          log.debug("Starting Bitcoin network...");
          handleBitcoinNetwork();

        } catch (Exception e) {
          // TODO localise and put on UI
          log.error("Services did not start ok. Error was {}", e.getClass().getCanonicalName() + " " + e.getMessage(), e);
        }
      }
    });
  }
}
