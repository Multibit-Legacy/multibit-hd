package org.multibit.hd.core.exchanges;

import com.google.common.base.Optional;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitbay.BitbayExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.bitmarket.BitMarketExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.btctrade.BTCTradeExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.cexio.CexIOExchange;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.cryptonit.v2.CryptonitExchange;
import com.xeiam.xchange.hitbtc.HitbtcExchange;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.lakebtc.LakeBTCExchange;
import com.xeiam.xchange.oer.OERExchange;
import com.xeiam.xchange.okcoin.OkCoinExchange;
import com.xeiam.xchange.virtex.v2.VirtExExchange;
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
  // ANX(ANXExchange.class.getName()), // Rates broken
  // ATLASATS(AtlasATS.class.getName()), // No 2.1.0 release
  BITBAY(BitbayExchange.class.getName()),
  // BITCOIN_AVERAGE(BitcoinAverage.class.getName()), // Causes problems with enum creation
  // BITCOIN_CHARTS(BitcoinChartsExchange.class.getName()), // Aggregator over exchanges
  // BITCOINIUM(BitcoiniumExchange.class.getName()), // No currency pair lookup
  BITCUREX(BitcurexExchange.class.getName()),
  BITFINEX(BitfinexExchange.class.getName()),
  // BIT_KONAN(BitKonanExchange.class.getName()), // No market rates
  BITMARKET(BitMarketExchange.class.getName()),
  BITSTAMP(BitstampExchange.class.getName()),
  // BITTREX(BittrexExchange.class.getName()), // Rates broken
  // BITVC(BitVcExchange.class.getName()), // No exchange URL
  // BLOCKCHAIN(BlockchainExchange.class.getName()), // Not a rate supplier
  //BTC_CENTRAL(BTCCentralExchange.class.getName()),// No 2.1.0 release
  BTC_CHINA(BTCChinaExchange.class.getName()),
  CAMPBX(CampBXExchange.class.getName()),
  BTC_E(BTCEExchange.class.getName()),
  BTC_TRADE(BTCTradeExchange.class.getName()),
  BTER(BTERExchange.class.getName()),
  CA_VIRTEX(VirtExExchange.class.getName()),
  CEXIO(CexIOExchange.class.getName()), // Weird GHS/NMC combo
  COINBASE(CoinbaseExchange.class.getName()), // No dynamic currency pair lookup
  // COINSETTER(CoinSetterExchange.class.getName()),// No 2.1.0 release
  // COINFLOOR(CoinfloorExchange.class.getName()), // Requires non-trivial registration
  CRYPTONIT(CryptonitExchange.class.getName()),
  // CRYPTO_TRADE(CryptoTradeExchange.class.getName()), // Out of business
  //CRYPTSY(CryptsyExchange.class.getName()),// Several broken currencies
  HITBTC(HitbtcExchange.class.getName()),// GBP feed broken, EUR and USD OK
  // IT_BIT(ItBitExchange.class.getName()),// No working feed
  // JUSTCOIN(JustcoinExchange.class.getName()), // No exchange URL at 2.1.0
  KRAKEN(KrakenExchange.class.getName()),
  LAKE_BTC(LakeBTCExchange.class.getName()),
  // MINT_PAL(MintPalExchange.class.getName()),// Feed broken (no response)
  OK_COIN(OkCoinExchange.class.getName()),
  OPEN_EXCHANGE_RATES(OERExchange.class.getName()),
  // POLONIEX(PoloniexExchange.class.getName()),// No working feed
  //VAULT_OF_SATOSHI(VaultOfSatoshiExchange.class.getName()), // Out of business
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
