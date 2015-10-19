package org.multibit.hd.ui.controllers;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.*;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.joda.time.DateTime;
import org.multibit.commons.concurrent.SafeExecutors;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.events.*;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.*;
import org.multibit.hd.core.store.TransactionInfo;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.HardwareWalletMessage;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.SwitchWalletEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.*;
import org.multibit.hd.ui.services.ExternalDataListeningService;
import org.multibit.hd.ui.views.MainView;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Theme;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsRequestType;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsState;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsWizard;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletState;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizardModel;
import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.use_hardware_wallet.UseHardwareWalletState;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
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
  GenericOpenFilesEventListener,
  GenericPreferencesEventListener,
  GenericAboutEventListener,
  GenericQuitEventListener {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private Optional<ExchangeTickerService> exchangeTickerService = Optional.absent();

  private final ListeningExecutorService handoverExecutorService = SafeExecutors.newSingleThreadExecutor("wizard-handover");

  // Keep a thread pool for transaction status checking
  private static final ListeningExecutorService transactionCheckingExecutorService = SafeExecutors.newFixedThreadPool(10, "transaction-checking");

  // Provide a separate executor service for wallet operations
  private static final ListeningExecutorService walletExecutorService = SafeExecutors.newFixedThreadPool(10, "wallet-services");

  private static final int NUMBER_OF_SECONDS_TO_WAIT_BEFORE_TRANSACTION_CHECKING = 60;

  // Keep track of other controllers for use after a preferences change
  private final HeaderController headerController;

  // Main view may be replaced during a soft shutdown
  private MainView mainView;

  // Start with the assumption that it is fine to avoid annoying "everything is OK" alert
  private RAGStatus lastExchangeSeverity = RAGStatus.GREEN;

  // Assume a password rather than a hardware wallet cipher key
  private CredentialsRequestType deferredCredentialsRequestType = CredentialsRequestType.PASSWORD;

  /**
   * The delay between a wipe and insertion of a new device
   */
  private static final int HARDWARE_WALLET_WIPE_TIME_THRESHOLD = 4;

  /**
   * The last time a Trezor device was wiped (or yesterday as the default)
   */
  private DateTime lastWipedHardwareWalletDateTime = Dates.nowUtc().minusDays(1);

  /**
   * Whether alerts should be fired when new transactions appear (true = fire alerts, false = suppress alerts)
   */
  private static boolean fireTransactionAlerts = true;

  /**
   * @param headerController The header controller
   */
  public MainController(HeaderController headerController) {

    super();

    // MainController must also subscribe to ViewEvents
    ViewEvents.subscribe(this);

    Preconditions.checkNotNull(headerController, "'headerController' must be present");

    this.headerController = headerController;

  }

  @Override
  public void unsubscribe() {
    super.unsubscribe();
    ViewEvents.unsubscribe(this);
  }

  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    log.info("Received shutdown: {}", shutdownEvent.getShutdownType());

    switch (shutdownEvent.getShutdownType()) {
      case HARD:
      case SOFT:

        // Unsubscribe views for events
        mainView.unsubscribe();

        // Unregister controllers for events
        headerController.unsubscribe();

        // Unregister this
        unsubscribe();

        shutdownCurrentWallet(shutdownEvent.getShutdownType());

        break;
      case SWITCH:
        // Do nothing - the wizard hide event triggers this process

    }

  }

  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "This event should be running on the EDT.");

    log.debug("Wizard hide: '{}' Exit/Cancel: {}", event.getPanelName(), event.isExitCancel());

    if (!event.isExitCancel()) {

      // Successful wizard interaction

      if (WelcomeWizardState.CREATE_WALLET_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.HARDWARE_CREATE_WALLET_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.RESTORE_WALLET_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.RESTORE_PASSWORD_REPORT.name().equals(event.getPanelName())
        || WelcomeWizardState.WELCOME_SELECT_WALLET.name().equals(event.getPanelName())
        ) {

        // We have just finished the welcome wizard and want the credentials screen

        handoverToCredentialsWizard();
      }

      if (CredentialsState.CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK.name().equals(event.getPanelName())
        || CredentialsState.CREDENTIALS_LOAD_WALLET_REPORT.name().equals(event.getPanelName())
        ) {

        // We have just finished the credentials wizard and want the wallet details screen

        hideCredentialsWizard();
      }

      if (CredentialsState.CREDENTIALS_RESTORE.name().equals(event.getPanelName())) {

        // We are exiting the credentials wizard via the restore button and want the welcome wizard

        handoverToWelcomeWizardRestore();
      }

      if (CredentialsState.CREDENTIALS_CREATE.name().equals(event.getPanelName())) {

        // We are exiting the credentials wizard via the create button and want the welcome wizard

        handoverToWelcomeWizardCreate();
      }

      if (CredentialsState.CREDENTIALS_REQUEST_CIPHER_KEY.name().equals(event.getPanelName()) ||
        CredentialsState.CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY.name().equals(event.getPanelName())) {

        // We are exiting the credentials wizard as the hardware wallet is uninitialised and want the welcome wizard

        handoverToWelcomeWizardCreateHardwareWallet();
      }
      if (EditWalletState.EDIT_WALLET.name().equals(event.getPanelName())) {

        // We are exiting the edit wallet wizard and want the details screen to update with changes

        // Update the details screen (title etc)
        String walletName = ((EditWalletWizardModel) event.getWizardModel()).getWalletSummary().getName();
        hideEditWalletWizard(walletName);
      }

      if (ExitState.SWITCH_WALLET.equals(event.getWizardModel().getState())) {

        // We have just finished with the exit wizard and want the credentials screen

        handleSwitchWallet();
      }

      if (ExitState.CONFIRM_EXIT.equals(event.getWizardModel().getState())) {

        // We have just finished with the exit wizard and want to shut down
        CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);

      }

      // Do nothing other than usual wizard hide operations

    } else {

      // Shift focus depending on what was cancelled
      hideAsExitCancel(event.getPanelName());
    }
  }


  @Subscribe
  public void onSwitchWalletEvent(SwitchWalletEvent event) {

    log.debug("Received 'switch wallet' event");

    handleSwitchWallet();
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

      // Restarting the main view from a language change
      handleBasicMainViewRefresh();
    } else {

      // Restarting the main view from a configuration change
      handleFullMainViewRefresh();

    }

  }

  @Subscribe
  public void onBitcoinNetworkChangeEvent(BitcoinNetworkChangedEvent event) {

    log.trace("Received 'Bitcoin network changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    final BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");
    Preconditions.checkNotNull(summary.getMessageKey(), "'errorKey' must be present");
    Preconditions.checkNotNull(summary.getMessageData(), "'errorData' must be present");

    if (BitcoinNetworkStatus.SYNCHRONIZED.equals(event.getSummary().getStatus())) {
      // Enable alerts for new transactions (suppressed on repair wallet for user simplicity)
      MainController.setFireTransactionAlerts(true);

      // Ensure that the header shows the header after a sync (if the configuration permits)
      final boolean viewHeader = Configurations.currentConfiguration.getAppearance().isShowBalance();
      ViewEvents.fireViewChangedEvent(ViewKey.HEADER, viewHeader);

      // For Trezor hard wallets, get the date of the earliest transaction and use it to set the
      // earliestKeyCreationDate. This enables future repair wallets to be quicker
      if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() &&
        WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletType() == WalletType.TREZOR_HARD_WALLET) {
        // See if the synced wallet has transactions
        Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
        java.util.List<Transaction> transactions = wallet.getTransactionsByTime();
        if (!transactions.isEmpty()) {
          // Last is the oldest
          Date earliestTransactionDate = transactions.get(transactions.size() - 1).getUpdateTime();
          if (earliestTransactionDate != null) {
            // Set the wallet key creation time to be the transaction date (minus one day to cater for blockchain forkiness)
            wallet.setEarliestKeyCreationTime(earliestTransactionDate.getTime() / 1000 - Dates.NUMBER_OF_SECONDS_IN_A_DAY);
            log.debug("Setting hardware wallet 'earliestKeyCreationDate' to one day before : {}", earliestTransactionDate);
          }
        }
      }
    }

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
      JButton button = Buttons.newAlertPanelButton(action, MessageKey.SETTINGS, MessageKey.SETTINGS_TOOLTIP, AwesomeIcon.CHECK);

      // Provide the alert
      ControllerEvents.fireAddAlertEvent(
        Models.newAlertModel(
          localisedMessage,
          summary.getSeverity(), button)
      );
    }

  }

  @Subscribe
  public void onEnvironmentEvent(EnvironmentEvent event) {

    log.trace("Received 'environment' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    EnvironmentSummary summary = event.getSummary();

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
      case SYSTEM_TIME_DRIFT:
        // Present the localised message
        ControllerEvents.fireAddAlertEvent(
          Models.newAlertModel(
            localisedMessage,
            summary.getSeverity())
        );
        break;
      case CERTIFICATE_FAILED:
        // Create a button to the repair wallet tool
        JButton button = Buttons.newAlertPanelButton(getShowRepairWalletAction(), MessageKey.REPAIR, MessageKey.REPAIR_TOOLTIP, AwesomeIcon.MEDKIT);

        // Append general security advice allowing for LTR/RTL
        ControllerEvents.fireAddAlertEvent(
          Models.newAlertModel(
            localisedMessage + "\n" + Languages.safeText(CoreMessageKey.SECURITY_ADVICE),
            summary.getSeverity(),
            button)
        );
        break;
      case UNSUPPORTED_FIRMWARE_ATTACHED:
      case DEPRECATED_FIRMWARE_ATTACHED:
      case UNSUPPORTED_CONFIGURATION_ATTACHED:
        Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

        if (walletSummary.isPresent() && !Panels.isLightBoxShowing()) {
          // Present the localised message as an alert bar since a popover won't appear any time soon
          ControllerEvents.fireAddAlertEvent(
            Models.newAlertModel(
              localisedMessage,
              summary.getSeverity())
          );

        }
        break;
      default:
        throw new IllegalStateException("Unknown alert type: " + summary.getAlertType());
    }
  }

  @Subscribe
  public void onComponentChangedEvent(ComponentChangedEvent event) {

    // Check for specific component changes
    if (UseHardwareWalletState.CONFIRM_WIPE_DEVICE.name().equals(event.getPanelName())) {
      // The user has successfully completed wiping a Trezor device
      lastWipedHardwareWalletDateTime = (DateTime) event.getComponentModel().get();
    }

  }

  /**
   * @param mainView The main view (the deferred credentials request type will also be set)
   */
  public void setMainView(MainView mainView) {

    this.mainView = mainView;

    log.debug("Setting MainView credentials type: {}", deferredCredentialsRequestType.name());
    mainView.setCredentialsRequestType(deferredCredentialsRequestType);

  }

  /**
   * <p>Complete tear down and rebuild of the detail screens comprising the main view</p>
   * <p>All services are restarted</p>
   * <p>The main view references remain intact</p>
   * <p/>
   * <p>This is not done through a simple SWITCH shutdown event being fired since the order
   * of shutdown is important and cannot be guaranteed otherwise.</p>
   */
  private void handleSwitchWallet() {

    // Run this in a separate thread to ensure the original event returns promptly
    // and that the switch panel view is able to close before the MainView resets
    final ListenableFuture<Boolean> future = handoverExecutorService.submit(
      new Callable<Boolean>() {

        @Override
        public Boolean call() {

          log.trace("Using switch wallet view refresh");

          // Sleep for a short time to reduce UI jolt
          Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

          // Hide the application frame to prevent user interacting with the detail
          // panels after the exit panel view has hidden
          // It is very tricky to get the timing right so hiding the UI is the safest
          // course of action here
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                // Dim the application using the glass pane
                Panels.getApplicationFrame().getRootPane().getGlassPane().setVisible(true);
              }
            });

          // Sleep for a short time to allow UI events to occur
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          try {
            // Close the supporting services
            // This can take some time
            shutdownCurrentWallet(ShutdownEvent.ShutdownType.SWITCH);
          } catch (Exception e) {
            log.error("Failed to shutdown current wallet", e);
          }

          // Avoiding repeating latest events which will leave traces of the earlier wallet
          // on the MainView during unlock
          mainView.setRepeatLatestEvents(false);

          log.debug("Find existing wallet directories");

          // Check for any pre-existing wallets in the application directory
          File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
          java.util.List<File> walletDirectories = WalletManager.findWalletDirectories(applicationDataDirectory);

          if (walletDirectories.isEmpty() || !Configurations.currentConfiguration.isLicenceAccepted()) {

            log.debug("No wallets in the directory or licence not accepted - showing the welcome wizard");
            mainView.setShowExitingWelcomeWizard(true);
            mainView.setShowExitingCredentialsWizard(false);

          } else {

            log.debug("Wallets are present - showing the credentials wizard");
            mainView.setShowExitingCredentialsWizard(true);
            mainView.setShowExitingWelcomeWizard(false);

          }

          log.debug("Perform MainView refresh");

          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                // Switch off the background dimming and trigger the showing of the wizard
                Panels.getApplicationFrame().getRootPane().getGlassPane().setVisible(false);
                mainView.refresh(false);
                mainView.setRepeatLatestEvents(true);
              }
            });

          // Sleep for a short time to allow UI events to occur
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          return true;
        }
      });

    Futures.addCallback(
      future, new FutureCallback<Boolean>() {
        @Override
        public void onSuccess(@Nullable Boolean result) {

          // We successfully switched the wallet

        }

        @Override
        public void onFailure(Throwable t) {

          // Show the application frame to provide some assistance to user
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                Panels.getApplicationFrame().setVisible(false);
              }
            });

          // Sleep for a short time to allow UI events to occur
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

          // Use the generic handler since we're all over the place at this point
          ExceptionHandler.handleThrowable(t);

        }

      }

    );


  }

  /**
   * Handles the process of shutting down the current wallet support services
   */

  public void shutdownCurrentWallet(ShutdownEvent.ShutdownType shutdownType) {

    log.debug("Shutdown current wallet...");

    if (!shutdownType.equals(ShutdownEvent.ShutdownType.SWITCH)) {
      // Hide the UI if not switching
      shutdownMainView();
    }

    // Provide a graceful shutdown of the relevant core services in the correct order
    CoreServices.shutdownNow(shutdownType);

    // Close the current wallet
    WalletManager.INSTANCE.shutdownNow(shutdownType);

    // Close the backup manager for the wallet
    BackupManager.INSTANCE.shutdownNow();

    // Close the installation manager
    InstallationManager.shutdownNow(shutdownType);

  }

  /**
   * Shutdown the MainView and dispose of the main application frame
   */
  private void shutdownMainView() {

    // Dispose of the main view and all its attendant references
    log.debug("Disposing of MainView");
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          Panels.hideLightBoxIfPresent();
          Panels.getApplicationFrame().dispose();
        }
      });

    // Remove the reference
    mainView = null;

    // Sleep for a short time to allow UI events to occur
    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

  }

  /**
   * <p>Complete tear down and rebuild of the detail screens comprising the main view</p>
   * <p>Non-Bitcoin services are restarted</p>
   * <p>The main view references remain intact</p>
   */
  private void handleFullMainViewRefresh() {

    log.debug("Using full view refresh (configuration change)");

    // Switch the exchange ticker service before the UI to ensure the
    // exchange rate provider is rendered correctly
    handleExchange();

    // Ensure the Swing thread can perform a complete refresh
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {

          // Switch the theme before any other UI building takes place
          handleTheme();

          // Rebuild MainView contents
          handleLocale();

          // Force a frame redraw
          Panels.getApplicationFrame().invalidate();

          // Rebuild the detail views and alert panels
          mainView.refresh(false);

          // Show the current detail screen
          Screen screen = Screen.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentScreen());
          ViewEvents.fireShowDetailScreenEvent(screen);

          // Trigger the alert panels to refresh
          headerController.refresh();

        }
      });

    // Restart the hardware wallet service (devices may have changed)
    handleHardwareWallets();

    // Check for system time drift (runs in the background)
    handleSystemTimeDrift();

  }

  /**
   * <p>Partial rebuild of the detail screens comprising the main view</p>
   * <p>The main view references remain intact</p>
   */
  private void handleBasicMainViewRefresh() {

    log.debug("Using simplified view refresh (language change)");

    // Ensure the Swing thread can perform a complete refresh
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {

          // Switch the theme before any other UI building takes place
          handleTheme();

          // Rebuild MainView contents
          handleLocale();

          // Force a frame redraw
          Panels.getApplicationFrame().invalidate();

          // Rebuild the detail views and alert panels
          if (mainView != null) {
            mainView.refresh(true);
          }

        }
      });
  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      // Show the Tools screen
      ViewEvents.fireShowDetailScreenEvent(Screen.TOOLS);

      // Show the About screen
      Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());
    }
  }

  @Subscribe
  @Override
  public void onOpenURIEvent(GenericOpenURIEvent event) {

    ExternalDataListeningService.parseRawData(event.getURI().toString());

    // Now would be a good time to attempt to alert the user
    ExternalDataListeningService.purgeAlertModelQueue();
  }

  @Override
  public void onOpenFilesEvent(GenericOpenFilesEvent event) {

    for (File file : event.getFiles()) {
      URI uri = file.toURI();
      ExternalDataListeningService.parseRawData(uri.toString());
    }

    // Now would be a good time to attempt to alert the user
    ExternalDataListeningService.purgeAlertModelQueue();

  }

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      // Show the Preferences screen
      ViewEvents.fireShowDetailScreenEvent(Screen.SETTINGS);
    }

  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    log.debug("Received quit event (close button). Initiating hard shutdown...");
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);

  }

  /**
   * @param transactionSeenEvent The event (very high frequency during synchronisation)
   */
  @Subscribe
  public void onTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {
    if (transactionSeenEvent.isFirstAppearanceInWallet() && isFireTransactionAlerts()) {
      log.debug("Firing an alert for a new transaction");
      transactionSeenEvent.setFirstAppearanceInWallet(false);
      Sounds.playPaymentReceived(Configurations.currentConfiguration.getSound());
      AlertModel alertModel = Models.newPaymentReceivedAlertModel(transactionSeenEvent);
      ControllerEvents.fireAddAlertEvent(alertModel);
    }
  }

  /**
   * Make sure that when a transaction is successfully created its 'metadata' is stored in a transactionInfo
   *
   * @param transactionCreationEvent The transaction creation event from the EventBus
   */
  @Subscribe
  public void onTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {

    initiateDelayedTransactionStatusCheck(transactionCreationEvent);

    // Only store successful transactions
    if (!transactionCreationEvent.isTransactionCreationWasSuccessful()) {
      return;
    }

    // Create a transactionInfo to match the event created
    TransactionInfo transactionInfo = new TransactionInfo();
    transactionInfo.setHash(transactionCreationEvent.getTransactionId());
    String note = transactionCreationEvent.getNotes().or("");
    transactionInfo.setNote(note);

    // Append miner's fee info
    log.debug("Transaction creation event with mining fee of {}", transactionCreationEvent.getMiningFeePaid());
    transactionInfo.setMinerFee(transactionCreationEvent.getMiningFeePaid());

    // Append client fee info
    transactionInfo.setClientFee(transactionCreationEvent.getClientFeePaid());

    // Set the fiat payment amount
    transactionInfo.setAmountFiat(transactionCreationEvent.getFiatPayment().orNull());

    // Set whether the transaction was created in this copy of MBHD
    // This is a copy of the TransactionConfidence.Source which does not survive a repair wallet.
    transactionInfo.setSentBySelf(transactionCreationEvent.isSentByMe());

    WalletService walletService = CoreServices.getCurrentWalletService().get();
    walletService.addTransactionInfo(transactionInfo);
    log.debug("Added transactionInfo {} to walletService {}", transactionInfo, walletService);
    try {
      CharSequence password = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword();
      if (password != null) {
        walletService.writePayments(password);
      }
    } catch (PaymentsSaveException pse) {
      ExceptionHandler.handleThrowable(pse);
    }
  }

  /**
   * Respond to a hardware wallet system event
   *
   * @param event The event
   */
  @Subscribe
  public void onHardwareWalletEvent(final HardwareWalletEvent event) {
    log.debug("Received hardware event: '{}'", event.getEventType().name());

    if (!ApplicationEventService.isHardwareWalletEventAllowed()) {
      log.debug("Ignoring hardware wallet event due to event threshold");
      return;
    }

    // Check if CoreServices has identified a hardware wallet (could be just starting or in FEST mode)
    if (!CoreServices.getCurrentHardwareWalletService().isPresent()) {
      CoreServices.useFirstReadyHardwareWalletService();
    }

    // Quick check for relevancy
    switch (event.getEventType()) {
      case SHOW_DEVICE_STOPPED:
      case SHOW_DEVICE_DETACHED:
        // Rely on the hardware wallet wizard to inform the user
        // An alert tends to stack and gets messy/irrelevant
        deferredCredentialsRequestType = CredentialsRequestType.PASSWORD;

        // Clear any alert-inducing events as the user has detached the device
        Optional<EnvironmentEvent> lastEnvironmentEventOptional = CoreServices.getApplicationEventService().getLatestEnvironmentEvent();
        if (lastEnvironmentEventOptional.isPresent()
          && lastEnvironmentEventOptional.get().is(EnvironmentSummary.AlertType.UNSUPPORTED_FIRMWARE_ATTACHED)) {
          CoreServices.getApplicationEventService().onEnvironmentEvent(null);
        }
        if (lastEnvironmentEventOptional.isPresent()
          && lastEnvironmentEventOptional.get().is(EnvironmentSummary.AlertType.UNSUPPORTED_CONFIGURATION_ATTACHED)) {
          CoreServices.getApplicationEventService().onEnvironmentEvent(null);
        }
        if (lastEnvironmentEventOptional.isPresent()
          && lastEnvironmentEventOptional.get().is(EnvironmentSummary.AlertType.DEPRECATED_FIRMWARE_ATTACHED)) {
          CoreServices.getApplicationEventService().onEnvironmentEvent(null);
        }

        break;
      case SHOW_DEVICE_FAILED:
        handleShowDeviceFailed(event);
        break;
      case SHOW_DEVICE_READY:
        handleShowDeviceReady(event);
        break;
      default:
        // The AbstractHardwareWalletWizard handles everything when a wizard is showing
        return;
    }

    log.debug("Selected deferred credentials type: {}", deferredCredentialsRequestType);

    // Set the credentials immediately if possible so that MainView.refresh() works correctly
    if (mainView != null) {
      mainView.setCredentialsRequestType(deferredCredentialsRequestType);
    }

  }

  /**
   * <p>Handle the "show device failure" event</p>
   * <p>There are two conditions to check here: a failed device from a USB communication
   * problem, or one that has unsupported firmware (e.g. Trezor 1.2.x).</p>
   * <p>If the device is unsupported then two forms of alert are needed: a popover if a
   * light box is showing; an alert bar if a wallet is open.</p>
   *
   * @param event The hardware wallet event
   */
  private void handleShowDeviceFailed(final HardwareWalletEvent event) {

    // Determine the nature of the failure
    Optional<Features> featuresOptional = CoreServices.getCurrentHardwareWalletService().get().getContext().getFeatures();

    boolean isUnsupportedFirmware = featuresOptional.isPresent()
      && !featuresOptional.get().isSupported();
    boolean isUnsupportedConfigurationPassphrase = featuresOptional.isPresent()
      && featuresOptional.get().isSupported()
      && featuresOptional.get().hasPassphraseProtection();

    if (isUnsupportedFirmware) {
      // Show as a environment popover
      CoreEvents.fireEnvironmentEvent(EnvironmentSummary.newUnsupportedFirmware());
    } else if (isUnsupportedConfigurationPassphrase) {
      // Show as an info popover
      CoreEvents.fireEnvironmentEvent(EnvironmentSummary.newUnsupportedConfigurationPassphrase());
    } else {
      // Use the alert bar mechanism

      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // Attempt to create a suitable alert model in addition to view event
            AlertModel alertModel = Models.newHardwareWalletAlertModel(event);
            ControllerEvents.fireAddAlertEvent(alertModel);
          }
        });

    }

    // Set the deferred credentials request type to be password since the device has failed
    deferredCredentialsRequestType = CredentialsRequestType.PASSWORD;

  }

  /**
   * <p>Handle the "show device ready" event</p>
   * <p>Show an alert if a hardware wallet connects when:</p>
   * <ul>
   * <li>there is a current wallet</li>
   * <li>the current wallet is not the same "hard" wallet as in the alert (different device)</li>
   * <li>there has not been a "wipe" in the last few seconds</li>
   * <li>the device has a supported configuration (e.g. not passphrase protected)</li>
   * </ul>
   *
   * @param event The hardware wallet event
   */
  private void handleShowDeviceReady(final HardwareWalletEvent event) {

    // Configuration check
    if (event.getMessage().isPresent()) {
      final HardwareWalletMessage message = event.getMessage().get();
      if (message instanceof Features) {

        Features features = (Features) message;

        if (features.hasPassphraseProtection()) {
          handleShowDeviceFailed(event);
          return;
        }

        // You would check for deprecated firmware versions here - there are none currently.
        // See git history for how to do it.
      }
    }

    Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (walletSummary.isPresent()) {
      Optional<HardwareWalletService> currentHardwareWalletService = CoreServices.getCurrentHardwareWalletService();

      boolean showAlert = false;

      if (walletSummary.get().getWalletType() != WalletType.TREZOR_HARD_WALLET) {
        // Not currently using a hardware wallet so show the alert
        showAlert = true;
      } else {
        // Currently using a hardware wallet so check for a change in device
        if (currentHardwareWalletService.isPresent()) {

          // Get the current wallet name (label)
          String currentWalletName = walletSummary.get().getName();

          // Get the current features
          Optional<Features> currentFeatures = currentHardwareWalletService.get().getContext().getFeatures();

          // Check for a different device type
          String currentSource = currentHardwareWalletService.get().getContext().getClient().name();
          String newSource = event.getSource();

          if (!currentSource.equals(newSource)) {
            // Different device type (e.g. Trezor attached during KeepKey session)
            showAlert = true;
          } else {
            // Same device type (e.g. accidental detach of device or swap in advance of switch)
            // Check for a different device label
            if (currentFeatures.isPresent()) {
              // The current device is remains attached
              if (!currentFeatures.get().getLabel().equals(currentWalletName)) {
                showAlert = true;
              }
            } else {
              // The current device was detached so we can't tell if it is different
              showAlert = true;
            }
          }
        }
      }

      // Suppress alert if there has been a recent wipe
      if (currentHardwareWalletService.isPresent()) {
        if (lastWipedHardwareWalletDateTime
          .plusSeconds(HARDWARE_WALLET_WIPE_TIME_THRESHOLD)
          .isAfter(Dates.nowUtc())) {
          showAlert = false;
        }
      }

      if (showAlert) {

        // NOTE Currently getting a false positive due to FEST test use of WalletManager during fixture creation
        log.debug("Hardware wallet attached during an unlocked soft wallet session - showing alert");

        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              // Attempt to create a suitable alert model in addition to view event
              AlertModel alertModel = Models.newHardwareWalletAlertModel(event);
              ControllerEvents.fireAddAlertEvent(alertModel);
            }
          });
      }
    }

    // Set the deferred credentials request type
    deferredCredentialsRequestType = CredentialsRequestType.HARDWARE;

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
   * @return An action to show the help screen for 'spendable balance may be lower"
   */
  private AbstractAction getShowHelpAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO show the 'spendable balance may be lower' help screen when it is written
        ViewEvents.fireShowDetailScreenEvent(Screen.HELP);
      }
    };
  }

  /**
   * Handles the changes to the exchange ticker service
   */
  private void handleExchange() {

    BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();
    ExchangeKey exchangeKey;
    try {
      exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());
    } catch (IllegalArgumentException e) {
      // Exchange in configuration is not supported
      exchangeKey = ExchangeKey.NONE;
      Configurations.currentConfiguration.getBitcoin().setCurrentExchange(exchangeKey.name());
      Configurations.persistCurrentConfiguration();
    }

    if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
      if (bitcoinConfiguration.getExchangeApiKeys().containsKey(ExchangeKey.OPEN_EXCHANGE_RATES.name())) {
        String apiKey = Configurations.currentConfiguration.getBitcoin().getExchangeApiKeys().get(ExchangeKey.OPEN_EXCHANGE_RATES.name());
        exchangeKey.getExchange().get().getExchangeSpecification().setApiKey(apiKey);
      }
    }

    // Stop (with block) any existing exchange ticker service
    if (exchangeTickerService.isPresent()) {
      exchangeTickerService.get().shutdownNow(ShutdownEvent.ShutdownType.HARD);
    }

    // Create and start the exchange ticker service
    exchangeTickerService = Optional.of(CoreServices.createAndStartExchangeService(bitcoinConfiguration));

  }

  /**
   * Handles the changes to the theme
   */
  private void handleTheme() {

    Theme newTheme = ThemeKey.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentTheme()).theme();
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
    Panels.getApplicationFrame().setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    Panels.getApplicationFrame().applyComponentOrientation(ComponentOrientation.getOrientation(locale));

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
      String cloudBackupLocationString = Configurations.currentConfiguration.getAppearance().getCloudBackupLocation();
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
   * <p>Show any alerts coming about as part of the startup sequence (we have just unlocked a wallet).</p>
   * <p>See the ExternalDataListeningService for runtime handling</p>
   */
  private void handleExternalDataAlerts() {

    // Now would be a good time to alert the user to any events
    ExternalDataListeningService.purgeAlertModelQueue();

  }

  /**
   * <p>Restart the hardware wallet service if necessary and subscribe to hardware wallet events</p>
   */
  public void handleHardwareWallets() {

    boolean isServiceAllowed = false;

    // Determine if at least one hardware wallet is selected
    if (Configurations.currentConfiguration.isTrezor()) {
      isServiceAllowed = true;
    }

    Optional<HardwareWalletService> currentHardwareWalletService = CoreServices.getCurrentHardwareWalletService();

    // Check if the service is running and is allowed
    if (currentHardwareWalletService.isPresent() && !isServiceAllowed) {

      // Stop the service, all listeners and clear the CoreServices reference
      CoreServices.stopHardwareWalletServices();

      return;
    }

    // Must require hardware wallet services to be here

    // Ensure we have initialised
    List<Optional<HardwareWalletService>> hardwareWalletServices = CoreServices.getOrCreateHardwareWalletServices();

    // (Re)subscribe to hardware wallet events
    // This is required in case the user stops and starts the
    // hardware wallet service during a session
    HardwareWalletEvents.subscribe(this);

    // Start the services
    for (Optional<HardwareWalletService> hardwareWalletService : hardwareWalletServices) {
      if (hardwareWalletService.isPresent()) {
        log.info("Starting hardware wallet service: {}", hardwareWalletService.get().getContext().getClient().name());
        hardwareWalletService.get().start();
      }
    }

  }

  /**
   * <p>Performs a system time check against an internet time source over NTP
   * If the system time has drifted then blocks will be rejected and the
   * balance will be wrong</p>
   */
  private void handleSystemTimeDrift() {

    // Check time is not more than 60 min off (60 x 60 x 1000)
    // in either direction
    final ListenableFuture<Integer> driftFuture = Dates.calculateDriftInMillis("pool.ntp.org");
    Futures.addCallback(
      driftFuture, new FutureCallback<Integer>() {
        @Override
        public void onSuccess(@Nullable Integer result) {

          if (result != null && Math.abs(result) > 3_600_000) {
            log.warn("System time is adrift by: {} min(s)", result / 60_000);
            // Issue the info alert
            CoreEvents.fireEnvironmentEvent(EnvironmentSummary.newSystemTimeDrift());
          } else {
            log.debug("System time drift is within limits");
          }

        }

        @Override
        public void onFailure(Throwable t) {

          // Problem encountered - user won't be able to fix it
          log.warn("System drift check timed out: '{}'", t.getMessage());

        }
      });

  }

  private void hideAsExitCancel(String panelName) {

    // The exit dialog state is determined by the radio button selection
    if (ExitState.CONFIRM_EXIT.name().equals(panelName)
      || ExitState.SWITCH_WALLET.name().equals(panelName)) {
      mainView.sidebarRequestFocus();
    }

    // The detail screens do not have an intuitive way to capture focus
    // we rely on CTRL+TAB to relocate the focus with keyboard

  }

  private void hideEditWalletWizard(String walletName) {

    mainView.sidebarWalletName(walletName);

  }

  /**
   * Welcome wizard has created a new wallet so hand over to the credentials wizard for access
   */
  private void handoverToCredentialsWizard() {

    log.debug("Hand over to credentials wizard");

    // Handover
    mainView.setShowExitingWelcomeWizard(false);
    mainView.setShowExitingCredentialsWizard(true);
    mainView.setCredentialsRequestType(deferredCredentialsRequestType);

    // Use a new thread to handle the new wizard so that the handover can complete
    // hiding the existing wizard before drawing the replacement
    handoverExecutorService.execute(
      new Runnable() {
        @Override
        public void run() {

          // Allow time for the other wizard to finish hiding (200ms is the minimum)
          Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

          // Must execute on the EDT
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                // Start building the wizard on the EDT to ensure darkened background remains
                final CredentialsWizard credentialsWizard = Wizards.newExitingCredentialsWizard(deferredCredentialsRequestType);

                log.debug("Forcing hide of existing light box");
                Panels.hideLightBoxIfPresent();

                log.debug("Showing exiting credentials wizard after handover");
                Panels.showLightBox(credentialsWizard.getWizardScreenHolder());

              }
            });

        }
      });

  }


  /**
   * Credentials wizard needs to perform a restore so hand over to the welcome wizard
   */
  private void handoverToWelcomeWizardRestore() {

    log.debug("Hand over to welcome wizard (restore wallet)");

    // Handover
    mainView.setShowExitingWelcomeWizard(true);
    mainView.setShowExitingCredentialsWizard(false);

    // Select the appropriate wallet mode
    final WalletMode walletMode;
    if (CredentialsRequestType.HARDWARE.equals(deferredCredentialsRequestType)) {
      Optional<HardwareWalletService> currentHardwareWalletService = CoreServices.getCurrentHardwareWalletService();
      walletMode = WalletMode.of(currentHardwareWalletService);
    } else {
      walletMode = WalletMode.STANDARD;
    }

    // For soft wallets the restore goes to the select wallet screen, for Trezor hard wallets go directly to the restore
    final WelcomeWizardState initialState = (WalletMode.STANDARD == walletMode) ? WelcomeWizardState.WELCOME_SELECT_WALLET : WelcomeWizardState.RESTORE_WALLET_SELECT_BACKUP;

    // Start building the wizard on the EDT to prevent UI updates
    final WelcomeWizard welcomeWizard = Wizards.newExitingWelcomeWizard(initialState, walletMode);

    // Use a new thread to handle the new wizard so that the handover can complete
    handoverExecutorService.execute(
      new Runnable() {
        @Override
        public void run() {

          // Allow time for the other wizard to finish hiding (200ms is the minimum)
          Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

          // Must execute on the EDT
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                Panels.hideLightBoxIfPresent();

                log.debug("Showing exiting welcome wizard after handover");
                Panels.showLightBox(welcomeWizard.getWizardScreenHolder());
              }
            });
        }
      });
  }

  /**
   * Credentials wizard needs to perform a create so hand over to the welcome wizard
   */
  private void handoverToWelcomeWizardCreate() {

    log.debug("Hand over to welcome wizard (create wallet)");

    // Handover
    mainView.setShowExitingWelcomeWizard(true);
    mainView.setShowExitingCredentialsWizard(false);

    // For soft wallets the create goes to the wallet preparation screen
    final WelcomeWizardState initialState = WelcomeWizardState.CREATE_WALLET_PREPARATION;
    // Start building the wizard on the EDT to prevent UI updates
    final WelcomeWizard welcomeWizard = Wizards.newExitingWelcomeWizard(
      initialState, WalletMode.STANDARD
    );

    // Use a new thread to handle the new wizard so that the handover can complete
    handoverExecutorService.execute(
      new Runnable() {
        @Override
        public void run() {

          // Allow time for the other wizard to finish hiding (200ms is the minimum)
          Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

          // Must execute on the EDT
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                Panels.hideLightBoxIfPresent();

                log.debug("Showing exiting welcome wizard after handover");
                Panels.showLightBox(welcomeWizard.getWizardScreenHolder());
              }
            });
        }
      });
  }

  /**
   * Credentials wizard needs to perform a create new Trezor wallet over to the welcome wizard
   */
  private void handoverToWelcomeWizardCreateHardwareWallet() {

    log.debug("Hand over to welcome wizard (create Trezor wallet)");

    // Handover
    mainView.setShowExitingWelcomeWizard(true);
    mainView.setShowExitingCredentialsWizard(false);

    // Get the wallet mode
    WalletMode walletMode = WalletMode.of(CoreServices.getCurrentHardwareWalletService());

    // Start building the wizard on the EDT to prevent UI updates
    final WelcomeWizard welcomeWizard = Wizards.newExitingWelcomeWizard(
      WelcomeWizardState.HARDWARE_CREATE_WALLET_PREPARATION,
      walletMode
    );

    // Use a new thread to handle the new wizard so that the handover can complete
    handoverExecutorService.execute(
      new Runnable() {
        @Override
        public void run() {

          // Allow time for the other wizard to finish hiding (200ms is the minimum)
          Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);

          // Must execute on the EDT
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                Panels.hideLightBoxIfPresent();

                log.debug("Showing exiting welcome wizard after handover");
                Panels.showLightBox(welcomeWizard.getWizardScreenHolder());

              }
            });

        }
      });

  }

  /**
   * Credentials wizard has hidden
   */
  private void hideCredentialsWizard() {

    log.debug("Wallet unlocked. Starting services...");

    // No wizards on further refreshes
    mainView.setShowExitingWelcomeWizard(false);
    mainView.setShowExitingCredentialsWizard(false);

    Optional<HardwareWalletEvent> lastHardwareWalletEvent = CoreServices.getApplicationEventService().getLatestHardwareWalletEvent();

    // Refresh the main view
    mainView.refresh(false);

    if (lastHardwareWalletEvent.isPresent()
      && lastHardwareWalletEvent.get().getEventType() == HardwareWalletEventType.SHOW_DEVICE_READY) {
      // Make sure the 'DEVICE_READY' event is not lost
      HardwareWalletEvents.fireHardwareWalletEvent(
        lastHardwareWalletEvent.get().getEventType(),
        lastHardwareWalletEvent.get().getMessage().get(),
        lastHardwareWalletEvent.get().getSource()
      );
    }

    // Don't hold up the UI thread with these background operations
    walletExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {
          try {
            // Get a ticker going
            log.debug("Starting exchange...");
            handleExchange();

            // Check for external data (wants to be quick)
            log.debug("Check for external data...");
            handleExternalDataAlerts();

            // Check for system time drift (runs in the background)
            log.debug("Check for system time drift...");
            handleSystemTimeDrift();

          } catch (Exception e) {
            // TODO localise and put on UI via an alert
            log.error("Services did not start ok. Error was {}", e.getClass().getCanonicalName() + " " + e.getMessage(), e);
          }
        }
      });

    // Start the backup manager
    log.debug("Starting backup manager...");
    handleBackupManager();

    // Get the current wallet summary
    Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    mainView.sidebarWalletName(walletSummary.get().getName());


    // Start the wallet service
    log.debug("Starting wallet service...");
    CoreServices.getOrCreateWalletService(walletSummary.get().getWalletId());

    // Show the initial detail screen
    Screen screen = Screen.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentScreen());
    ViewEvents.fireShowDetailScreenEvent(screen);
  }

  /**
   * When a transaction is created, fire off a delayed check of the transaction confidence/ network status
   *
   * @param transactionCreationEvent The transaction creation event from the EventBus
   */
  private void initiateDelayedTransactionStatusCheck(final TransactionCreationEvent transactionCreationEvent) {
    transactionCheckingExecutorService.submit(
      new Runnable() {

        @Override
        public void run() {
          log.debug("Performing delayed status check on transaction '" + transactionCreationEvent.getTransactionId() + "'");

          // Wait for a while to let the Bitcoin network respond to the tx being sent
          Uninterruptibles.sleepUninterruptibly(NUMBER_OF_SECONDS_TO_WAIT_BEFORE_TRANSACTION_CHECKING, TimeUnit.SECONDS);

          // See if the transaction has a RAGStatus if red.
          // This could be the tx has not been transmitted ok or is only seen by zero or one peers.
          // In this case the user will not have access to the tx change and notify them with a warning alert
          WalletService currentWalletService = CoreServices.getCurrentWalletService().get();
          if (currentWalletService != null) {
            TransactionData transactionData = currentWalletService.getTransactionDataByHash(transactionCreationEvent.getTransactionId());
            if (transactionData != null) {
              PaymentStatus status = transactionData.getStatus();
              if (status.getStatus().equals(RAGStatus.RED)) {
                // The transaction has not been sent correctly, or change is not spendable, throw a warning alert
                final AlertModel alertModel = Models.newAlertModel(Languages.safeText(MessageKey.SPENDABLE_BALANCE_IS_LOWER), RAGStatus.AMBER);
                SwingUtilities.invokeLater(
                  new Runnable() {
                    @Override
                    public void run() {
                      ControllerEvents.fireAddAlertEvent(alertModel);
                    }
                  });
              }

              if (!status.getStatus().equals(RAGStatus.GREEN)) {
                // Ensure that there is a message that the spendable balance is lower - Fire a BitcoinSentEvent failure
                CoreEvents.fireBitcoinSentEvent(
                  new BitcoinSentEvent(
                    Optional.<Transaction>absent(), null, transactionData.getAmountCoin().orNull(),
                    null,
                    Optional.<Coin>absent(),
                    Optional.<Coin>absent(),
                    false,
                    CoreMessageKey.THE_ERROR_WAS,
                    new String[]{Languages.safeText(MessageKey.SPENDABLE_BALANCE_IS_LOWER)}
                  ));
              }
            }
          }
        }
      });

  }

  public static boolean isFireTransactionAlerts() {
    return fireTransactionAlerts;
  }

  public static void setFireTransactionAlerts(boolean fireTransactionAlerts) {
    MainController.fireTransactionAlerts = fireTransactionAlerts;
  }
}
