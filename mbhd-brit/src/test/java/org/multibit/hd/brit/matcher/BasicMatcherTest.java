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

import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.payer.Payer;
import org.multibit.hd.brit.payer.PayerConfig;
import org.multibit.hd.brit.payer.Payers;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class BasicMatcherTest {

  private static final Logger log = LoggerFactory.getLogger(BasicMatcherTest.class);

  private SecureRandom secureRandom;

  private static List<Address> testAddresses = Lists.newArrayList();

  @BeforeClass
  public static void setUpOnce() throws Exception {

    String[] rawTestAddresses = new String[]{

      "1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty",
      "14Ru32Lb4kdLGfAMz1VAtxh3UFku62HaNH",
      "1KesQEF2yC2FzkJYLLozZJdbBF7zRhrdSC",
      "1CuWW5fDxuFN6CcrRi51ADWHXAMJPYxY5y",
      "1NfNX36S8aocBomvWgySaK9fn93pbpEhmY",
      "1J1nTRJJT3ghsnAEvwd8dMmoTuaAMSLf4V"
    };

    for (String rawTestAddress : rawTestAddresses) {
      testAddresses.add(new Address(MainNetParams.get(), rawTestAddress));

    }

  }

  @Before
  public void setUp() throws Exception {
    secureRandom = new SecureRandom();
  }

  @Test
  public void testPayerRequestAndMatcherResponse1() throws Exception {
    // Create a payer
    Payer payer = createTestPayer();

    // Create a BRITWalletId (in real life this would be using the Payer's wallet seed)
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_1));
    BRITWalletId britWalletId = new BRITWalletId(seed);

    // Create a random session id
    byte[] sessionId = new byte[AESUtils.BLOCK_LENGTH];
    secureRandom.nextBytes(sessionId);

    // Create a first transaction date (In real life this would come from a wallet)
    Optional<Date> firstTransactionDateOptional = Optional.of(new Date());

    // Ask the payer to create an EncryptedPayerRequest containing a BRITWalletId, a session id and a firstTransactionDate
    PayerRequest payerRequest = payer.newPayerRequest(britWalletId, sessionId, firstTransactionDateOptional);
    assertThat(payerRequest).isNotNull();
    // Encrypt the PayerRequest with the Matcher PGP public key.
    EncryptedPayerRequest encryptedPayerRequest = payer.encryptPayerRequest(payerRequest);

    String payloadAsString = new String(encryptedPayerRequest.getPayload(), Charsets.UTF_8);
    log.debug("payloadAsString = \n" + payloadAsString);

    // In real life the encryptedPayerRequest is transported from the Payer to the Matcher here

    // Create a matcher
    Matcher matcher = createTestMatcher();

    // The Matcher can decrypt the EncryptedPaymentRequest using its PGP secret key
    PayerRequest matcherPayerRequest = matcher.decryptPayerRequest(encryptedPayerRequest);

    // The decrypted payment request should be the same as the original
    assertThat(payerRequest).isEqualTo(matcherPayerRequest);

    // Get the matcher to process the EncryptedPayerRequest
    MatcherResponse matcherResponse = matcher.process(matcherPayerRequest);
    assertThat(matcherResponse).isNotNull();

    // Encrypt the MatcherResponse with the AES session key
    EncryptedMatcherResponse encryptedMatcherResponse = matcher.encryptMatcherResponse(matcherResponse);
    assertThat(encryptedMatcherResponse).isNotNull();

    // In real life the encryptedMatcherResponse is transported from the Matcher to the Payer here

    // The payer can decrypt the encryptedMatcherResponse
    // as it knows the BRITWalletId and session id
    MatcherResponse payersMatcherResponse = payer.decryptMatcherResponse(encryptedMatcherResponse);
    assertThat(payersMatcherResponse).isNotNull();

    // The original matcher response should be the same as the decrypted version
    assertThat(matcherResponse).isEqualTo(payersMatcherResponse);

    // The Payer's Matcher response contains the list of addresses the Payer will use
    Set<Address> addressList = payersMatcherResponse.getBitcoinAddresses();
    assertThat(addressList).isNotNull();

    // The Payer's Matcher response contains a stored replay date for the wallet
    Date replayDate = payersMatcherResponse.getReplayDate().get();
    assertThat(replayDate).isNotNull();
  }

  private Matcher createTestMatcher() throws Exception {

    // Find the example Matcher PGP secret key ring file
    File matcherSecretKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_SECRET_KEYRING_FILE);
    MatcherConfig matcherConfig = new MatcherConfig(matcherSecretKeyFile, PGPUtilsTest.TEST_DATA_PASSWORD);

    // Create a random temporary directory for the Matcher store to use
    File matcherStoreDirectory = FileUtils.makeRandomTemporaryDirectory();
    MatcherStore matcherStore = MatcherStores.newBasicMatcherStore(matcherStoreDirectory);

    Matcher matcher = Matchers.newBasicMatcher(matcherConfig, matcherStore);
    assertThat(matcher).isNotNull();

    // Add some test data for today's bitcoin addresses
    Set<Address> bitcoinAddresses = Sets.newHashSet();
    bitcoinAddresses.add(testAddresses.get(0));
    bitcoinAddresses.add(testAddresses.get(1));
    bitcoinAddresses.add(testAddresses.get(2));
    bitcoinAddresses.add(testAddresses.get(3));

    matcherStore.storeBitcoinAddressesForDate(bitcoinAddresses, new Date());

    return matcher;
  }

  private Payer createTestPayer() throws Exception {
    // Load the example Matcher PGP public key
    File matcherPublicKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_PUBLIC_KEY_FILE);
    FileInputStream matcherPublicKeyInputStream = new FileInputStream(matcherPublicKeyFile);
    PGPPublicKey matcherPGPPublicKey = PGPUtils.readPublicKey(matcherPublicKeyInputStream);

    PayerConfig payerConfig = new PayerConfig(matcherPGPPublicKey);

    Payer payer = Payers.newBasicPayer(payerConfig);
    assertThat(payer).isNotNull();

    return payer;
  }
}
