package org.multibit.hd.ui.controllers;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.*;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.*;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BackupService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.events.HardwareWalletEvent;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.SwitchWalletEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.*;
import org.multibit.hd.ui.services.BitcoinURIListeningService;
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
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardMode;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
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
  GenericPreferencesEventListener,
  GenericAboutEventListener,
  GenericQuitEventListener {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private Optional<ExchangeTickerService> exchangeTickerService = Optional.absent();

  private Optional<HardwareWalletService> hardwareWalletService = Optional.absent();

  private final BitcoinURIListeningService bitcoinURIListeningService;

  private final ListeningExecutorService handoverExecutorService = SafeExecutors.newSingleThreadExecutor("wizard-handover");

  // Keep a thread pool for transaction status checking
  private static final ListeningExecutorService transactionCheckingExecutorService = SafeExecutors.newFixedThreadPool(10, "transaction-checking");

  // Provide a separate executor service for wallet operations
  private static final ListeningExecutorService walletExecutorService = SafeExecutors.newFixedThreadPool(10, "wallet-services");

  private static final int NUMBER_OF_SECONDS_TO_WAIT_BEFORE_TRANSACTION_CHECKING = 10;

  // Keep track of other controllers for use after a preferences change
  private final HeaderController headerController;

  // Main view may be replaced during a soft shutdown
  private MainView mainView;

  // Start with the assumption that it is fine to avoid annoying "everything is OK" alert
  private RAGStatus lastExchangeSeverity = RAGStatus.GREEN;

  // Assume a password rather than a hardware wallet cipher key
  private CredentialsRequestType deferredCredentialsRequestType = CredentialsRequestType.PASSWORD;

  /**
   * @param bitcoinURIListeningService The Bitcoin URI listening service (must be present to permit a UI)
   * @param headerController           The header controller
   */
  public MainController(
    BitcoinURIListeningService bitcoinURIListeningService,
    HeaderController headerController
  ) {

    super();

    // MainController must also subscribe to ViewEvents
    ViewEvents.subscribe(this);

    Preconditions.checkNotNull(bitcoinURIListeningService, "'bitcoinURIListeningService' must be present");
    Preconditions.checkNotNull(headerController, "'headerController' must be present");

    this.bitcoinURIListeningService = bitcoinURIListeningService;
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
        || WelcomeWizardState.TREZOR_CREATE_WALLET_REPORT.name().equals(event.getPanelName())
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

      if (CredentialsState.CREDENTIALS_REQUEST_CIPHER_KEY.name().equals(event.getPanelName()) ||
        CredentialsState.CREDENTIALS_REQUEST_MASTER_PUBLIC_KEY.name().equals(event.getPanelName())) {

        // We are exiting the credentials wizard as the Trezor is uninitialised and want the welcome wizard

        handoverToWelcomeWizardCreateTrezorWallet();
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

    log.debug("Switch Wallet event: '{}'", event);

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

    // Ensure that the header shows the header after a sync (if the configuration permits)
    if (BitcoinNetworkStatus.SYNCHRONIZED.equals(event.getSummary().getStatus())) {
      boolean viewHeader = Configurations.currentConfiguration.getAppearance().isShowBalance();
      log.debug("Firing event to header viewable to:  {}", viewHeader);
      ViewEvents.fireViewChangedEvent(ViewKey.HEADER, viewHeader);
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

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          ViewEvents.fireProgressChangedEvent(localisedMessage, summary.getPercent());

          // Ensure everyone is aware of the update
          ViewEvents.fireSystemStatusChangedEvent(localisedMessage, summary.getSeverity());
        }
      });
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
      default:
        throw new IllegalStateException("Unknown alert type: " + summary.getAlertType());
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
   *
   * <p>This is not done through a simple SWITCH shutdown event being fire since the order
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
          Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

          // Hide the application frame to prevent user interacting with the detail
          // panels after the exit panel view has hidden
          // It is very tricky to get the timing right so hiding the UI is the safest
          // course of action here
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                // Dim the application using the glass pane
                Panels.applicationFrame.getRootPane().getGlassPane().setVisible(true);
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
                Panels.applicationFrame.getRootPane().getGlassPane().setVisible(false);
                mainView.refresh();
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
                Panels.applicationFrame.setVisible(false);
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

  private void shutdownCurrentWallet(ShutdownEvent.ShutdownType shutdownType) {

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
          Panels.applicationFrame.dispose();
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
          Panels.applicationFrame.invalidate();

          // Rebuild the detail views and alert panels
          mainView.refresh();

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
          Panels.applicationFrame.invalidate();

          // Rebuild the detail views and alert panels
          mainView.refresh();

        }
      });
  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {

      // Show the About screen
      Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());

    }
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

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // Show the Preferences screen
            ViewEvents.fireShowDetailScreenEvent(Screen.SETTINGS);
          }
        });
    }

  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    log.debug("Received quit event (close button). Initiating hard shutdown...");
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);

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
      walletService.writePayments();
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

    // Quick check for relevancy
    switch (event.getEventType()) {
      case SHOW_DEVICE_STOPPED:
      case SHOW_DEVICE_DETACHED:
        // Rely on the hardware wallet wizard to inform the user
        // An alert tends to stack and gets messy/irrelevant
        deferredCredentialsRequestType = CredentialsRequestType.PASSWORD;
        break;
      case SHOW_DEVICE_FAILED:
      case SHOW_DEVICE_READY:
        // Show an alert if the Trezor connects when
        // - there is a current wallet
        // - the current wallet is not a "hard" Trezor wallet
        Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
        if (walletSummary.isPresent()
          && !WalletType.TREZOR_HARD_WALLET.equals(walletSummary.get().getWalletType())
          ) {

          // TODO Currently getting a false positive due to FEST test use of WalletManager
          // during fixture creation

          log.debug("Trezor attached during an unlocked soft wallet session - showing alert");

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
        // Set the deferred credentials request type
        deferredCredentialsRequestType = CredentialsRequestType.TREZOR;
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
   * <p>Restart the hardware wallet service if necessary and subscribe to hardware wallet events</p>
   */
  public void handleHardwareWallets() {

    boolean isServiceAllowed = false;

    // Determine if at least one hardware wallet is selected
    if (Configurations.currentConfiguration.isTrezor()) {
      isServiceAllowed = true;
    }

    // Check if the service is running and is allowed
    if (hardwareWalletService.isPresent() && !isServiceAllowed) {

      // Stop the service, all listeners and clear the CoreServices reference
      CoreServices.stopHardwareWalletService();

      // Clear our reference
      hardwareWalletService = Optional.absent();

      return;
    }

    // Service is allowed and may need to be started
    // If it is present then it is already started
    if (!hardwareWalletService.isPresent()) {

      hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

      if (hardwareWalletService.isPresent()) {

        // (Re)subscribe to hardware wallet events
        // This is required in case the user stops and starts the
        // hardware wallet service during a session
        HardwareWalletEvents.subscribe(this);

        // Start the service
        hardwareWalletService.get().start();

        log.info("Started hardware wallet service");

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
            // Issue the alert
            CoreEvents.fireSecurityEvent(SecuritySummary.newSystemTimeDrift());
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

    // Determine if we are in Trezor mode for the welcome wizard
    WelcomeWizardMode mode = CredentialsRequestType.TREZOR.equals(deferredCredentialsRequestType) ? WelcomeWizardMode.TREZOR : WelcomeWizardMode.STANDARD;

    // For soft wallets the restore goes to the select wallet screen, for Trezor hard wallets go directly to the restore
    final WelcomeWizardState initialState = WelcomeWizardMode.STANDARD.equals(mode) ? WelcomeWizardState.WELCOME_SELECT_WALLET : WelcomeWizardState.RESTORE_WALLET_SELECT_BACKUP;
    // Start building the wizard on the EDT to prevent UI updates
    final WelcomeWizard welcomeWizard = Wizards.newExitingWelcomeWizard(
      initialState, mode
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
  private void handoverToWelcomeWizardCreateTrezorWallet() {

    log.debug("Hand over to welcome wizard (create Trezor wallet)");

    // Handover
    mainView.setShowExitingWelcomeWizard(true);
    mainView.setShowExitingCredentialsWizard(false);

    // Start building the wizard on the EDT to prevent UI updates
    final WelcomeWizard welcomeWizard = Wizards.newExitingWelcomeWizard(
      WelcomeWizardState.TREZOR_CREATE_WALLET_PREPARATION, WelcomeWizardMode.TREZOR
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

    mainView.refresh();

    // Allow time for MainView to refresh
    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

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
    Screen screen = Screen.valueOf(Configurations.currentConfiguration.getAppearance().getCurrentScreen());
    ViewEvents.fireShowDetailScreenEvent(screen);

    // Don't hold up the UI thread with these background operations
    walletExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {
          try {

            // Get a ticker going
            log.debug("Starting exchange...");
            handleExchange();

            // Check for system time drift (runs in the background)
            log.debug("Check for system time drift...");
            handleSystemTimeDrift();

            // Check for Bitcoin URIs
            log.debug("Check for Bitcoin URIs...");
            handleBitcoinURIAlert();

          } catch (Exception e) {
            // TODO localise and put on UI via an alert
            log.error("Services did not start ok. Error was {}", e.getClass().getCanonicalName() + " " + e.getMessage(), e);
          }
        }
      });
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
            java.util.List<PaymentData> paymentDataList = currentWalletService.getPaymentDataList();
            if (paymentDataList != null) {
              for (PaymentData paymentData : paymentDataList) {
                PaymentStatus status = paymentData.getStatus();
                if (status.getStatus().equals(RAGStatus.RED)) {
                  JButton button = Buttons.newAlertPanelButton(getShowHelpAction(), MessageKey.DETAILS, MessageKey.DETAILS_TOOLTIP, AwesomeIcon.QUESTION);

                  // The transaction has not been sent correctly, or change is not spendable, throw a warning alert
                  AlertModel alertModel = Models.newAlertModel(Languages.safeText(MessageKey.SPENDABLE_BALANCE_IS_LOWER), RAGStatus.AMBER, button);
                  ViewEvents.fireAlertAddedEvent(alertModel);
                }
              }
            }
          }
        }
      });

  }

  /**
   * @return The deferred credentials request type
   */
  public CredentialsRequestType getDeferredCredentialsRequestType() {
    return deferredCredentialsRequestType;
  }
}
