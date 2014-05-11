package org.multibit.hd.core.utils;

import com.google.bitcoin.core.Utils;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * <p>Utility to provide the following to low level Bitcoin operations:</p>
 * <ul>
 * <li>Conversion between local and Bitcoin amounts</li>
 * <li>Conversion between various representations of Bitcoin values</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Satoshis {

  /**
   * The exchange rate between bitcoin and satoshi is fixed at 1 bitcoin = 100 000 000 satoshis
   */
  public static final BigDecimal BTC_SAT = new BigDecimal("100000000");
  private static final int BITCOIN_SCALE = 12;
  private static final int LOCAL_SCALE = 12;

  /**
   * @param satoshis     The Bitcoin amount in satoshis
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   *
   * @return The plain amount in the local currency
   */
  public static BigDecimal toLocalAmount(BigInteger satoshis, BigDecimal exchangeRate) {

    Preconditions.checkNotNull(satoshis, "'satoshis' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    // Convert satoshis to bitcoins
    BigDecimal bitcoins = new BigDecimal(satoshis)
      .divide(BTC_SAT, LOCAL_SCALE, RoundingMode.HALF_EVEN);

    return exchangeRate.multiply(bitcoins);

  }

  /**
   * @param localAmount  A monetary amount denominated in the local currency
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "1000" means 1000 local = 1 bitcoin)
   *
   * @return The satoshi value (e.g. 150000)
   */
  public static BigInteger fromLocalAmount(BigDecimal localAmount, BigDecimal exchangeRate) {

    Preconditions.checkNotNull(localAmount, "'localAmount' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    // Truncate to 8 dp to ensure conversion to Satoshis can take place
    BigDecimal bitcoinAmount = localAmount
      .setScale(BITCOIN_SCALE)
      .divide(exchangeRate, BITCOIN_SCALE, RoundingMode.HALF_EVEN)
      .setScale(8, RoundingMode.HALF_EVEN);

    return Utils.toNanoCoins(bitcoinAmount.toPlainString());

  }

  /**
   * @param plainAmount A big decimal denominated in BTC (e.g. 0.0015)
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static BigInteger fromPlainAmount(BigDecimal plainAmount) {

    Preconditions.checkNotNull(plainAmount, "'plainAmount' must be present");

    return Utils.toNanoCoins(plainAmount.toPlainString());
  }

  /**
   * @param plainAmount A String denominated in BTC (e.g. "0.0015")
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static BigInteger fromPlainAmount(String plainAmount) {

    Preconditions.checkNotNull(plainAmount, "'plainAmount' must be present");

    return Utils.toNanoCoins(plainAmount);
  }

  /**
   * @param symbolicAmount A Bitcoin amount expressed in terms of the current symbolic multiplier (e.g. "1.5" in mBTC is 150 000 satoshis)
   * @param bitcoinSymbol  The Bitcoin symbol to use for the multiplier
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static BigInteger fromSymbolicAmount(BigDecimal symbolicAmount, BitcoinSymbol bitcoinSymbol) {

    Preconditions.checkNotNull(symbolicAmount, "'symbolicAmount' must be present");

    // Convert to plain amount ensuring Bitcoin scale
    BigDecimal plainAmount = symbolicAmount
      .setScale(BITCOIN_SCALE)
      .divide(bitcoinSymbol.multiplier(), BITCOIN_SCALE, RoundingMode.HALF_EVEN);

    // Convert to satoshis
    return fromPlainAmount(plainAmount);
  }

  /**
   * <p>Convert the given satoshi value into a symbolic amount <strong>suitable for display only</strong>.</p>
   * <p>The result is scaled so that decimals are dropped making it unsuitable for calculations.</p>
   *
   * @param satoshis      The satoshi value (e.g. 150 000)
   * @param bitcoinSymbol The Bitcoin symbol to use for the multiplier
   *
   * @return A Bitcoin amount expressed in terms of the current symbolic multiplier (e.g. "1.5" in mBTC)
   */
  public static BigDecimal toSymbolicAmount(BigInteger satoshis, BitcoinSymbol bitcoinSymbol) {

    Preconditions.checkNotNull(satoshis, "'satoshis' must be present");

    // Convert to plain string
    String plainString = Utils.bitcoinValueToPlainString(satoshis);

    // Apply the current symbolic multiplier
    BigDecimal symbolicAmount = new BigDecimal(plainString)
      .setScale(BITCOIN_SCALE)
      .multiply(bitcoinSymbol.multiplier());

    // Reduce the scale to match the multiplier
    return symbolicAmount.setScale(bitcoinSymbol.decimalPlaces());

  }

}
