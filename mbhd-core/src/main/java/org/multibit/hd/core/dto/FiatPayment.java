package org.multibit.hd.core.dto;

/**
 *  <p>DTO to provide the following to WalletService:<br>
 *  <ul>
 *  <li>A fiat equivalent to a bitcoin amount</li>
 *  </ul>
 * </p>
 *  
 */
public class FiatPayment {
  private String amount;
  private String exchange;
  private String rate;
  private String currency;

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiatPayment that = (FiatPayment) o;

    if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
    if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
    if (exchange != null ? !exchange.equals(that.exchange) : that.exchange != null) return false;
    if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = amount != null ? amount.hashCode() : 0;
    result = 31 * result + (exchange != null ? exchange.hashCode() : 0);
    result = 31 * result + (rate != null ? rate.hashCode() : 0);
    result = 31 * result + (currency != null ? currency.hashCode() : 0);
    return result;
  }
}
