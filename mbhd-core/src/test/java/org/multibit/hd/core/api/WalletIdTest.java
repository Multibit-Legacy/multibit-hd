package org.multibit.hd.core.api;

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

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.services.CoreServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletIdTest {
  private static final String SEED_PHRASE_1 = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";

  @Before
  public void setUp() throws Exception {
    // Start the core services
    CoreServices.main(null);

  }

  @Test
  public void testCreateWalletId() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(split(SEED_PHRASE_1));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getWalletIdBytes()).isNotNull();

    // Generate the wallet Id again - it should get the same result.
    WalletId walletId2 = new WalletId(seed);
    assertThat(walletId2).isNotNull();
    assertThat(walletId2.getWalletIdBytes()).isNotNull();

    assertThat(Arrays.equals(walletId.getWalletIdBytes(), walletId2.getWalletIdBytes()));
  }

  private List<String> split(String words) {
       return new ArrayList<String>(Arrays.asList(words.split("\\s+")));
   }
}

