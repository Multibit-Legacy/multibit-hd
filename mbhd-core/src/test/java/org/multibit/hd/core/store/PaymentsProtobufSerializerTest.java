package org.multibit.hd.core.store;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.services.WalletService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
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
    Collection<PaymentRequest> paymentRequests = Lists.newArrayList();

    PaymentRequest paymentRequest1 = new PaymentRequest();

    paymentRequest1.setAddress("1abc");
    paymentRequest1.setAmountBTC(BigInteger.valueOf(245));
    DateTime date1 = new DateTime();
    paymentRequest1.setDate(date1);
    paymentRequest1.setLabel("label1");
    paymentRequest1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    paymentRequest1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount("12345.6");
    fiatPayment1.setCurrency("USD");
    fiatPayment1.setRate("10.0");
    fiatPayment1.setExchange("Bitstamp");

    PaymentRequest paymentRequest2 = new PaymentRequest();
    paymentRequest2.setAddress("1xyz");
    paymentRequest2.setAmountBTC(BigInteger.valueOf(789));
    DateTime date2 = date1.plusDays(7);
    paymentRequest2.setDate(date2);
    paymentRequest2.setLabel("label2");
    paymentRequest2.setNote("note2");

    FiatPayment fiatPayment2 = new FiatPayment();
    paymentRequest2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount("12345.678");
    fiatPayment2.setCurrency("GBP");
    fiatPayment2.setRate("20.0");
    fiatPayment2.setExchange("OER");

    paymentRequests.add(paymentRequest1);
    paymentRequests.add(paymentRequest2);

    Payments payments = new Payments(1);
    payments.setPaymentRequests(paymentRequests);

    Payments newPayments = roundTrip(payments);

    Collection<PaymentRequest> newPaymentRequests = newPayments.getPaymentRequests();
    assertThat(newPaymentRequests.size()).isEqualTo(2);

    Iterator<PaymentRequest> iterator = newPaymentRequests.iterator();
    PaymentRequest newPaymentRequest1 = iterator.next();
    PaymentRequest newPaymentRequest2 = iterator.next();

    checkPaymentRequest(paymentRequest1, newPaymentRequest1);
    checkPaymentRequest(paymentRequest2, newPaymentRequest2);
  }

  private void checkPaymentRequest(PaymentRequest paymentRequest, PaymentRequest other) {
    assertThat(other.getAddress()).isEqualTo(paymentRequest.getAddress());
    assertThat(other.getLabel()).isEqualTo(paymentRequest.getLabel());
    assertThat(other.getNote()).isEqualTo(paymentRequest.getNote());
    assertThat(other.getAmountBTC()).isEqualTo(paymentRequest.getAmountBTC());
    assertThat(other.getDate()).isEqualTo(paymentRequest.getDate());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = paymentRequest.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getCurrency()).isEqualTo(otherFiatPayment.getCurrency());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchange()).isEqualTo(otherFiatPayment.getExchange());
  }

  @Test
  public void testTransactionInfos() throws Exception {
    Collection<TransactionInfo> transactionInfos = Lists.newArrayList();

    TransactionInfo transactionInfo1 = new TransactionInfo();
    transactionInfos.add(transactionInfo1);
    transactionInfo1.setHash(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0xFF});
    transactionInfo1.setNote("notes1");

    Collection<String> requestAddresses1 = Lists.newArrayList();
    requestAddresses1.add("1abc");
    requestAddresses1.add("1def");
    requestAddresses1.add("1ghi");

    transactionInfo1.setRequestAddresses(requestAddresses1);

    FiatPayment fiatPayment1 = new FiatPayment();
    transactionInfo1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount("99.9");
    fiatPayment1.setCurrency("EUR");
    fiatPayment1.setRate("30.0");
    fiatPayment1.setExchange("MtGox");

    TransactionInfo transactionInfo2 = new TransactionInfo();
    transactionInfos.add(transactionInfo2);
    transactionInfo2.setHash(new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0xFF});
    transactionInfo2.setNote("notes1");

    Collection<String> requestAddresses2 = Lists.newArrayList();
    requestAddresses2.add("1abcdef");
    requestAddresses2.add("1defghi");
    requestAddresses2.add("1ghijkl");

    transactionInfo2.setRequestAddresses(requestAddresses2);

    FiatPayment fiatPayment2 = new FiatPayment();
    transactionInfo2.setAmountFiat(fiatPayment2);
    fiatPayment2.setAmount("11.1");
    fiatPayment2.setCurrency("JPY");
    fiatPayment2.setRate("50.0");
    fiatPayment2.setExchange("MtGoxJunior");

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
    assertThat(Arrays.equals(other.getHash(), transactionInfo.getHash())).isTrue();

    Collection<String> requestAddresses = transactionInfo.getRequestAddresses();
    Collection<String> otherRequestAddresses = other.getRequestAddresses();

    if (requestAddresses != null) {
      assertThat(otherRequestAddresses).isNotNull();
      Iterator iterator = requestAddresses.iterator();
      Iterator otherIterator = otherRequestAddresses.iterator();

      assertThat(otherIterator.next()).isEqualTo(iterator.next());
      assertThat(otherIterator.next()).isEqualTo(iterator.next());
      assertThat(otherIterator.next()).isEqualTo(iterator.next());
    } else {
      assertThat(otherRequestAddresses).isNull();
    }

    assertThat(other.getNote()).isEqualTo(transactionInfo.getNote());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = transactionInfo.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getCurrency()).isEqualTo(otherFiatPayment.getCurrency());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchange()).isEqualTo(otherFiatPayment.getExchange());
  }

  /**
   * Round trip the payments i.e. writeContacts to disk and read back in
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
