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
  private static final String WALLET_ID_1 = "5c81964a030c3b659dc56fe63dbe27aef3370750";
  private static final String WALLET_ID_FORMATTED_1 = "5c81964a-030c3b65-9dc56fe6-3dbe27ae-f3370750";

  private static final String SEED_PHRASE_2 = "require want tube elegant juice cool cup noble town poem plate harsh";
  private static final String WALLET_ID_2 = "a0bf136f8ce97d0358b4b29a87f6662cf14e594f";
  private static final String WALLET_ID_FORMATTED_2 = "a0bf136f-8ce97d03-58b4b29a-87f6662c-f14e594f";

  private static final String SEED_PHRASE_3 = "morning truly witness grass pill typical blur then notable session exact coyote word noodle dentist hurry ability dignity";
  private static final String WALLET_ID_3= "621a029836c3d152ae62134fb8ff7618900c5f9a";
  private static final String WALLET_ID_FORMATTED_3 = "621a0298-36c3d152-ae62134f-b8ff7618-900c5f9a";

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
    assertThat(walletIdString).isEqualTo(WALLET_ID_1);
    assertThat(walletId.toFormattedString()).isEqualTo(WALLET_ID_FORMATTED_1);

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
    assertThat(walletIdString).isEqualTo(WALLET_ID_2);
    assertThat(walletId.toFormattedString()).isEqualTo(WALLET_ID_FORMATTED_2);

  }

  @Test
   public void testCreateWalletId3() throws Exception {
     SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
     byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(SEED_PHRASE_3));

     WalletId walletId = new WalletId(seed);

     assertThat(walletId).isNotNull();
     assertThat(walletId.getBytes()).isNotNull();
     String walletIdString = Utils.bytesToHexString(walletId.getBytes());
     assertThat(walletIdString).isEqualTo(WALLET_ID_3);
     assertThat(walletId.toFormattedString()).isEqualTo(WALLET_ID_FORMATTED_3);

   }

}

