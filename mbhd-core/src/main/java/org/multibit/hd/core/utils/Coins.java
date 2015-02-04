package org.multibit.hd.core.utils;

import org.bitcoinj.core.Coin;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>Utility to provide the following to low level Bitcoin operations:</p>
 * <ul>
 * <li>Conversion between local and Bitcoin amounts</li>
 * <li>Conversion between various representations of Bitcoin values</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class Coins {

  /**
   * The exchange rate between bitcoin and coin is fixed at 1 bitcoin = 100 000 000 coins
   */
  public static final BigDecimal BTC_COIN = new BigDecimal("1"+Strings.repeat("0",8));
  private static final int BITCOIN_SCALE = 12;
  private static final int LOCAL_SCALE = 12;

  /**
   * @param coin         The Bitcoin amount
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   *
   * @return The plain amount in the local currency
   */
  public static BigDecimal toLocalAmount(Coin coin, BigDecimal exchangeRate) {

    Preconditions.checkNotNull(coin, "'coin' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    // Convert coins to bitcoins
    BigDecimal bitcoins = BigDecimal.valueOf(coin.longValue()).divide(BTC_COIN, LOCAL_SCALE, RoundingMode.HALF_EVEN);

    return exchangeRate.multiply(bitcoins);

  }

  /**
   * @param localAmount  A monetary amount denominated in the local currency
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   *
   * @return The satoshi value (e.g. 150000)
   */
  public static Coin fromLocalAmount(BigDecimal localAmount, BigDecimal exchangeRate) {

    Preconditions.checkNotNull(localAmount, "'localAmount' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    // Truncate to 8 dp to ensure conversion to coins can take place
    BigDecimal bitcoinAmount = localAmount
      .setScale(BITCOIN_SCALE)
      .divide(exchangeRate, BITCOIN_SCALE, RoundingMode.HALF_EVEN)
      .setScale(8, RoundingMode.HALF_EVEN);

    return Coin.parseCoin(bitcoinAmount.toPlainString());

  }

  /**
   * @param plainAmount A big decimal denominated in BTC (e.g. 0.0015)
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static Coin fromPlainAmount(BigDecimal plainAmount) {

    Preconditions.checkNotNull(plainAmount, "'plainAmount' must be present");

    return Coin.parseCoin(plainAmount.toPlainString());
  }

  /**
   * @param plainAmount A String denominated in BTC (e.g. "0.0015")
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static Coin fromPlainAmount(String plainAmount) {

    Preconditions.checkNotNull(plainAmount, "'plainAmount' must be present");

    return Coin.parseCoin(plainAmount);
  }

  /**
   * @param symbolicAmount A Bitcoin amount expressed in terms of the current symbolic multiplier (e.g. "1.5" in mBTC is 150 000 coins)
   * @param bitcoinSymbol  The Bitcoin symbol to use for the multiplier
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static Coin fromSymbolicAmount(BigDecimal symbolicAmount, BitcoinSymbol bitcoinSymbol) {

    Preconditions.checkNotNull(symbolicAmount, "'symbolicAmount' must be present");

    // Convert to plain amount ensuring Bitcoin scale
    BigDecimal plainAmount = symbolicAmount
      .setScale(BITCOIN_SCALE)
      .divide(bitcoinSymbol.multiplier(), BITCOIN_SCALE, RoundingMode.HALF_EVEN);

    // Convert to coins
    return fromPlainAmount(plainAmount);
  }

  /**
   * <p>Convert the given satoshi value into a symbolic amount <strong>suitable for display only</strong>.</p>
   * <p>The result is scaled so that decimals are dropped making it unsuitable for calculations.</p>
   *
   * @param coin          The coin value (e.g. 150 000)
   * @param bitcoinSymbol The Bitcoin symbol to use for the multiplier
   *
   * @return A Bitcoin amount expressed in terms of the current symbolic multiplier (e.g. "1.5" in mBTC)
   */
  public static BigDecimal toSymbolicAmount(Coin coin, BitcoinSymbol bitcoinSymbol) {

    Preconditions.checkNotNull(coin, "'coin' must be present");

    // Convert to plain string
    String plainString = coin.toPlainString();

    // Apply the current symbolic multiplier
    BigDecimal symbolicAmount = new BigDecimal(plainString)
      .setScale(BITCOIN_SCALE)
      .multiply(bitcoinSymbol.multiplier());

    // Reduce the scale to match the multiplier
    return symbolicAmount.setScale(bitcoinSymbol.decimalPlaces());

  }

}
