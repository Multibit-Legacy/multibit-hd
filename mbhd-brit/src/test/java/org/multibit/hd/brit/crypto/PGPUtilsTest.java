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

import org.bitcoinj.core.Utils;
import com.google.common.base.Charsets;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class PGPUtilsTest {

  private static final Logger log = LoggerFactory.getLogger(PGPUtilsTest.class);

  private static final String EXAMPLE_TEXT = "The quick brown fox jumps over the lazy dog. 01234567890. !@#$%^&*(). ,.;:[]-_=+";

  public static final String TEST_MATCHER_PUBLIC_KEYRING_FILE = "/src/test/resources/matcher/gpg/pubring.gpg";

  public static final String TEST_MATCHER_SECRET_KEYRING_FILE = "/src/test/resources/matcher/gpg/secring.gpg";

  public static final String TEST_MATCHER_PUBLIC_KEY_FILE = "/src/test/resources/matcher/export-to-payer/matcher-key.asc";

  private static final String MBHD_BRIT_PREFIX = "mbhd-brit";

  /**
   * The password used in the generation of the test PGP keys
   */
  public static final char[] TEST_DATA_PASSWORD = "password".toCharArray();


  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPGPEncryptDecrypt() throws Exception {
    // Read the manually created public keyring in the test directory to find a public key suitable for encryption
    File publicKeyRingFile = makeFile(TEST_MATCHER_PUBLIC_KEYRING_FILE);
    log.debug("Loading public keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");
    FileInputStream publicKeyRingInputStream = new FileInputStream(publicKeyRingFile);
    PGPPublicKey encryptionKey = PGPUtils.readPublicKey(publicKeyRingInputStream);
    assertThat(encryptionKey).isNotNull();

    // Make a temporary random directory
    File testDir = FileUtils.makeRandomTemporaryDirectory();

    // Write some text to the plain text.
    File inputFile = new File(testDir.getAbsolutePath() + File.separator + "plain.txt");
    try (FileOutputStream fileOutputStream = new FileOutputStream(inputFile)) {
      FileUtils.writeFile(new ByteArrayInputStream(EXAMPLE_TEXT.getBytes(Charsets.UTF_8)), fileOutputStream);
    }
    assertThat(inputFile.length()).isGreaterThanOrEqualTo(EXAMPLE_TEXT.length());

    // Create the file that the encrypted output will be written to
    File encryptedFile = new File(testDir.getAbsolutePath() + File.separator + "encrypted.asc");
    assertThat(encryptedFile.length()).isEqualTo(0);
    OutputStream encryptedOutputStream = new FileOutputStream(encryptedFile);

    // Encrypt the plain text
    PGPUtils.encryptFile(encryptedOutputStream, inputFile, encryptionKey);
    assertThat(encryptedFile.length()).isGreaterThanOrEqualTo(EXAMPLE_TEXT.length());

    // Locate the secret keyring file
    File secretKeyRingFile = makeFile(TEST_MATCHER_SECRET_KEYRING_FILE);
    log.debug("Loading secret keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");

    // Decrypt the encrypted file
    FileInputStream secretKeyRingInputStream = new FileInputStream(secretKeyRingFile);
    FileInputStream encryptedInputStream = new FileInputStream(encryptedFile);
    File rebornPlainTextFile = new File(testDir.getAbsolutePath() + File.separator + "reborn.txt");
    FileOutputStream rebornPlainTextOutputStream = new FileOutputStream(rebornPlainTextFile);

    PGPUtils.decryptFile(encryptedInputStream, rebornPlainTextOutputStream, secretKeyRingInputStream, TEST_DATA_PASSWORD);
    assertThat(inputFile.length()).isEqualTo(rebornPlainTextFile.length());

    byte[] rebornBytes = FileUtils.readFile(rebornPlainTextFile);
    assertThat(Arrays.equals( EXAMPLE_TEXT.getBytes(Charsets.UTF_8), rebornBytes)).isTrue();
  }

  @Test
  public void testLoadPGPPublicKeyFromASCIIArmoredFile() throws Exception {
    File publicKeyFile = makeFile(TEST_MATCHER_PUBLIC_KEY_FILE);
    log.debug("Loading public key from '" + publicKeyFile.getAbsolutePath() + "'");
    FileInputStream publicKeyInputStream = new FileInputStream(publicKeyFile);
    PGPPublicKey publicKey = PGPUtils.readPublicKey(publicKeyInputStream);
    assertThat(publicKey).isNotNull();
    log.debug("Loaded PGP public key :\nAlgorithm: " + publicKey.getAlgorithm() + ", bitStrength: "  + publicKey.getBitStrength()
      + ", fingerprint: " + Utils.HEX.encode(publicKey.getFingerprint()));
  }

  /**
   * Make a file reference - a file can be referenced as, say,
   *     brit-mbhd/src/test/resources/redeemer1/gpg/pubring.gpg
   * or
   *     ./src/test/resources/redeemer1/gpg/pubring.gpg
   * depending on whether you are running via an IDE or in Maven
   * @param rootFilename The root filename (relative to the root of the mbhd directory e.g. src/test/resources/redeemer1/gpg/pubring.gpg
   *                     in the example above)
   * @return File reference
   */
  public static File makeFile(String rootFilename) {
    File file = new File(MBHD_BRIT_PREFIX + File.separator + rootFilename);
    if (!file.exists()) {
      file =  new File("." + File.separator + rootFilename);
    }
    return file;
  }
}
