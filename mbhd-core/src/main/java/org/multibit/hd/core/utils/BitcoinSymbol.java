package org.multibit.hd.core.utils;

import org.multibit.hd.core.config.Configurations;

import java.math.BigDecimal;

/**
 * <p>Enum to provide the following to controllers:</p>
 * <ul>
 * <li>Various Bitcoin symbols</li>
 * </ul>
 * <h3>A note on internationalisation</h3>
 * <p>According to the <a href="http://physics.nist.gov/Pubs/SP811/sec07.html">NIST guidelines</a>, quantities expressed in SI units should be
 * represented in a left-to-right manner using English prefixes (e.g. "m", "\u00B5") and standard Arabic numerals (0,1,2,3...). This is because
 * the combination of an SI prefix and a quantity is treated as a mathematical entity that is not subject to translation or localisation. The
 * usual treatment of currency representation is then applied so that local grouping and decimal separators and/or leading/trailing currency symbols
 * can be shown.</p>
 *
 * @since 0.0.1
 *  
 */
public enum BitcoinSymbol {

  /**
   * The Font Awesome icon (becoming a de facto standard)
   */
  ICON("", "B", BigDecimal.ONE, 8),
  /**
   * The Font Awesome icon with milli
   */
  MICON("m", "mB", new BigDecimal(1_000), 5),
  /**
   * The Font Awesome icon with micro
   */
  UICON("\u00b5", "\u00b5B", new BigDecimal(1_000_000), 2),
  /**
   * The current de facto standard but may be superseded (cannot be an ISO standard)
   */
  BTC("BTC", "BTC", BigDecimal.ONE, 8),
  /**
   * A milli in the current de facto standard
   */
  MBTC("mBTC", "mBTC", new BigDecimal(1_000), 5),
  /**
   * A micro in the current de facto standard
   */
  UBTC("\u00b5BTC","\u00b5BTC", new BigDecimal(1_000_000), 2),
  /**
   * A possible ISO standard name
   */
  XBT("XBT", "XBT", BigDecimal.ONE, 8),
  /**
   * A milli in a possible ISO standard name
   */
  MXBT("mXBT", "mXBT", new BigDecimal(1_000), 5),
  /**
   * A micro in a possible ISO standard name
   */
  UXBT("\u00b5XBT", "µXBT", new BigDecimal(1_000_000), 2),
  /**
   * The <a href="http://bitcoinsymbol.org">Ecogex alternative symbol</a>
   */
  ECO("\u0243", "\u0243", BigDecimal.ONE, 8),
  /**
   * A milli with the Ecogex alternative symbol
   */
  MECO("m\u0243", "m\u0243", new BigDecimal(1_000), 5),
  /**
   * A micro with the Ecogex alternative symbol
   */
  UECO("\u00b5\u0243", "\u00b5\u0243",new BigDecimal(1_000_000), 2),
  /**
   * Subject of much debate (see <a href="http://www.reddit.com/r/Bitcoin/comments/1rmto3/its_bits/">this Reddit article</a>)
   * However, a "bit" is already used for measuring data transmission and reusing it here would be confusing
   */
  //BIT,
  /**
   * The smallest possible unit in the current version of Bitcoin
   */
  SATOSHI("sat", "sat", new BigDecimal(100_000_000), 0),

  // End of enum
  ;

  private final String symbol;
  private final String textSymbol;

  private final BigDecimal multiplier;

  private final int decimalPlaces;

  BitcoinSymbol(String symbol, String textSymbol, BigDecimal multiplier, int decimalPlaces) {
    this.symbol = symbol;
    this.textSymbol = textSymbol;
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
   * <p>Suitable for use with JLabel with horizontal text leading</p>
   *
   * @return The (possibly incomplete) textual component of the symbol to display to the user (e.g. "m", "mBTC", "XBT" etc)
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * <p>Suitable for use with String with Font Awesome icon replaced with B</p>
   *
   * @return The complete textual symbol to display to the user (e.g. "mB", "XBT" etc)
   */
  public String getTextSymbol() {

    return textSymbol;

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
