package org.multibit.hd.ui.controllers;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ChangeLocaleEvent;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.views.components.Panels;
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
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  public MainController() {

    CoreServices.uiEventBus.register(this);

  }

  /**
   * <p>Update the application locale</p>
   *
   * @param event The change locale event
   */
  @Subscribe
  public synchronized void onChangeLocaleEvent(ChangeLocaleEvent event) {

    log.trace("Received 'locale change' event");

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getLocale(), "'locale' must be present");

    Locale locale = event.getLocale();

    // Configure all Swing components to use the new locale
    Locale.setDefault(locale);
    Panels.frame.setLocale(locale);

    // Ensure the resource bundle is reset
    ResourceBundle.clearCache();

    // Update the main configuration
    Configurations.currentConfiguration.getI18NConfiguration().setLocale(locale);

    // Update the frame to allow for LTR or RTL transition
    Panels.frame.setLocale(locale);

    // Ensure LTR and RTL language formats are in place
    Panels.frame.applyComponentOrientation(ComponentOrientation.getOrientation(locale));

    // Update the views
    ViewEvents.fireLocaleChangedEvent();

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
        Languages.append(localisedMessage, Languages.safeText(CoreMessageKey.SECURITY_ADVICE), " "),
        summary.getSeverity())
    );
  }

}
