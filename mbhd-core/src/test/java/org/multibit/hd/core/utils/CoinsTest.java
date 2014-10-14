package org.multibit.hd.core.utils;

import org.bitcoinj.core.Coin;
import org.junit.Test;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;

public class CoinsTest {

  private Coin[] testAmounts = new Coin[]{
    Coin.parseCoin("20999999.12345678"), // 20,999,999.123 456 78
    Coin.parseCoin("0.12345678"), // 0.123 456 78
    Coin.parseCoin("0.00000001") // 0.000 000 01
  };

  private BigDecimal exchangeRate = new BigDecimal("1000");

  @Test
  public void testToLocalAmount() throws Exception {

    // Large
    BigDecimal expected1 = new BigDecimal("20999999123.456780000000");
    BigDecimal actual1 = Coins.toLocalAmount(testAmounts[0], exchangeRate);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal expected2 = new BigDecimal("123.456780000000");
    BigDecimal actual2 = Coins.toLocalAmount(testAmounts[1], exchangeRate);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal expected3 = new BigDecimal("0.000010000000");
    BigDecimal actual3 = Coins.toLocalAmount(testAmounts[2], exchangeRate);

    assertThat(actual3).isEqualTo(expected3);


  }

  @Test
  public void testFromLocalAmount() throws Exception {

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999123.456780000000");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromLocalAmount(localAmount1, exchangeRate);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("123.456780000000");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromLocalAmount(localAmount2, exchangeRate);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.000010000000");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromLocalAmount(localAmount3, exchangeRate);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromPlainAmount_BigDecimal() throws Exception {

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999.12345678");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromPlainAmount(localAmount1);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("0.12345678");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromPlainAmount(localAmount2);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00000001");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromPlainAmount(localAmount3);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromPlainAmount_String() throws Exception {

    // Large
    String localAmount1 = "20999999.12345678";

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromPlainAmount(localAmount1);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    String localAmount2 = "0.12345678";

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromPlainAmount(localAmount2);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    String localAmount3 = "0.00000001";

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromPlainAmount(localAmount3);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_BTC_XBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.BTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999.12345678");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("0.12345678");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00000001");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_mBTC_mXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.MBTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999123.45678");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("123.45678");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.00001");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_uBTC_uXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.UBTC;

    // Large
    BigDecimal localAmount1 = new BigDecimal("20999999123456.78");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("123456.78");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("0.01");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testFromSymbolicAmount_Satoshi() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.SATOSHI;

    // Large
    BigDecimal localAmount1 = new BigDecimal("2099999912345678");

    Coin expected1 = testAmounts[0];
    Coin actual1 = Coins.fromSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    BigDecimal localAmount2 = new BigDecimal("12345678");

    Coin expected2 = testAmounts[1];
    Coin actual2 = Coins.fromSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    BigDecimal localAmount3 = new BigDecimal("1");

    Coin expected3 = testAmounts[2];
    Coin actual3 = Coins.fromSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_BTC_XBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.BTC;

    // Large
    Coin localAmount1 = Coin.parseCoin("20999999.12345678");

    BigDecimal expected1 = new BigDecimal("20999999.12345678");
    BigDecimal actual1 = Coins.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    Coin localAmount2 = Coin.parseCoin("0.12345678");

    BigDecimal expected2 = new BigDecimal("0.12345678");
    BigDecimal actual2 = Coins.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    Coin localAmount3 = Coin.parseCoin("0.00000001");

    BigDecimal expected3 = new BigDecimal("0.00000001");
    BigDecimal actual3 = Coins.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_mBTC_mXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.MBTC;

    // Large
    Coin localAmount1 = Coin.parseCoin("20999999.12345678");

    BigDecimal expected1 = new BigDecimal("20999999123.45678");
    BigDecimal actual1 = Coins.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    Coin localAmount2 = Coin.parseCoin("0.12345678");

    BigDecimal expected2 = new BigDecimal("123.45678");
    BigDecimal actual2 = Coins.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    Coin localAmount3 = Coin.parseCoin("0.001");

    BigDecimal expected3 = new BigDecimal("1.00000");
    BigDecimal actual3 = Coins.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_uBTC_uXBT() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.UBTC;

    // Large
    Coin localAmount1 = Coin.parseCoin("20999999.12345678");

    BigDecimal expected1 = new BigDecimal("20999999123456.78");
    BigDecimal actual1 = Coins.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    Coin localAmount2 = Coin.parseCoin("0.12345678");

    BigDecimal expected2 = new BigDecimal("123456.78");
    BigDecimal actual2 = Coins.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    Coin localAmount3 = Coin.parseCoin("0.00000001");

    BigDecimal expected3 = new BigDecimal("0.01");
    BigDecimal actual3 = Coins.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }

  @Test
  public void testToSymbolicAmount_Satoshi() throws Exception {

    // Bitcoin symbol
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.SATOSHI;

    // Large
    Coin localAmount1 = Coin.parseCoin("20999999.12345678");

    BigDecimal expected1 = new BigDecimal("2099999912345678");
    BigDecimal actual1 = Coins.toSymbolicAmount(localAmount1, bitcoinSymbol);

    assertThat(actual1).isEqualTo(expected1);

    // Medium
    Coin localAmount2 = Coin.parseCoin("0.12345678");

    BigDecimal expected2 = new BigDecimal("12345678");
    BigDecimal actual2 = Coins.toSymbolicAmount(localAmount2, bitcoinSymbol);

    assertThat(actual2).isEqualTo(expected2);

    // Small
    Coin localAmount3 = Coin.parseCoin("0.00000001");

    BigDecimal expected3 = new BigDecimal("1");
    BigDecimal actual3 = Coins.toSymbolicAmount(localAmount3, bitcoinSymbol);

    assertThat(actual3).isEqualTo(expected3);

  }
}
