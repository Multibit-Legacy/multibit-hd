package org.multibit.hd.core.events;

import org.joda.money.BigMoney;
import org.joda.time.DateTime;
import org.multibit.hd.core.utils.Dates;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of an exchange rate change</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ExchangeRateChangedEvent {

  private final BigMoney rate;
  private final String exchangeName;
  private final DateTime expires;

  /**
   * @param rate         The rate with the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   * @param exchangeName The exchange name
   * @param expires      The expiry timestamp of this rate
   */
  public ExchangeRateChangedEvent(BigMoney rate, String exchangeName, DateTime expires) {
    this.rate = rate;
    this.exchangeName = exchangeName;
    this.expires = expires;
  }

  /**
   * @return The rate in the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   */
  public BigMoney getRate() {
    return rate;
  }

  /**
   * @return The name of the exchange (e.g. "Bitstamp")
   */
  public String getExchangeName() {
    return exchangeName;
  }

  /**
   * @return The expiry timestamp of this exchange rate
   */
  public DateTime getExpires() {
    return expires;
  }

  /**
   * @return True if this rate is still within the expiry timestamp
   */
  public boolean isValid() {
    return Dates.nowUtc().isBefore(expires);
  }

  @Override
  public String toString() {
    return "ExchangeRateChangedEvent{" +
      "rate=" + rate +
      ", exchangeName='" + exchangeName + '\'' +
      ", expires=" + expires +
      '}';
  }
}
