package org.multibit.hd.brit.crypto;

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
import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

public class AESUtilsTest {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


  private static final String EXAMPLE_TEXT = "The quick brown fox jumps over the lazy dog. 01234567890. !@#$%^&*(). ,.;:[]-_=+";

  private static final char[] TEST_DATA_PASSWORD = "ipbnprb48864571".toCharArray();

  // Nonsense bytes for encryption test.
  private static final byte[] TEST_BYTES1 = {0, -101, 2, 103, -4, 105, 6, 107, 8, -109, 10, 111, -12, 113, 14, -115, 16, 117, -18, 119, 20, 121, 22, 123, -24, 125, 26, 127, -28, 29, -30, 31};

  private static final CharSequence PASSWORD1 = "aTestPassword";
  private static final CharSequence PASSWORD2 = "0123456789";

  private static final CharSequence WRONG_PASSWORD = "thisIsTheWrongPassword";

  private byte[] initialisationVector;
  private byte[] keyBytes;
  private KeyParameter keyParameter;

  private SecureRandom secureRandom;

  @Before
  public void setUp() throws Exception {
    secureRandom = new SecureRandom();

    // Create a random initialisationVector
    initialisationVector = new byte[AESUtils.BLOCK_LENGTH];
    secureRandom.nextBytes(initialisationVector);

    // Create a random key
    secureRandom.nextBytes(initialisationVector);
    keyBytes = new byte[AESUtils.KEY_LENGTH];
    keyParameter = new KeyParameter(keyBytes);

    BriefLogFormatter.init();
  }

  @Test
  public void testEncrypt_ExpectSuccess() throws Exception {

    // Plain text
    byte[] plainBytes = EXAMPLE_TEXT.getBytes(Charsets.UTF_8);
    log.debug("Initial message: " + Utils.bytesToHexString(plainBytes));

    // Encrypt
    byte[] encryptedBytes = AESUtils.encrypt(EXAMPLE_TEXT.getBytes(Charsets.UTF_8), keyParameter, initialisationVector);
    log.debug("Encrypted message: " + Utils.bytesToHexString(encryptedBytes));

    // Decrypt
    byte[] rebornBytes = AESUtils.decrypt(encryptedBytes, keyParameter, initialisationVector);
    log.debug("Reborn message: " + Utils.bytesToHexString(rebornBytes));

    assertEquals(Utils.bytesToHexString(plainBytes), Utils.bytesToHexString(rebornBytes));

  }
}
