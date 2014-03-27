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

import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Ignore;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.multibit.hd.brit.dto.*;
import org.multibit.hd.brit.payer.Payer;
import org.multibit.hd.brit.payer.PayerConfig;
import org.multibit.hd.brit.payer.PayerFactory;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;

import static org.fest.assertions.api.Assertions.assertThat;

public class MatcherTest {

  private static final Logger log = LoggerFactory.getLogger(MatcherTest.class);

  private SecureRandom secureRandom;

  @Before
  public void setUp() throws Exception {
    secureRandom = new SecureRandom();
  }

  @Ignore
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

    // Ask the payer to create an EncryptedPayerRequest containing a BRITWalletId and a session id
    PayerRequest payerRequest = payer.createPayerRequest(britWalletId, sessionId);
    EncryptedPayerRequest encryptedPayerRequest = payer.encryptPayerRequest(payerRequest);

    //
    // In real life the encryptedPayerRequest is transported from the Payer to the Matcher here
    //

    // Create a matcher
    Matcher matcher = createTestMatcher();

    // Get the matcher to process the EncryptedPayerRequest.
    // It responds with an EncryptedMatcherResponse containing a new AddressGenerator
    PayerRequest theMatchersPaymentRequest = matcher.decryptPayerRequest(encryptedPayerRequest);
    MatcherResponse matcherResponse = matcher.process(theMatchersPaymentRequest);
    assertThat(matcherResponse).isNotNull();
    EncryptedMatcherResponse encryptedMatcherResponse = matcher.encryptMatcherResponse(matcherResponse);
    assertThat(encryptedMatcherResponse).isNotNull();

    //
    // In real life the encryptedMatcherResponse is transported from the Matcher to the Payer here
    //

    // The payer can decrypt the encryptedMatcherResponse
    // as it knows the BRITWalletId and session id
    MatcherResponse thePayersMatcherResponse = payer.decryptMatcherReponse(encryptedMatcherResponse);
    assertThat(thePayersMatcherResponse).isNotNull();

    // The thePayersMatcherResponse contains the addressGenerator that the payer will use to generate addresses
    AddressGenerator addressGenerator = thePayersMatcherResponse.getAddressGenerator();
    assertThat(addressGenerator).isNotNull();

    // Ask the matcher to validate the addressGenerator
    // (This is a test facility that would not be exposed by the Matcher daemon - it checks all the encryption/ decryption/ transport is ok)
    boolean addressGenerateIsCorrect = matcher.validateAddressGenerator(britWalletId, sessionId, addressGenerator);
    assertThat(addressGenerateIsCorrect).isTrue();
  }

  private Matcher createTestMatcher() {
        // Find the example Matcher PGP secret key ring file
    File matcherSecretKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_SECRET_KEYRING_FILE);

    MatcherConfig matcherConfig = new MatcherConfig(matcherSecretKeyFile);

    Matcher matcher = MatcherFactory.createBasicMatcher(matcherConfig);
    assertThat(matcher).isNotNull();
    return matcher;
  }

  private Payer createTestPayer() throws Exception{
    // Load the example Matcher PGP public key
    File matcherPublicKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_PUBLIC_KEY_FILE);
    FileInputStream matcherPublicKeyInputStream = new FileInputStream(matcherPublicKeyFile);
    PGPPublicKey matcherPGPPublicKey = PGPUtils.readPublicKey(matcherPublicKeyInputStream);

    PayerConfig payerConfig = new PayerConfig(matcherPGPPublicKey);

    Payer payer = PayerFactory.createBasicPayer(payerConfig);
    assertThat(payer).isNotNull();
    return payer;
  }
}
