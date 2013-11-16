package org.multibit.hd.ui.javafx.config;

import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of Bitcoin related items</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BitcoinConfiguration {

  private BitcoinSymbol bitcoinSymbol = BitcoinSymbol.ICON;

  /**
   * @return The Bitcoin symbol to use
   */
  public BitcoinSymbol getBitcoinSymbol() {
    return bitcoinSymbol;
  }

  public void setBitcoinSymbol(BitcoinSymbol bitcoinSymbol) {
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
}
