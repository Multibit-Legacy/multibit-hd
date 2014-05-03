package org.multibit.hd.core.crypto;

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
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.brit.utils.FileUtils;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.managers.WalletManagerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;

import static org.fest.assertions.Assertions.assertThat;

public class EncryptedFileReaderWriterTest {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


  private static final String EXAMPLE_TEXT = "The quick brown fox jumps over the lazy dog. 01234567890. !@#$%^&*(). ,.;:[]-_=+";

  // Nonsense bytes for encryption test.
  private static final byte[] TEST_BYTES1 = {0, -101, 2, 103, -4, 105, 6, 107, 8, -109, 10, 111, -12, 113, 14, -115, 16, 117, -18, 119, 20, 121, 22, 123, -24, 125, 26, 127, -28, 29, -30, 31};

  private static final CharSequence PASSWORD1 = "aTestPassword";

  private byte[] initialisationVector;
  private byte[] keyBytes;
  private KeyParameter keyParameter;

  private SecureRandom secureRandom;

  @Before
  public void setUp() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

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
  public void testEncryptDecryptSuccess() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();

    File outputFile = new File(temporaryDirectory + File.separator + "outputFile.aes");

    EncryptedFileReaderWriter.encryptAndWrite(TEST_BYTES1, PASSWORD1, outputFile);
    InputStream decryptedInputstream = EncryptedFileReaderWriter.readAndDecrypt(outputFile, PASSWORD1);

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    while ((nRead = decryptedInputstream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();

    assertThat(Utils.bytesToHexString(buffer.toByteArray())).isEqualTo(Utils.bytesToHexString(TEST_BYTES1));

    decryptedInputstream.close();
  }
}
