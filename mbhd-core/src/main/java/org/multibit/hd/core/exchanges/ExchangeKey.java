package org.multibit.hd.core.exchanges;

import com.google.common.base.Optional;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.anx.v2.ANXExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.cryptotrade.CryptoTradeExchange;
import com.xeiam.xchange.justcoin.JustcoinExchange;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.oer.OERExchange;
import com.xeiam.xchange.vaultofsatoshi.VaultOfSatoshiExchange;
import org.multibit.hd.core.config.Configurations;

/**
 * <p>Enum to provide the following to Exchange API:</p>
 * <ul>
 * <li>All supported exchange providers</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum ExchangeKey {

  // Full list of available exchanges from XChange library

  /**
   * NONE is always the first entry in the list
   */
  NONE(""),
  ANX(ANXExchange.class.getName()),
  // BITCOIN_AVERAGE(BitcoinAverage.class.getName()), // Causes problems with enum creation
  // BITCOIN_CHARTS(BitcoinChartsExchange.class.getName()), // Aggregator over exchanges
  // BITCOINIUM(BitcoiniumExchange.class.getName()), // No currency pair lookup
  BITCUREX(BitcurexExchange.class.getName()),
  BITFINEX(BitfinexExchange.class.getName()),
  BITSTAMP(BitstampExchange.class.getName()),
  // BLOCKCHAIN(BlockchainExchange.class.getName()), // Not a rate supplier
  BTC_CHINA(BTCChinaExchange.class.getName()),
  BTC_E(BTCEExchange.class.getName()),
  BTER(BTERExchange.class.getName()),
  CAMPBX(CampBXExchange.class.getName()),
  // CA_VIRTEX(VirtExExchange.class.getName()), // Broken
  // CEXIO(CexIOExchange.class.getName()), // Weird GHS/NMC combo
  COINBASE(CoinbaseExchange.class.getName()), // No dynamic currency pair lookup
  // COINFLOOR(CoinfloorExchange.class.getName()), // Requires non-trivial registration
  CRYPTO_TRADE(CryptoTradeExchange.class.getName()),
  JUSTCOIN(JustcoinExchange.class.getName()),
  KRAKEN(KrakenExchange.class.getName()),
  OPEN_EXCHANGE_RATES(OERExchange.class.getName()),
  VAULT_OF_SATOSHI(VaultOfSatoshiExchange.class.getName()),
  // VIRCUREX(VircurexExchange.class.getName()), // Broken

  // End of enum
  ;

  private final Optional<Exchange> exchange;

  ExchangeKey(String exchangeClassName) {

    // #35 Support the idea of no exchange for Bitcoin-only situations
    if ("".equals(exchangeClassName)) {
      this.exchange = Optional.absent();
    } else {
      // Force the use of the default exchange specification
      this.exchange = Optional.of(ExchangeFactory.INSTANCE.createExchange(exchangeClassName));
    }
  }

  /**
   * @return The exchange instance (not connected) providing access to the default exchange specification
   */
  public Optional<Exchange> getExchange() {
    return exchange;
  }

  /**
   * @return The exchange name (not localised)
   */
  public String getExchangeName() {

    if (exchange.isPresent()) {
      return exchange.get().getExchangeSpecification().getExchangeName();
    } else {
      return "";
    }

  }

  /**
   * @return The exchange key from the current configuration
   */
  public static ExchangeKey current() {

    return valueOf(Configurations.currentConfiguration.getBitcoin().getCurrentExchange());
  }

  /**
   * @return All the exchange names in the order they are declared
   */
  public static String[] allExchangeNames() {

    String[] allExchangeNames = new String[values().length];

    for (ExchangeKey exchangeKey : values()) {
      allExchangeNames[exchangeKey.ordinal()] = exchangeKey.getExchangeName();
    }

    return allExchangeNames;

  }

  /**
   * <p>Provides a non-ISO code appropriate for the given exchange</p>
   * <p>Note: This is likely to be replaced with code in XChange directly</p>
   *
   * @param currencyCode The currency code (could be ISO or not)
   * @param exchangeKey  The exchange key
   *
   * @return The most appropriate exchange code for the offered candidate ISO code (e.g. "XBT" becomes "BTC")
   */
  public static String exchangeCode(String currencyCode, ExchangeKey exchangeKey) {

    // All exchanges quote in BTC over XBT at this time
    if ("XBT".equalsIgnoreCase(currencyCode)) {
      return "BTC";
    }

    // BTC-e uses legacy "RUR" code
    if (ExchangeKey.BTC_E.equals(exchangeKey) && "RUB".equalsIgnoreCase(currencyCode)) {
      return "RUR";
    }

    // Default is the ISO code
    return currencyCode;
  }

}
