package org.multibit.hd.brit.dto;

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

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class BRITWalletIdTest {
  public static final String SEED_PHRASE_1 = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";
  private static final String WALLET_ID_1 = "4bbd8a749179d65a5f1b0859684f53ba5b761714";

  public static final String SEED_PHRASE_2 = "require want tube elegant juice cool cup noble town poem plate harsh";
  private static final String WALLET_ID_2 = "7e5218ea0428cbd44de74567fd8af557d8715545";

  private static final String SEED_PHRASE_3 = "morning truly witness grass pill typical blur then notable session exact coyote word noodle dentist hurry ability dignity";
  private static final String WALLET_ID_3 = "b1de12bdf20f332144851da717ae32c8aebcadb7";

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateWalletId1() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_1));

    BRITWalletId walletId = new BRITWalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(walletIdString).isEqualTo(WALLET_ID_1);

    // Generate the wallet Id again - it should get the same result.
    BRITWalletId walletId2 = new BRITWalletId(seed);
    assertThat(walletId2).isNotNull();
    assertThat(walletId2.getBytes()).isNotNull();

    assertThat(Arrays.equals(walletId.getBytes(), walletId2.getBytes())).isTrue();
  }

  @Test
  public void testCreateWalletId2() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_2));

    BRITWalletId walletId = new BRITWalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(walletIdString).isEqualTo(WALLET_ID_2);
  }

  @Test
  public void testCreateWalletId3() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_3));

    BRITWalletId walletId = new BRITWalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(walletIdString).isEqualTo(WALLET_ID_3);
  }

}

