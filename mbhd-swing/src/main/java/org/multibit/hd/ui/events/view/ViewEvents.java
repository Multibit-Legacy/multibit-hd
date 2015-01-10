package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetail;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast application events associated with the UI</li>
 * </ul>
 * <p>An application event is a high level event with specific semantics. Normally a
 * low level event (such as a mouse click) will initiate it.</p>
 *
 * <p>It is expected that ViewEvents will interact with UI components and as such is
 * expected to execute on the EDT.</p>
 *
 * @since 0.0.1
 */
public class ViewEvents {

  private static final Logger log = LoggerFactory.getLogger(ViewEvents.class);

  /**
   * Utilities have a private constructor
   */
  private ViewEvents() {
  }

  /**
   * <p>Broadcast a new "balance changed" event</p>
   *
   * @param coinBalance  The current balance in coins
   * @param localBalance The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp")
   */
  public static void fireBalanceChangedEvent(
    final Coin coinBalance,
    final BigDecimal localBalance,
    final Optional<String> rateProvider
  ) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");

    log.trace("Firing 'balance changed' event");
    CoreServices.uiEventBus.post(
      new BalanceChangedEvent(
        coinBalance,
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
  public static void fireSystemStatusChangedEvent(final String localisedMessage, final RAGStatus severity) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'system status changed' event");
    CoreServices.uiEventBus.post(new SystemStatusChangedEvent(localisedMessage, severity));

  }

  /**
   * <p>Broadcast a new "progress changed" event </p>
   *
   * @param localisedMessage The localised message to display alongside the progress bar
   * @param percent          The amount to display in percent
   */
  public static void fireProgressChangedEvent(final String localisedMessage, final int percent) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'progress changed' event: '{}'", percent);
    CoreServices.uiEventBus.post(new ProgressChangedEvent(localisedMessage, percent));

  }

  /**
   * <p>Broadcast a new "alert added" event</p>
   *
   * @param alertModel The alert model for the new display
   */
  public static void fireAlertAddedEvent(final AlertModel alertModel) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'alert added' event");
    CoreServices.uiEventBus.post(new AlertAddedEvent(alertModel));
  }

  /**
   * <p>Broadcast a new "switch wallet" event</p>
   */
  public static void fireSwitchWalletEvent() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.debug("Firing 'switch wallet' event");
    CoreServices.uiEventBus.post(new SwitchWalletEvent());

  }

  /**
   * <p>Broadcast a new "alert removed" event</p>
   */
  public static void fireAlertRemovedEvent() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'alert removed' event");
    CoreServices.uiEventBus.post(new AlertRemovedEvent());
  }

  /**
   * <p>Broadcast a new "wallet detail changed" event</p>
   */
  public static void fireWalletDetailChangedEvent(final WalletDetail walletDetail) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'walletDetailChanged' event");
    CoreServices.uiEventBus.post(new WalletDetailChangedEvent(walletDetail));

  }

  /**
   * <p>Broadcast a new "wizard button enabled" event</p>
   *
   * @param panelName    The panel name to which this applies
   * @param wizardButton The wizard button to which this applies
   * @param enabled      True if the button should be enabled
   */
  public static void fireWizardButtonEnabledEvent(
    final String panelName,
    final WizardButton wizardButton,
    final boolean enabled
  ) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'wizard button enabled {}' event: {}", panelName, enabled);
    CoreServices.uiEventBus.post(new WizardButtonEnabledEvent(panelName, wizardButton, enabled));

  }

  /**
   * <p>Broadcast a new "wizard hide" event</p>
   *
   * @param panelName    The unique panel name to which this applies (use screen name for detail screens)
   * @param wizardModel  The wizard model containing all the user data
   * @param isExitCancel True if this hide event comes as a result of an exit or cancel
   */
  public static void fireWizardHideEvent(
    final String panelName,
    final AbstractWizardModel wizardModel,
    final boolean isExitCancel
  ) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'wizard hide' event");
    CoreServices.uiEventBus.post(new WizardHideEvent(panelName, wizardModel, isExitCancel));

  }

  /**
   * <p>Broadcast a new "wizard popover hide" event</p>
   *
   * @param panelName    The unique panel name to which this applies (use screen name for detail screens)
   * @param isExitCancel True if this hide event comes as a result of an exit or cancel
   */
  public static void fireWizardPopoverHideEvent(final String panelName, final boolean isExitCancel) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'wizard popover hide' event");
    CoreServices.uiEventBus.post(new WizardPopoverHideEvent(panelName, isExitCancel));

  }

  /**
   * <p>Broadcast a new "wizard deferred hide" event</p>
   *
   * @param panelName    The unique panel name to which this applies (use screen name for detail screens)
   * @param isExitCancel True if this deferred hide event comes as a result of an exit or cancel
   */
  public static void fireWizardDeferredHideEvent(final String panelName, final boolean isExitCancel) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'wizard deferred hide' event");
    CoreServices.uiEventBus.post(new WizardDeferredHideEvent(panelName, isExitCancel));

  }

  /**
   * <p>Broadcast a new "component changed" event</p>
   *
   * @param panelName      The unique panel name to which this applies (use screen name for detail screens)
   * @param componentModel The component model containing the change (absent if the component has no model)
   */
  public static void fireComponentChangedEvent(final String panelName, final Optional componentModel) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'component changed' event");
    CoreServices.uiEventBus.post(new ComponentChangedEvent(panelName, componentModel));

  }

  /**
   * <p>Broadcast a new "verification status changed" event</p>
   *
   * @param panelName The panel name to which this applies
   * @param status    True if the verification is OK
   */
  public static void fireVerificationStatusChangedEvent(final String panelName, final boolean status) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'verification status changed' event: {}", status);
    CoreServices.uiEventBus.post(new VerificationStatusChangedEvent(panelName, status));

  }

  /**
   * <p>Broadcast a new "view changed" event</p>
   *
   * @param viewKey The view to which this applies
   * @param visible True if the view is "visible" (could be reduced height etc)
   */
  public static void fireViewChangedEvent(final ViewKey viewKey, final boolean visible) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "ViewEvents are expected to run on the EDT thread");
    log.trace("Firing 'view changed' event: {}", visible);
    CoreServices.uiEventBus.post(new ViewChangedEvent(viewKey, visible));

  }

}