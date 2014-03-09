package org.multibit.hd.core.exchanges;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.blockchain.BlockchainExchange;
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

  CAMPBX(new ExchangeSpecification(CampBXExchange.class)),
  BITCOIN_CHARTS(new ExchangeSpecification(BitcoinChartsExchange.class)),
  BITSTAMP(new ExchangeSpecification(BitstampExchange.class)),
  BITCUREX(new ExchangeSpecification(BitcurexExchange.class)),
  BLOCKCHAIN_INFO(new ExchangeSpecification(BlockchainExchange.class)),
  BTC_CHINA(new ExchangeSpecification(BTCChinaExchange.class)),
  BTC_E(new ExchangeSpecification(BTCEExchange.class)),
  KRAKEN(new ExchangeSpecification(KrakenExchange.class)),
  OPEN_EXCHANGE_RATES(new ExchangeSpecification(OERExchange.class)),
  CA_VIRTEX(new ExchangeSpecification(VirtExExchange.class)),

  // End of enum
  ;

  private final Exchange exchange;

  ExchangeKey(ExchangeSpecification exchangeSpecification) {
    this.exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
  }

  /**
   * @return The exchange instance (not connected) providing access to the exchange specification
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
}
