package org.multibit.hd.core.trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Uninterruptibles;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.KeyChain;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

public class TrezorWalletTest {

  private static NetworkParameters networkParameters;

  private static final Logger log = LoggerFactory.getLogger(TrezorWalletTest.class);

  private static final CharSequence PASSWORD = "bingBongaDingDong";

  private static final CharSequence CHANGED_PASSWORD = "I have a dream";

  private static final String TREZOR_SNIFF_SEED_PHRASE = "sniff divert demise scrub pony motor struggle innocent model mask enroll settle cash junior denial harsh peasant update estate aspect lyrics season empower asset";

  private static final DateTime TREZOR_SNIFF_WALLET_CREATION_DATE = new DateTime("2014-08-12T16:23:32");

  // These addresses were taken directly from myTrezor.com for a wallet with the TREZOR_SNIFF_SEED_PHRASE

  // m/44h/0h/0h/0/0
  private static final String SNIFF_EXPECTED_ADDRESS_0 = "1MkTpZN4TpLwJjZt9zHBXREJA8avUHXB3q";

  // m/44h/0h/0h/0/1
  private static final String SNIFF_EXPECTED_ADDRESS_1 = "1WGmwv86m1fFNVDRQ2YagdAFCButd36SV";

  // m/44h/0h/0h/0/2
  private static final String SNIFF_EXPECTED_ADDRESS_2 = "1PP1BvDeXjUcPDiEHBPWptQBAukhAwsLFt";

  // m/44h/0h/0h/0/3
  private static final String SNIFF_EXPECTED_ADDRESS_3 = "128f69V7GRqNSKwrjMkcuB6dbFKKEPtaLC";

  // m/44h/0h/0h/0/4
  private static final String SNIFF_EXPECTED_ADDRESS_4 = "18dxk72otf2amyAsjiKnEWhox5CJGQHYGA";

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
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(TREZOR_SNIFF_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44h/0h
    DeterministicKey trezorRootNode = WalletManager.generateTrezorWalletRootNode(privateMasterKey);

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

    assertThat(address0.toString()).isEqualTo(SNIFF_EXPECTED_ADDRESS_0);

    assertThat(address1.toString()).isEqualTo(SNIFF_EXPECTED_ADDRESS_1);

    assertThat(address2.toString()).isEqualTo(SNIFF_EXPECTED_ADDRESS_2);

    assertThat(address3.toString()).isEqualTo(SNIFF_EXPECTED_ADDRESS_3);

    assertThat(address4.toString()).isEqualTo(SNIFF_EXPECTED_ADDRESS_4);
  }

  @Test
  /**
   * Create a wallet that derives addresses using BIP 44 - this is the HD account structure used by Trezor
   *
   * This is a Trezor hard wallet, based on a pubkey only root node
   *
   * The wallet is roundtripped through protobuf and the expected keys/ addresses checked
   */
  public void testCreateTrezorHardWalletFromRootNode() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(TREZOR_SNIFF_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44h/0h/0h
    // Create a root node from which all addresses will be generated
    DeterministicKey trezorRootNode = WalletManager.generateTrezorWalletRootNode(privateMasterKey);

    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());
    InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

    // Create a Trezor hard wallet using the test root node, using a BIP44 account structure
    WalletSummary walletSummary = WalletManager
            .INSTANCE
            .getOrCreateTrezorHardWalletSummaryFromRootNode(
                    temporaryDirectory,
                    trezorRootNode,
                    TREZOR_SNIFF_WALLET_CREATION_DATE.getMillis() / 1000,
                    (String) PASSWORD,
                    "trezor-hard-example",
                    "trezor-hard-example"
            );

    assertThat(WalletType.TREZOR_HARD_WALLET.equals(walletSummary.getWalletType()));

    Wallet wallet = walletSummary.getWallet();

    log.debug("Trezor hard wallet: {}", wallet);

    // Get the first five keys and addresses
    DeterministicKey key0 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address0 = key0.toAddress(networkParameters).toString();
    log.debug("key0: {}", key0);

    DeterministicKey key1 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address1 = key1.toAddress(networkParameters).toString();

    DeterministicKey key2 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address2 = key2.toAddress(networkParameters).toString();

    DeterministicKey key3 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address3 = key3.toAddress(networkParameters).toString();

    DeterministicKey key4 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address4 = key4.toAddress(networkParameters).toString();

    // Ensure it is saved with the newly generated addresses
    File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(temporaryDirectory).get();

    log.debug("TrezorWalletTest#testCreateWalletWithTrezorAccountUsingMasterPrivateKey wallet: " + wallet.toString());
    wallet.saveToFile(walletFile);
    EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, PASSWORD);

    log.debug("address 0  = " + address0);
    log.debug("address 1  = " + address1);
    log.debug("address 2  = " + address2);
    log.debug("address 3  = " + address3);
    log.debug("address 4  = " + address4);

    assertThat(address0).isEqualTo(SNIFF_EXPECTED_ADDRESS_0);
    assertThat(address1).isEqualTo(SNIFF_EXPECTED_ADDRESS_1);
    assertThat(address2).isEqualTo(SNIFF_EXPECTED_ADDRESS_2);
    assertThat(address3).isEqualTo(SNIFF_EXPECTED_ADDRESS_3);
    assertThat(address4).isEqualTo(SNIFF_EXPECTED_ADDRESS_4);

    // Load the wallet up again to check it loads ok
    Optional<WalletSummary> rereadWalletSummary = WalletManager.INSTANCE.openWalletFromWalletId(temporaryDirectory, walletSummary.getWalletId(), PASSWORD);
    assertThat(rereadWalletSummary.isPresent());

    // Check the newly read in wallet has all the expected addresses
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key0.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key1.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key2.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key3.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key4.getPubKey())).isNotNull();

    // Remove comment if you want to: Sync the wallet to get the wallet transactions
    // syncWallet();

    log.debug("Wallet at end of test = " + walletSummary.getWallet().toString());

  }

  @Test
  /**
   * Create a wallet that derives addresses using BIP 44 - this is the HD account structure used by Trezor
   *
   * This is a Trezor soft wallet, based on the 'SNIFF" seed.
   * It has the private keys available for signing
   *
   * The wallet is roundtripped through protobuf and the expected keys/ addresses checked
   */
  public void testCreateTrezorSoftWalletFromSniffSeed() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    // Create a wallet from a seed phrase

    // Trezor uses BIP-44
    // BIP-44 starts from M/44h/0h/0h

    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());
    InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

    // Create a Trezor soft wallet using a seed phrase, using a BIP44 account structure
    WalletSummary walletSummary = WalletManager
            .INSTANCE
            .getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
                    temporaryDirectory,
                    TREZOR_SNIFF_SEED_PHRASE,
                    TREZOR_SNIFF_WALLET_CREATION_DATE.getMillis() / 1000,
                    (String) PASSWORD,
                    "trezor-soft-example",
                    "trezor-soft-example"
            );

    assertThat(WalletType.TREZOR_SOFT_WALLET.equals(walletSummary.getWalletType()));

    Wallet wallet = walletSummary.getWallet();

    log.debug("TrezorWalletTest#testCreateTrezorSoftWalletFromSniffSeed Trezor soft wallet: " + wallet.toString());

    assertThat(wallet.getActiveKeychain()).isNotNull();
    assertThat(wallet.getActiveKeychain().getRootKey()).isNotNull();
    assertThat(wallet.getActiveKeychain().getRootKey().getPath()).isNotNull();

    // Check that the root node has path M/44h/0h/0h
    List<ChildNumber> expectedRootNodePathList = new ArrayList<>();
    expectedRootNodePathList.add(new ChildNumber(44 | ChildNumber.HARDENED_BIT));
    expectedRootNodePathList.add(new ChildNumber(ChildNumber.HARDENED_BIT));

    assertThat(expectedRootNodePathList.equals(wallet.getActiveKeychain().getRootKey().getPath())).isTrue();

    // Get the first five keys and addresses
    DeterministicKey key0 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address0 = key0.toAddress(networkParameters).toString();
    log.debug("key0: {}", key0);

    // Check the first receiving key has the expected path - this should be m/44h/0h/0h/0/0
    List<ChildNumber> expectedFirstKeyPathList = new ArrayList<>();
    expectedFirstKeyPathList.add(new ChildNumber(44 | ChildNumber.HARDENED_BIT));
    expectedFirstKeyPathList.add(new ChildNumber(ChildNumber.HARDENED_BIT));
    expectedFirstKeyPathList.add(new ChildNumber(ChildNumber.HARDENED_BIT));
    expectedFirstKeyPathList.add(ChildNumber.ZERO);
    expectedFirstKeyPathList.add(ChildNumber.ZERO);
    assertThat(expectedFirstKeyPathList.equals(key0.getPath())).isTrue();

    DeterministicKey key1 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address1 = key1.toAddress(networkParameters).toString();

    DeterministicKey key2 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address2 = key2.toAddress(networkParameters).toString();

    DeterministicKey key3 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address3 = key3.toAddress(networkParameters).toString();

    DeterministicKey key4 = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String address4 = key4.toAddress(networkParameters).toString();

    // Ensure it is saved with the newly generated addresses
    File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(temporaryDirectory).get();

    wallet.saveToFile(walletFile);
    EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(walletFile, PASSWORD);

    log.debug("address 0  = " + address0);
    log.debug("address 1  = " + address1);
    log.debug("address 2  = " + address2);
    log.debug("address 3  = " + address3);
    log.debug("address 4  = " + address4);

    assertThat(address0).isEqualTo(SNIFF_EXPECTED_ADDRESS_0);
    assertThat(address1).isEqualTo(SNIFF_EXPECTED_ADDRESS_1);
    assertThat(address2).isEqualTo(SNIFF_EXPECTED_ADDRESS_2);
    assertThat(address3).isEqualTo(SNIFF_EXPECTED_ADDRESS_3);
    assertThat(address4).isEqualTo(SNIFF_EXPECTED_ADDRESS_4);

    // Load the wallet up again to check it loads ok
    Optional<WalletSummary> rereadWalletSummary = WalletManager.INSTANCE.openWalletFromWalletId(temporaryDirectory, walletSummary.getWalletId(), PASSWORD);
    assertThat(rereadWalletSummary.isPresent());

    // Check the root node path is the same
    assertThat(expectedRootNodePathList.equals(rereadWalletSummary.get().getWallet().getActiveKeychain().getRootKey().getPath())).isTrue();

    // Check the newly read in wallet has all the expected addresses and key structure
    DeterministicKey rebornKey0 = (DeterministicKey) rereadWalletSummary.get().getWallet().findKeyFromPubKey(key0.getPubKey());
    assertThat(rebornKey0).isNotNull();
    assertThat(expectedFirstKeyPathList.equals(rebornKey0.getPath())).isTrue();

    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key1.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key2.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key3.getPubKey())).isNotNull();
    assertThat(rereadWalletSummary.get().getWallet().findKeyFromPubKey(key4.getPubKey())).isNotNull();
  }

  @Test
   /**
    * Decrypt and then encrypt a Trezor soft wallet.
    * This is not actually used in MBHD on the UI but is used in the change password
    */
   public void testDecryptAndEncryptTrezorSoftWallet() throws Exception {
     Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
     networkParameters = BitcoinNetwork.current().get();

     // Create a random temporary directory where the wallet directory will be written
     File temporaryDirectory = SecureFiles.createTemporaryDirectory();

     // Create a wallet from a seed phrase
     BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());
     InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

     // Create a Trezor soft wallet using the seed phrase
     WalletSummary walletSummary = WalletManager
             .INSTANCE
             .getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
                     temporaryDirectory,
                     TREZOR_SNIFF_SEED_PHRASE,
                     TREZOR_SNIFF_WALLET_CREATION_DATE.getMillis() / 1000,
                     (String) PASSWORD,
                     "trezor-soft-example",
                     "trezor-soft-example"
             );

     walletSummary.getWallet().decrypt(PASSWORD);

     assertThat(walletSummary.getWallet().isEncrypted()).isFalse();

     // Encrypt it again
     walletSummary.getWallet().encrypt("BLAH BLAH BLAH DI BLAH");

     assertThat(walletSummary.getWallet().isEncrypted()).isTrue();
   }


  @Test
  /**
   * Change the password on a Trezor soft wallet
   */
  public void testChangePasswordTrezorSoftWallet() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();
    networkParameters = BitcoinNetwork.current().get();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    // Create a wallet from a seed phrase
    BackupManager.INSTANCE.initialise(temporaryDirectory, Optional.<File>absent());
    InstallationManager.setCurrentApplicationDataDirectory(temporaryDirectory);

    // Create a Trezor soft wallet using the seed phrase
    WalletSummary walletSummary = WalletManager
            .INSTANCE
            .getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
                    temporaryDirectory,
                    TREZOR_SNIFF_SEED_PHRASE,
                    TREZOR_SNIFF_WALLET_CREATION_DATE.getMillis() / 1000,
                    (String) PASSWORD,
                    "trezor-soft-example",
                    "trezor-soft-example"
            );

    // Check the old password is what is expected
    assertThat(walletSummary.getWallet().checkPassword(PASSWORD)).isTrue();

    // Change the password and check it
    WalletService.changeWalletPassword(walletSummary, (String) PASSWORD, (String) CHANGED_PASSWORD);

    // The change password is run on an executor thread so wait 5 seconds for it to complete
    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);

    assertThat(walletSummary.getWallet().checkPassword(CHANGED_PASSWORD)).isTrue();

    // TODO check wallet roundtrips via protobuf
  }
}
