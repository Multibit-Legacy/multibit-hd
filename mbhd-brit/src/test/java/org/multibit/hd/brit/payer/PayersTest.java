package org.multibit.hd.brit.payer;

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
import org.junit.Test;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.fest.assertions.api.Assertions.assertThat;

public class PayersTest {

  private static final Logger log = LoggerFactory.getLogger(PayersTest.class);

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreatePayer() throws Exception {
    // Load the example Matcher PGP public key
    File matcherPublicKeyFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_PUBLIC_KEY_FILE);
    FileInputStream matcherPublicKeyInputStream = new FileInputStream(matcherPublicKeyFile);
    PGPPublicKey matcherPGPPublicKey = PGPUtils.readPublicKey(matcherPublicKeyInputStream);

    log.debug("Matcher public key id = " + matcherPGPPublicKey.getKeyID());

    PayerConfig payerConfig = new PayerConfig(matcherPGPPublicKey);

    Payer payer = Payers.newBasicPayer(payerConfig);
    assertThat(payer).isNotNull();

    // Check the Matcher PGP public key is stored properly
    assertThat(payer.getConfig().getMatcherPublicKey()).isEqualTo(matcherPGPPublicKey);
  }
}
