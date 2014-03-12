package org.multibit.hd.core.exchanges;

import com.google.common.collect.Sets;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.oer.OERExchange;
import com.xeiam.xchange.virtex.VirtExExchange;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.CurrencyUtils;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

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
   * @return All the currencies supported by this exchange
   */
  public String[] allCurrencies() {

    Locale currentLocale = Configurations.currentConfiguration.getLocale();

    // This may involve a call to the exchange or not
    List<CurrencyPair> currencyPairs = exchange.getPollingMarketDataService().getExchangeSymbols();

    if (currencyPairs == null || currencyPairs.isEmpty()) {
      return new String[]{};
    }

    SortedSet<String> allCurrencies = Sets.newTreeSet();
    for (CurrencyPair currencyPair : currencyPairs) {
      // Add the currency (if non-BTC we can triangulate through USD)
      String baseCode = currencyPair.baseCurrency;
      String counterCode = currencyPair.counterCurrency;

      // Make any adjustments
      counterCode = CurrencyUtils.isoCandidateFor(counterCode);

      try {
        Currency base = Currency.getInstance(baseCode);
        if (base != null) {
          String localName = base.getDisplayName(currentLocale);
          allCurrencies.add(baseCode + " (" + localName + ")");
        }
      } catch (IllegalArgumentException e) {
        // Base code is not in ISO 4217 so attempt to locate counter currency (e.g. BTC/RUR)
        try {
          Currency counter = Currency.getInstance(counterCode);
          if (counter != null) {
            String localName = counter.getDisplayName(currentLocale);
            allCurrencies.add(counterCode + " (" + localName + ")");
          }
        } catch (IllegalArgumentException e1) {
          // Neither base nor counter code is in ISO 4217 so ignore since we're only working with fiat
        }
      }
    }

    // Return the unique list of currencies
    return allCurrencies.toArray(new String[allCurrencies.size()]);

  }

  /**
   * @return The exchange key from the current configuration
   */
  public static ExchangeKey current() {

    return valueOf(Configurations.currentConfiguration.getBitcoinConfiguration().getExchangeKey());
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
