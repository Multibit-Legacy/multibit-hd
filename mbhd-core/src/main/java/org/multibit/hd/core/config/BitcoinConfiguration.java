package org.multibit.hd.core.config;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of Bitcoin related items</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public BitcoinConfiguration {

  private String bitcoinSymbol = "ICON";
  private String exchangeClassName;

  /**
   * @return The Bitcoin symbol to use
   */
  public String getBitcoinSymbol() {
    return bitcoinSymbol;
  }

  public void setBitcoinSymbol(String bitcoinSymbol) {
    this.bitcoinSymbol = bitcoinSymbol;
  }

  /**
   * @return A deep copy of this object
   */
  public BitcoinConfiguration deepCopy() {

    BitcoinConfiguration bitcoin = new BitcoinConfiguration();

    bitcoin.setBitcoinSymbol(getBitcoinSymbol());

    return bitcoin;
  }

  public String getExchangeClassName() {
    return exchangeClassName;
  }

  public void setExchangeClassName(String exchangeClassName) {
    this.exchangeClassName = exchangeClassName;
  }
}
