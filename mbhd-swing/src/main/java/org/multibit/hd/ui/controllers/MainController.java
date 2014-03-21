package org.multibit.hd.ui.controllers;

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
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.ExchangeTickerService;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.*;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.themes.Theme;
import org.multibit.hd.ui.views.themes.ThemeKey;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
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
public class MainController implements GenericOpenURIEventListener, GenericPreferencesEventListener,
  GenericAboutEventListener, GenericQuitEventListener {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  private Optional<ExchangeTickerService> exchangeTickerService = Optional.absent();

  public MainController() {

    CoreServices.uiEventBus.register(this);

    handleExchange();

    handleTheme();

  }

  /**
   * Handles the changes to the exchange ticker service
   */
  private void handleExchange() {

    // Don't hold up the UI if the exchange doesn't respond
    SafeExecutors.newFixedThreadPool(1).execute(new Runnable() {
      @Override
      public void run() {

        BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
        ExchangeKey exchangeKey = ExchangeKey.valueOf(bitcoinConfiguration.getCurrentExchange());

        if (ExchangeKey.OPEN_EXCHANGE_RATES.equals(exchangeKey)) {
          if (bitcoinConfiguration.getExchangeApiKeys().isPresent()) {
            String apiKey = Configurations.currentConfiguration.getBitcoinConfiguration().getExchangeApiKeys().get();
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
      }
    });

  }

  /**
   * Handles the changes to the theme
   */
  private void handleTheme() {

    Theme newTheme = ThemeKey.valueOf(Configurations.currentConfiguration.getApplicationConfiguration().getCurrentTheme()).theme();
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
    Panels.frame.setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    Panels.frame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

    // Update the views to use the new locale (and any other relevant configuration)
    ViewEvents.fireLocaleChangedEvent();
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
        Panels.frame.invalidate();
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

    // Show the About screen
    Panels.showLightBox(Wizards.newAboutWizard().getWizardScreenHolder());

  }

  @Override
  public void onOpenURIEvent(GenericOpenURIEvent event) {

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
