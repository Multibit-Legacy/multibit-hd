package org.multibit.hd.ui.events.view;

import org.joda.money.BigMoney;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for a balance change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BalanceChangedEvent {

  private final BigMoney btcBalance;
  private final BigMoney localBalance;
  private final String rateProvider;

  /**
   * @param btcbalance   The current balance in BTC
   * @param localBalance The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp")
   */
  public BalanceChangedEvent(BigMoney btcbalance, BigMoney localBalance, String rateProvider) {

    this.btcBalance = btcbalance;
    this.localBalance = localBalance;
    this.rateProvider = rateProvider;

  }

  /**
   * @return The Bitcoin balance
   */
  public BigMoney getBtcBalance() {
    return btcBalance;
  }

  /**
   * @return The local currency balance
   */
  public BigMoney getLocalBalance() {
    return localBalance;
  }

  /**
   * @return The exchange rate provider
   */
  public String getRateProvider() {
    return rateProvider;
  }
}
