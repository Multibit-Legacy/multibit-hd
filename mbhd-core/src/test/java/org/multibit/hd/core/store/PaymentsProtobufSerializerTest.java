package org.multibit.hd.core.store;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.services.WalletService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
//    // Test you can add some requests and read them back
//    List<Contact> allContacts = serializer.allContacts(1, 10);
//
//    assertThat(allContacts.size()).isEqualTo(6);
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
