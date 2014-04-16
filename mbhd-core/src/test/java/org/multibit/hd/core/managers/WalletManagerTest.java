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
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.bitcoin.params.MainNetParams;
import com.google.common.base.Optional;
import org.bitcoinj.wallet.Protos;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.services.CoreServices;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.multibit.hd.core.dto.WalletId.WALLET_ID_SEPARATOR;

public class WalletManagerTest {

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";

  private final static String WALLET_DIRECTORY_1 = "mbhd-11111111-22222222-33333333-44444444-55555555";
  private final static String WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String EXPECTED_WALLET_ID_1 = "11111111-22222222-33333333-44444444-55555555";
  private final static String EXPECTED_WALLET_ID_2 = "66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String INVALID_WALLET_DIRECTORY_1 = "not-mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";
  private final static String INVALID_WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-gggggggg";
  private final static String INVALID_WALLET_DIRECTORY_3 = "mbhd-1166666666-77777777-88888888-99999999-aaaaaaaa";

  private WalletManager walletManager;

  @Before
  public void setUp() throws Exception {

    // Start the core services
    CoreServices.main(null);
    walletManager = WalletManager.INSTANCE;

  }

  @Test
  public void testCreateProtobufEncryptedWallet() throws Exception {

    // Create a random temporary directory to writeContacts the wallets
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    // TODO May not be required
    // WalletManager.INSTANCE.open(temporaryDirectory);
    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    // Create a wallet directory from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    WalletId walletId = new WalletId(seed1);

    // TODO Refactor into a consistent "get absolute wallet file path" from WalletManager
    String walletRootDirectoryPath = temporaryDirectory.getAbsolutePath()
      + File.separator
      + WalletManager.WALLET_DIRECTORY_PREFIX
      + WALLET_ID_SEPARATOR
      + walletId.toFormattedString();
    assertThat((new File(walletRootDirectoryPath)).mkdir()).isTrue();
    String newWalletFilename = walletRootDirectoryPath + File.separator + WalletManager.MBHD_WALLET_NAME;

    KeyCrypterScrypt initialKeyCrypter = new KeyCrypterScrypt();
    System.out.println("testCreateProtobufEncryptedWallet - InitialKeyCrypter = " + initialKeyCrypter);
    Wallet newWallet = new Wallet(MainNetParams.get(), initialKeyCrypter);
    newWallet.setVersion(3); // PROTOBUF_ENCRYPTED


    ECKey newKey = new ECKey();

    // Copy the private key bytes for checking later.
    byte[] originalPrivateKeyBytes1 = new byte[32];
    if (newKey.getPrivKeyBytes() == null) {
      fail();
    }
    System.arraycopy(newKey.getPrivKeyBytes(), 0, originalPrivateKeyBytes1, 0, 32);
    System.out.println("testCreateProtobufEncryptedWallet - Original private key 1 = " + Utils.bytesToHexString(originalPrivateKeyBytes1));

    newKey = newKey.encrypt(newWallet.getKeyCrypter(), newWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));
    newWallet.addKey(newKey);

    newKey = new ECKey();

    byte[] originalPrivateKeyBytes2 = new byte[32];
    if (newKey.getPrivKeyBytes() == null) {
      fail();
    }
    System.arraycopy(newKey.getPrivKeyBytes(), 0, originalPrivateKeyBytes2, 0, 32);
    System.out.println("testCreateProtobufEncryptedWallet - Original private key 2 = " + Utils.bytesToHexString(originalPrivateKeyBytes2));

    newKey = newKey.encrypt(newWallet.getKeyCrypter(), newWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));
    newWallet.addKey(newKey);

    // Get the keys of the wallet and check that all the keys are encrypted.
    Collection<ECKey> keys = newWallet.getKeys();
    for (ECKey key : keys) {
      assertThat(key.isEncrypted()).isTrue();
    }

    // Save the wallet and read it back in again.
    walletManager.saveWallet(newWallet, newWalletFilename);

    // Check the wallet and wallet info file exists.
    File newWalletFile = new File(newWalletFilename);
    assertThat(newWalletFile.exists()).isTrue();

    // Check wallet can be loaded and is still protobuf and encrypted.
    WalletSummary rebornWalletSummary = walletManager.loadFromFile(newWalletFile,"password");
    assertThat(rebornWalletSummary).isNotNull();
    assertThat(rebornWalletSummary.getWallet().getBalance()).isEqualTo(BigInteger.ZERO);
    assertThat(rebornWalletSummary.getWallet().getKeys().size()).isEqualTo(2);
    assertThat(rebornWalletSummary.getWallet().getEncryptionType()).describedAs("Wallet is not of type ENCRYPTED when it should be").isEqualTo(Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES);

    // Get the keys out the reborn wallet and check that all the keys are encrypted.
    Collection<ECKey> rebornEncryptedKeys = rebornWalletSummary.getWallet().getKeys();
    for (ECKey key : rebornEncryptedKeys) {
      assertThat(key.isEncrypted()).describedAs("Key is not encrypted when it should be").isTrue();
    }

    System.out.println("Reborn KeyCrypter = " + rebornWalletSummary.getWallet().getKeyCrypter());

    // Decrypt the reborn wallet.
    rebornWalletSummary.getWallet().decrypt(rebornWalletSummary.getWallet().getKeyCrypter().deriveKey(WALLET_PASSWORD));

    // Get the keys out the reborn wallet and check that all the keys match.
    Collection<ECKey> rebornKeys = rebornWalletSummary.getWallet().getKeys();

    assertThat(rebornKeys.size()).describedAs("Wrong number of keys in reborn wallet").isEqualTo(2);

    Iterator<ECKey> iterator = rebornKeys.iterator();
    ECKey firstRebornKey = iterator.next();
    assertThat(!firstRebornKey.isEncrypted()).describedAs("firstRebornKey should now de decrypted but is not").isTrue();
    // The reborn unencrypted private key bytes should match the original private key.
    byte[] firstRebornPrivateKeyBytes = firstRebornKey.getPrivKeyBytes();
    if (firstRebornPrivateKeyBytes == null) {
      fail();
    }
    System.out.println("FileHandlerTest - Reborn decrypted first private key = " + Utils.bytesToHexString(firstRebornPrivateKeyBytes));

    for (int i = 0; i < firstRebornPrivateKeyBytes.length; i++) {
      assertThat(originalPrivateKeyBytes1[i]).describedAs("Byte " + i + " of the reborn first private key did not match the original").isEqualTo(firstRebornPrivateKeyBytes[i]);
    }

    ECKey secondRebornKey = iterator.next();
    assertThat(!secondRebornKey.isEncrypted()).describedAs("secondRebornKey should now de decrypted but is not").isTrue();
    // The reborn unencrypted private key bytes should match the original private key.
    byte[] secondRebornPrivateKeyBytes = secondRebornKey.getPrivKeyBytes();
    if (secondRebornPrivateKeyBytes == null) {
      fail();
    }
    System.out.println("FileHandlerTest - Reborn decrypted second private key = " + Utils.bytesToHexString(secondRebornPrivateKeyBytes));

    for (int i = 0; i < secondRebornPrivateKeyBytes.length; i++) {
      assertThat(originalPrivateKeyBytes2[i]).describedAs("Byte " + i + " of the reborn second private key did not match the original").isEqualTo(secondRebornPrivateKeyBytes[i]);
    }
  }

  @Test
  public void testCreateWallet() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory1 = makeRandomTemporaryDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(temporaryDirectory1, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    WalletSummary walletSummary1 = walletManager.getOrCreateWalletSummary(temporaryDirectory1, seed, "password");

    // Uncomment this next line if you want a wallet created in your MultiBitHD user data directory.
    //walletManager.createWallet( seed, "password");

    assertThat(walletSummary1).isNotNull();

    // There should be a single key
    assertThat(walletSummary1.getWallet().getKeychainSize() == 1).isTrue();


    // Create another wallet - it should have the same wallet id and the private key should be the same
    File temporaryDirectory2 = makeRandomTemporaryDirectory();
    BackupManager.INSTANCE.initialise(temporaryDirectory2, null);

    WalletSummary walletSummary2 = walletManager.getOrCreateWalletSummary(temporaryDirectory2, seed, "password");

    assertThat(walletSummary2).isNotNull();

    // There should be a single key
    assertThat(walletSummary2.getWallet().getKeychainSize() == 1).isTrue();

    ECKey key1 = walletSummary1.getWallet().getKeys().get(0);
    ECKey key2 = walletSummary2.getWallet().getKeys().get(0);

    assertThat(Arrays.equals(key1.getPrivKeyBytes(), key2.getPrivKeyBytes())).isTrue();

    File expectedFile = new File(
      temporaryDirectory2.getAbsolutePath()
        + File.separator
        + "mbhd-"
        + walletSummary2.getWalletId().toFormattedString()
        + File.separator
        + WalletManager.MBHD_WALLET_NAME
    );

    assertThat(expectedFile.exists()).isTrue();
  }

  @Test
  public void testFindWalletDirectories() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryDirectory();

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
    File temporaryDirectory = makeRandomTemporaryDirectory();

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

    File directory = new File(parentDirectory, directoryName);
    assertThat(directory.mkdir()).isTrue();
    directory.deleteOnExit();

    return directory.getAbsolutePath();

  }

  public static File makeRandomTemporaryDirectory() throws IOException {

    File temporaryFile = File.createTempFile("nothing", "nothing");
    temporaryFile.deleteOnExit();

    File parentDirectory = temporaryFile.getParentFile();

    File temporaryDirectory = new File(parentDirectory.getAbsolutePath() + File.separator + ("" + (new Random()).nextInt(1000000)));
    assertThat(temporaryDirectory.mkdir()).isTrue();

    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;
  }
}

