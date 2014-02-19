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
import java.util.Collection;
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
    paymentRequest1.setAmount_btc(BigInteger.valueOf(245));
    DateTime date1 = new DateTime();
    paymentRequest1.setDate(date1);
    paymentRequest1.setLabel("label1");
    paymentRequest1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    paymentRequest1.setFiatPayment(fiatPayment1);
    fiatPayment1.setAmount("12345.6");
    fiatPayment1.setCurrency("USD");
    fiatPayment1.setRate("10.0");
    fiatPayment1.setExchange("Bitstamp");

    PaymentRequest paymentRequest2 = new PaymentRequest();
    paymentRequest2.setAddress("1xyz");
    paymentRequest2.setAmount_btc(BigInteger.valueOf(789));
    DateTime date2 = date1.plusDays(7);
    paymentRequest2.setDate(date2);
    paymentRequest2.setLabel("label2");
    paymentRequest2.setNote("note2");

    FiatPayment fiatPayment2 = new FiatPayment();
    paymentRequest2.setFiatPayment(fiatPayment2);
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

    // TODO check the contents are the same

  }

  @Test
   public void testTransactions() throws Exception {
//     // Test you can add some transactions and read them back
//     serializer.clear();
//     List<Contact> allContacts = serializer.allContacts(1, 10);
//
//     assertThat(allContacts.size()).isEqualTo(0);
   }

  /**
   * Round trip the payments i.e. store to disk and read back in
   * @throws Exception
   */
  public Payments roundTrip(Payments payments) throws Exception {

    // Store the payments to the backing store
    serializer.writePayments(payments, new FileOutputStream(paymentsFile));

    // Reload it
    return serializer.readPayments(new FileInputStream((paymentsFile)));
  }
}
