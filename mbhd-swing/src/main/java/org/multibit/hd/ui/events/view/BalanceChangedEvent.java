package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;
import org.joda.money.BigMoney;

import java.math.BigInteger;

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

  private final BigInteger satoshis;
  private final BigMoney localBalance;
  private final Optional<String> rateProvider;

  /**
   * @param satoshis     The current balance in satoshis
   * @param localBalance The current balance in local currency
   * @param rateProvider The exchange rate provider (e.g. "Bitstamp" or absent if no network)
   */
  public BalanceChangedEvent(BigInteger satoshis, BigMoney localBalance, Optional<String> rateProvider) {

    this.satoshis = satoshis;
    this.localBalance = localBalance;
    this.rateProvider = rateProvider;

  }

  /**
   * @return The Bitcoin balance in satoshis
   */
  public BigInteger getSatoshis() {
    return satoshis;
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
  public Optional<String> getRateProvider() {
    return rateProvider;
  }
}
