package org.multibit.hd.core.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Enable this test if you want to manually test a wallet
 */
@Ignore
public class BitcoinNetworkServiceFunctionalTest {

  private static final String WALLET_PASSWORD = "orinocoFlow";

  private static final String WALLET_SEED_1_PROPERTY_NAME = "walletSeed1";
  private static final String WALLET_SEED_2_PROPERTY_NAME = "walletSeed2";

  private static final String WALLET_TIMESTAMP_1_PROPERTY_NAME = "walletTimestamp1";
  private static final String WALLET_TIMESTAMP_2_PROPERTY_NAME = "walletTimestamp2";

  private static final Coin SEND_AMOUNT = Coin.valueOf(100000); // 0.001 BTC
  private static final Coin FEE_PER_KB = Coin.valueOf(10000); // 0.0001 BTC / KB

  private static final int MAX_TIMEOUT = 600000; // ms
  private static final int WAIT_INTERVAL = 100; // ms

  /**
   * Separate from BitcoinNetworkService since some tests do not require it to be initialised
   */
  private static final NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

  private Properties seedProperties;

  private BitcoinNetworkService bitcoinNetworkService;

  private BitcoinSentEvent bitcoinSentEvent;
  private int percentComplete;

  private WalletManager walletManager;

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkServiceFunctionalTest.class);

  @Before
  public void setUp() throws IOException {
    CoreServices.main(null);
    CoreEvents.subscribe(this);

    walletManager = WalletManager.INSTANCE;

    // Create two wallets based on different seeds.
    // The seeds are stored in a file in resources called seed.properties.
    // This SHOULD NOT be under source control.
    //
    // A template file is provided called seed.properties.template for you to copy.
    seedProperties = loadWalletSeeds();
    assertThat(seedProperties).isNotNull();
    assertThat(seedProperties.getProperty(WALLET_SEED_1_PROPERTY_NAME).length() > 0).isTrue();
    assertThat(seedProperties.getProperty(WALLET_SEED_2_PROPERTY_NAME).length() > 0).isTrue();
    assertThat(seedProperties.getProperty(WALLET_TIMESTAMP_1_PROPERTY_NAME).length() > 0).isTrue();
    assertThat(seedProperties.getProperty(WALLET_TIMESTAMP_2_PROPERTY_NAME).length() > 0).isTrue();

    // You also need to fund one (or both) of the wallets so that there is some bitcoin to send from one to the other.
  }


  // This test is a simpler version of the testSendBetweenTwoRealWallets one.
  // It creates and replays a single wallet.
  @Test
  public void testSyncSingleWallet() throws Exception {

    // Create a random temporary directory and use it for wallet storage
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());

    // Create a wallet from the WALLET_SEED_1_PROPERTY_NAME
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_1_PROPERTY_NAME)));
    WalletSummary walletSummary = createWallet(temporaryDirectory, seed, "Example", "Example");

    DateTime timestamp1 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_1_PROPERTY_NAME));

    // Replay the single (first) wallet
    replayWallet(timestamp1);

    log.debug("WalletSummary (after sync) = \n{}", walletSummary.toString());

    Coin walletBalance = walletSummary.getWallet().getBalance(Wallet.BalanceType.ESTIMATED);

    // If this test fails please fund the test wallet with bitcoin as it is needed for the sending test !
    // (The wallet is logged to the console so you can see the address you need to fund).
    assertThat(walletBalance.compareTo(Coin.ZERO) > 0).isTrue();

    // See if there are any payments
    WalletService walletService = new WalletService(BitcoinNetwork.current().get());

    walletService.initialise(temporaryDirectory, new WalletId(seed));

    // Get the current wallets payments - there should be some
    List<PaymentData> transactions = walletService.getPaymentDataList();

    log.debug("The payments in the wallet are:\n{}", transactions);
    assertThat(transactions.size() > 0).isTrue();
  }

  @Test
  public void testSendBetweenTwoRealWallets() throws Exception {

    // Create a random temporary directory to writeContacts the wallets
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();
    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());

    // Create two wallets from the two seeds
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_1_PROPERTY_NAME)));
    WalletSummary walletSummary1 = createWallet(temporaryDirectory, seed1, "Example", "Example");
    String walletRoot1 = WalletManager.createWalletRoot(walletSummary1.getWalletId());

    DateTime timestamp1 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_1_PROPERTY_NAME));

    byte[] seed2 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_2_PROPERTY_NAME)));
    WalletSummary walletSummary2 = createWallet(temporaryDirectory, seed2, "Example", "Example");
    String walletRoot2 = WalletManager.createWalletRoot(walletSummary2.getWalletId());

    DateTime timestamp2 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_2_PROPERTY_NAME));

    // Remember the addresses in the wallets, which will be used for the send
    ECKey key1 = walletSummary1.getWallet().freshReceiveKey();
    Address address1 = key1.toAddress(networkParameters);

    ECKey key2 = walletSummary2.getWallet().freshReceiveKey();
    Address address2 = key2.toAddress(networkParameters);

    // Synchronize the current wallet, which will be wallet2 as that was created last
    replayWallet(timestamp2);

    Coin walletBalance2 = walletSummary2.getWallet().getBalance();

    // Set the current wallet to be wallet1 and synchronize that
    Configurations.currentConfiguration.getWallet().setLastSoftWalletRoot(walletRoot1);
    walletManager.setCurrentWalletSummary(walletSummary1);
    replayWallet(timestamp1);

    Coin walletBalance1 = walletSummary1.getWallet().getBalance();

    log.debug("Wallet data 1 = \n{}", walletSummary1.toString());
    log.debug("Wallet data 2 = \n{}", walletSummary2.toString());


    // Check older payments (probably from earlier runs of this test) have confirmed
    assertThat(transactionsAreOK(walletSummary1)).isTrue();
    assertThat(transactionsAreOK(walletSummary2)).isTrue();

    // Create a send from the wallet with the larger balance to the one with the smaller balance
    boolean sendFromWallet1 = walletBalance1.compareTo(walletBalance2) > 0;

    WalletSummary sourceWalletSummary;
    String walletRoot;
    Address changeAddress;
    Address destinationAddress;
    if (sendFromWallet1) {
      sourceWalletSummary = walletSummary1;
      walletRoot = walletRoot1;
      changeAddress = address1;
      destinationAddress = address2;
    } else {
      sourceWalletSummary = walletSummary2;
      walletRoot = walletRoot2;
      changeAddress = address2;
      destinationAddress = address1;
    }

    // Ensure MBHD has the correct current wallet (which will be used for the send)
    Configurations.currentConfiguration.getWallet().setLastSoftWalletRoot(walletRoot);
    walletManager.setCurrentWalletSummary(sourceWalletSummary);

    // Start up the bitcoin network connection
    bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    bitcoinNetworkService.start();

    // Wait a while for everything to start
    Uninterruptibles.sleepUninterruptibly(4000, TimeUnit.MILLISECONDS);

    try {
      // Clear the bitcoinSentEvent member variable so we know it is a new one later
      bitcoinSentEvent = null;

      // Send the bitcoins
      final SendRequestSummary sendRequestSummary = new SendRequestSummary(
        destinationAddress,
        SEND_AMOUNT,
        Optional.<FiatPayment>absent(),
        changeAddress,
        FEE_PER_KB,
        WALLET_PASSWORD,
        Optional.<FeeState>absent(),
        false);
      sendRequestSummary.setNotes(Optional.of("BitcoinNetworkServiceFunctionalTest"));
      bitcoinNetworkService.send(sendRequestSummary);

      // the onBitcoinSentEvent method receives the bitcoinSentEvent once the send has completed
      // wait for a while for the send to actually be transmitted
      Uninterruptibles.sleepUninterruptibly(10000, TimeUnit.MILLISECONDS);

      // Check for success
      assertThat(bitcoinSentEvent).isNotNull();
      assertThat(bitcoinSentEvent.isSendWasSuccessful()).isTrue();
    } finally {
      CoreServices.stopBitcoinNetworkService();
    }
  }

  @Subscribe
  public void onBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {

    this.bitcoinSentEvent = bitcoinSentEvent;
  }

  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent bitcoinNetworkChangedEvent) {

    // Remember the percentage complete for a download
    if (bitcoinNetworkChangedEvent.getSummary().getPercent() > 0) {
      percentComplete = bitcoinNetworkChangedEvent.getSummary().getPercent();
    }
  }

  private WalletSummary createWallet(File walletDirectory, byte[] seed, String name, String notes) throws IOException {

    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary = walletManager.badlyGetOrCreateMBHDSoftWalletSummaryFromSeed(
            walletDirectory,
            seed,
            nowInSeconds,
            WALLET_PASSWORD,
            name,
            notes,
            true); // Perform sync

    assertThat(walletSummary).isNotNull();
    assertThat(walletSummary.getWallet()).isNotNull();

    // There should be a single key
    assertThat(walletSummary.getWallet().getKeychainSize() == 1).isTrue();

    return walletSummary;
  }

  private void replayWallet(DateTime replayDate) throws IOException, BlockStoreException, TimeoutException {

    bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();

    // Clear percentage complete
    percentComplete = 0;

    bitcoinNetworkService.replayWallet(InstallationManager.getOrCreateApplicationDataDirectory(), Optional.of(replayDate.toDate()));

    int timeout = 0;
    while (timeout < MAX_TIMEOUT && (percentComplete < 100)) {
      // Download still not complete
      Uninterruptibles.sleepUninterruptibly(WAIT_INTERVAL, TimeUnit.MILLISECONDS);
      timeout += WAIT_INTERVAL;

      if (timeout % 1000 == 0) {
        log.debug("Percent complete = '" + percentComplete + "'");
      }
    }
    if (percentComplete < 100) {
      throw new IllegalStateException("Download did not complete.");
    }

    CoreServices.stopBitcoinNetworkService();

  }

  /**
   * Check that payments have been confirmed in a reasonable time.
   *
   * @param walletSummary The wallet summary whose payments you want to check
   *
   * @return true is payments have confirmed as expected, false otherwise
   */
  private boolean transactionsAreOK(WalletSummary walletSummary) {

    boolean transactionsAreOK = true;

    if (walletSummary.getWallet() != null) {
      Set<Transaction> transactions = walletSummary.getWallet().getTransactions(true);
      for (Transaction transaction : transactions) {
        // If the payments is 'reasonably old' we expect it to have confirmed
        DateTime now = new org.joda.time.DateTime();

        if (now.minusHours(4).toDate().after(transaction.getUpdateTime())) {
          // If the transaction is over 4 hours old it should have confirmed
          if (transaction.getConfidence().getDepthInBlocks() == 0) {
            transactionsAreOK = false;
            break;
          }
        }
      }
    }
    return transactionsAreOK;
  }

  /**
   * Load the wallet seeds, which are stored in a properties file called seed.properties
   * The keys are specified by the WALLET_SEED_1_PROPERTY_NAME and WALLET_SEED_2_PROPERTY_NAME constants
   *
   * @return Properties containing wallet seeds
   */
  public static Properties loadWalletSeeds() throws FileNotFoundException, IOException {
    Properties seedProperties = new Properties();
    InputStream inputStream = BitcoinNetworkServiceFunctionalTest.class.getResourceAsStream("seed.properties");

    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
    seedProperties.load(inputStreamReader);

    return seedProperties;
  }
}
