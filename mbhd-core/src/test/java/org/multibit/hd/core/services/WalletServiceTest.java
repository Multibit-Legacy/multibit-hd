package org.multibit.hd.core.services;

import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Optional;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.managers.WalletManagerTest;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

public class WalletServiceTest {

  private static final NetworkParameters NETWORK_PARAMETERS = BitcoinNetwork.current().get();

  private WalletService walletService;

  private static final CharSequence PASSWORD = "throckSplockChockAdock";

  private String firstAddress;

  @Before
  public void setUp() throws Exception {
    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    WalletId walletId = new WalletId(seed1);

    BackupManager.INSTANCE.initialise(temporaryDirectory, null);
    WalletSummary walletSummary = WalletManager.INSTANCE.getOrCreateWalletSummary(temporaryDirectory, seed1, PASSWORD);

    firstAddress = walletSummary.getWallet().getKeys().get(0).toAddress(NETWORK_PARAMETERS).toString();

    walletService = new WalletService();

    walletService.initialise(temporaryDirectory, walletId);
  }

  @Test
  public void testCreatePaymentRequest() throws Exception {
    // Initially there are no payment requests
    assertThat(walletService.getPaymentRequests().size()).isEqualTo(0);

    // Create a new payment request
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

    walletService.addPaymentRequest(paymentRequestData1);

    // Write the payment requests to the backing store
    walletService.writePayments();

    // Read the payment requests
    walletService.readPayments();

    // Check the new payment request is present
    Collection<PaymentRequestData> newPaymentRequestDatas = walletService.getPaymentRequests();
    assertThat(newPaymentRequestDatas.size()).isEqualTo(1);

    checkPaymentRequest(paymentRequestData1, newPaymentRequestDatas.iterator().next());
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
   public void testGenerateNextReceivingAddress() throws Exception {
    // The generated addresses for indices 1, 2, 3, 4 respectively (index = 0 is added to the wallet at creation time)
    assertThat("1ELwsxsbJEWTn9RCmkViLVGehwxEk61SbY").isEqualTo(walletService.generateNextReceivingAddress(Optional.of(PASSWORD)));
    assertThat("1L8HQhmDbs2i662EgitrRnFUyzXFckK5t").isEqualTo(walletService.generateNextReceivingAddress(Optional.of(PASSWORD)));
    assertThat("1AoKyvbxbvLWHQqRo2xkWzzyaLq1u1Mr2j").isEqualTo(walletService.generateNextReceivingAddress(Optional.of(PASSWORD)));
    assertThat("1D8hgjF8pWBKDMsuN2N59JsvuUNaQs6dAz").isEqualTo(walletService.generateNextReceivingAddress(Optional.of(PASSWORD)));

    // In real life you now need to save the payments db to save the lastIndexUsed !

    // Test the 'no-generate' functionality works
    assertThat(firstAddress).isEqualTo(walletService.generateNextReceivingAddress(Optional.<CharSequence>absent()));
  }

}
