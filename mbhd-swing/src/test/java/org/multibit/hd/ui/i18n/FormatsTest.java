package org.multibit.hd.ui.i18n;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;

import java.math.BigInteger;

import static org.fest.assertions.api.Assertions.assertThat;

public class FormatsTest {

  private BitcoinConfiguration bitcoinConfiguration;
  private I18NConfiguration i18nConfiguration;

  private String[] testAmounts = new String[] {
    "2099999912345678",
    "100000000",
    "1"
  };

  @Before
  public void setUp() {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();
    i18nConfiguration = Configurations.currentConfiguration.getI18NConfiguration();
  }

  @Test
  public void testFormatSatoshisAsSymbolic_Icon() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.ICON.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");

  }

  @Test
  public void testFormatSatoshisAsSymbolic_BTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_XBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_mBTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_mXBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_uBTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UBTC.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_uXBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UXBT.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatSatoshisAsSymbolic_Satoshi() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.SATOSHI.name());

    String[] balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[0]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("2,099,999,912,345,678");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[1]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("100,000,000");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatSatoshisAsSymbolic(new BigInteger(testAmounts[2]), i18nConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1");
    assertThat(balance[1]).isEqualTo("");
  }

}
