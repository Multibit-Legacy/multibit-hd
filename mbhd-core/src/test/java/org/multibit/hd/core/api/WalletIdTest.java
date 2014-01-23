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

import com.google.bitcoin.core.Utils;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;

import java.io.File;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletIdTest {
  public static final String SEED_PHRASE_1 = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";
  private static final String WALLET_ID_1 = "19131cc83cdfefdb2331af110d5c44c5b8f39103";
  private static final String WALLET_ID_FORMATTED_1 = "19131cc8-3cdfefdb-2331af11-0d5c44c5-b8f39103";

  private static final String SEED_PHRASE_2 = "require want tube elegant juice cool cup noble town poem plate harsh";
  private static final String WALLET_ID_2 = "3d39a00a9e3e33c5e97c298019eaa9d4cbe04f91";
  private static final String WALLET_ID_FORMATTED_2 = "3d39a00a-9e3e33c5-e97c2980-19eaa9d4-cbe04f91";

  private static final String SEED_PHRASE_3 = "morning truly witness grass pill typical blur then notable session exact coyote word noodle dentist hurry ability dignity";
  private static final String WALLET_ID_3= "59ca26f67f8ba291b23751de3fd5f0d15a8f5f99";
  private static final String WALLET_ID_FORMATTED_3 = "59ca26f6-7f8ba291-b23751de-3fd5f0d1-5a8f5f99";

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateWalletId1() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_1));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(WALLET_ID_1.equals(walletIdString)).isTrue();
    assertThat(WALLET_ID_FORMATTED_1.equals(walletId.toFormattedString())).isTrue();

    WalletId walletIdPhoenix = WalletId.parseWalletFilename(File.separator + "herp" + File.separator + "derp" +
            File.separator + "mbhd-" + walletId.toFormattedString() + File.separator + "mbhd.wallet");
    assertThat(walletId.equals(walletIdPhoenix)).isTrue();

    // Generate the wallet Id again - it should get the same result.
    WalletId walletId2 = new WalletId(seed);
    assertThat(walletId2).isNotNull();
    assertThat(walletId2.getBytes()).isNotNull();

    assertThat(Arrays.equals(walletId.getBytes(), walletId2.getBytes())).isTrue();
  }

  @Test
  public void testCreateWalletId2() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_2));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    String walletIdString = Utils.bytesToHexString(walletId.getBytes());
    assertThat(WALLET_ID_2.equals(walletIdString)).isTrue();
    assertThat(WALLET_ID_FORMATTED_2.equals(walletId.toFormattedString())).isTrue();
  }

  @Test
   public void testCreateWalletId3() throws Exception {
     SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
     byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_3));

     WalletId walletId = new WalletId(seed);

     assertThat(walletId).isNotNull();
     assertThat(walletId.getBytes()).isNotNull();
     String walletIdString = Utils.bytesToHexString(walletId.getBytes());
     assertThat(WALLET_ID_3.equals(walletIdString)).isTrue();

     assertThat(WALLET_ID_FORMATTED_3.equals(walletId.toFormattedString())).isTrue();
   }

}

