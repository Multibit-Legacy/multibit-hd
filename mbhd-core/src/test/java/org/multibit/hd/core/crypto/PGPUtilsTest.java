package org.multibit.hd.core.crypto;

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

import com.google.bitcoin.core.Utils;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletIdTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class PGPUtilsTest {

  private static final Logger log = LoggerFactory.getLogger(PGPUtilsTest.class);

  private static final String MBHD_BRIT_PREFIX = "mbhd-brit";


  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreatePGPKeyFromWalletId() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(walletIdString).isEqualTo(WalletIdTest.WALLET_ID_1);

    // Use the walletId to generate an entropy source, using a one way function
    char[] password = "herpDerp".toCharArray();

    // Generate a PGP secret key ring from the entropy source
    //PGPSecretKeyRing secretKeyRing = org.multibit.hd.core.crypto.PGPUtils.createKey("herp@derp.com", password);
    //assertThat(secretKeyRing).isNotNull();
    //1) User wants to be able to recover their wallet password from the seed phrase.
    //log.debug("Secret key ring = " + secretKeyRing.toString());
    // Generate a PGP public key from the PGP private key

    // Persist the PGP public key

    // Read in the PGP public key and use it to encrypt some data

    // Check that the PGP private key can decrypt the encrypted data
  }

  /**
   * Make a file reference - a file can be referenced as, say,
   *     brit-mbhd/src/test/resources/redeemer1/gpg/pubring.gpg
   * or
   *     ./src/test/resources/redeemer1/gpg/pubring.gpg
   * depending on whether you are running via an IDE or in Maven
   * @param rootFilename The root filename (relative to the root of the mbhd directory e.g. src/test/resources/redeemer1/gpg/pubring.gpg
   *                     in the example above)
   * @return File reference
   */
  public static File makeFile(String rootFilename) {
    File file = new File(MBHD_BRIT_PREFIX + File.separator + rootFilename);
    if (!file.exists()) {
      file =  new File("." + File.separator + rootFilename);
    }
    return file;
  }
}
