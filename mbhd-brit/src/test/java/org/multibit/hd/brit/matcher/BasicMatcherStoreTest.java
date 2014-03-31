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

import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BasicMatcherStoreTest {

  private static final Logger log = LoggerFactory.getLogger(BasicMatcherStoreTest.class);

  private MatcherStore matcherStore;

  private String matcherStoreDirectoryLocation;

  @Before
  public void setUp() throws Exception {
    // Create a random temporary directory in which to create the matcher store
    matcherStoreDirectoryLocation = FileUtils.makeRandomTemporaryDirectory().getAbsolutePath();

    log.debug("Creating a matcher store in the directory '" + matcherStoreDirectoryLocation + "'");

    matcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectoryLocation);
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

    // Bounce the MatcherStore to check everything is being persisted
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectoryLocation);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.getAllBitcoinAddress()).isEqualTo(allBitcoinAddresses);
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

    // Bounce the MatcherStore to check everything is being persisted
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectoryLocation);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(yesterday)).isEqualTo(bitcoinAddressList1);
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(today)).isEqualTo(bitcoinAddressList2);
    assertThat(rebornMatcherStore.lookupBitcoinAddressListForDate(tomorrow)).isEqualTo(bitcoinAddressList3);
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
    MatcherStore rebornMatcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectoryLocation);

    // Check they have been stored ok
    assertThat(rebornMatcherStore.lookupWalletToEncounterDateLink(britWalletId1)).isEqualTo(walletToEncounterLink1);
    assertThat(rebornMatcherStore.lookupWalletToEncounterDateLink(britWalletId2)).isEqualTo(walletToEncounterLink2);
  }
}
