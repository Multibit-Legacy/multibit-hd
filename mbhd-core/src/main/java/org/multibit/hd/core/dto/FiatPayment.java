package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>A fiat equivalent to a bitcoin amount</li>
 * </ul>
 * </p>
 */
public class FiatPayment {

  private Optional<BigDecimal> amount = Optional.absent();
  private Optional<Currency> currency = Optional.absent();

  private Optional<String> exchangeName = Optional.absent();
  private Optional<String> rate = Optional.absent();

  /**
   * @return amount of fiat as a BigDecimal
   */
  public Optional<BigDecimal> getAmount() {
    return amount;
  }

  public void setAmount(Optional<BigDecimal> amount) {
    Preconditions.checkNotNull(amount);
    this.amount = amount;
  }

  /**
   * @return The local currency associated with this payment (e.g. "USD")
   */
  public Optional<Currency> getCurrency() {
    return currency;
  }

  public void setCurrency(Optional<Currency> currency) {
    Preconditions.checkNotNull(currency);
    this.currency = currency;
  }

  /**
   * @return The exchange name
   */
  public Optional<String> getExchangeName() {
    return exchangeName;
  }

  public void setExchangeName(Optional<String> exchangeName) {
    Preconditions.checkNotNull(exchangeName);
    this.exchangeName = exchangeName;
  }

  /**
   * @return the exchange rate at the time of creation (use String to ease representation)
   */
  public Optional<String> getRate() {
    return rate;
  }

  public void setRate(Optional<String> rate) {
    Preconditions.checkNotNull(rate);
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiatPayment that = (FiatPayment) o;

    if (!amount.equals(that.amount)) return false;
    if (!currency.equals(that.currency)) return false;
    if (!exchangeName.equals(that.exchangeName)) return false;
    if (!rate.equals(that.rate)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = amount.hashCode();
    result = 31 * result + currency.hashCode();
    result = 31 * result + exchangeName.hashCode();
    result = 31 * result + rate.hashCode();
    return result;
  }

  public int compareTo(FiatPayment other) {
    if (!amount.isPresent() || !other.getAmount().isPresent()) {
      return 0;
    }
    return amount.get().compareTo(other.getAmount().get());
  }

  @Override
  public String toString() {
    return "FiatPayment{" +
            "amount=" + amount +
            ", currency=" + currency +
            ", exchangeName=" + exchangeName +
            ", rate=" + rate +
            '}';
  }

  public boolean hasData() {
    return amount.isPresent() || currency.isPresent() || exchangeName.isPresent() || rate.isPresent();
  }
}
