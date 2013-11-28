package org.multibit.hd.ui.events;

import org.joda.money.BigMoney;
import org.multibit.hd.core.services.CoreServices;

import java.math.BigDecimal;

/**
 * <p>Factory to provide the following to application API:</p>
 * <ul>
 * <li>Entry point to broadcast core events</li>
 * </ul>
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
   * @param btcbalance The current balance in BTC
   * @param localBalance   The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp")
   */
  public static void fireBalanceChangeEvent(
    BigMoney btcbalance,
    BigMoney localBalance,
    String rateProvider
  ) {

    CoreServices.uiEventBus.post(new BalanceChangeEvent(btcbalance, localBalance, rateProvider));

  }

}

