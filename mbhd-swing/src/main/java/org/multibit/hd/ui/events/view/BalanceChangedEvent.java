package org.multibit.hd.ui.events.view;

import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;

import java.math.BigDecimal;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a balance change has occurred</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BalanceChangedEvent implements ViewEvent {

  /**
   * Spendable balance
   */
  private final Coin coinBalance;

  /**
   * Total balance included unconfirmed
   */
  private final Coin coinWithUnconfirmedBalance;
  private final BigDecimal localBalance;
  private final Optional<String> rateProvider;

  /**
   * @param coinBalance  The current spendable balance in coins
   * @param coinWithUnconfirmedBalance The current balance including unconfirmed
   * @param localBalance The current spendable balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp" or absent if no network)
   */
  public BalanceChangedEvent(Coin coinBalance, Coin coinWithUnconfirmedBalance, BigDecimal localBalance, Optional<String> rateProvider) {

    this.coinBalance = coinBalance;
    this.coinWithUnconfirmedBalance = coinWithUnconfirmedBalance;
    this.localBalance = localBalance;
    this.rateProvider = rateProvider;

  }

  /**
   * @return The Bitcoin spendable balance in coins
   */
  public Coin getCoinBalance() {
    return coinBalance;
  }

  /**
   * @return The Bitcoin balance including unconfirmed in coins
   */
  public Coin getCoinWithUnconfirmedBalance() {
    return coinWithUnconfirmedBalance;
  }

  /**
   * @return The local spendable currency balance
   */
  public BigDecimal getLocalBalance() {
    return localBalance;
  }

  /**
   * @return The exchange rate provider
   */
  public Optional<String> getRateProvider() {
    return rateProvider;
  }

  @Override
  public String toString() {
    return "BalanceChangedEvent{" +
            "coinBalance=" + coinBalance +
            "coinWithUnconfirmedBalance=" + coinWithUnconfirmedBalance +
            ", localBalance=" + localBalance +
            ", rateProvider=" + rateProvider +
            '}';
  }
}
