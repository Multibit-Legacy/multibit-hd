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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class WalletIdTest {
  public static final String SEED_PHRASE_1 = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";
  private static final String WALLET_ID_1 = "23bb865e161bfefc3020c41866bf6f757fecdfcc";
  private static final String WALLET_ID_FORMATTED_1 = "23bb865e-161bfefc-3020c418-66bf6f75-7fecdfcc";

  private static final String SEED_PHRASE_2 = "require want tube elegant juice cool cup noble town poem plate harsh";
  private static final String WALLET_ID_2 = "a1e7c31c0a4138ff3a520082f7f746542d674fa6";
  private static final String WALLET_ID_FORMATTED_2 = "a1e7c31c-0a4138ff-3a520082-f7f74654-2d674fa6";

  private static final String SEED_PHRASE_3 = "morning truly witness grass pill typical blur then notable session exact coyote word noodle dentist hurry ability dignity";
  private static final String WALLET_ID_3= "7e9c3887743e9920b5a7afde142ef5cdc3fe7e3d";
  private static final String WALLET_ID_FORMATTED_3 = "7e9c3887-743e9920-b5a7afde-142ef5cd-c3fe7e3d";

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateWalletId1() throws Exception {
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(split(SEED_PHRASE_1));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    assertThat(WALLET_ID_1.equals(Utils.bytesToHexString(walletId.getBytes()))).isTrue();
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
    byte[] seed = seedGenerator.convertToSeed(split(SEED_PHRASE_2));

    WalletId walletId = new WalletId(seed);

    assertThat(walletId).isNotNull();
    assertThat(walletId.getBytes()).isNotNull();
    assertThat(WALLET_ID_2.equals(Utils.bytesToHexString(walletId.getBytes()))).isTrue();
    assertThat(WALLET_ID_FORMATTED_2.equals(walletId.toFormattedString())).isTrue();
  }

  @Test
   public void testCreateWalletId3() throws Exception {
     SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
     byte[] seed = seedGenerator.convertToSeed(split(SEED_PHRASE_3));

     WalletId walletId = new WalletId(seed);

     assertThat(walletId).isNotNull();
     assertThat(walletId.getBytes()).isNotNull();
     assertThat(WALLET_ID_3.equals(Utils.bytesToHexString(walletId.getBytes()))).isTrue();
     assertThat(WALLET_ID_FORMATTED_3.equals(walletId.toFormattedString())).isTrue();
   }

   public static List<String> split(String words) {
       return new ArrayList<String>(Arrays.asList(words.split("\\s+")));
   }
}

