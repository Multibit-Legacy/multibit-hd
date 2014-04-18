package org.multibit.hd.core.utils;

import com.xeiam.xchange.currency.MoneyUtils;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.fest.assertions.Assertions.assertThat;

public class SatoshisTest {

  private BigInteger[] testAmounts = new BigInteger[]{
    new BigInteger("2099999912345678"), // 20,999,999.123 456 78
    new BigInteger("12345678"), // 0.123 456 78
    new BigInteger("1") // 0.000 000 01
  };

  private BigMoney exchangeRate = MoneyUtils.parseMoney("GBP", new BigDecimal("1000"));

  @Test
  public void testToLocalAmount() throws Exception {

    // Large
    BigMoney expected1 = MoneyUtils.parse("GBP 20999999123.456780000000");
    BigMoney actual1 = Satoshis.toLocalAmount(testAmounts[0], exchangeRate);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigMoney expected2 = MoneyUtils.parse("GBP 123.456780000000");
    BigMoney actual2 = Satoshis.toLocalAmount(testAmounts[1], exchangeRate);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigMoney expected3 = MoneyUtils.parse("GBP 0.000010000000");
    BigMoney actual3 = Satoshis.toLocalAmount(testAmounts[2], exchangeRate);

    assertThat(actual3).isEqualTo(expected3);


  }

  @Test
  public void testFromLocalAmount() throws Exception {

    // Large
    BigMoney localAmount1 = MoneyUtils.parse("GBP 20999999123.456780000000");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromLocalAmount(localAmount1, exchangeRate);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigMoney localAmount2 = MoneyUtils.parse("GBP 123.456780000000");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromLocalAmount(localAmount2, exchangeRate);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigMoney localAmount3 = MoneyUtils.parse("GBP 0.000010000000");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromLocalAmount(localAmount3, exchangeRate);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromPlainAmount_BigMoney() throws Exception {

    // Large
    BigMoney localAmount1 = MoneyUtils.parse("GBP 20999999.123456780000000");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromPlainAmount(localAmount1);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigMoney localAmount2 = MoneyUtils.parse("GBP 0.123456780000000");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromPlainAmount(localAmount2);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigMoney localAmount3 = MoneyUtils.parse("GBP 0.000000010000000");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromPlainAmount(localAmount3);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromPlainAmount_BigDecimal() throws Exception {

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999.12345678");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromPlainAmount(localAmount1);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("0.12345678");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromPlainAmount(localAmount2);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00000001");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromPlainAmount(localAmount3);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromPlainAmount_String() throws Exception {

    // Large
    String localAmount1 = "20999999.12345678";

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromPlainAmount(localAmount1);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    String localAmount2 = "0.12345678";

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromPlainAmount(localAmount2);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    String localAmount3 = "0.00000001";

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromPlainAmount(localAmount3);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_BTC_XBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.BTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999.12345678");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("0.12345678");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00000001");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_mBTC_mXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.MBTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999123.45678");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("123.45678");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00001");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_uBTC_uXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.UBTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999123456.78");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("123456.78");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.01");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_Satoshi() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.SATOSHI;

    // Large
    BigDecimal localAmount1 = new BigDecimal("2099999912345678");

    BigInteger expected1 = testAmounts[0];
    BigInteger actual1 = Satoshis.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("12345678");

    BigInteger expected2 = testAmounts[1];
    BigInteger actual2 = Satoshis.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("1");

    BigInteger expected3 = testAmounts[2];
    BigInteger actual3 = Satoshis.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_BTC_XBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.BTC;

    // Large
    BigInteger localAmount1 = new BigInteger("2099999912345678");

    BigDecimal expected1 = new BigDecimal("20999999.12345678");
    BigDecimal actual1 = Satoshis.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigInteger localAmount2 = new BigInteger("12345678");

    BigDecimal expected2 = new BigDecimal("0.12345678");
    BigDecimal actual2 = Satoshis.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigInteger localAmount3 = new BigInteger("1");

    BigDecimal expected3 = new BigDecimal("0.00000001");
    BigDecimal actual3 = Satoshis.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_mBTC_mXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.MBTC;

    // Large
    BigInteger localAmount1 = new BigInteger("2099999912345678");

    BigDecimal expected1 = new BigDecimal("20999999123.45678");
    BigDecimal actual1 = Satoshis.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigInteger localAmount2 = new BigInteger("12345678");

    BigDecimal expected2 = new BigDecimal("123.45678");
    BigDecimal actual2 = Satoshis.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigInteger localAmount3 = new BigInteger("1");

    BigDecimal expected3 = new BigDecimal("0.00001");
    BigDecimal actual3 = Satoshis.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_uBTC_uXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.UBTC;

    // Large
    BigInteger localAmount1 = new BigInteger("2099999912345678");

    BigDecimal expected1 = new BigDecimal("20999999123456.78");
    BigDecimal actual1 = Satoshis.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigInteger localAmount2 = new BigInteger("12345678");

    BigDecimal expected2 = new BigDecimal("123456.78");
    BigDecimal actual2 = Satoshis.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigInteger localAmount3 = new BigInteger("1");

    BigDecimal expected3 = new BigDecimal("0.01");
    BigDecimal actual3 = Satoshis.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_Satoshi() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.SATOSHI;

    // Large
    BigInteger localAmount1 = new BigInteger("2099999912345678");

    BigDecimal expected1 = new BigDecimal("2099999912345678");
    BigDecimal actual1 = Satoshis.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigInteger localAmount2 = new BigInteger("12345678");

    BigDecimal expected2 = new BigDecimal("12345678");
    BigDecimal actual2 = Satoshis.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigInteger localAmount3 = new BigInteger("1");

    BigDecimal expected3 = new BigDecimal("1");
    BigDecimal actual3 = Satoshis.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }
}
