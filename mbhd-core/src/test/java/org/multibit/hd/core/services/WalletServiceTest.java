package org.multibit.hd.core.services;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.store.FiatPayment;
import org.multibit.hd.core.store.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletServiceTest {

  private static final Logger log = LoggerFactory.getLogger(WalletServiceTest.class);

  private WalletService walletService;

  private static final String PASSWORD = "throckSplockChockAdock";

  @Before
  public void setUp() throws Exception {
    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    WalletId walletId = new WalletId(seed1);

    WalletManager.INSTANCE.initialise(temporaryDirectory);
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);
    WalletManager.INSTANCE.createWallet(temporaryDirectory.getAbsolutePath(), seed1, PASSWORD);

    walletService = new WalletService();

    walletService.initialise(temporaryDirectory, walletId);
  }

  @Test
  public void testCreatePaymentRequest() throws Exception {
    // Initially there are no payment requests
    assertThat(walletService.getPayments().getPaymentRequests().size()).isEqualTo(0);

    // Create a new payment request
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

    // TODO should be using unmodifiable Collections
    walletService.getPayments().getPaymentRequests().add(paymentRequest1);

    // Write the payment requests to the backing store
    walletService.writePayments();

    // Clear the payment requests
    walletService.getPayments().setPaymentRequests(new ArrayList<PaymentRequest>());
    assertThat(walletService.getPayments().getPaymentRequests().size()).isEqualTo(0);

    // Read the payment requests
    walletService.readPayments();

    // Check the new payment request is present
    Collection<PaymentRequest> newPaymentRequests = walletService.getPayments().getPaymentRequests();
    assertThat(newPaymentRequests.size()).isEqualTo(1);

    checkPaymentRequest(paymentRequest1, newPaymentRequests.iterator().next());
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
   public void testGenerateNextReceivingAddress() throws Exception {
    // The generated addresses for indices 1, 2, 3, 4 respectively (index = 0 is added to the wallet at creation time)
    assertThat("1ELwsxsbJEWTn9RCmkViLVGehwxEk61SbY").isEqualTo(walletService.generateNextReceivingAddress(PASSWORD));
    assertThat("1L8HQhmDbs2i662EgitrRnFUyzXFckK5t").isEqualTo(walletService.generateNextReceivingAddress(PASSWORD));
    assertThat("1AoKyvbxbvLWHQqRo2xkWzzyaLq1u1Mr2j").isEqualTo(walletService.generateNextReceivingAddress(PASSWORD));
    assertThat("1D8hgjF8pWBKDMsuN2N59JsvuUNaQs6dAz").isEqualTo(walletService.generateNextReceivingAddress(PASSWORD));

    // In real life you now need to save the payments db to save the lastIndexUsed !
  }

}
