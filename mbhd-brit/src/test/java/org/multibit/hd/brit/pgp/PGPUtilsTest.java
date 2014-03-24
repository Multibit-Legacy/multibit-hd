package org.multibit.hd.brit.pgp;

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
import org.multibit.hd.brit.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class PGPUtilsTest {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


  private static final String EXAMPLE_TEXT = "The quick brown fox jumps over the lazy dog. 01234567890. !@#$%^&*(). ,.;:[]-_=+";

  private static final String TEST_PUBLIC_KEYRING_FILE = "/src/test/resources/redeemer/gpg/pubring.gpg";

  private static final String TEST_SECRET_KEYRING_FILE = "/src/test/resources/redeemer/gpg/secring.gpg";

  /**
   * The password used in the generation of the test PGP keys
   */
  private static final char[] TEST_DATA_PASSWORD = "password".toCharArray();


  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPGPEncryptDecrypt() throws Exception {
    // Read the manually created public keyring in the test directory to find a public key suitable for encryption
    // (the location varies if you run it in an IDE or via Maven)
    File publicKeyRingFile = new File("mbhd-brit" + File.separator + TEST_PUBLIC_KEYRING_FILE);
    if (!publicKeyRingFile.exists()) {
      publicKeyRingFile =  new File("." + File.separator + TEST_PUBLIC_KEYRING_FILE);
    }
    log.debug("Loading public keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");
    FileInputStream publicKeyRingInputStream = new FileInputStream(publicKeyRingFile);
    PGPPublicKey encryptionKey = PGPUtils.readPublicKey(publicKeyRingInputStream);
    assertThat(encryptionKey).isNotNull();

    // Make a temporary random directory
    File testDir = FileUtils.makeRandomTemporaryDirectory();

    // Write some text to the plain text.
    File inputFile = new File(testDir.getAbsolutePath() + File.separator + "plain.txt");
    try (FileOutputStream fileOutputStream = new FileOutputStream(inputFile)) {
      FileUtils.writeFile(new ByteArrayInputStream(EXAMPLE_TEXT.getBytes("UTF8")), fileOutputStream);
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
    // (the location varies if you run it in an IDE or via Maven)
    File secretKeyRingFile = new File("mbhd-brit" + File.separator + TEST_SECRET_KEYRING_FILE);
    if (!secretKeyRingFile.exists()) {
      secretKeyRingFile =  new File("." + File.separator + TEST_SECRET_KEYRING_FILE);
    }
    log.debug("Loading secret keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");

    // Decrypt the encrypted file
    FileInputStream secretKeyRingInputStream = new FileInputStream(secretKeyRingFile);
    FileInputStream encryptedInputStream = new FileInputStream(encryptedFile);
    File rebornPlainTextFile = new File(testDir.getAbsolutePath() + File.separator + "reborn.txt");
    FileOutputStream rebornPlainTextOutputStream = new FileOutputStream(rebornPlainTextFile);

    PGPUtils.decryptFile(encryptedInputStream, rebornPlainTextOutputStream, secretKeyRingInputStream, TEST_DATA_PASSWORD);
    assertThat(inputFile.length()).isEqualTo(rebornPlainTextFile.length());

    byte[] rebornBytes = FileUtils.readFile(rebornPlainTextFile);
    assertThat(Arrays.equals( EXAMPLE_TEXT.getBytes("UTF8"), rebornBytes)).isTrue();
  }
}
