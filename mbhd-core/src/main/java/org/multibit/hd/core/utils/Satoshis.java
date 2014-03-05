package org.multibit.hd.core.utils;

import com.google.bitcoin.core.Utils;
import com.google.common.base.Preconditions;
import org.joda.money.BigMoney;

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

  /**
   * @param satoshis     The Bitcoin amount in satoshis
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   *
   * @return The plain amount in the local currency
   */
  public static BigMoney toLocalAmount(BigInteger satoshis, BigMoney exchangeRate) {

    Preconditions.checkNotNull(satoshis, "'satoshis' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    // Convert satoshis to bitcoins
    BigDecimal bitcoins = new BigDecimal(satoshis).divide(BTC_SAT, 12, RoundingMode.HALF_EVEN);

    return exchangeRate.multipliedBy(bitcoins);

  }

  /**
   * @param localAmount  A monetary amount denominated in the local currency
   * @param exchangeRate The exchange rate in terms of the local currency (e.g. "USD 1000" means 1000 USD = 1 bitcoin)
   *
   * @return The satoshi value (e.g. 150000)
   */
  public static BigInteger fromLocalAmount(BigMoney localAmount, BigMoney exchangeRate) {

    Preconditions.checkNotNull(localAmount, "'localAmount' must be present");
    Preconditions.checkNotNull(exchangeRate, "'exchangeRate' must be present");

    Preconditions.checkState(localAmount.getCurrencyUnit().equals(exchangeRate.getCurrencyUnit()), "'localAmount' has a different currency unit to 'exchangeRate': " + localAmount.getCurrencyUnit().getCode() + " vs " + exchangeRate.getCurrencyUnit().getCode());

    BigDecimal bitcoinAmount = localAmount.getAmount().setScale(8).divide(exchangeRate.getAmount(), 8, RoundingMode.HALF_EVEN);

    return Utils.toNanoCoins(bitcoinAmount.toPlainString());

  }

  /**
   * @param plainAmount A big money denominated in Bitcoin (e.g. "BTC 0.0015")
   *
   * @return The satoshi value (e.g. 150 000)
   */
  public static BigInteger fromPlainAmount(BigMoney plainAmount) {

    Preconditions.checkNotNull(plainAmount, "'plainAmount' must be present");

    return Utils.toNanoCoins(plainAmount.getAmount().toPlainString());
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

    // Convert to plain amount
    BigDecimal plainAmount = symbolicAmount.divide(bitcoinSymbol.multiplier(), 12, RoundingMode.HALF_EVEN);

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
    BigDecimal symbolicAmount = new BigDecimal(plainString).multiply(bitcoinSymbol.multiplier());

    // Reduce the scale to match the multiplier
    return symbolicAmount.setScale(bitcoinSymbol.decimalPlaces());

  }

}
