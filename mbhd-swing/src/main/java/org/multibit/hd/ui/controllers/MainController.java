package org.multibit.hd.ui.controllers;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.bitcoin.uri.BitcoinURIParseException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.ConfigurationChangedEvent;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.*;
import org.multibit.hd.ui.services.BitcoinURIListeningService;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Theme;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.password.PasswordState;
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
public class MainController implements GenericOpenURIEventListener, GenericPreferencesEventListener, GenericAboutEventListener, GenericQuitEventListener {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private Optional<ExchangeTickerService> exchangeTickerService = Optional.absent();

  private Optional<BitcoinNetworkService> bitcoinNetworkService = Optional.absent();

  private final BitcoinURIListeningService bitcoinURIListeningService;

  /**
   * @param bitcoinURIListeningService The Bitcoin URI listening service (must be present to permit a UI)
   */
  public MainController(BitcoinURIListeningService bitcoinURIListeningService) {

    Preconditions.checkNotNull(bitcoinURIListeningService, "'bitcoinURIListeningService' must be present");

    CoreServices.uiEventBus.register(this);

    this.bitcoinURIListeningService = bitcoinURIListeningService;

  }

  /**
   * Handles the changes to the exchange ticker service
   */
  private void handleExchange() {

//    // Don't hold up the UI if the exchange doesn't respond
//    SafeExecutors.newSingleThreadExecutor().execute(new Runnable() {
//      @Override
//      public void run() {
//
        BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();
        ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());

        if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
          if (bitcoinConfiguration.getExchangeApiKeys().containsKey(ExchangeKey.OPEN_EXCHANGE_RATES.name())) {
            String apiKey = Configurations.currentConfiguration.getBitcoin().getExchangeApiKeys().get(ExchangeKey.OPEN_EXCHANGE_RATES.name());
            exchangeKey.getExchange().getExchangeSpecification().setApiKey(apiKey);
          }
        }

        // Stop (with block) any existing exchange ticker service
        if (exchangeTickerService.isPresent()) {
          exchangeTickerService.get().stopAndWait();
        }

        // Create and start the exchange ticker service
        exchangeTickerService = Optional.of(CoreServices.newExchangeService(bitcoinConfiguration));
        exchangeTickerService.get().start();
//      }
//    });

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

    Locale locale = Configurations.currentConfiguration.getLocale();

    // Ensure the resource bundle is reset
    ResourceBundle.clearCache();

    // Update the frame to allow for LTR or RTL transition
    Panels.applicationFrame.setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    Panels.applicationFrame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

    // Update the views to use the new locale (and any other relevant configuration)
    ViewEvents.fireLocaleChangedEvent();
  }

  /**
   * <p>Start the backup manager</p>
   */
  private void handleBackupManager() {

    // Locate the installation directory
    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Initialise backup (must be before Bitcoin network starts and on the main thread)
    BackupManager.INSTANCE.initialise(applicationDataDirectory, null); // TODO the null needs replacing with the cloud backup location

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
   * <p>Start the Bitcoin network</p>
   */
  private void handleBitcoinNetworkStart() {
    // Only start the network once
    if (bitcoinNetworkService.isPresent()) {
      return;
    }

    bitcoinNetworkService = Optional.of(CoreServices.getOrCreateBitcoinNetworkService());

    // Start the network now that the password has been validated
    bitcoinNetworkService.get().start();

    if (bitcoinNetworkService.get().isStartedOk()) {
      bitcoinNetworkService.get().downloadBlockChainInBackground();
    }
  }

  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    if (!event.isExitCancel()) {

      // Successful wizard interaction

      // Special case for the password entry screen since it represents an
      // incomplete initialisation of the UI
      if (PasswordState.PASSWORD_ENTER_PASSWORD.name().equals(event.getPanelName())) {

        // Show the initial screen as soon as possible to reassure the user
        Screen screen = Screen.valueOf(Configurations.currentConfiguration.getApplication().getCurrentScreen());
        ControllerEvents.fireShowDetailScreenEvent(screen);

        // Don't hold up the UI thread with these background operations
        SafeExecutors.newSingleThreadExecutor("initialise").submit(new Runnable() {
          @Override
          public void run() {

            // Get a ticker going
            handleExchange();

            // Start the backup manager
            handleBackupManager();

            // Check for Bitcoin URIs
            handleBitcoinURIAlert();

            // Lastly start the Bitcoin network
            handleBitcoinNetworkStart();

          }
        });
      }
    }
  }

  /**
   * <p>Update all views to use the current configuration</p>
   *
   * @param event The change configuration event
   */
  @Subscribe
  public synchronized void onConfigurationChangedEvent(ConfigurationChangedEvent event) {

    log.trace("Received 'configuration changed' event");

    Preconditions.checkNotNull(event, "'event' must be present");

    // Switch the exchange ticker service
    handleExchange();

    // Switch the theme before any other UI building takes place
    handleTheme();

    // Rebuild MainView contents
    handleLocale();

    // Allow time for the views to update
    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

    // Ensure the Swing thread can perform a complete refresh
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Panels.applicationFrame.invalidate();
      }
    });

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

    // Append general security advice allowing for LTR/RTL
    ControllerEvents.fireAddAlertEvent(
      Models.newAlertModel(
        localisedMessage + " " + Languages.safeText(CoreMessageKey.SECURITY_ADVICE),
        summary.getSeverity())
    );
  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    log.debug("Called");

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

    // Action to show the "send Bitcoin" wizard
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        ControllerEvents.fireRemoveAlertEvent();
        Panels.showLightBox(Wizards.newSendBitcoinWizard(Optional.of(bitcoinURI)).getWizardScreenHolder());

      }
    };
    JButton button = Buttons.newAlertPanelButton(action, MessageKey.YES, AwesomeIcon.CHECK);

    // Attempt to decode the Bitcoin URI
    Optional<String> alertMessage = Formats.formatAlertMessage(bitcoinURI);

    // If there is sufficient information in the Bitcoin URI display it to the user as an alert
    if (alertMessage.isPresent()) {

      AlertModel alertModel = Models.newAlertModel(
        alertMessage.get(),
        RAGStatus.AMBER,
        button
      );

      // Add the alert
      ControllerEvents.fireAddAlertEvent(alertModel);
    }


    // Show a Bitcoin URI alert
    ControllerEvents.fireAddAlertEvent(Models.newAlertModel("Bitcoin URI", RAGStatus.PINK));

  }

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    // Show the Preferences screen
    ControllerEvents.fireShowDetailScreenEvent(Screen.SETTINGS);

  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    // Immediately shutdown without requesting confirmation
    CoreEvents.fireShutdownEvent();

  }

}
