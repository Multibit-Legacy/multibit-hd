package org.multibit.hd.core.dto;

import org.joda.money.BigMoney;

import javax.annotation.Nullable;

/**
 *  <p>DTO to provide the following to WalletService:<br>
 *  <ul>
 *  <li>A fiat equivalent to a bitcoin amount</li>
 *  </ul>
 * </p>
 *  
 */
public class FiatPayment {
  private BigMoney amount;
  private String exchange;
  private String rate;

  /**
   * The Joda money representation of the amount.
   * This is a BigMoney (BigDecimal wrapped with a currency code)
   * @return amount of fiat as a BigMoney
   */
  public BigMoney getAmount() {
    return amount;
  }

  public void setAmount(@Nullable BigMoney amount) {
    this.amount = amount;
  }

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiatPayment that = (FiatPayment) o;

    if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
    if (exchange != null ? !exchange.equals(that.exchange) : that.exchange != null) return false;
    if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = amount != null ? amount.hashCode() : 0;
    result = 31 * result + (exchange != null ? exchange.hashCode() : 0);
    result = 31 * result + (rate != null ? rate.hashCode() : 0);
    return result;
  }

  public int compareTo(FiatPayment other) {
    return amount.compareTo(other.getAmount());
  }
}
