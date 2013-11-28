package org.multibit.hd.ui.i18n;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.i18n.BitcoinSymbol;
import org.multibit.hd.ui.i18n.Formats;

import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

public class FormatsTest {

  private BitcoinConfiguration configuration;

  @Before
  public void setUp() {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    configuration = Configurations.currentConfiguration.getBitcoinConfiguration();
  }

  @Test
  public void testFormatBitcoinBalance_Icon() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.ICON.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

  }

  @Test
  public void testFormatBitcoinBalance_BTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

  }

  @Test
  public void testFormatBitcoinBalance_XBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

  }

  @Test
  public void testFormatBitcoinBalance_mBTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

  }

  @Test
  public void testFormatBitcoinBalance_mXBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

  }

  @Test
  public void testFormatBitcoinBalance_uBTC() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.UBTC.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

  }

  @Test
  public void testFormatBitcoinBalance_uXBT() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.UXBT.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

  }

  @Test
  public void testFormatBitcoinBalance_Satoshi() throws Exception {

    configuration.setBitcoinSymbol(BitcoinSymbol.SATOSHI.name());

    String[] balance = Formats.formatBitcoinBalance(new BigDecimal("20999999.12345678"));

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("2,099,999,912,345,678");
    assertThat(balance[1]).isEqualTo("");

  }

}
