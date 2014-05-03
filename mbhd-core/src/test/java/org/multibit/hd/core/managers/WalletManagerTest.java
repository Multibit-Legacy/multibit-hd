package org.multibit.hd.core.managers;

/**
 * Copyright 2014 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.Assertions.assertThat;

public class WalletManagerTest {

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";

  private final static String WALLET_DIRECTORY_1 = "mbhd-11111111-22222222-33333333-44444444-55555555";
  private final static String WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String EXPECTED_WALLET_ID_1 = "11111111-22222222-33333333-44444444-55555555";
  private final static String EXPECTED_WALLET_ID_2 = "66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String INVALID_WALLET_DIRECTORY_1 = "not-mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";
  private final static String INVALID_WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-gggggggg";
  private final static String INVALID_WALLET_DIRECTORY_3 = "mbhd-1166666666-77777777-88888888-99999999-aaaaaaaa";

  @Before
  public void setUp() throws Exception {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Start the core services
    CoreServices.main(null);

  }

//  @Test
//  public void testCreateProtobufEncryptedWallet() throws Exception {
//
//    // Create a random temporary directory to write the wallets
//    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();
//
//    BackupManager.INSTANCE.initialise(temporaryDirectory, null);
//
//    // Create a wallet directory from a seed
//    final SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
//    final byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
//    final WalletId walletId = new WalletId(seed);
//    final String walletRoot = WalletManager.createWalletRoot(walletId);
//
//    final File walletDirectory = WalletManager.getOrCreateWalletDirectory(temporaryDirectory, walletRoot);
//    final File walletFile = SecureFiles.verifyOrCreateFile(walletDirectory, WalletManager.MBHD_WALLET_NAME);
//
//    final KeyCrypterScrypt initialKeyCrypter = new KeyCrypterScrypt();
//
//    // Create a new wallet
//    final Wallet wallet = new Wallet(BitcoinNetwork.current().get(), initialKeyCrypter);
//    wallet.setVersion(3); // PROTOBUF_ENCRYPTED
//
//    // Create and add encrypted keys to the wallet
//    final byte[] plainPrivateKey1Bytes = addEncryptedKey(wallet);
//    final byte[] plainPrivateKey2Bytes = addEncryptedKey(wallet);
//
//    // Get the keys of the wallet and check that all the keys are encrypted
//    final Collection<ECKey> keys = wallet.getKeys();
//    for (ECKey key : keys) {
//      assertThat(key.isEncrypted()).isTrue();
//    }
//
//    // Save the wallet and read it back in again
//    WalletManager.INSTANCE.saveWallet(wallet, walletFile);
//
//    // Check the wallet and wallet info file exists
//    assertThat(walletFile.exists()).isTrue();
//
//    // Check wallet can be loaded and is still protobuf and encrypted
//    final WalletSummary rebornWalletSummary = WalletManager.INSTANCE.loadFromWalletDirectory(walletDirectory, "password");
//    final Wallet rebornWallet = rebornWalletSummary.getWallet();
//
//    assertThat(rebornWalletSummary).isNotNull();
//    assertThat(rebornWallet).isNotNull();
//
//    assertThat(rebornWallet.getBalance()).isEqualTo(BigInteger.ZERO);
//    assertThat(rebornWallet.getKeys().size()).isEqualTo(2);
//    assertThat(rebornWallet.getEncryptionType())
//      .describedAs("Wallet is not of type ENCRYPTED when it should be")
//      .isEqualTo(Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES);
//
//    // Get the keys out of the reborn wallet and check that all the keys are encrypted
//    final Collection<ECKey> rebornEncryptedKeys = rebornWallet.getKeys();
//    for (ECKey key : rebornEncryptedKeys) {
//      assertThat(key.isEncrypted()).describedAs("Reborn key should be encrypted").isTrue();
//    }
//
//    // Decrypt the reborn wallet
//    rebornWallet.decrypt(rebornWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));
//
//    // Get the keys out the reborn wallet and check that all the keys match.
//    final Collection<ECKey> rebornPlainKeys = rebornWallet.getKeys();
//
//    assertThat(rebornPlainKeys.size()).describedAs("Wrong number of keys in reborn wallet").isEqualTo(2);
//
//    Iterator<ECKey> iterator = rebornPlainKeys.iterator();
//    compareRebornToOriginal(plainPrivateKey1Bytes, iterator);
//    compareRebornToOriginal(plainPrivateKey2Bytes, iterator);
//
//  }

  private void compareRebornToOriginal(byte[] plainPrivateKey1Bytes, Iterator<ECKey> iterator) {

    ECKey rebornPlainPrivateKey = iterator.next();
    assertThat(!rebornPlainPrivateKey.isEncrypted()).describedAs("firstRebornKey should now de decrypted but is not").isTrue();

    // The reborn unencrypted private key bytes should match the original private key.
    byte[] rebornPlainPrivateKeyBytes = rebornPlainPrivateKey.getPrivKeyBytes();
    if (rebornPlainPrivateKeyBytes == null) {
      fail();
    }
    System.out.println("FileHandlerTest - Reborn decrypted private key = " + Utils.bytesToHexString(rebornPlainPrivateKeyBytes));

    assertThat(Arrays.equals(rebornPlainPrivateKeyBytes, plainPrivateKey1Bytes)).isTrue();
  }

  /**
   * @param wallet The wallet
   *
   * @return The private key bytes before encryption
   */
  private byte[] addEncryptedKey(Wallet wallet) {

    final ECKey plainKey = new ECKey();

    // Copy the private key bytes for checking later.
    byte[] plainPrivateKeyBytes = new byte[32];
    if (plainKey.getPrivKeyBytes() == null) {
      fail();
    }
    System.arraycopy(plainKey.getPrivKeyBytes(), 0, plainPrivateKeyBytes, 0, 32);
    System.out.println("testCreateProtobufEncryptedWallet - Original private key 1 = " + Utils.bytesToHexString(plainPrivateKeyBytes));

    final ECKey encryptedKey1 = plainKey.encrypt(wallet.getKeyCrypter(), wallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));
    wallet.addKey(encryptedKey1);

    return plainPrivateKeyBytes;
  }

  @Test
  public void testCreateWallet() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory1 = makeRandomTemporaryApplicationDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(temporaryDirectory1, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary1 = walletManager.getOrCreateWalletSummary(temporaryDirectory1, seed, nowInSeconds, "password");

    // Uncomment this next line if you want a wallet created in your MultiBitHD user data directory.
    //walletManager.createWallet( seed, "password");

    assertThat(walletSummary1).isNotNull();

    // Create another wallet - it should have the same wallet id and the private key should be the same
    File temporaryDirectory2 = makeRandomTemporaryApplicationDirectory();
    BackupManager.INSTANCE.initialise(temporaryDirectory2, null);

    WalletSummary walletSummary2 = walletManager.getOrCreateWalletSummary(temporaryDirectory2, seed, nowInSeconds, "password");

    assertThat(walletSummary2).isNotNull();

    ECKey key1 = walletSummary1.getWallet().freshReceiveKey();
    ECKey key2 = walletSummary2.getWallet().freshReceiveKey();

    assertThat(key1).isEqualTo(key2);

    File expectedFile = new File(
      temporaryDirectory2.getAbsolutePath()
        + File.separator
        + "mbhd-"
        + walletSummary2.getWalletId().toFormattedString()
        + File.separator
        + WalletManager.MBHD_WALLET_NAME
        + WalletManager.MBHD_AES_SUFFIX
    );

    assertThat(expectedFile.exists()).isTrue();
  }

  @Test
  public void testFindWalletDirectories() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();

    String walletPath1 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    String walletPath2 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_1);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_3);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);
    assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath1);
    assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath2);

  }

  @Test
  public void testFindWallets() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();

    String walletPath1 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    String walletPath2 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_2);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);
    assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath1);
    assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath2);

    // Attempt to retrieve the wallet summary
    List<WalletSummary> wallets = WalletManager.findWalletSummaries(walletDirectories, Optional.of(WALLET_DIRECTORY_2));
    assertThat(wallets).isNotNull();
    assertThat(wallets.size()).isEqualTo(2);

    // Expect the current wallet root to be first
    assertThat(wallets.get(0).getWalletId().toFormattedString()).isEqualTo(EXPECTED_WALLET_ID_2);
    assertThat(wallets.get(1).getWalletId().toFormattedString()).isEqualTo(EXPECTED_WALLET_ID_1);

  }

  private String makeDirectory(File parentDirectory, String directoryName) {

    File directory =  new File(parentDirectory, directoryName);
    assertThat(directory.mkdir()).isTrue();
    directory.deleteOnExit();

    return directory.getAbsolutePath();

  }

  /**
   * @return A random temporary directory suitable for use as an application directory
   *
   * @throws IOException If something goes wrong
   */
  public static File makeRandomTemporaryApplicationDirectory() throws IOException {

    File temporaryDirectory = Files.createTempDir();
    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;
  }
}

