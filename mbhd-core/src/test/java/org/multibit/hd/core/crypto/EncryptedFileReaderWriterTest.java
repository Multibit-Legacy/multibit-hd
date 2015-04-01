package org.multibit.hd.core.crypto;

/**
 * Copyright 2015 multibit.org
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

import com.google.common.collect.Lists;
import org.bitcoinj.core.Utils;
import org.bitcoinj.utils.BriefLogFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.InstallationManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class EncryptedFileReaderWriterTest {

  // Nonsense bytes for encryption tests.
  private static final byte[] TEST_BYTES1 = {0, -101, 2, 103, -4, 105, 6, 107, 8, -109, 10, 111, -12, 113, 14, -115, 16, 117, -18, 119, 20, 121, 22, 123, -24, 125, 26, 127, -28, 29, -30, 31};

  private static final byte[] TEST_BYTES2 = {7, -19, 8, 13, -35, 65, 56, 123, 22, -19, 40, 11, -17, 7, 14, -115, 16, 117, -15, 119, 20, 121, 22, 13, -87, 125, 26, 127, -28, 15};

  private static final byte[][] TEST_BYTES_ARRAY = new byte[][] {TEST_BYTES1, TEST_BYTES2};

   private static final CharSequence PASSWORD1 = "aTestPassword";

  private static final CharSequence PASSWORD2 = "flim flam bim bam jim jam";

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    SecureRandom secureRandom = new SecureRandom();

    // Create a random initialisationVector
    byte[] initialisationVector = new byte[AESUtils.BLOCK_LENGTH];
    secureRandom.nextBytes(initialisationVector);

    // Create a random key
    secureRandom.nextBytes(initialisationVector);

    BriefLogFormatter.init();
  }

  @After
  public void tearDown() throws Exception {

    InstallationManager.unrestricted = false;

  }

  @Test
  public void testEncryptDecryptSuccess() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    File outputFile = new File(temporaryDirectory + File.separator + "outputFile.aes");

    EncryptedFileReaderWriter.encryptAndWrite(TEST_BYTES1, PASSWORD1, outputFile);
    InputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(
            outputFile,
            PASSWORD1
    );

    byte[] rebornBytes = readBytes(decryptedInputStream);
    assertThat(Utils.HEX.encode(rebornBytes)).isEqualTo(Utils.HEX.encode(TEST_BYTES1));

    decryptedInputStream.close();
  }

  @Test
  public void testChangeEncryption() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    File outputFile1 = new File(temporaryDirectory + File.separator + "outputFile1.aes");
    File outputFile2 = new File(temporaryDirectory + File.separator + "outputFile2.aes");

    // Write out the test bytes to the output files
    EncryptedFileReaderWriter.encryptAndWriteDirect(TEST_BYTES1, PASSWORD1, outputFile1);
    EncryptedFileReaderWriter.encryptAndWriteDirect(TEST_BYTES2, PASSWORD1, outputFile2);

    List<File> filesToChange = Lists.newArrayList();
    filesToChange.add(outputFile1);
    filesToChange.add(outputFile2);

    // Change the encryption on the file
    List<File> newFiles = EncryptedFileReaderWriter.changeEncryptionPrepare(filesToChange, PASSWORD1, PASSWORD2);
    EncryptedFileReaderWriter.changeEncryptionCommit(filesToChange, newFiles);

    int i = 0;
    for (File loopFile : filesToChange) {
      // Read back in and check the encryption
      InputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(
              loopFile,
              PASSWORD2
      );

      byte[] rebornBytes = readBytes(decryptedInputStream);

      assertThat(Utils.HEX.encode(rebornBytes)).isEqualTo(Utils.HEX.encode(TEST_BYTES_ARRAY[i]));

      decryptedInputStream.close();

      i++;
    }
  }

  private byte[] readBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();

    return buffer.toByteArray();
  }
}
