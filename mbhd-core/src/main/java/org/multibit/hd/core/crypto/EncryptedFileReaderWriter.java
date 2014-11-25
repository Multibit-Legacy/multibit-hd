package org.multibit.hd.core.crypto;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.Protos;
import org.multibit.hd.brit.crypto.AESUtils;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.WalletManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * <p>Reader / Writer to provide the following to Services:<br>
 * <ul>
 * <li>load an AES encrypted file</li>
 * <li>write an AES encrypted file</li>
 * </ul>
 * Example:<br>
 * <pre>
 * </pre>
 * </p>
 *
 */
public class EncryptedFileReaderWriter {
  private static final Logger log = LoggerFactory.getLogger(EncryptedFileReaderWriter.class);

  private static final String TEMPORARY_FILE_EXTENSION = ".tmp";

  /**
   * Decrypt an AES encrypted file and return it as an inputStream
   */
  public static ByteArrayInputStream readAndDecrypt(File encryptedProtobufFile, CharSequence password, byte[] salt, byte[] initialisationVector) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(encryptedProtobufFile);
    Preconditions.checkNotNull(password);
    try {
      // Read the encrypted file in and decrypt it.
      byte[] encryptedWalletBytes = Files.toByteArray(encryptedProtobufFile);
      //log.debug("Encrypted wallet bytes after load:\n" + Utils.HEX.encode(encryptedWalletBytes));

      KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(salt));
      KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);

      // Decrypt the wallet bytes
      byte[] decryptedBytes = AESUtils.decrypt(encryptedWalletBytes, keyParameter, initialisationVector);

      return new ByteArrayInputStream(decryptedBytes);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot read and decrypt the file '" + encryptedProtobufFile.getAbsolutePath() + "'", e);
    }
  }

  /**
   * Encrypt a byte array and output to a file, using an intermediate temporary file
   */
  public static void encryptAndWrite(byte[] unencryptedBytes, CharSequence password, File outputFile) throws EncryptedFileReaderWriterException {
    try {
      KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.SCRYPT_SALT));
      KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);

      // Create an AES encoded version of the unencryptedBytes, using the credentials
      byte[] encryptedBytes = AESUtils.encrypt(unencryptedBytes, keyParameter, WalletManager.AES_INITIALISATION_VECTOR);

      //log.debug("Encrypted wallet bytes (original):\n" + Utils.HEX.encode(encryptedBytes));

      // Check that the encryption is reversible
      byte[] rebornBytes = AESUtils.decrypt(encryptedBytes, keyParameter, WalletManager.AES_INITIALISATION_VECTOR);

      if (Arrays.equals(unencryptedBytes, rebornBytes)) {
        // Save encrypted bytes

        ByteArrayInputStream encryptedWalletByteArrayInputStream = new ByteArrayInputStream(encryptedBytes);
        File temporaryFile = new File(outputFile.getAbsolutePath() + TEMPORARY_FILE_EXTENSION);
        SecureFiles.writeFile(encryptedWalletByteArrayInputStream, temporaryFile, outputFile);
      } else {
        throw new EncryptedFileReaderWriterException("The encryption was not reversible so aborting.");
      }
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot encryptAndWrite", e);
    }
  }

  /**
    * Encrypt the file specified using the backup AES key derived from the supplied credentials
    * @param fileToEncrypt file to encrypt
    * @param password credentials to use to do the encryption
    * @return the resultant encrypted file
    * @throws EncryptedFileReaderWriterException
    */
   public static File makeBackupAESEncryptedCopyAndDeleteOriginal(File fileToEncrypt, String password, byte[] encryptedBackupAESKey) throws EncryptedFileReaderWriterException {
     Preconditions.checkNotNull(fileToEncrypt);
     Preconditions.checkNotNull(password);
     Preconditions.checkNotNull(encryptedBackupAESKey);
     try {
       // Decrypt the backup AES key stored in the wallet summary
       KeyParameter walletPasswordDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(password.getBytes(Charsets.UTF_8), WalletManager.SCRYPT_SALT);
       byte[] backupAESKeyBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(encryptedBackupAESKey, walletPasswordDerivedAESKey, WalletManager.AES_INITIALISATION_VECTOR);
       KeyParameter backupAESKey = new KeyParameter(backupAESKeyBytes);

       return encryptAndDeleteOriginal(fileToEncrypt, backupAESKey, WalletManager.AES_INITIALISATION_VECTOR);
     } catch (Exception e) {
       throw new EncryptedFileReaderWriterException("Could not decrypt backup AES key", e);
     }
   }


  /**
   * Encrypt the file specified using an AES key derived from the supplied credentials
   * @param fileToEncrypt file to encrypt
   * @param password credentials to use to do the encryption
   * @return the resultant encrypted file
   * @throws EncryptedFileReaderWriterException
   */
  public static File makeAESEncryptedCopyAndDeleteOriginal(File fileToEncrypt, CharSequence password) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(fileToEncrypt);
    Preconditions.checkNotNull(password);

    KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.SCRYPT_SALT));
    KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);
    return encryptAndDeleteOriginal(fileToEncrypt, keyParameter, WalletManager.AES_INITIALISATION_VECTOR);
  }

  private static File encryptAndDeleteOriginal(File fileToEncrypt, KeyParameter keyParameter, byte[] initialisationVector) throws EncryptedFileReaderWriterException {
    try {
      // Read in the file
      byte[] unencryptedBytes = Files.toByteArray(fileToEncrypt);

      // Create an AES encoded version of the fileToEncrypt, using the KeyParameter supplied
      byte[] encryptedBytes = AESUtils.encrypt(unencryptedBytes, keyParameter, initialisationVector);

      //log.debug("Encrypted wallet bytes (original):\n" + Utils.HEX.encode(encryptedBytes));

      // Check that the encryption is reversible
      byte[] rebornBytes = AESUtils.decrypt(encryptedBytes, keyParameter, initialisationVector);

      if (Arrays.equals(unencryptedBytes, rebornBytes)) {
        // Save encrypted bytes
        File encryptedFilename = new File(fileToEncrypt.getAbsoluteFile() + WalletManager.MBHD_AES_SUFFIX);
        ByteArrayInputStream encryptedWalletByteArrayInputStream = new ByteArrayInputStream(encryptedBytes);
        FileOutputStream encryptedWalletOutputStream = new FileOutputStream(encryptedFilename);
        ByteStreams.copy(encryptedWalletByteArrayInputStream, encryptedWalletOutputStream);

        if (encryptedFilename.length() == encryptedBytes.length) {
          SecureFiles.secureDelete(fileToEncrypt);
        } else {
          // The saved file isn't the correct size - do not delete the original
          throw new EncryptedFileReaderWriterException("The saved file " + encryptedFilename + " is not the size of the encrypted bytes - not deleting the original file");
        }

        return encryptedFilename;
      } else {
        throw new EncryptedFileReaderWriterException("The file encryption was not reversible. Aborting. This means the file " + fileToEncrypt.getAbsolutePath() +  " is being stored unencrypted");
      }
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot make encrypted copy for file '" + fileToEncrypt.getAbsolutePath() + "'", e);
    }
  }

  public static Protos.ScryptParameters makeScryptParameters(byte[] salt) {
    Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(salt));
    return scryptParametersBuilder.build();
  }
}
