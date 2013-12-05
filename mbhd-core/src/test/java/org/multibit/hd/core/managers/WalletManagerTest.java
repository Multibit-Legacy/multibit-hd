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
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.Protos;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import static junit.framework.Assert.*;

public class WalletManagerTest {

    private static final String TEST_CREATE_ENCRYPTED_PROTOBUF_PREFIX = "testCreateEncryptedProtobuf";

    private final CharSequence WALLET_PASSWORD = "horatio nelson 123";

    private WalletManager walletManager;

    @Before
    public void setUp() throws Exception {
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
        Wallet newWallet = new Wallet(NetworkParameters.prodNet(), initialKeyCrypter);
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
            assertTrue("Key is not encrypted when it should be", key.isEncrypted());
        }

        // Save the wallet and read it back in again.
        walletManager.saveWallet(newWallet, newWalletFilename);

        // Check the wallet and wallet info file exists.
        File newWalletFile = new File(newWalletFilename);
        assertTrue(newWalletFile.exists());

        // Check wallet can be loaded and is still protobuf and encrypted.
        Wallet rebornWallet = walletManager.loadFromFile(newWalletFile);
        assertNotNull(rebornWallet);
        assertEquals(BigInteger.ZERO, rebornWallet.getBalance());
        assertEquals(2, rebornWallet.getKeys().size());
        assertTrue("Wallet is not of type ENCRYPTED when it should be", rebornWallet.getEncryptionType() == Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES);

        // Get the keys out the reborn wallet and check that all the keys are encrypted.
        Collection<ECKey> rebornEncryptedKeys = rebornWallet.getKeys();
        for (ECKey key : rebornEncryptedKeys) {
            assertTrue("Key is not encrypted when it should be", key.isEncrypted());
        }

        System.out.println("Reborn KeyCrypter = " + rebornWallet.getKeyCrypter());

        // Decrypt the reborn wallet.
        rebornWallet.decrypt(rebornWallet.getKeyCrypter().deriveKey(WALLET_PASSWORD));

        // Get the keys out the reborn wallet and check that all the keys match.
        Collection<ECKey> rebornKeys = rebornWallet.getKeys();

        assertEquals("Wrong number of keys in reborn wallet", 2, rebornKeys.size());

        Iterator<ECKey> iterator = rebornKeys.iterator();
        ECKey firstRebornKey = iterator.next();
        assertTrue("firstRebornKey should now de decrypted but is not", !firstRebornKey.isEncrypted());
        // The reborn unencrypted private key bytes should match the original private key.
        byte[] firstRebornPrivateKeyBytes = firstRebornKey.getPrivKeyBytes();
        System.out.println("FileHandlerTest - Reborn decrypted first private key = " + Utils.bytesToHexString(firstRebornPrivateKeyBytes));

        for (int i = 0; i < firstRebornPrivateKeyBytes.length; i++) {
            assertEquals("Byte " + i + " of the reborn first private key did not match the original", originalPrivateKeyBytes1[i], firstRebornPrivateKeyBytes[i]);
        }

        ECKey secondRebornKey = iterator.next();
        assertTrue("secondRebornKey should now de decrypted but is not", !secondRebornKey.isEncrypted());
        // The reborn unencrypted private key bytes should match the original private key.
        byte[] secondRebornPrivateKeyBytes = secondRebornKey.getPrivKeyBytes();
        System.out.println("FileHandlerTest - Reborn decrypted second private key = " + Utils.bytesToHexString(secondRebornPrivateKeyBytes));

        for (int i = 0; i < secondRebornPrivateKeyBytes.length; i++) {
            assertEquals("Byte " + i + " of the reborn second private key did not match the original", originalPrivateKeyBytes2[i], secondRebornPrivateKeyBytes[i]);
        }
    }
}

