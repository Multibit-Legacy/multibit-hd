package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.testing.FakeTxBuilder;
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
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

import static org.fest.assertions.Assertions.assertThat;

public class WalletServiceTest {

  private static NetworkParameters networkParameters;

  private WalletService walletService;

  private WalletSummary walletSummary;

  public static final String PASSWORD = "1throckSplockChockAdock";

  public static final String CHANGED_PASSWORD1 = "2orinocoFlow";

  public static final String CHANGED_PASSWORD2 = PASSWORD; // "3the quick brown fox jumps over the lazy dog";

  public static final String CHANGED_PASSWORD3 = PASSWORD; // "4bebop a doolah shen am o bing bang";

  private static final Logger log = LoggerFactory.getLogger(WalletServiceTest.class);

  private static final byte[] MERCHANT_DATA = new byte[] { 0, 1, 2 };

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
    WalletId walletId = new WalletId(seed1);

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

    walletService = CoreServices.getOrCreateWalletService(walletSummary.getWalletId());
    walletService.initialise(temporaryDirectory, walletId, PASSWORD);
  }

  @Test
  public void testCreateMBHDPaymentRequest() throws Exception {
    // Initially there are no payment requests
    assertThat(walletService.getMBHDPaymentRequestDataList().size()).isEqualTo(0);

    // Create a new payment request
    MBHDPaymentRequestData mbhdPaymentRequestData = new MBHDPaymentRequestData();

    mbhdPaymentRequestData.setAddress(Addresses.parse("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty").get());
    mbhdPaymentRequestData.setAmountCoin(Coin.valueOf(245));
    DateTime date1 = new DateTime();
    mbhdPaymentRequestData.setDate(date1);
    mbhdPaymentRequestData.setLabel("label1");
    mbhdPaymentRequestData.setNote("note1");

    FiatPayment fiatPayment1 = new FiatPayment();
    mbhdPaymentRequestData.setAmountFiat(fiatPayment1);
    fiatPayment1.setAmount(Optional.of(new BigDecimal("12345.6")));
    fiatPayment1.setCurrency(Optional.of(Currency.getInstance("USD")));
    fiatPayment1.setRate(Optional.of("10.0"));
    fiatPayment1.setExchangeName(Optional.of("Bitstamp"));

    walletService.addMBHDPaymentRequestData(mbhdPaymentRequestData);

    // Write the payment requests to the backing store
    walletService.writePayments(PASSWORD);

    // Read the payment requests
    walletService.readPayments(PASSWORD);

    // Check the new payment request is present
    Collection<MBHDPaymentRequestData> newMBHDPaymentRequestDataList = walletService.getMBHDPaymentRequestDataList();
    assertThat(newMBHDPaymentRequestDataList.size()).isEqualTo(1);

    checkMBHDPaymentRequestData(mbhdPaymentRequestData, newMBHDPaymentRequestDataList.iterator().next());

    // Delete the payment request
    walletService.deleteMBHDPaymentRequest(mbhdPaymentRequestData);

    // Check the new payment request is deleted
    Collection<MBHDPaymentRequestData> deletedMBHDPaymentRequestDataList = walletService.getMBHDPaymentRequestDataList();
    assertThat(deletedMBHDPaymentRequestDataList.size()).isEqualTo(0);

    // Undo the delete
    walletService.undoDeletePaymentData();

    // Check it is back
    Collection<MBHDPaymentRequestData> rebornMBHDPaymentRequestDataList = walletService.getMBHDPaymentRequestDataList();
    assertThat(rebornMBHDPaymentRequestDataList.size()).isEqualTo(1);
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
  public void testCreateBIP70PaymentRequest() throws Exception {
    // Create a BIP70 PaymentRequestData containing a Payment and a PaymentACK
    PaymentRequestData paymentRequestData = WalletServiceTest.createPlumpPaymentDataRequest();

    // Initially there are no BIP70 payment requests
    assertThat(walletService.getPaymentRequestDataList().size()).isEqualTo(0);
    walletService.addPaymentRequestData(paymentRequestData);

    // Write the payment requests, payments and paymentACKs to the backing store
    walletService.writePayments(PASSWORD);

    // Check the BIP70 files are stored - they are stored in a subdirectory 'bip70' with the name "uuid".paymentrequest.aes or ".payment.aes" or ".packmentack.aes"
    String root = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletFile().getParentFile()
                + File.separator + "payments" + File.separator + "bip70"
                + File.separator + paymentRequestData.getUuid().toString();

    File expectedPaymentRequestFile = new File (root + ".paymentrequest.aes");
    File expectedPaymentFile = new File (root + ".payment.aes");
    File expectedPaymentACKFile = new File (root + ".paymentack.aes");

    log.debug("Expected payment request file is {}", expectedPaymentRequestFile.getAbsoluteFile());
    log.debug("Expected payment file is {}", expectedPaymentFile.getAbsoluteFile());
    log.debug("Expected payment ACK file is {}", expectedPaymentACKFile.getAbsoluteFile());
    assertThat(expectedPaymentRequestFile.exists()).isTrue();
    assertThat(expectedPaymentFile.exists()).isTrue();
    assertThat(expectedPaymentACKFile.exists()).isTrue();

    // Read the payment requests from disk
    walletService.readPayments(PASSWORD);

    // Check the new payment request is present
    Collection<PaymentRequestData> newPaymentRequestDataList = walletService.getPaymentRequestDataList();
    assertThat(newPaymentRequestDataList.size()).isEqualTo(1);

    checkPaymentRequestData(paymentRequestData, newPaymentRequestDataList.iterator().next());

    // Delete the BIP70 payment request, this will also delete any payment and paymentACK files
    walletService.deletePaymentRequest(paymentRequestData);
    walletService.writePayments(PASSWORD);

    // Check the new payment request, payment and paymentACK are deleted
    Collection<PaymentRequestData> deletedPaymentRequestDataList = walletService.getPaymentRequestDataList();
    assertThat(deletedPaymentRequestDataList.size()).isEqualTo(0);

    // Check the payment request, payment and payment ACK files are deleted
    assertThat(expectedPaymentRequestFile.exists()).isFalse();
    assertThat(expectedPaymentFile.exists()).isFalse();
    assertThat(expectedPaymentACKFile.exists()).isFalse();

    // Undo the delete
    walletService.undoDeletePaymentData();
    walletService.writePayments(PASSWORD);

    // Check everything is back
    Collection<PaymentRequestData> rebornPaymentRequestDataList = walletService.getPaymentRequestDataList();
    assertThat(rebornPaymentRequestDataList.size()).isEqualTo(1);

    checkPaymentRequestData(paymentRequestData, rebornPaymentRequestDataList.iterator().next());

    // Check the payment request, payment and paymentACK files are back
    assertThat(expectedPaymentRequestFile.exists()).isTrue();
    assertThat(expectedPaymentFile.exists()).isTrue();
    assertThat(expectedPaymentACKFile.exists()).isTrue();
  }

  private void checkPaymentRequestData(PaymentRequestData first, PaymentRequestData other) {
    assertThat(other.getUuid()).isEqualTo(first.getUuid());
    assertThat(other.getTransactionHash()).isEqualTo(first.getTransactionHash());
    assertThat(other.getDescription()).isEqualTo(first.getDescription());
    assertThat(other.getAmountCoin()).isEqualTo(first.getAmountCoin());
    assertThat(other.getAmountFiat()).isEqualTo(first.getAmountFiat());
    assertThat(other.getDate()).isEqualTo(first.getDate());
    assertThat(other.getExpirationDate()).isEqualTo(first.getExpirationDate());
    assertThat(other.getTrustStatus()).isEqualTo(first.getTrustStatus());
    assertThat(other.getTrustErrorMessage()).isEqualTo(first.getTrustErrorMessage());
    assertThat(other.getIdentityDisplayName()).isEqualTo(first.getIdentityDisplayName());
    assertThat(other.getNote()).isEqualTo(first.getNote());
    assertThat(other.getType()).isEqualTo(first.getType());

    // BIP 70 payment request
    assertThat(first.getPaymentRequest().isPresent()).isEqualTo(other.getPaymentRequest().isPresent());

    // BIP 70 payment
    assertThat(first.getPayment().isPresent()).isEqualTo(other.getPayment().isPresent());
    assertThat(other.getPayment().get().getMemo()).isEqualTo(first.getPayment().get().getMemo());

    // BIP 70 payment ACK
    assertThat(first.getPaymentACK().isPresent()).isEqualTo(other.getPaymentACK().isPresent());
    assertThat(other.getPaymentACK().get().getMemo()).isEqualTo(first.getPaymentACK().get().getMemo());
  }

  public static PaymentRequestData createPlumpPaymentDataRequest() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, AddressFormatException {
      // Create a BIP70 PaymentRequestData that the BIP70 protobuf objects will be stored in
    PaymentRequestData paymentRequestData = new PaymentRequestData();
    paymentRequestData.setUuid(UUID.randomUUID());
    paymentRequestData.setAmountCoin(Coin.MILLICOIN);
    paymentRequestData.setTrustStatus(PaymentSessionStatus.UNKNOWN);
    paymentRequestData.setTrustErrorMessage("");

    PaymentProtocolService paymentProtocolService = new PaymentProtocolService(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
    assertThat(paymentProtocolService).isNotNull();

    // Load the signing key store locally
    KeyStore keyStore = KeyStore.getInstance("JKS");
    InputStream keyStream = PaymentProtocolService.class.getResourceAsStream("/localhost.jks");
    keyStore.load(keyStream, HttpsManager.PASSPHRASE.toCharArray());

    SignedPaymentRequestSummary signedPaymentRequestSummary = new SignedPaymentRequestSummary(
            new Address(networkParameters, "1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"),
            Coin.MILLICOIN,
            "Please donate to MultiBit",
            new URL("https://localhost:8443/payment"),
            MERCHANT_DATA,
            keyStore,
            "serverkey",
            HttpsManager.PASSPHRASE.toCharArray()
    );

    // Create a payment request
    final Optional<Protos.PaymentRequest> paymentRequest = paymentProtocolService.newSignedPaymentRequest(signedPaymentRequestSummary);
    assertThat(paymentRequest).isNotNull();
    assertThat(paymentRequest.isPresent()).isTrue();

    paymentRequestData.setPaymentRequest(paymentRequest);


    // Create a payment for the above payment request
    String paymentMemo = "This is a payment memo";
    List<Transaction> transactions = new LinkedList<>();
    Coin amount = Coin.COIN;
    Address toAddress = new ECKey().toAddress(networkParameters);
    transactions.add(FakeTxBuilder.createFakeTx(networkParameters, amount, toAddress));
    Coin refundAmount = Coin.SATOSHI;
    Address refundAddress = new ECKey().toAddress(networkParameters);

    final Optional<Protos.Payment> payment = paymentProtocolService.newPayment(MERCHANT_DATA, transactions, refundAmount, refundAddress, paymentMemo);
    assertThat(payment).isNotNull();
    assertThat(payment.isPresent()).isTrue();

    paymentRequestData.setPayment(payment);


    // Create a payment ACK for the above payment
    String paymentACKMemo = "This is a payment ACK memo";
    final Optional<Protos.PaymentACK> paymentACK = paymentProtocolService.newPaymentACK(payment.get(), paymentACKMemo);
    assertThat(paymentACK).isNotNull();
    assertThat(paymentACK.isPresent()).isTrue();

    paymentRequestData.setPaymentACK(paymentACK);

    return paymentRequestData;
  }

  @Test
  public void testChangePassword() throws Exception {
    log.debug("Start of testChangePassword");
     // Create a BIP70 PaymentRequestData containing a Payment and a PaymentACK
    PaymentRequestData paymentRequestData = WalletServiceTest.createPlumpPaymentDataRequest();

    // Remove any extant BIP70 payment requests
    List<PaymentRequestData> extantPaymentRequestDatas = walletService.getPaymentRequestDataList();
    if (extantPaymentRequestDatas != null) {
      for (PaymentRequestData extantPaymentRequestData : extantPaymentRequestDatas) {
        walletService.deletePaymentRequest(extantPaymentRequestData);
      }
    }
    assertThat(walletService.getPaymentRequestDataList().size()).isEqualTo(0);
    walletService.addPaymentRequestData(paymentRequestData);

    // Write the payment requests, payments and paymentACKs to the backing store
    walletService.writePayments(PASSWORD);

    assertThat(walletService.getPaymentRequestDataList().size()).isEqualTo(1);

    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Save the wallet with the old password
    CoreServices.getOrCreateWalletService(walletSummary.getWalletId());
    WalletManager.INSTANCE.saveWallet();

    // Change the credentials once
    // (use the changeCurrentWalletPasswordInternal as that does not use an executor - easier to test)
    WalletService.changeCurrentWalletPasswordInternal(PASSWORD, CHANGED_PASSWORD1);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD1)).isTrue();

    checkPasswordHasChanged(CHANGED_PASSWORD1);

    // Change the credentials again
    WalletService.changeCurrentWalletPasswordInternal(CHANGED_PASSWORD1, CHANGED_PASSWORD2);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD2)).isTrue();

    checkPasswordHasChanged(CHANGED_PASSWORD2);

    // And change it back to the original value just for good measure
    WalletService.changeCurrentWalletPasswordInternal(CHANGED_PASSWORD2, PASSWORD);
    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    checkPasswordHasChanged(PASSWORD);

    // Change the credentials again
    WalletService.changeCurrentWalletPasswordInternal(PASSWORD, CHANGED_PASSWORD3);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD3)).isTrue();

    checkPasswordHasChanged(CHANGED_PASSWORD3);
  }

  private void checkPasswordHasChanged(String password) throws Exception {
    // Reload the wallet
    Wallet wallet = WalletManager.INSTANCE.loadWalletFromFile(new File(walletSummary.getWalletFile().getAbsolutePath() + ".aes"), password);
    assertThat(wallet).isNotNull();

    // Reload contacts db
    CoreServices.getCurrentContactService().loadContacts(password);

    // Reload history db
    CoreServices.getCurrentHistoryService().loadHistory(password);

    // Reload payment db
    CoreServices.getCurrentWalletService().get().readPayments(password);
  }

  @Test
  /**
   * A repeat of the change password test to check raciness (issue #322)
   */
  public void testChangePasswordRepeat() throws Exception {
    log.debug("Start of testChangePassword repeat");

    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials once
    WalletService.changeCurrentWalletPasswordInternal(PASSWORD, CHANGED_PASSWORD1);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD1)).isTrue();

    // Change the credentials again
    WalletService.changeCurrentWalletPasswordInternal(CHANGED_PASSWORD1, CHANGED_PASSWORD2);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD2)).isTrue();

    // Change it back to the original value
    WalletService.changeCurrentWalletPasswordInternal(CHANGED_PASSWORD2, PASSWORD);
    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the credentials again
    WalletService.changeCurrentWalletPasswordInternal(PASSWORD, CHANGED_PASSWORD3);
    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD3)).isTrue();
  }

  @Test
  /**
   * Simple test to check decryption of a bitcoinj wallet - referenced in bitcoinj issue:
   * https://code.google.com/p/bitcoinj/issues/detail?id=573&thanks=573&ts=1406733004
   */
  public void testChangePasswordSimple() throws Exception {
    NetworkParameters networkParameters = MainNetParams.get();

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
