package org.multibit.hd.ui.i18n;

import org.multibit.hd.ui.fonts.AwesomeIcon;

import java.math.BigDecimal;

/**
 * <p>Enum to provide the following to controllers:</p>
 * <ul>
 * <li>Various Bitcoin symbols</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum BitcoinSymbol {

  ICON(AwesomeIcon.BITCOIN.getChar().toString(),BigDecimal.ONE),
  BTC("BTC",BigDecimal.ONE),
  MBTC("mBTC",new BigDecimal(1_000)),
  UBTC("\u00B5BTC",new BigDecimal(1_000_000)),
  XBT("XBT",BigDecimal.ONE),
  MXBT("mXBT",new BigDecimal(1_000)),
  UXBT("\u00B5XBT",new BigDecimal(1_000_000)),
  SATOSHI("s",new BigDecimal(100_000_000)),

  // End of enum
  ;

  private final String symbol;
  private final BigDecimal multiplier;

  BitcoinSymbol(String symbol, BigDecimal multiplier) {
    this.symbol = symbol;
    this.multiplier = multiplier;
  }

  /**
   * @return The Unicode value of the symbol if applicable (Font Awesome requires a
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * @return The multiplier to use on plain amounts for this symbol to be accurate
   */
  public BigDecimal multiplier() {
    return multiplier;
  }
}
