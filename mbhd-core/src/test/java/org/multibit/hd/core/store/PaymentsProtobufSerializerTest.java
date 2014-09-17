package org.multibit.hd.core.store;

import com.google.bitcoin.core.Coin;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class PaymentsProtobufSerializerTest {

  private PaymentsProtobufSerializer serializer;

  private File paymentsFile;

  @Before
  public void setUp() throws Exception {

    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();
    paymentsFile = new File(temporaryDirectory.getAbsolutePath() + File.separator + WalletService.PAYMENTS_DATABASE_NAME);

    serializer = new PaymentsProtobufSerializer();
  }



  @Test
  public void testRequests() throws Exception {
    // Test you can add some payment requests and read them back
    Collection<PaymentRequestData> paymentRequestDatas = Lists.newArrayList();

    PaymentRequestData paymentRequestData1 = new PaymentRequestData();

    paymentRequestData1.setAddress("1abc");
    paymentRequestData1.setAmountCoin(Coin.valueOf(245));
    DateTime date1 = new DateTime();
    paymentRequestData1.setDate(date1);
    paymentRequestData1.setLabel("label1");
    paymentRequestData1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    paymentRequestData1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("12345.6")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("USD")));
    fiatPayment1.setRate(Optional.of("10.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    PaymentRequestData paymentRequestData2 = new PaymentRequestData();
    paymentRequestData2.setAddress("1xyz");
    paymentRequestData2.setAmountCoin(Coin.valueOf(789));
    DateTime date2 = date1.plusDays(7);
    paymentRequestData2.setDate(date2);
    paymentRequestData2.setLabel("label2");
    paymentRequestData2.setNote("note2");

    FiatPayment fiatPayment2 = new FiatPayment();
    paymentRequestData2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(Optional.of(new BigDecimal("12345.678")));
    fiatPayment2.setCurrency(Optional.of(Currency.getInstance("GBP")));
    fiatPayment2.setRate(Optional.of("20.0"));
    fiatPayment2.setExchangeName(Optional.of("OER"));

    paymentRequestDatas.add(paymentRequestData1);
    paymentRequestDatas.add(paymentRequestData2);

    Payments payments = new Payments();
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
    assertThat(other.getAmountCoin()).isEqualTo(paymentRequestData.getAmountCoin());
    assertThat(other.getDate()).isEqualTo(paymentRequestData.getDate());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = paymentRequestData.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchangeName()).isEqualTo(otherFiatPayment.getExchangeName());
  }

  @Test
  public void testTransactionInfos() throws Exception {
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    TransactionInfo transactionInfo1 = new TransactionInfo();
    transactionInfos.add(transactionInfo1);
    transactionInfo1.setHash("010203");
    transactionInfo1.setNote("notes1");
    transactionInfo1.setSentBySelf(false);

    FiatPayment fiatPayment1 = new FiatPayment();
    transactionInfo1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("99.9")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("EUR")));
    fiatPayment1.setRate(Optional.of("30.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    transactionInfo1.setClientFee(Optional.<Coin>absent());
    transactionInfo1.setMinerFee(Optional.of(Coin.valueOf(123)));

    TransactionInfo transactionInfo2 = new TransactionInfo();
    transactionInfos.add(transactionInfo2);
    transactionInfo2.setHash("010203");
    transactionInfo2.setNote("notes1");
    transactionInfo1.setSentBySelf(true);

    FiatPayment fiatPayment2 = new FiatPayment();
    transactionInfo2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount(Optional.of(new BigDecimal("11.1")));
    fiatPayment2.setCurrency(Optional.of(Currency.getInstance("JPY")));
    fiatPayment2.setRate(Optional.of("50.0"));
    fiatPayment2.setExchangeName(Optional.of("BitstampJunior"));

    transactionInfo2.setClientFee(Optional.of(Coin.valueOf(456)));
    transactionInfo2.setMinerFee(Optional.<Coin>absent());

    TransactionInfo transactionInfo3 = new TransactionInfo();
     transactionInfos.add(transactionInfo3);
     transactionInfo3.setHash("01020304");
     transactionInfo3.setNote("notes3");

     FiatPayment fiatPayment3 = new FiatPayment();
     transactionInfo3.setAmountFiat(fiatPayment3);

     transactionInfo3.setClientFee(Optional.of(Coin.valueOf(456)));
     transactionInfo3.setMinerFee(Optional.<Coin>absent());


    Payments payments = new Payments();
    payments.setTransactionInfos(transactionInfos);

    Payments newPayments = roundTrip(payments);


    Collection<TransactionInfo> newTransactionInfos = newPayments.getTransactionInfos();
    assertThat(newTransactionInfos.size()).isEqualTo(3);

    Iterator<TransactionInfo> iterator = newTransactionInfos.iterator();
    TransactionInfo newTransactionInfo1 = iterator.next();
    TransactionInfo newTransactionInfo2 = iterator.next();
    TransactionInfo newTransactionInfo3 = iterator.next();

    checkTransactionInfo(transactionInfo1, newTransactionInfo1);
    checkTransactionInfo(transactionInfo2, newTransactionInfo2);
    checkTransactionInfo(transactionInfo3, newTransactionInfo3);
  }

  private void checkTransactionInfo(TransactionInfo transactionInfo, TransactionInfo other) throws Exception {
    assertThat(other.getHash().equals(transactionInfo.getHash())).isTrue();

    assertThat(other.getNote()).isEqualTo(transactionInfo.getNote());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = transactionInfo.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchangeName()).isEqualTo(otherFiatPayment.getExchangeName());

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
