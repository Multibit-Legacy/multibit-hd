package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import org.joda.money.BigMoney;
import org.multibit.hd.core.api.RAGStatus;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast application events associated with the UI</li>
 * </ul>
 * <p>An application event is a high level event with specific semantics. Normally a
 * low level event (such as a mouse click) will initiate it.</p>
 *
 * @since 0.0.1
 *  
 */
public class ViewEvents {

  private static final Logger log = LoggerFactory.getLogger(ViewEvents.class);

  /**
   * Utilities have a private constructor
   */
  private ViewEvents() {
  }

  /**
   * <p>Broadcast a new "locale changed" event</p>
   */
  public static void fireLocaleChangedEvent() {
    CoreServices.uiEventBus.post(new LocaleChangedEvent());
  }

  /**
   * <p>Broadcast a new "balance changed" event</p>
   *
   * @param btcbalance   The current balance in BTC
   * @param localBalance The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp")
   */
  public static void fireBalanceChangedEvent(
    BigMoney btcbalance,
    BigMoney localBalance,
    String rateProvider
  ) {

    log.debug("Firing 'balance changed' event");
    CoreServices.uiEventBus.post(new BalanceChangedEvent(
      btcbalance,
      localBalance,
      rateProvider
    ));

  }

  /**
   * <p>Broadcast a new "system status changed" event</p>
   *
   * @param localisedMessage The localised message to display alongside the severity
   * @param severity         The system status severity (normally in line with an alert)
   */
  public static void fireSystemStatusChangedEvent(String localisedMessage, RAGStatus severity) {
    log.debug("Firing 'system status changed' event");
    CoreServices.uiEventBus.post(new SystemStatusChangedEvent(localisedMessage, severity));
  }

  /**
   * <p>Broadcast a new "progress changed" event </p>
   *
   * @param localisedMessage The localised message to display alongside the progress bar
   * @param percent          The amount to display in percent
   */
  public static void fireProgressChangedEvent(String localisedMessage, int percent) {
    log.debug("Firing 'progress changed' event");
    CoreServices.uiEventBus.post(new ProgressChangedEvent(localisedMessage, percent));
  }

  /**
   * <p>Broadcast a new "alert added" event</p>
   *
   * @param alertModel The alert model for the new display
   */
  public static void fireAlertAddedEvent(AlertModel alertModel) {
    log.debug("Firing 'alert added' event");
    CoreServices.uiEventBus.post(new AlertAddedEvent(alertModel));
  }

  /**
   * <p>Broadcast a new "alert removed" event</p>
   */
  public static void fireAlertRemovedEvent() {
    log.debug("Firing 'alert removed' event");
    CoreServices.uiEventBus.post(new AlertRemovedEvent());
  }

  /**
   * <p>Broadcast a new "wizard button enabled" event</p>
   *
   * @param panelName    The panel name to which this applies
   * @param wizardButton The wizard button to which this applies
   * @param enabled      True if the button should be enabled
   */
  public static void fireWizardButtonEnabledEvent(String panelName, WizardButton wizardButton, boolean enabled) {
    log.debug("Firing 'wizard button enabled {}' event: {}", panelName, enabled);
    CoreServices.uiEventBus.post(new WizardButtonEnabledEvent(panelName, wizardButton, enabled));

  }

  /**
   * <p>Broadcast a new "wizard panel model changed" event</p>
   *
   * @param panelName  The panel name to which this applies
   * @param panelModel The panel model
   */
  public static void fireWizardPanelModelChangedEvent(String panelName, Optional panelModel) {
    log.debug("Firing 'wizard panel model changed' event");
    CoreServices.uiEventBus.post(new WizardPanelModelChangedEvent(panelName, panelModel));
  }

  /**
   * <p>Broadcast a new "wizard model changed" event - normally consumed by "finishing" panels</p>
   *
   * @param panelName The panel name to restrict the update to a particular panel
   */
  public static void fireWizardModelChangedEvent(String panelName) {
    log.debug("Firing 'wizard model changed' event");
    CoreServices.uiEventBus.post(new WizardModelChangedEvent(panelName));
  }

  /**
   * <p>Broadcast a new "verification status changed" event</p>
   *
   * @param panelName The panel name to which this applies
   * @param status    True if the verification is OK
   */
  public static void fireVerificationStatusChangedEvent(String panelName, boolean status) {
    log.debug("Firing 'verification status changed' event: {}", status);
    CoreServices.uiEventBus.post(new VerificationStatusChangedEvent(panelName, status));
  }
}