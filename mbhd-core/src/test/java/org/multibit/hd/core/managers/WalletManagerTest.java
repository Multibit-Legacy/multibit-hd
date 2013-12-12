package org.multibit.hd.core.managers;

/**
 * Copyright 2012 multibit.org
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
import org.multibit.hd.core.services.CoreServices;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletManagerTest {

  private static final String TEST_CREATE_ENCRYPTED_PROTOBUF_PREFIX = "testCreateEncryptedProtobuf";

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";

  private WalletManager walletManager;

  @Before
  public void setUp() throws Exception {
    // Start the core services
    CoreServices.main(null);
    walletManager = new WalletManager();
  }

  @Test
  public void testCreateProtobufEncryptedWallet() throws Exception {
    // Create an encrypted wallet.
    File temporaryWallet = File.createTempFile(TEST_CREATE_ENCRYPTED_PROTOBUF_PREFIX, ".wallet");
    temporaryWallet.deleteOnExit();

    String newWalletFilename = temporaryWallet.getAbsolutePath();

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
    Wallet rebornWallet = walletManager.loadFromFile(newWalletFile);
    assertThat(rebornWallet).isNotNull();
    assertThat(rebornWallet.getBalance()).isEqualTo(BigInteger.ZERO);
    assertThat(rebornWallet.getKeys().size()).isEqualTo(2);
    assertThat(rebornWallet.getEncryptionType()).describedAs("Wallet is not of type ENCRYPTED when it should be").isEqualTo(Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES);

    // Get the keys out the reborn wallet and check that all the keys are encrypted.
    Collection<ECKey> rebornEncryptedKeys = rebornWallet.getKeys();
    for (ECKey key : rebornEncryptedKeys) {
      assertThat(key.isEncrypted()).describedAs("Key is not encrypted when it should be").isTrue();
    }

    System.out.println("Reborn KeyCrypter = " + rebornWallet.getKeyCrypter());

    // Decrypt the reborn wallet.
    rebornWallet.decrypt(rebornWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));

    // Get the keys out the reborn wallet and check that all the keys match.
    Collection<ECKey> rebornKeys = rebornWallet.getKeys();

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
  public void testGetWalletDirectory() throws Exception {
    File temporaryFile = File.createTempFile("something", ".txt");
    temporaryFile.deleteOnExit();

    File walletDirectory = WalletManager.getWalletDirectory(temporaryFile.getParent(), "aName");

    assertThat(temporaryFile.getParent() + File.separator + "aName").isEqualTo(walletDirectory.getAbsolutePath());
    assertThat(walletDirectory.isDirectory()).isTrue();

  }

  @Test
  public void testCreateSimpleWallet() throws Exception {
    // TODO should use a temporary directory for wallet creation
    WalletManager walletManager = new WalletManager();

    Wallet wallet = walletManager.createSimpleWallet("testPassword");

    assertThat(wallet).isNotNull();
  }
}

