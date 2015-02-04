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

import org.bitcoinj.utils.BriefLogFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.spongycastle.crypto.params.KeyParameter;

import java.security.SecureRandom;

import static org.fest.assertions.Assertions.assertThat;

public class AESUtilsTest {

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    SecureRandom secureRandom = new SecureRandom();

    // Create a random initialisationVector
    byte[] initialisationVector = new byte[org.multibit.hd.brit.crypto.AESUtils.BLOCK_LENGTH];
    secureRandom.nextBytes(initialisationVector);

    // Create a random key
    secureRandom.nextBytes(initialisationVector);

    BriefLogFormatter.init();
  }

  @After
  public void tearDown() throws Exception {

    InstallationManager.unrestricted = false;

  }

  @Test
  public void testCreateAESKey() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_3));

    KeyParameter aesKey1 = AESUtils.createAESKey(seed, WalletManager.scryptSalt());

    assertThat(aesKey1).isNotNull();
    assertThat(aesKey1.getKey()).isNotNull();
    assertThat(aesKey1.getKey().length).isEqualTo(org.multibit.hd.brit.crypto.AESUtils.KEY_LENGTH);
  }
}
