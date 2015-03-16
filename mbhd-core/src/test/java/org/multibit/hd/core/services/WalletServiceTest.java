package org.multibit.hd.core.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.security.KeyStore;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class WalletServiceTest {

  private static NetworkParameters networkParameters;

  private WalletService walletService;

  private WalletId walletId;

  private WalletSummary walletSummary;

  public static final String PASSWORD = "1throckSplockChockAdock";

  public static final String CHANGED_PASSWORD1 = "2orinocoFlow";

  public static final String CHANGED_PASSWORD2 = "3the quick brown fox jumps over the lazy dog";

  public static final String CHANGED_PASSWORD3 = "4bebop a doolah shen am o bing bang";

  private static final Logger log = LoggerFactory.getLogger(WalletServiceTest.class);


  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] entropy1 = MnemonicCode.INSTANCE.toEntropy(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    walletId = new WalletId(seed1);

    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());
    InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

    long nowInSeconds = Dates.nowInSeconds();
    walletSummary = WalletManager
            .INSTANCE
            .getOrCreateMBHDSoftWalletSummaryFromEntropy(
                    temporaryDirectory,
                    entropy1,
                    seed1,
                    nowInSeconds,
                    PASSWORD,
                    "Example",
                    "Example",
                    false); // No need to sync

    WalletManager.INSTANCE.setCurrentWalletSummary(walletSummary);

    walletService = new WalletService(networkParameters);

    walletService.initialise(temporaryDirectory, walletId);
  }

  @Test
  public void testCreateMBHDPaymentRequest() throws Exception {
    // Initially there are no payment requests
    assertThat(walletService.getMBHDPaymentRequestDatas().size()).isEqualTo(0);

    // Create a new payment request
    MBHDPaymentRequestData MBHDPaymentRequestData1 = new MBHDPaymentRequestData();

    MBHDPaymentRequestData1.setAddress(Addresses.parse("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty").get());
    MBHDPaymentRequestData1.setAmountCoin(Coin.valueOf(245));
    DateTime date1 = new DateTime();
    MBHDPaymentRequestData1.setDate(date1);
    MBHDPaymentRequestData1.setLabel("label1");
    MBHDPaymentRequestData1.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    MBHDPaymentRequestData1.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("12345.6")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("USD")));
    fiatPayment1.setRate(Optional.of("10.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    walletService.addMBHDPaymentRequestData(MBHDPaymentRequestData1);

    // Write the payment requests to the backing store
    walletService.writePayments();

    // Read the payment requests
    walletService.readPayments();

    // Check the new payment request is present
    Collection<MBHDPaymentRequestData> newMBHDPaymentRequestDatas = walletService.getMBHDPaymentRequestDatas();
    assertThat(newMBHDPaymentRequestDatas.size()).isEqualTo(1);

    checkMBHDPaymentRequestData(MBHDPaymentRequestData1, newMBHDPaymentRequestDatas.iterator().next());
  }

  private void checkMBHDPaymentRequestData(MBHDPaymentRequestData MBHDPaymentRequestData, MBHDPaymentRequestData other) {
    assertThat(other.getAddress()).isEqualTo(MBHDPaymentRequestData.getAddress());
    assertThat(other.getLabel()).isEqualTo(MBHDPaymentRequestData.getLabel());
    assertThat(other.getNote()).isEqualTo(MBHDPaymentRequestData.getNote());
    assertThat(other.getAmountCoin()).isEqualTo(MBHDPaymentRequestData.getAmountCoin());
    assertThat(other.getDate()).isEqualTo(MBHDPaymentRequestData.getDate());

    FiatPayment fiatPayment = other.getAmountFiat();
    FiatPayment otherFiatPayment = MBHDPaymentRequestData.getAmountFiat();
    assertThat(fiatPayment.getAmount()).isEqualTo(otherFiatPayment.getAmount());
    assertThat(fiatPayment.getRate()).isEqualTo(otherFiatPayment.getRate());
    assertThat(fiatPayment.getExchangeName()).isEqualTo(otherFiatPayment.getExchangeName());
  }

  @Test
  public void testCreatePaymentRequest() throws Exception {
    PaymentProtocolService paymentProtocolService = new PaymentProtocolService(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
    assertThat(paymentProtocolService).isNotNull();

    // Load the signing key store locally
    KeyStore keyStore = KeyStore.getInstance("JKS");
    InputStream keyStream = PaymentProtocolService.class.getResourceAsStream("/localhost.jks");
    keyStore.load(keyStream, SSLManager.PASSPHRASE.toCharArray());

    SignedPaymentRequestSummary signedPaymentRequestSummary = new SignedPaymentRequestSummary(
            new Address(networkParameters, "1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"),
            Coin.CENT,
            "Please donate to MultiBit",
            new URL("https://localhost:8443/payment"),
            "Donation 0001".getBytes(Charsets.UTF_8),
            keyStore,
            "serverkey",
            SSLManager.PASSPHRASE.toCharArray()
    );

    // Act
    final Optional<Protos.PaymentRequest> paymentRequestOptional = paymentProtocolService.newSignedPaymentRequest(signedPaymentRequestSummary);

    assertThat(paymentRequestOptional).isNotNull();
    assertThat(paymentRequestOptional.isPresent()).isTrue();

    // Initially there are no BIP70 payment requests
    assertThat(walletService.getPaymentRequestDatas().size()).isEqualTo(0);

    PaymentRequestData paymentRequestData = new PaymentRequestData(paymentRequestOptional.get(), Optional.<Sha256Hash>absent());

    walletService.addPaymentRequestData(paymentRequestData);

    // Write the payment requests to the backing store
    //walletService.writePayments();

    // Read the payment requests
    //walletService.readPayments();

    // Check the new payment request is present
    Collection<PaymentRequestData> newPaymentRequestDatas = walletService.getPaymentRequestDatas();
    assertThat(newPaymentRequestDatas.size()).isEqualTo(1);

    checkPaymentRequestData(paymentRequestData, newPaymentRequestDatas.iterator().next());
  }

  private void checkPaymentRequestData(PaymentRequestData first, PaymentRequestData other) {
    assertThat(other.getUuid().equals(first.getUuid()));
    assertThat(other.getTransactionHashOptional().equals(first.getTransactionHashOptional()));
    assertThat(other.getPaymentRequest().equals(first.getPaymentRequest()));
  }

  @Test
  public void testChangePassword() throws Exception {
    log.debug("Start of testChangePassword");

    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials once
    WalletService.changeWalletPasswordInternal(walletSummary, PASSWORD, CHANGED_PASSWORD1);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD1)).isTrue();

    // Change the credentials again
    WalletService.changeWalletPasswordInternal(walletSummary, CHANGED_PASSWORD1, CHANGED_PASSWORD2);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD2)).isTrue();

    // And change it back to the original value just for good measure
    WalletService.changeWalletPasswordInternal(walletSummary, CHANGED_PASSWORD2, PASSWORD);
    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials again
    WalletService.changeWalletPasswordInternal(walletSummary, PASSWORD, CHANGED_PASSWORD3);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD3)).isTrue();
  }

  @Test
  /**
   * A repeat of the change password test to check raciness (issue #322)
   */
  public void testChangePasswordRepeat() throws Exception {
    log.debug("Start of testChangePassword repeat");

    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials once
    WalletService.changeWalletPasswordInternal(walletSummary, PASSWORD, CHANGED_PASSWORD1);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD1)).isTrue();

    // Change the credentials again
    WalletService.changeWalletPasswordInternal(walletSummary, CHANGED_PASSWORD1, CHANGED_PASSWORD2);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD2)).isTrue();

    // Change it back to the original value
    WalletService.changeWalletPasswordInternal(walletSummary, CHANGED_PASSWORD2, PASSWORD);
    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials again
    WalletService.changeWalletPasswordInternal(walletSummary, PASSWORD, CHANGED_PASSWORD3);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD3)).isTrue();
  }

  @Test
  /**
   * Simple test to check decryption of a bitcoinj wallet - referenced in bitcoinj issue:
   * https://code.google.com/p/bitcoinj/issues/detail?id=573&thanks=573&ts=1406733004
   */
  public void testChangePasswordSimple() throws Exception {
    NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

    long creationTimeSecs = MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    String seedStr = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";

    // Parse as mnemonic code.
    final List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedStr));


    // Test encrypt / decrypt with empty passphrase
    DeterministicSeed seed1 = new DeterministicSeed(split, null, "", creationTimeSecs);

    Wallet wallet1 = Wallet.fromSeed(networkParameters, seed1);

    // Encrypt wallet
    wallet1.encrypt(PASSWORD);

    // Decrypt the wallet
    wallet1.decrypt(PASSWORD);
  }
}
