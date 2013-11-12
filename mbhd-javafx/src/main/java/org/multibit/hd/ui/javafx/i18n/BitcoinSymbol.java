package org.multibit.hd.ui.javafx.i18n;

import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;

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

  FONT_AWESOME_ICON(AwesomeIcon.BITCOIN.getChar().toString()),
  ECOGEX("\u0243"),
  BTC("BTC"),
  XBT("XBT"),
  MBTC("mBTC"),
  UBTC("\u00B5BTC"),
  SATOSHI("s"),

  // End of enum
  ;

  private final String symbol;

  BitcoinSymbol(String symbol) {
    this.symbol = symbol;
  }

  /**
   * @return The Unicode value of the symbol if applicable (Font Awesome requires a
   */
  public String getSymbol() {
    return symbol;
  }
}
