package org.multibit.hd.ui.i18n;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

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

  /**
   * The Font Awesome icon (becoming a de facto standard)
   */
  ICON(AwesomeIcon.BITCOIN.getChar().toString(), BigDecimal.ONE, 8),
  /**
   * The Font Awesome icon with milli
   */
  MICON(AwesomeIcon.BITCOIN.getChar().toString(), new BigDecimal(1_000), 5),
  /**
   * The Font Awesome icon with micro
   */
  UICON(AwesomeIcon.BITCOIN.getChar().toString(), new BigDecimal(1_000_000), 2),
  /**
   * The current de facto standard but may be superseded (cannot be an ISO standard)
   */
  BTC("BTC", BigDecimal.ONE, 8),
  /**
   * A milli in the current de facto standard
   */
  MBTC("mBTC", new BigDecimal(1_000), 5),
  /**
   * A micro in the current de facto standard
   */
  UBTC("\u00B5BTC", new BigDecimal(1_000_000), 2),
  /**
   * The only possible ISO standard name
   */
  XBT("XBT", BigDecimal.ONE, 8),
  /**
   * A milli in the only possible ISO standard name
   */
  MXBT("mXBT", new BigDecimal(1_000), 5),
  /**
   * A micro in the only possible ISO standard name
   */
  UXBT("\u00B5XBT", new BigDecimal(1_000_000), 2),
  /**
   * Subject of much debate (see <a href="http://www.reddit.com/r/Bitcoin/comments/1rmto3/its_bits/">this Reddit article</a>)
   * However, a "bit" is already used for measuring data transmission and reusing it here would be confusing
   */
  //BIT("bit", new BigDecimal(1_000_000)),
  /**
   * The smallest possible unit in the current version of Bitcoin
   */
  SATOSHI("sat", new BigDecimal(100_000_000), 0),

  // End of enum
  ;

  private final String symbol;
  private final BigDecimal multiplier;
  private final int decimalPlaces;

  BitcoinSymbol(String symbol, BigDecimal multiplier, int decimalPlaces) {
    this.symbol = symbol;
    this.multiplier = multiplier;
    this.decimalPlaces = decimalPlaces;
  }

  /**
   * @param bitcoinSymbol A text representation of an enum constant (case-insensitive)
   *
   * @return The matching enum value
   */
  public static BitcoinSymbol of(String bitcoinSymbol) {
    return BitcoinSymbol.valueOf(bitcoinSymbol.toUpperCase());
  }

  /**
   * @return The current Bitcoin symbol
   */
  public static BitcoinSymbol current() {
    return BitcoinSymbol.of(Configurations.currentConfiguration.getBitcoinConfiguration().getBitcoinSymbol());
  }

  /**
   * @return The Bitcoin maximum value with symbolic multiplier applied (useful for amount entry)
   */
  public static BigDecimal maxSymbolicAmount() {
    return new BigDecimal("21000000.00000000").multiply(current().multiplier());
  }

  /**
   * @return The next Bitcoin symbol in the enum wrapping as required
   */
  public BitcoinSymbol next() {

    int ordinal = this.ordinal();

    ordinal = (ordinal + 1) % BitcoinSymbol.values().length;

    return BitcoinSymbol.class.getEnumConstants()[ordinal];

  }

  /**
   * @return The textual symbol to display to the user (e.g. "mBTC", "XBT" etc)
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

  /**
   * @return The decimal places to show on plain amounts for this symbol to be accurate
   */
  public int decimalPlaces() {
    return decimalPlaces;
  }

  /**
   * @return The max input length for data entry without grouping symbols
   */
  public int maxRepresentationLength() {
    if (this.equals(SATOSHI)) {
      return "210000000000000000000".length();
    } else {
      return "2100000000000.00000000".length();
    }
  }

}
