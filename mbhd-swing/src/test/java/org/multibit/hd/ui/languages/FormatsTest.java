package org.multibit.hd.ui.languages;

import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;

import static org.fest.assertions.Assertions.assertThat;

public class FormatsTest {

  private BitcoinConfiguration bitcoinConfiguration;
  private LanguageConfiguration languageConfiguration;

  private String[] testAmounts = new String[] {
    "20999999.12345678",
    "1.00000000",
    "0.00000001"
  };

  @Before
  public void setUp() {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();
    languageConfiguration = Configurations.currentConfiguration.getLanguage();
  }

  @Test
  public void testFormatCoinAsSymbolic_Icon() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.ICON.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");

  }

  @Test
  public void testFormatCoinAsSymbolic_BTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatCoinAsSymbolic_XBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.BTC.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999.12");
    assertThat(balance[1]).isEqualTo("345678");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1.00");
    assertThat(balance[1]).isEqualTo("000000");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("000001");
  }

  @Test
  public void testFormatCoinAsSymbolic_mBTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatCoinAsSymbolic_mXBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123.45");
    assertThat(balance[1]).isEqualTo("678");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000.00");
    assertThat(balance[1]).isEqualTo("000");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.00");
    assertThat(balance[1]).isEqualTo("001");
  }

  @Test
  public void testFormatCoinAsSymbolic_uBTC() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UBTC.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatCoinAsSymbolic_uXBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UXBT.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("20,999,999,123,456.78");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1,000,000.00");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("0.01");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatCoinAsSymbolic_Satoshi() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.SATOSHI.name());

    String[] balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[0]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("2,099,999,912,345,678");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[1]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("100,000,000");
    assertThat(balance[1]).isEqualTo("");

    balance = Formats.formatCoinAsSymbolic(Coin.parseCoin(testAmounts[2]), languageConfiguration, bitcoinConfiguration);

    assertThat(balance.length).isEqualTo(2);
    assertThat(balance[0]).isEqualTo("1");
    assertThat(balance[1]).isEqualTo("");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_B() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.ICON.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"Please donate to multibit.org\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"B 0.01000000\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_mB() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MICON.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"Please donate to multibit.org\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"mB 10.00000\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_uB() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UICON.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"Please donate to multibit.org\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"\u00b5B 10,000.00\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_uXBT() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.UXBT.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"Please donate to multibit.org\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"\u00b5XBT 10,000.00\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_mBTC_No_Label() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"n/a\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"mBTC 10.00000\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_mB_Long_Label() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MICON.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty" +
      "?amount=0.01" +
      "&label=Please%20donate%20to%20multibit.org.%20We%20appreciate%20your%20generosity.");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"Please donate to multibit.org. ...\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"mB 10.00000\". Continue ?");
  }

  @Test
  public void testFormatAlertMessage_MultiBit_No_Amount_No_Label() throws Exception {

    bitcoinConfiguration.setBitcoinSymbol(BitcoinSymbol.MBTC.name());

    final BitcoinURI bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    assertThat(Formats.formatAlertMessage(bitcoinURI).get()).isEqualTo("Payment \"n/a\" (1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty) for \"n/a\". Continue ?");
  }

}
