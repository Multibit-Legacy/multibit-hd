package org.multibit.hd.core.exchanges;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.oer.OERExchange;
import com.xeiam.xchange.virtex.VirtExExchange;
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

  BITCOIN_CHARTS(BitcoinChartsExchange.class.getName()),
  BITSTAMP(BitstampExchange.class.getName()),
  BITCUREX(BitcurexExchange.class.getName()),
  BTC_CHINA(BTCChinaExchange.class.getName()),
  BTC_E(BTCEExchange.class.getName()),
  CAMPBX(CampBXExchange.class.getName()),
  KRAKEN(KrakenExchange.class.getName()),
  OPEN_EXCHANGE_RATES(OERExchange.class.getName()),
  CA_VIRTEX(VirtExExchange.class.getName()),

  // End of enum
  ;

  private Exchange exchange;

  ExchangeKey(String exchangeClassName) {
    // Force the use of the default exchange specification
    this.exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);
  }

  /**
   * @return The exchange instance (not connected) providing access to the default exchange specification
   */
  public Exchange getExchange() {
    return exchange;
  }

  /**
   * @return The exchange name (not localised)
   */
  public String getExchangeName() {
    return exchange.getExchangeSpecification().getExchangeName();
  }

  /**
   * @return The exchange key from the current configuration
   */
  public static ExchangeKey current() {

    return valueOf(Configurations.currentConfiguration.getBitcoinConfiguration().getCurrentExchange());
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
