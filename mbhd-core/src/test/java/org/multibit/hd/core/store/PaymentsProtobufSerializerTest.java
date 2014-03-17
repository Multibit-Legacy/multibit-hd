package org.multibit.hd.core.store;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.services.WalletService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static org.fest.assertions.api.Assertions.assertThat;

public class PaymentsProtobufSerializerTest {

  private PaymentsProtobufSerializer serializer;

  private Random random;

  private File paymentsFile;

  @Before
  public void setUp() throws Exception {

    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    paymentsFile = new File(temporaryDirectory.getAbsolutePath() + File.separator + WalletService.PAYMENTS_DATABASE_NAME);

    serializer = new PaymentsProtobufSerializer();

    random = new Random();
  }

  @Test
  public void testLastIndexUsed() throws Exception {
    // Test you can set and get the last index used
    int lastIndexUsed = random.nextInt();
    Payments payments = new Payments(lastIndexUsed);

    Payments newPayments = roundTrip(payments);

    assertThat(lastIndexUsed == newPayments.getLastIndexUsed());

  }

  @Test
  public void testRequests() throws Exception {
    // Test you can add some payment requests and read them back
    Collection<PaymentRequestData> paymentRequestDatas = Lists.newArrayList();

    PaymentRequestData paymentRequestData1 = new PaymentRequestData();

    paymentRequestData1.setAddress("1abc");
    paymentRequestData1.setAmountBTC(BigInteger.valueOf(245));
    DateTime date1 = new DateTime();
    paymentRequestData1.setDate(date1);
    paymentRequestData1.setLabel("label1");
    paymentRequestData1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    paymentRequestData1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(BigMoney.of(CurrencyUnit.USD, new BigDecimal("12345.6")));
    fiatPayment1.setRate("10.0");
    fiatPayment1.setExchange("Bitstamp");

    PaymentRequestData paymentRequestData2 = new PaymentRequestData();
    paymentRequestData2.setAddress("1xyz");
    paymentRequestData2.setAmountBTC(BigInteger.valueOf(789));
    DateTime date2 = date1.plusDays(7);
    paymentRequestData2.setDate(date2);
    paymentRequestData2.setLabel("label2");
    paymentRequestData2.setNote("note2");

    FiatPayment fiatPayment2 = new FiatPayment();
    paymentRequestData2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(BigMoney.of(CurrencyUnit.GBP, new BigDecimal("12345.678")));
    fiatPayment2.setRate("20.0");
    fiatPayment2.setExchange("OER");

    paymentRequestDatas.add(paymentRequestData1);
    paymentRequestDatas.add(paymentRequestData2);

    Payments payments = new Payments(1);
    payments.setPaymentRequestDatas(paymentRequestDatas);

    Payments newPayments = roundTrip(payments);

    Collection<PaymentRequestData> newPaymentRequestDatas = newPayments.getPaymentRequestDatas();
    assertThat(newPaymentRequestDatas.size()).isEqualTo(2);

    Iterator<PaymentRequestData> iterator = newPaymentRequestDatas.iterator();
    PaymentRequestData newPaymentRequestData1 = iterator.next();
    PaymentRequestData newPaymentRequestData2 = iterator.next();

    checkPaymentRequest(paymentRequestData1, newPaymentRequestData1);
    checkPaymentRequest(paymentRequestData2, newPaymentRequestData2);
  }

  private void checkPaymentRequest(PaymentRequestData paymentRequestData, PaymentRequestData other) {
    assertThat(other.getAddress()).isEqualTo(paymentRequestData.getAddress());
    assertThat(other.getLabel()).isEqualTo(paymentRequestData.getLabel());
    assertThat(other.getNote()).isEqualTo(paymentRequestData.getNote());
    assertThat(other.getAmountBTC()).isEqualTo(paymentRequestData.getAmountBTC());
    assertThat(other.getDate()).isEqualTo(paymentRequestData.getDate());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = paymentRequestData.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchange()).isEqualTo(otherFiatPayment.getExchange());
  }

  @Test
  public void testTransactionInfos() throws Exception {
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    TransactionInfo transactionInfo1 = new TransactionInfo();
    transactionInfos.add(transactionInfo1);
    transactionInfo1.setHash("010203");
    transactionInfo1.setNote("notes1");

    FiatPayment fiatPayment1 = new FiatPayment();
    transactionInfo1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(BigMoney.of(CurrencyUnit.EUR, new BigDecimal("99.9")));
    fiatPayment1.setRate("30.0");
    fiatPayment1.setExchange("Bitstamp");

    transactionInfo1.setClientFee(Optional.of(BigInteger.ZERO));
    transactionInfo1.setMinerFee(Optional.of(BigInteger.valueOf(123)));

    TransactionInfo transactionInfo2 = new TransactionInfo();
    transactionInfos.add(transactionInfo2);
    transactionInfo2.setHash("010203");
    transactionInfo2.setNote("notes1");

    FiatPayment fiatPayment2 = new FiatPayment();
    transactionInfo2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(BigMoney.of(CurrencyUnit.JPY, new BigDecimal("11.1")));
    fiatPayment2.setRate("50.0");
    fiatPayment2.setExchange("BitstampJunior");

    transactionInfo2.setClientFee(Optional.of(BigInteger.valueOf(456)));
    transactionInfo2.setMinerFee(Optional.of(BigInteger.ZERO));

    Payments payments = new Payments(1);
    payments.setTransactionInfos(transactionInfos);

    Payments newPayments = roundTrip(payments);


    Collection<TransactionInfo> newTransactionInfos = newPayments.getTransactionInfos();
    assertThat(newTransactionInfos.size()).isEqualTo(2);

    Iterator<TransactionInfo> iterator = newTransactionInfos.iterator();
    TransactionInfo newTransactionInfo1 = iterator.next();
    TransactionInfo newTransactionInfo2 = iterator.next();

    checkTransactionInfo(transactionInfo1, newTransactionInfo1);
    checkTransactionInfo(transactionInfo2, newTransactionInfo2);
  }

  private void checkTransactionInfo(TransactionInfo transactionInfo, TransactionInfo other) throws Exception {
    assertThat(other.getHash().equals(transactionInfo.getHash())).isTrue();

    assertThat(other.getNote()).isEqualTo(transactionInfo.getNote());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = transactionInfo.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchange()).isEqualTo(otherFiatPayment.getExchange());

    assertThat(transactionInfo.getClientFee()).isEqualTo(other.getClientFee());
    assertThat(transactionInfo.getMinerFee()).isEqualTo(other.getMinerFee());
  }

  /**
   * Round trip the payments i.e. writePayments to disk and read back in
   *
   * @throws Exception
   */
  public Payments roundTrip(Payments payments) throws Exception {

    // Store the payments to the backing writeContacts
    serializer.writePayments(payments, new FileOutputStream(paymentsFile));

    // Reload it
    return serializer.readPayments(new FileInputStream((paymentsFile)));
  }
}
