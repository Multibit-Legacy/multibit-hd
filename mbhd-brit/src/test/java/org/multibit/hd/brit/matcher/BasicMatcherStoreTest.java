package org.multibit.hd.brit.matcher;

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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.dto.BRITWalletId;
import org.multibit.hd.brit.dto.BRITWalletIdTest;
import org.multibit.hd.brit.dto.WalletToEncounterDateLink;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BasicMatcherStoreTest {

  private static final Logger log = LoggerFactory.getLogger(BasicMatcherStoreTest.class);

  private MatcherStore matcherStore;

  @Before
  public void setUp() throws Exception {
    // Create a random temporary directory in which to create the matcher store
    File matcherStoreDirectory = FileUtils.makeRandomTemporaryDirectory();

    log.debug("Creating a matcher store in the directory '" + matcherStoreDirectory + "'");

    matcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory.getAbsolutePath());
    assertThat(matcherStore).isNotNull();
  }

  @Test
  public void testStoreAndGetAllBitcoinAddresses() throws Exception {
    // Store some bitcoin addresses
    List<String> allBitcoinAddresses = Lists.newArrayList();
    allBitcoinAddresses.add("one");
    allBitcoinAddresses.add("two");
    allBitcoinAddresses.add("three");

    matcherStore.storeAllBitcoinAddresses(allBitcoinAddresses);

    // TODO Bounce the MatcherStore to check everything is being persisted

    // Check they have been stored ok
    assertThat(matcherStore.getAllBitcoinAddress()).isEqualTo(allBitcoinAddresses);
  }

  @Test
  public void testStoreAndGetBitcoinAddressListByDate() throws Exception {
    Date yesterday = DateTime.now().minusDays(1).toDate();
    Date today = DateTime.now().toDate();
    Date tomorrow = DateTime.now().plusDays(1).toDate();

    // Store some bitcoin address lists by date
    List<String> bitcoinAddressList1 = Lists.newArrayList();
    bitcoinAddressList1.add("one.cat");
    bitcoinAddressList1.add("one.dog");
    bitcoinAddressList1.add("one.elephant");
    matcherStore.storeBitcoinAddressListForDate(bitcoinAddressList1, yesterday);

    List<String> bitcoinAddressList2 = Lists.newArrayList();
    bitcoinAddressList2.add("two.cat");
    bitcoinAddressList2.add("two.dog");
    bitcoinAddressList2.add("two.elephant");
    bitcoinAddressList2.add("two.worm");
    matcherStore.storeBitcoinAddressListForDate(bitcoinAddressList2, today);

    List<String> bitcoinAddressList3 = Lists.newArrayList();
    bitcoinAddressList3.add("three.cat");
    bitcoinAddressList3.add("three.dog");
    bitcoinAddressList3.add("three.elephant");
    bitcoinAddressList3.add("three.wallaby");
    matcherStore.storeBitcoinAddressListForDate(bitcoinAddressList3, tomorrow);

    // TODO Bounce the MatcherStore to check everything is being persisted

    // Check they have been stored ok
    assertThat(matcherStore.getBitcoinAddressListForDate(yesterday)).isEqualTo(bitcoinAddressList1);
    assertThat(matcherStore.getBitcoinAddressListForDate(today)).isEqualTo(bitcoinAddressList2);
    assertThat(matcherStore.getBitcoinAddressListForDate(tomorrow)).isEqualTo(bitcoinAddressList3);
  }

  @Test
  public void testStoreAndLookupWalletToEncounterDateLinks() throws Exception {
    // Create a BRITWalletId (in real life this would be using the Payer's wallet seed)
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_1));
    BRITWalletId britWalletId = new BRITWalletId(seed);

    Date encounterDate = DateTime.now().toDate();
    Date firstTransactionDate = DateTime.now().minusDays(1).toDate();

    // Store an encounter with this britWalletId and no lastTransactionDate
    WalletToEncounterDateLink walletToEncounterLink = new WalletToEncounterDateLink(britWalletId, Optional.of(encounterDate), Optional.of(firstTransactionDate));

    matcherStore.storeWalletToEncounterDateLink(walletToEncounterLink);

    // TODO Bounce the MatcherStore to check everything is being persisted

    // Check it has been stored ok
    assertThat(matcherStore.lookupWalletToEncounterDateLink(britWalletId)).isEqualTo(walletToEncounterLink);
  }
}
