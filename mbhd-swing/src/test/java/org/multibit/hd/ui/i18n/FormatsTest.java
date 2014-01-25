package org.multibit.hd.ui.i18n;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;

import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

public class FormatsTest {

  private BitcoinConfiguration configuration;

  private String[] testAmounts = new String[] {
    "20999999.12345678",
    "1",
    "0.00000001"
  };

  @Before
  public void setUp() {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    configuration = Configurations.currentConfiguration.getBitcoinConfiguration();
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_Icon() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.ICON.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");

  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_BTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_XBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_mBTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_mXBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_uBTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.UBTC.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_uXBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.UXBT.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatPlainBitcoinAmountAsSymbolic_Satoshi() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.SATOSHI.name());

    String[] balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[0]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("2,099,999,912,345,678");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[1]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("100,000,000");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatPlainBitcoinAmountAsSymbolic(new BigDecimal(testAmounts[2]));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1");
    assertThat(balance[1]).isEqualTo("");
  }

}
