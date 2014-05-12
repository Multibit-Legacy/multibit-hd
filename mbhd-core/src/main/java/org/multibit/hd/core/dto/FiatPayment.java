package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.multibit.hd.core.config.Configurations;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>A fiat equivalent to a bitcoin amount</li>
 * </ul>
 * </p>
 *
 * TODO The values here need more strict handling (which should be final, mandatory, optional to avoid nulls in hashCode/equals etc)
 */
public class FiatPayment {

  private BigDecimal amount;
  private Currency currency= Currency.getInstance(Configurations.currentConfiguration.getBitcoin().getLocalCurrencyCode());

  private Optional<String> exchangeName = Optional.absent();
  private Optional<String> rate = Optional.absent();

  /**
   * @return amount of fiat as a BigDecimal
   */
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * @return The local currency associated with this payment (e.g. "USD")
   */
  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  /**
   * @return The exchange name
   */
  public Optional<String> getExchangeName() {
    return exchangeName;
  }

  public void setExchangeName(String exchangeName) {
    this.exchangeName = Optional.fromNullable(exchangeName);
  }

  /**
   * @return the exchange rate at the time of creation (use String to ease representation)
   */
  public Optional<String> getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = Optional.fromNullable(rate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiatPayment that = (FiatPayment) o;

    if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
    if (!currency.equals(that.currency)) return false;
    if (!exchangeName.equals(that.exchangeName)) return false;
    if (!rate.equals(that.rate)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = amount != null ? amount.hashCode() : 0;
    result = 31 * result + currency.hashCode();
    result = 31 * result + exchangeName.hashCode();
    result = 31 * result + rate.hashCode();
    return result;
  }

  public int compareTo(FiatPayment other) {
    return amount.compareTo(other.getAmount());
  }
}
