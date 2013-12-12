package org.multibit.hd.ui.events.view;

import org.joda.money.BigMoney;
import org.multibit.hd.core.api.RAGStatus;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.controller.ShowDetailScreenEvent;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.views.Screen;

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

  /**
   * Utilities have a private constructor
   */
  private ViewEvents() {
  }

  /**
   * <p>Broadcast a new "exchange rate change" event</p>
   *
   * @param btcbalance   The current balance in BTC
   * @param localBalance The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp")
   */
  public static void fireBalanceChangeEvent(
    BigMoney btcbalance,
    BigMoney localBalance,
    String rateProvider
  ) {

    CoreServices.uiEventBus.post(new BalanceChangedEvent(
      btcbalance,
      localBalance,
      rateProvider
    ));

  }

  /**
   * <p>Broadcast a new "show detail screen" event</p>
   *
   * @param screen The screen to show
   */
  public static void fireShowDetailScreenEvent(Screen screen) {
    CoreServices.uiEventBus.post(new ShowDetailScreenEvent(screen));
  }

  /**
   * <p>Broadcast a new "system status changed" event</p>
   *
   * @param localisedMessage The localised message to display alongside the severity
   * @param severity         The system status severity (normally in line with an alert)
   */
  public static void fireSystemStatusChangedEvent(String localisedMessage, RAGStatus severity) {
    CoreServices.uiEventBus.post(new SystemStatusChangedEvent(localisedMessage, severity));
  }

  /**
   * <p>Broadcast a new "progress changed" event </p>
   *
   * @param localisedMessage The localised message to display alongside the progress bar
   * @param percent          The amount to display in percent
   */
  public static void fireProgressChangedEvent(String localisedMessage, int percent) {
    CoreServices.uiEventBus.post(new ProgressChangedEvent(localisedMessage, percent));
  }

  /**
   * <p>Broadcast a new "alert changed" event</p>
   *
   * @param alertModel The alert model for the new display
   */
  public static void fireAlertChangedEvent(AlertModel alertModel) {
    CoreServices.uiEventBus.post(new AlertChangedEvent(alertModel));
  }

  /**
   * <p>Broadcast a new "alert removed" event</p>
   */
  public static void fireAlertRemovedEvent() {
    CoreServices.uiEventBus.post(new AlertRemovedEvent());
  }
}