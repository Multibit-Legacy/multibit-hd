package org.multibit.hd.brit.services;

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
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.crypto.ECUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.multibit.hd.brit.dto.BRITWalletIdTest;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URL;

import static org.fest.assertions.api.Assertions.assertThat;

public class FeeServicesTest {

  private static final Logger log = LoggerFactory.getLogger(FeeServicesTest.class);

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";
  private Wallet wallet1;

  private PGPPublicKey encryptionKey;

  /**
   * A dummy URL - forces the hardwired list of addresses to be used
   */
  private static final String DUMMY_MATCHER_URL = "http://nowhere.com";

  /**
   * The wallet version number for protobuf encrypted wallets - compatible with MultiBit
   */
  public static final int ENCRYPTED_WALLET_VERSION = 3; // TODO - need a new version when the wallet HD format is created


  @Before
  public void setUp() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_1));

    // Create the wallet 'wallet1'
    createWallet(seed, WALLET_PASSWORD);

        // Read the manually created public keyring in the test directory to find a public key suitable for encryption
    File publicKeyRingFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_PUBLIC_KEYRING_FILE);
    log.debug("Loading public keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");
    FileInputStream publicKeyRingInputStream = new FileInputStream(publicKeyRingFile);
    encryptionKey = PGPUtils.readPublicKey(publicKeyRingInputStream);
    assertThat(encryptionKey).isNotNull();
  }

  @Test
  public void testCalculateFeeState() throws Exception {
    // Get the FeeService
    FeeService feeService = BRITServices.newFeeService(encryptionKey, new URL(DUMMY_MATCHER_URL));
    assertThat(feeService).isNotNull();

    // Calculate the fee state for an empty wallet
    FeeState feeState = feeService.calculateFeeState(wallet1);
    assertThat(feeState).isNotNull();




  }

  public void createWallet(byte[] seed, CharSequence password) throws Exception {
    // Create a wallet with a single private key using the seed (modulo-ed), encrypted with the password
    KeyCrypter keyCrypter = new KeyCrypterScrypt();

    wallet1 = new Wallet(NetworkParameters.fromID(NetworkParameters.ID_MAINNET), keyCrypter);
    wallet1.setVersion(ENCRYPTED_WALLET_VERSION);

    // Add the 'zero index' key into the wallet
    // Ensure that the seed is within the Bitcoin EC group.
    BigInteger privateKeyToUse = ECUtils.moduloSeedByECGroupSize(new BigInteger(1, seed));

    ECKey newKey = new ECKey(privateKeyToUse);
    newKey = newKey.encrypt(wallet1.getKeyCrypter(), wallet1.getKeyCrypter().deriveKey(password));
    wallet1.addKey(newKey);

    assertThat(wallet1).isNotNull();

    // There should be a single key
    assertThat(wallet1.getKeychainSize() == 1).isTrue();
  }
}

