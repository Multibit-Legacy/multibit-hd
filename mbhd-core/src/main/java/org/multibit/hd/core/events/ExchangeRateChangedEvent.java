package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.multibit.hd.core.utils.Dates;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification of an exchange rate change</li>
 * </ul>
 *
 * <p>This is an infrequent event</p>

 * @since 0.0.1
 *
 */
public class ExchangeRateChangedEvent implements CoreEvent {

  private final BigDecimal rate;
  private final Currency currency;
  private final Optional<String> rateProvider;
  private final DateTime expires;

  /**
   * @param rate         The rate with the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   * @param rateProvider The rate provider (absent if unknown)
   * @param expires      The expiry timestamp of this rate
   */
  public ExchangeRateChangedEvent(BigDecimal rate, Currency currency, Optional<String> rateProvider, DateTime expires) {
    this.rate = rate;
    this.currency = currency;
    this.rateProvider = rateProvider;
    this.expires = expires;
  }

  /**
   * @return The rate in the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   */
  public BigDecimal getRate() {
    return rate;
  }

  /**
   * @return The name of the exchange (e.g. "Bitstamp" or absent if "None")
   */
  public Optional<String> getRateProvider() {
    return rateProvider;
  }

  /**
   * @return The expiry timestamp of this exchange rate
   */
  public DateTime getExpires() {
    return expires;
  }

  /**
   * @return True if this rate is still within the expiry timestamp and the rate provider is present
   */
  public boolean isValid() {
    return Dates.nowUtc().isBefore(expires) && rateProvider.isPresent();
  }

  public Currency getCurrency() {
    return currency;
  }

  @Override
  public String toString() {
    return "ExchangeRateChangedEvent{" +
      "rate=" + rate +
      ", currency=" + currency +
      ", exchangeName='" + rateProvider.or("None") + '\'' +
      ", expires=" + expires +
      '}';
  }
}
