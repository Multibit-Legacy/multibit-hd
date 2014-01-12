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
import org.bitcoinj.wallet.Protos;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.WalletData;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.api.WalletIdTest;
import org.multibit.hd.core.api.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.services.CoreServices;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletManagerTest {

  private static final String TEST_CREATE_ENCRYPTED_PROTOBUF_PREFIX = "testCreateEncryptedProtobuf";

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";

  private final static String WALLET_DIRECTORY_1 = "mbhd-11111111-22222222-33333333-44444444-55555555";
  private final static String WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";
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
    // Create a random temporary directory to store the wallets
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    walletManager.initialise(temporaryDirectory);

    // Create a wallet directory from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(WalletIdTest.split(WalletIdTest.SEED_PHRASE_1));
    WalletId walletId = new WalletId(seed1);

    String walletRootDirectoryPath = temporaryDirectory.getAbsolutePath() + File.separator + WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR + walletId.toFormattedString();
    (new File(walletRootDirectoryPath)).mkdir();
    String newWalletFilename = walletRootDirectoryPath + File.separator + WalletManager.MBHD_WALLET_NAME;

    KeyCrypterScrypt initialKeyCrypter = new KeyCrypterScrypt();
    System.out.println("testCreateProtobufEncryptedWallet - InitialKeyCrypter = " + initialKeyCrypter);
    Wallet newWallet = new Wallet(MainNetParams.get(), initialKeyCrypter);
    newWallet.setVersion(3); // PROTOBUF_ENCRYPTED


    ECKey newKey = new ECKey();

    // Copy the private key bytes for checking later.
    byte[] originalPrivateKeyBytes1 = new byte[32];
    System.arraycopy(newKey.getPrivKeyBytes(), 0, originalPrivateKeyBytes1, 0, 32);
    System.out.println("testCreateProtobufEncryptedWallet - Original private key 1 = " + Utils.bytesToHexString(originalPrivateKeyBytes1));

    newKey = newKey.encrypt(newWallet.getKeyCrypter(), newWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));
    newWallet.addKey(newKey);

    newKey = new ECKey();

    byte[] originalPrivateKeyBytes2 = new byte[32];
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
    WalletData rebornWalletData = walletManager.loadFromFile(newWalletFile);
    assertThat(rebornWalletData).isNotNull();
    assertThat(rebornWalletData.getWallet().getBalance()).isEqualTo(BigInteger.ZERO);
    assertThat(rebornWalletData.getWallet().getKeys().size()).isEqualTo(2);
    assertThat(rebornWalletData.getWallet().getEncryptionType()).describedAs("Wallet is not of type ENCRYPTED when it should be").isEqualTo(Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES);

    // Get the keys out the reborn wallet and check that all the keys are encrypted.
    Collection<ECKey> rebornEncryptedKeys = rebornWalletData.getWallet().getKeys();
    for (ECKey key : rebornEncryptedKeys) {
      assertThat(key.isEncrypted()).describedAs("Key is not encrypted when it should be").isTrue();
    }

    System.out.println("Reborn KeyCrypter = " + rebornWalletData.getWallet().getKeyCrypter());

    // Decrypt the reborn wallet.
    rebornWalletData.getWallet().decrypt(rebornWalletData.getWallet().getKeyCrypter().deriveKey(WALLET_PASSWORD));

    // Get the keys out the reborn wallet and check that all the keys match.
    Collection<ECKey> rebornKeys = rebornWalletData.getWallet().getKeys();

    assertThat(rebornKeys.size()).describedAs("Wrong number of keys in reborn wallet").isEqualTo(2);

    Iterator<ECKey> iterator = rebornKeys.iterator();
    ECKey firstRebornKey = iterator.next();
    assertThat(!firstRebornKey.isEncrypted()).describedAs("firstRebornKey should now de decrypted but is not").isTrue();
    // The reborn unencrypted private key bytes should match the original private key.
    byte[] firstRebornPrivateKeyBytes = firstRebornKey.getPrivKeyBytes();
    System.out.println("FileHandlerTest - Reborn decrypted first private key = " + Utils.bytesToHexString(firstRebornPrivateKeyBytes));

    for (int i = 0; i < firstRebornPrivateKeyBytes.length; i++) {
      assertThat(originalPrivateKeyBytes1[i]).describedAs("Byte " + i + " of the reborn first private key did not match the original").isEqualTo(firstRebornPrivateKeyBytes[i]);
    }

    ECKey secondRebornKey = iterator.next();
    assertThat(!secondRebornKey.isEncrypted()).describedAs("secondRebornKey should now de decrypted but is not").isTrue();
    // The reborn unencrypted private key bytes should match the original private key.
    byte[] secondRebornPrivateKeyBytes = secondRebornKey.getPrivKeyBytes();
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

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(WalletIdTest.split(WalletIdTest.SEED_PHRASE_1));

    WalletData walletData1 = walletManager.createWallet(temporaryDirectory1.getAbsolutePath(), seed, "password");

    // Uncomment this next line if you want a wallet created in your MultiBitHD user data directory.
    //walletManager.createWallet( seed, "password");

    assertThat(walletData1).isNotNull();

    // There should be a single key
    assertThat(walletData1.getWallet().getKeychainSize() == 1).isTrue();


    // Create another wallet - it should have the same wallet id and the private key should be the same
    File temporaryDirectory2 = makeRandomTemporaryDirectory();

    WalletData walletData2 = walletManager.createWallet(temporaryDirectory2.getAbsolutePath(), seed, "password");

    assertThat(walletData2).isNotNull();

    // There should be a single key
    assertThat(walletData2.getWallet().getKeychainSize() == 1).isTrue();

    ECKey key1 = walletData1.getWallet().getKeys().get(0);
    ECKey key2 = walletData2.getWallet().getKeys().get(0);

    assertThat(Arrays.equals(key1.getPrivKeyBytes(), key2.getPrivKeyBytes())).isTrue();

    assertThat(walletManager.getCurrentWalletDirectory().equals(
            new File(temporaryDirectory2.getAbsolutePath() + File.separator + "mbhd-" + walletData2.getWalletId().toFormattedString())));
  }

  @Test
  public void testSearchWalletDirectories() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryDirectory();

    String walletPath1 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    String walletPath2 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_1);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_3);

    WalletManager walletManager = WalletManager.INSTANCE;

    List<File> walletDirectories = walletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size() == 2).isTrue();
    assertThat(walletDirectories.get(0).getAbsolutePath().equals(walletPath1)).isTrue();
    assertThat(walletDirectories.get(1).getAbsolutePath().equals(walletPath2)).isTrue();

  }

  private String makeDirectory(File parentDirectory, String directoryName) {
    File directory = new File(parentDirectory.getAbsolutePath() + File.separator + directoryName);
    directory.mkdir();
    directory.deleteOnExit();
    return directory.getAbsolutePath();
  }

  public static File makeRandomTemporaryDirectory() throws IOException {
    File temporaryFile = File.createTempFile("nothing", "nothing");
    temporaryFile.deleteOnExit();

    File parentDirectory = temporaryFile.getParentFile();

    File temporaryDirectory = new File(parentDirectory.getAbsolutePath() + File.separator + ("" + (new Random()).nextInt(1000000)));
    temporaryDirectory.mkdir();
    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;
  }
}

