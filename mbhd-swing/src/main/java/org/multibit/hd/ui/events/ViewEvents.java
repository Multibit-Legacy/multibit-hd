package org.multibit.hd.ui.events;

import org.joda.money.BigMoney;
import org.multibit.hd.core.api.RAGStatus;
import org.multibit.hd.core.services.CoreServices;
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
   * <p>Broadcast a new exchange rate change event</p>
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

    CoreServices.uiEventBus.post(new BalanceChangeEvent(
      btcbalance,
      localBalance,
      rateProvider
    ));

  }

  /**
   * <p>Broadcast a new balance click event.</p>
   */
  public static void fireBalanceClickEvent() {
    CoreServices.uiEventBus.post(new BalanceClickEvent());
  }

  /**
   * <p>Broadcast a new show detail screen event</p>
   */
  public static void fireShowDetailScreenEvent(Screen screen) {
    CoreServices.uiEventBus.post(new ShowDetailScreenEvent(screen));
  }

  /**
   * <p>Broadcast a new show detail screen event</p>
   */
  public static void fireShowAlertEvent() {
    CoreServices.uiEventBus.post(new ShowAlertEvent("Something happened", RAGStatus.RED));
  }

  /**
   * <p>Broadcast a new system status change event</p>
   */
  public static void fireSystemStatusEvent(RAGStatus severity) {
    CoreServices.uiEventBus.post(new SystemStatusChangeEvent(severity));
  }
}