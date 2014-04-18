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
import com.google.common.collect.Sets;
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
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class BasicMatcherStoreTest {

  private static final Logger log = LoggerFactory.getLogger(BasicMatcherStoreTest.class);

  private MatcherStore matcherStore;

  private File matcherStoreDirectory;

  @Before
  public void setUp() throws Exception {
    // Create a random temporary directory in which to create the Matcher store
    matcherStoreDirectory = FileUtils.makeRandomTemporaryDirectory();

    log.debug("Creating a Matcher store in the directory '" + matcherStoreDirectory + "'");

    matcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory);
    assertThat(matcherStore).isNotNull();
  }

  @Test
  public void testStoreAndGetAllBitcoinAddresses() throws Exception {

    // Store some unique Bitcoin addresses
    Set<String> allBitcoinAddresses = Sets.newHashSet();
    allBitcoinAddresses.add("one");
    allBitcoinAddresses.add("two");
    allBitcoinAddresses.add("three");

    matcherStore.storeAllBitcoinAddresses(allBitcoinAddresses);

    // Bounce the MatcherStore to check everything is being persisted
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.getAllBitcoinAddresses()).isEqualTo(allBitcoinAddresses);
  }

  @Test
  public void testStoreAndGetBitcoinAddressListByDate() throws Exception {

    Date yesterday = DateTime.now().minusDays(1).toDate();
    Date today = DateTime.now().toDate();
    Date tomorrow = DateTime.now().plusDays(1).toDate();

    // Store some bitcoin address lists by date
    final Set<String> bitcoinAddresses1 = Sets.newHashSet();
    bitcoinAddresses1.add("one.cat");
    bitcoinAddresses1.add("one.dog");
    bitcoinAddresses1.add("one.elephant");
    matcherStore.storeBitcoinAddressesForDate(bitcoinAddresses1, yesterday);

    final Set<String> bitcoinAddresses2 = Sets.newHashSet();
    bitcoinAddresses2.add("two.cat");
    bitcoinAddresses2.add("two.dog");
    bitcoinAddresses2.add("two.elephant");
    bitcoinAddresses2.add("two.worm");
    matcherStore.storeBitcoinAddressesForDate(bitcoinAddresses2, today);

    final Set<String> bitcoinAddresses3 = Sets.newHashSet();
    bitcoinAddresses3.add("three.cat");
    bitcoinAddresses3.add("three.dog");
    bitcoinAddresses3.add("three.elephant");
    bitcoinAddresses3.add("three.wallaby");
    matcherStore.storeBitcoinAddressesForDate(bitcoinAddresses3, tomorrow);

    // Bounce the MatcherStore to check everything is being persisted
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(yesterday)).isEqualTo(bitcoinAddresses1);
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(today)).isEqualTo(bitcoinAddresses2);
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(tomorrow)).isEqualTo(bitcoinAddresses3);
  }

  @Test
  public void testStoreAndLookupWalletToEncounterDateLinks() throws Exception {
    // Create a BRITWalletId (in real life this would be using the Payer's wallet seed)
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_1));
    BRITWalletId britWalletId1 = new BRITWalletId(seed1);

    Date encounterDate1 = DateTime.now().toDate();
    Date firstTransactionDate1 = DateTime.now().minusDays(1).toDate();

    // Store an encounter with this britWalletId and an lastTransactionDate
    WalletToEncounterDateLink walletToEncounterLink1 = new WalletToEncounterDateLink(britWalletId1, Optional.of(encounterDate1), Optional.of(firstTransactionDate1));
    matcherStore.storeWalletToEncounterDateLink(walletToEncounterLink1);


    // Store another one
    byte[] seed2 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_2));
    BRITWalletId britWalletId2 = new BRITWalletId(seed2);

    Date encounterDate2 = DateTime.now().minusDays(2).toDate();

    // Store an encounter with this britWalletId and no lastTransactionDate
    WalletToEncounterDateLink walletToEncounterLink2 = new WalletToEncounterDateLink(britWalletId2, Optional.of(encounterDate2), Optional.<Date>absent());
    matcherStore.storeWalletToEncounterDateLink(walletToEncounterLink2);

    // Bounce the MatcherStore to check everything is being persisted
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.lookupWalletToEncounterDateLink(britWalletId1)).isEqualTo(walletToEncounterLink1);
    assertThat(rebornMatcherStore.lookupWalletToEncounterDateLink(britWalletId2)).isEqualTo(walletToEncounterLink2);
  }
}
