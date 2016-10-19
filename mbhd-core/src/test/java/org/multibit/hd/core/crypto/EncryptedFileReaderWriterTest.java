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
import org.bitcoinj.utils.BriefLogFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.commons.crypto.AESUtils;
import org.multibit.hd.core.config.Configurations;
import org.multibit.commons.files.SecureFiles;
import org.multibit.hd.core.files.EncryptedContactsFile;
import org.multibit.hd.core.files.EncryptedFileListItem;
import org.multibit.hd.core.files.EncryptedPaymentsFile;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.PersistentContactService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.fest.assertions.Assertions.assertThat;

public class EncryptedFileReaderWriterTest {

  // Nonsense bytes for encryption tests.
  private static final byte[] TEST_BYTES1 = {0, -101, 2, 103, -4, 105, 6, 107, 8, -109, 10, 111, -12, 113, 14, -115, 16, 117, -18, 119, 20, 121, 22, 123, -24, 125, 26, 127, -28, 29, -30, 31};
  private static final byte[] TEST_BYTES2 = {7, -19, 8, 13, -35, 65, 56, 123, 22, -19, 40, 11, -17, 7, 14, -115, 16, 117, -15, 119, 20, 121, 22, 13, -87, 125, 26, 127, -28, 15};
  private static final byte[] CONTACT_BYTES = {10, 104, 10, 36, 55, 100, 49, 57, 49, 49, 51, 49, 45, 102, 99, 99, 57, 45, 52, 55, 51, 51, 45, 97, 55, 55, 49, 45, 49, 98, 99, 57, 102, 55, 97, 50, 52, 48, 51, 55, 18, 4, 65, 110, 110, 97, 26, 16, 97, 110, 110, 97, 64, 107, 101, 101, 112, 107, 101, 121, 46, 99, 111, 109, 34, 0, 42, 34, 49, 80, 82, 81, 87, 122, 115, 105, 55, 84, 116, 101, 80, 56, 120, 50, 97, 109, 114, 114, 90, 111, 118, 81, 52, 113, 56, 106, 116, 110, 102, 81, 121, 72, 50, 0, 58, 0};
  private static final byte[] PAYMENT_BYTES = {10, 86, 10, 34, 49, 72, 90, 112, 57, 107, 99, 103, 66, 118, 78, 116, 105, 102, 117, 75, 100, 110, 52, 113, 99, 89, 85, 117, 122, 88, 80, 100, 112, 76, 85, 87, 71, 71, 18, 0, 24, -96, -115, 6, 34, 31, 10, 6, 48, 46, 54, 51, 52, 53, 18, 3, 85, 83, 68, 26, 8, 66, 105, 116, 115, 116, 97, 109, 112, 34, 6, 54, 51, 52, 46, 53, 48, 42, 0, 48, -124, -111, -101, -84, -5, 42, 56, 0};
  private static final byte[][] TEST_BYTES_ARRAY = new byte[][] {TEST_BYTES1, TEST_BYTES2};

   private static final CharSequence PASSWORD1 = "aTestPassword";

  private static final CharSequence PASSWORD2 = "flim flam bim bam jim jam";
  private PersistentContactService contactService;

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

    EncryptedPaymentsFile outputFile = new EncryptedPaymentsFile(temporaryDirectory + File.separator + "outputFile.aes");

    EncryptedFileReaderWriter.encryptAndWrite(PAYMENT_BYTES, PASSWORD1, outputFile);
    InputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(
            outputFile,
            PASSWORD1
    );

    assertTrue(outputFile.isValidDecryption(decryptedInputStream));
    decryptedInputStream.close();
  }

  @Test
  public void testChangeEncryption() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();
    EncryptedContactsFile contactDbFile = new EncryptedContactsFile(temporaryDirectory.getAbsolutePath() + File.separator + "contacts.aes");
    EncryptedPaymentsFile paymentsFile = new EncryptedPaymentsFile(temporaryDirectory.getAbsolutePath() + File.separator + "payments.aes");
    // Write out the test bytes to the output files
    EncryptedFileReaderWriter.encryptAndWriteDirect(CONTACT_BYTES, PASSWORD1, contactDbFile);
    EncryptedFileReaderWriter.encryptAndWriteDirect(PAYMENT_BYTES, PASSWORD1, paymentsFile);

    List<EncryptedFileListItem> filesToChange = Lists.newArrayList();
    filesToChange.add(contactDbFile);
    filesToChange.add(paymentsFile);

    // Change the encryption on the file
    List<EncryptedFileListItem> newFiles = EncryptedFileReaderWriter.changeEncryptionPrepare(filesToChange, PASSWORD1, PASSWORD2);
    EncryptedFileReaderWriter.changeEncryptionCommit(filesToChange, newFiles);

    int i = 0;
    for (EncryptedFileListItem loopFile : filesToChange) {
      // Read back in and check the encryption
      InputStream decryptedInputStream = EncryptedFileReaderWriter.readAndDecrypt(
              loopFile,
              PASSWORD2
      );

      assertTrue(loopFile.isValidDecryption(decryptedInputStream));
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
