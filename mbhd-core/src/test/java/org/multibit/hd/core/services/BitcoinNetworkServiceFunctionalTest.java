package org.multibit.hd.core.services;

import com.google.bitcoin.core.*;
import com.google.bitcoin.store.BlockStoreException;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class BitcoinNetworkServiceFunctionalTest {

  private static final String WALLET_PASSWORD = "orinocoFlow";

  private static final String WALLET_SEED_1_PROPERTY_NAME = "walletSeed1";
  private static final String WALLET_SEED_2_PROPERTY_NAME = "walletSeed2";

  private static final String WALLET_TIMESTAMP_1_PROPERTY_NAME = "walletTimestamp1";
  private static final String WALLET_TIMESTAMP_2_PROPERTY_NAME = "walletTimestamp2";

  private static final BigInteger SEND_AMOUNT = BigInteger.valueOf(100000); // 0.001 BTC
  private static final BigInteger FEE_PER_KB = BigInteger.valueOf(10000); // 0.0001 BTC / KB

  private static final int MAX_TIMEOUT = 600000; // ms
  private static final int WAIT_INTERVAL = 100; // ms

  private Properties seedProperties;

  private BitcoinNetworkService bitcoinNetworkService;

  private BitcoinSentEvent bitcoinSentEvent;
  private int percentComplete;

  private WalletManager walletManager;

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkServiceFunctionalTest.class);

  @Before
  public void setUp() throws IOException {
    CoreServices.main(null);
    CoreServices.uiEventBus.register(this);

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
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    walletManager.initialise(temporaryDirectory);
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Create a wallet from the WALLET_SEED_1_PROPERTY_NAME
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_1_PROPERTY_NAME)));
    WalletData walletData = createWallet(temporaryDirectory, seed);



    DateTime timestamp1 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_1_PROPERTY_NAME));

    // Replay the single (first) wallet
    replayWallet(timestamp1);

    log.debug("WalletData (after sync) = \n" + walletData.toString());

    BigInteger walletBalance = walletData.getWallet().getBalance(Wallet.BalanceType.ESTIMATED);

    // If this test fails please fund the test wallet with bitcoin as it is needed for the sending test !
    // (The wallet is logged to the console so you can see the address you need to fund).
    assertThat(walletBalance.compareTo(BigInteger.ZERO) > 0).isTrue();

    // See if there are any transactions
    WalletService walletService = CoreServices.newWalletService();

    walletService.initialise(temporaryDirectory, new WalletId(seed));

    // Get the current wallets transactions - there should be some
    Set<TransactionData>transactions = walletService.getTransactions();

    log.debug("The transactions in the wallet are:\n" + transactions);
    assertThat(transactions.size() > 0).isTrue();
  }

  @Test
  public void testSendBetweenTwoRealWallets() throws Exception {
    // Create a random temporary directory to writeContacts the wallets
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    walletManager.initialise(temporaryDirectory);
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Create two wallets from the two seeds
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_1_PROPERTY_NAME)));
    WalletData walletData1 = createWallet(temporaryDirectory, seed1);
    String walletRoot1 = walletManager.createWalletRoot(walletData1.getWalletId());

    DateTime timestamp1 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_1_PROPERTY_NAME));

    byte[] seed2 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(seedProperties.getProperty(WALLET_SEED_2_PROPERTY_NAME)));
    WalletData walletData2 = createWallet(temporaryDirectory, seed2);
    String walletRoot2 = walletManager.createWalletRoot(walletData2.getWalletId());

    DateTime timestamp2 = Dates.parseSeedTimestamp(seedProperties.getProperty(WALLET_TIMESTAMP_2_PROPERTY_NAME));

    // Remember the addresses in the wallets, which will be used for the send
    ECKey key1 = walletData1.getWallet().getKeys().get(0);
    Address address1 = key1.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));

    ECKey key2 = walletData2.getWallet().getKeys().get(0);
    Address address2 = key2.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));

    // Synchronize the current wallet, which will be wallet2 as that was created last
    replayWallet(timestamp2);

    BigInteger walletBalance2 = walletData2.getWallet().getBalance();

    // Set the current wallet to be wallet1 and synchronize that
    Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot1);
    walletManager.setCurrentWalletData(walletData1);
    replayWallet(timestamp1);

    BigInteger walletBalance1 = walletData1.getWallet().getBalance();

    log.debug("Wallet data 1 = \n" + walletData1.toString());
    log.debug("Wallet data 2 = \n" + walletData2.toString());


    // Check older transactions (probably from earlier runs of this test) have confirmed
    assertThat(transactionsAreOK(walletData1)).isTrue();
    assertThat(transactionsAreOK(walletData2)).isTrue();

    // Create a send from the wallet with the larger balance to the one with the smaller balance
    boolean sendFromWallet1 = walletBalance1.compareTo(walletBalance2) > 0;

    WalletData sourceWalletData;
    String walletRoot;
    Address sourceAddress;
    Address destinationAddress;
    if (sendFromWallet1) {
      sourceWalletData = walletData1;
      walletRoot = walletRoot1;
      sourceAddress = address1;
      destinationAddress = address2;
    } else {
      sourceWalletData = walletData2;
      walletRoot = walletRoot2;
      sourceAddress = address2;
      destinationAddress = address1;
    }

    // Ensure MBHD has the correct current wallet (which will be used for the send)
    Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletRoot(walletRoot);
    walletManager.setCurrentWalletData(sourceWalletData);

    // Start up the bitcoin network connection
    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();
    bitcoinNetworkService.start();

    // Wait a while for everything to start
    Uninterruptibles.sleepUninterruptibly(4000, TimeUnit.MILLISECONDS);

    try {
      // Clear the bitcoinSentEvent member variable so we know it is a new one later
      bitcoinSentEvent = null;

      // Send the bitcoins
      bitcoinNetworkService.send(destinationAddress.toString(), SEND_AMOUNT, sourceAddress.toString(), FEE_PER_KB, WALLET_PASSWORD);

      // the onBitcoinSentEvent method receives the bitcoinSentEvent once the send has completed
      // wait for a while for the send to actually be transmitted
      Uninterruptibles.sleepUninterruptibly(10000, TimeUnit.MILLISECONDS);

      // Check for success
      assertThat(bitcoinSentEvent).isNotNull();
      assertThat(bitcoinSentEvent.isSendWasSuccessful()).isTrue();
    } finally {
      bitcoinNetworkService.stopAndWait();
    }
  }

  @Subscribe
  public void onBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {
    //log.debug(bitcoinSentEvent.toString());
    this.bitcoinSentEvent = bitcoinSentEvent;
  }

  @Subscribe
  public void onBitcoinNetworkChangedEvent(BitcoinNetworkChangedEvent bitcoinNetworkChangedEvent) {
    //log.debug(bitcoinNetworkChangedEvent.toString());

    // Remember the percentage complete for a download
    if (bitcoinNetworkChangedEvent.getSummary().getPercent() > 0) {
      percentComplete = bitcoinNetworkChangedEvent.getSummary().getPercent();
    }
  }

  private WalletData createWallet(File walletDirectory, byte[] seed) throws IOException {
    WalletData walletData = walletManager.createWallet(walletDirectory.getAbsolutePath(), seed, WALLET_PASSWORD);
    assertThat(walletData).isNotNull();
    assertThat(walletData.getWallet()).isNotNull();

    // There should be a single key
    assertThat(walletData.getWallet().getKeychainSize() == 1).isTrue();

    return walletData;
  }

  private void replayWallet(DateTime replayDate) throws IOException, BlockStoreException {
    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    // Clear percentage complete
    percentComplete = 0;

    bitcoinNetworkService.replayWallet(replayDate);

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

    bitcoinNetworkService.stopAndWait();
  }

  /**
   * Check that transactions have been confirmed in a reasonable time.
   *
   * @param walletData the walletdata whose transactions you want to check
   * @return true is transactions have confirmed as expected, false otherwise
   */
  private boolean transactionsAreOK(WalletData walletData) {
    boolean transactionsAreOK = true;

    if (walletData.getWallet() != null) {
      Set<Transaction> transactions = walletData.getWallet().getTransactions(true);
      for (Transaction transaction :transactions) {
        // If the transactions is 'reasonably old' we expect it to have confirmed
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

    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF8");
    seedProperties.load(inputStreamReader);

    return seedProperties;
  }
}
