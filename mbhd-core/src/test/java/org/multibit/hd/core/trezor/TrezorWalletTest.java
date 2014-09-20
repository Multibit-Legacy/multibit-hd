package org.multibit.hd.core.trezor;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.crypto.ChildNumber;
import com.google.bitcoin.crypto.DeterministicHierarchy;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.HDKeyDerivation;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.Uninterruptibles;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.BitcoinNetwork;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

public class TrezorWalletTest {

  private static NetworkParameters networkParameters;

  private static final Logger log = LoggerFactory.getLogger(TrezorWalletTest.class);

  private static final CharSequence PASSWORD = "bingBongaDingDong";

  private static final String TREZOR_SEED_PHRASE = "sniff divert demise scrub pony motor struggle innocent model mask enroll settle cash junior denial harsh peasant update estate aspect lyrics season empower asset";

  private static final DateTime TREZOR_WALLET_CREATION_DATE = new DateTime("2014-08-12T16:23:32");

  // m/44'/0'/0'/0/0
  private static final String EXPECTED_ADDRESS_0 = "1MkTpZN4TpLwJjZt9zHBXREJA8avUHXB3q";

  // m/44'/0'/0'/0/1
  private static final String EXPECTED_ADDRESS_1 = "1WGmwv86m1fFNVDRQ2YagdAFCButd36SV";

  // m/44'/0'/0'/0/2
  private static final String EXPECTED_ADDRESS_2 = "1PP1BvDeXjUcPDiEHBPWptQBAukhAwsLFt";

  // m/44'/0'/0'/0/3
  private static final String EXPECTED_ADDRESS_3 = "128f69V7GRqNSKwrjMkcuB6dbFKKEPtaLC";

  // m/44'/0'/0'/0/4
  private static final String EXPECTED_ADDRESS_4 = "18dxk72otf2amyAsjiKnEWhox5CJGQHYGA";

  @Before
  public void setUp() throws Exception {

  }

  @Test
  /**
   * Create some keys that derives addresses using BIP 44 - this is the HD account structure used by Trezor
   * This derivation uses the private master key
   */
  public void testCreateKeysWithTrezorAccount() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a private master key from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(TREZOR_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44'/0'/0'
    DeterministicKey trezorRootNode = WalletManager.generateTrezorRootNode(privateMasterKey);

    DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(trezorRootNode);

    DeterministicKey key_m_44h_0h_0h_0 = deterministicHierarchy.deriveChild(trezorRootNode.getPath(), false, false, new ChildNumber(0, false));
    log.debug("key_m_44h_0h_0h_0 = " + key_m_44h_0h_0h_0);

    DeterministicKey key_m_44h_0h_0h_0_0 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(0, false));
    log.debug("key_m_44h_0h_0h_0_0 = " + key_m_44h_0h_0h_0_0);
    Address address0 = key_m_44h_0h_0h_0_0.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_1 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(1, false));
    log.debug("key_m_44h_0h_0h_0_1 = " + key_m_44h_0h_0h_0_1);
    Address address1 = key_m_44h_0h_0h_0_1.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_2 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(2, false));
    log.debug("key_m_44h_0h_0h_0_2 = " + key_m_44h_0h_0h_0_2);
    Address address2 = key_m_44h_0h_0h_0_2.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_3 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(3, false));
    log.debug("key_m_44h_0h_0h_0_3 = " + key_m_44h_0h_0h_0_3);
    Address address3 = key_m_44h_0h_0h_0_3.toAddress(networkParameters);

    DeterministicKey key_m_44h_0h_0h_0_4 = deterministicHierarchy.deriveChild(key_m_44h_0h_0h_0.getPath(), false, false, new ChildNumber(4, false));
    log.debug("key_m_44h_0h_0h_0_4 = " + key_m_44h_0h_0h_0_4);
    Address address4 = key_m_44h_0h_0h_0_4.toAddress(networkParameters);

    assertThat(address0.toString()).isEqualTo(EXPECTED_ADDRESS_0);

    assertThat(address1.toString()).isEqualTo(EXPECTED_ADDRESS_1);

    assertThat(address2.toString()).isEqualTo(EXPECTED_ADDRESS_2);

    assertThat(address3.toString()).isEqualTo(EXPECTED_ADDRESS_3);

    assertThat(address4.toString()).isEqualTo(EXPECTED_ADDRESS_4);
  }

  @Test
  /**
   * Create a wallet that derives addresses using BIP 44 - this is the HD account structure used by Trezor
   *
   * Even though it uses the private master key is is actually a read only wallet - no private keys
   */
  public void testCreateWalletWithTrezorAccountUsingMasterPrivateKey() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(TREZOR_SEED_PHRASE));
    WalletId walletId = new WalletId(seed);

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44'/0'/0'
    // Create a root node from which all addresses will be generated
    DeterministicKey trezorRootNode = WalletManager.generateTrezorRootNode(privateMasterKey);

    BackupManager.INSTANCE.initialise(temporaryDirectory, null);
    InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

    // Create a Trezor soft wallet using the test seed phrase, using a BIP44 account structure
    WalletSummary walletSummary = WalletManager
            .INSTANCE
            .getOrCreateWalletSummary(
                    temporaryDirectory,
                    trezorRootNode,
                    TREZOR_WALLET_CREATION_DATE.getMillis() / 1000,
                    (String) PASSWORD,
                    "trezor-example",
                    "trezor-example"
            );

    assertThat(WalletType.TREZOR_SOFT_WALLET.equals(walletSummary.getWalletType()));

    WalletManager.INSTANCE.setCurrentWalletSummary(walletSummary);

    WalletService walletService = new WalletService(networkParameters);

    walletService.initialise(temporaryDirectory, walletId);

    String address0 = walletService.generateNextReceivingAddress(Optional.of(PASSWORD));
    String address1 = walletService.generateNextReceivingAddress(Optional.of(PASSWORD));
    String address2 = walletService.generateNextReceivingAddress(Optional.of(PASSWORD));
    String address3 = walletService.generateNextReceivingAddress(Optional.of(PASSWORD));
    String address4 = walletService.generateNextReceivingAddress(Optional.of(PASSWORD));

    log.debug("address 0  = " + address0);
    log.debug("address 1  = " + address1);
    log.debug("address 2  = " + address2);
    log.debug("address 3  = " + address3);
    log.debug("address 4  = " + address4);

    assertThat(address0).isEqualTo(EXPECTED_ADDRESS_0);

    assertThat(address1).isEqualTo(EXPECTED_ADDRESS_1);

    assertThat(address2).isEqualTo(EXPECTED_ADDRESS_2);

    assertThat(address3).isEqualTo(EXPECTED_ADDRESS_3);

    assertThat(address4).isEqualTo(EXPECTED_ADDRESS_4);

    // Remove comment if you want to: Sync the wallet to get the transactions
    // syncWallet();

    log.debug("Wallet at end of test = " + walletSummary.getWallet().toString());

  }

  private void syncWallet() throws Exception {
    BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
    bitcoinNetworkService.replayWallet(TREZOR_WALLET_CREATION_DATE);
    Uninterruptibles.sleepUninterruptibly(180, TimeUnit.SECONDS);
    bitcoinNetworkService.stopAndWait();
  }
}
