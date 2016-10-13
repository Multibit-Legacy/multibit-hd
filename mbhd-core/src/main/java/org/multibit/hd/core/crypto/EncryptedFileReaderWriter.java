package org.multibit.hd.core.crypto;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.Protos;
import org.multibit.commons.crypto.AESUtils;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.exceptions.EncryptedFileReaderWriterException;
import org.multibit.commons.files.SecureFiles;
import org.multibit.hd.core.files.EncryptedFileListItem;
import org.multibit.hd.core.managers.WalletManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

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
 */
public class EncryptedFileReaderWriter {
  private static final Logger log = LoggerFactory.getLogger(EncryptedFileReaderWriter.class);

  private static final String TEMPORARY_FILE_EXTENSION = ".tmp";
  private static final String OLD_FILE_EXTENSION = ".old";
  private static final String NEW_FILE_EXTENSION = ".new";

  /**
   * Decrypt an AES encrypted file and return it as an inputStream
   */
  public static ByteArrayInputStream readAndDecrypt(EncryptedFileListItem encryptedProtobufFile, CharSequence password) throws EncryptedFileReaderWriterException {
    return new ByteArrayInputStream(readAndDecryptToByteArray(encryptedProtobufFile, password));
  }

  /**
   * Decrypt an AES encrypted file and return it as a byte array
   */
  public static byte[] readAndDecryptToByteArray(EncryptedFileListItem encryptedProtobufFile, CharSequence password) throws EncryptedFileReaderWriterException {
      Preconditions.checkNotNull(encryptedProtobufFile);
      Preconditions.checkNotNull(password);
      try {
          // Read the encrypted file in and decrypt it.
          log.debug("Encrypted file is of size {} bytes", encryptedProtobufFile.length());
          byte[] fileBytes = Files.toByteArray(encryptedProtobufFile);
          byte[] ivBytes = Arrays.copyOfRange(fileBytes, 0, 16);
          byte[] encryptedWalletBytes = Arrays.copyOfRange(fileBytes, 16, fileBytes.length);

          KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.scryptSalt()));
          KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);
          byte [] decryptedBytes = AESUtils.decrypt(encryptedWalletBytes,keyParameter,ivBytes);
          InputStream inputStream = new ByteArrayInputStream(decryptedBytes);
          if(!encryptedProtobufFile.isValidDecryption(inputStream)){
              decryptedBytes = AESUtils.decrypt(fileBytes,keyParameter,WalletManager.aesInitialisationVector());
          }
          // Decrypt the file bytes
          return decryptedBytes;
      } catch (Exception e) {

          throw new EncryptedFileReaderWriterException("Cannot read and decrypt the file '" + encryptedProtobufFile.getAbsolutePath() + "'", e);
      }
  }

  /**
   * Encrypt a byte array and output to a file, using an intermediate temporary file
   */
  public static void encryptAndWrite(byte[] unencryptedBytes, CharSequence password, File outputFile) throws EncryptedFileReaderWriterException {
    try {
      byte[] encryptedBytes = encrypt(unencryptedBytes, password);

      ByteArrayInputStream encryptedWalletByteArrayInputStream = new ByteArrayInputStream(encryptedBytes);
      File temporaryFile = new File(outputFile.getAbsolutePath() + TEMPORARY_FILE_EXTENSION);
      SecureFiles.writeFile(encryptedWalletByteArrayInputStream, temporaryFile, outputFile);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot encryptAndWrite", e);
    }
  }

  /**
   * Encrypt a byte array and output directly to a file
   */
  public static void encryptAndWriteDirect(byte[] unencryptedBytes, CharSequence password, File outputFile) throws EncryptedFileReaderWriterException {
    try {
      byte[] encryptedBytes = encrypt(unencryptedBytes, password);

      ByteArrayInputStream encryptedWalletByteArrayInputStream = new ByteArrayInputStream(encryptedBytes);
      SecureFiles.writeFile(encryptedWalletByteArrayInputStream, outputFile);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot encryptAndWriteDirect", e);
    }
  }
  /**
   * Encrypt the file specified using the backup AES key derived from the supplied credentials
   *
   * @param fileToEncrypt file to encrypt
   * @param password      credentials to use to do the encryption
   * @return the resultant encrypted file
   * @throws EncryptedFileReaderWriterException
   */
  public static File makeBackupAESEncryptedCopyAndDeleteOriginal(File fileToEncrypt, String password, WalletSummary walletSummary) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(fileToEncrypt);
    Preconditions.checkNotNull(password);
    Preconditions.checkNotNull(walletSummary.getEncryptedBackupKey());
    Preconditions.checkNotNull(walletSummary.getIntializationVector());
    try {
      // Decrypt the backup AES key stored in the wallet summary
      KeyParameter walletPasswordDerivedAESKey = AESUtils.createAESKey(password.getBytes(Charsets.UTF_8), WalletManager.scryptSalt());
      byte[] backupAESKeyBytes = AESUtils.decrypt(walletSummary.getEncryptedBackupKey(), walletPasswordDerivedAESKey,walletSummary.getIntializationVector());
      KeyParameter backupAESKey = new KeyParameter(backupAESKeyBytes);
      File destinationFile = new File(fileToEncrypt.getAbsoluteFile() + WalletManager.MBHD_AES_SUFFIX);

      return encryptAndDeleteOriginal(fileToEncrypt, destinationFile, backupAESKey);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Could not decrypt backup AES key", e);
    }
  }

  /**
   * Encrypt the file specified using an AES key derived from the supplied credentials
   *
   * @param fileToEncrypt file to encrypt
   * @param password      credentials to use to do the encryption
   * @return the resultant encrypted file
   * @throws EncryptedFileReaderWriterException
   */
  public static File makeAESEncryptedCopyAndDeleteOriginal(File fileToEncrypt, CharSequence password) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(fileToEncrypt);
    Preconditions.checkNotNull(password);

    File destinationFile = new File(fileToEncrypt.getAbsoluteFile() + WalletManager.MBHD_AES_SUFFIX);
    return makeAESEncryptedCopyAndDeleteOriginal(fileToEncrypt, destinationFile, password);
  }

  /**
   * Encrypt the file specified using an AES key derived from the supplied credentials
   *
   * @param fileToEncrypt   file to encrypt
   * @param destinationFile destination file (if not set then fileToEncrypt + .aes
   * @param password        credentials to use to do the encryption
   * @return the resultant encrypted file
   * @throws EncryptedFileReaderWriterException
   */
  public static File makeAESEncryptedCopyAndDeleteOriginal(File fileToEncrypt, File destinationFile, CharSequence password) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(fileToEncrypt);
    Preconditions.checkNotNull(destinationFile);
    Preconditions.checkNotNull(password);

    KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.scryptSalt()));
    KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);
    return encryptAndDeleteOriginal(fileToEncrypt, destinationFile, keyParameter);
  }

  /**
   * Change the encryption on Collection of files.
   * This method is split into two parts:
   * 1) changeEncryptionPrepare - change the encryption on the files, giving them the suffix ".new"
   * 2) changeEncryptionCommit - rename the files
   *
   * @param files       The List of files to change the encryption on
   * @param oldPassword The original password
   * @param newPassword The new password
   * @return newFiles   A list containing the newly encrypted files
   * @throws EncryptedFileReaderWriterException
   */
  public static List<EncryptedFileListItem> changeEncryptionPrepare(List<EncryptedFileListItem> files, CharSequence oldPassword, CharSequence newPassword) throws IOException {
      Preconditions.checkNotNull(files);
      Preconditions.checkNotNull(oldPassword);
      Preconditions.checkNotNull(newPassword);

      // The files are expected to end with ".aes"
      for (EncryptedFileListItem fileToCheck : files) {
          Preconditions.checkState(fileToCheck.getAbsolutePath().endsWith(WalletManager.MBHD_AES_SUFFIX));
      }

      List<EncryptedFileListItem> newFiles = Lists.newArrayList();
      KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.scryptSalt()));
      KeyParameter oldKeyParameter = keyCrypterScrypt.deriveKey(oldPassword);
      KeyParameter newKeyParameter = keyCrypterScrypt.deriveKey(newPassword);

      for (EncryptedFileListItem file : files) {
          log.debug("Processing file\n'{}'", file.getAbsolutePath());
          EncryptedFileListItem newFile = new EncryptedFileListItem(file.getAbsolutePath() + NEW_FILE_EXTENSION) {
              @Override
              public boolean isValidDecryption(InputStream inputStream) throws IOException {
                  return false;
              }
          };
          newFiles.add(newFile);
          if (file.exists()) {
              // Read in the file bytes that are encrypted with the old password
              byte[] oldFileBytes = Files.toByteArray(file);
              byte[] ivBytes = Arrays.copyOfRange(oldFileBytes, 0, 16);
              byte[] encryptedWalletBytes = Arrays.copyOfRange(oldFileBytes, 16, oldFileBytes.length);
              byte[] plainBytes = AESUtils.decrypt(encryptedWalletBytes, oldKeyParameter, ivBytes);
              InputStream byteArrayInputStream= new ByteArrayInputStream(plainBytes);
              if(!file.isValidDecryption(byteArrayInputStream)){
                  plainBytes = AESUtils.decrypt(oldFileBytes,oldKeyParameter,WalletManager.aesInitialisationVector());
              }
              byte[] newEncryptedBytes = encrypt(plainBytes, newKeyParameter);
              // Write out the bytes to a file with the suffix ".new"
              SecureFiles.writeFile(new ByteArrayInputStream(newEncryptedBytes), newFile);
          }
      }
      return newFiles;
  }


  /**
   * Change the encryption on Collection of files.
   * This method is split into two parts:
   * 1) changeEncryptionPrepare - change the encryption on the files, giving them the suffix ".new"
   * 2) changeEncryptionCommit - rename the files to  ".old", rename the ".new" files to the original, secure delete the ".old"
   *
   * @param originalFiles The List of files to change the encryption on
   * @param newFiles      The list of new files, after their encryption has been changed
   * @throws EncryptedFileReaderWriterException
   */
  public static void changeEncryptionCommit(List<EncryptedFileListItem> originalFiles, List<EncryptedFileListItem> newFiles) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(originalFiles);
    Preconditions.checkNotNull(newFiles);
    Preconditions.checkState(originalFiles.size() == newFiles.size());

    // Once all files have been written to the ".new" files, rename the files to have the suffix ".old"
    List<File> oldFiles = Lists.newArrayList();
    for (int index = 0; index < originalFiles.size(); index++) {
       try {
        // Rename the file, giving it the suffix ".old"
        File oldFile = new File(originalFiles.get(index).getAbsolutePath() + OLD_FILE_EXTENSION);
        oldFiles.add(oldFile);
         if (originalFiles.get(index).exists()){
           SecureFiles.rename(originalFiles.get(index), oldFile);
           log.debug("Renamed:\n'{}'\n'{}'", originalFiles.get(index).getAbsolutePath(), oldFile.getAbsolutePath());
         }
       } catch (IOException ioe) {
         throw new EncryptedFileReaderWriterException("Could not rename file " + originalFiles.get(index).getAbsolutePath() + " to " + oldFiles.get(index).getAbsolutePath());
       }
     }

    // Rename all the new files to the original ones passed in
    for (int index = 0; index < originalFiles.size(); index++) {
      try {
        if (newFiles.get(index).exists()) {
          SecureFiles.rename(newFiles.get(index), originalFiles.get(index));
          log.debug("Renamed:\n'{}'\n'{}'", newFiles.get(index).getAbsolutePath(), originalFiles.get(index).getAbsolutePath());
        }
      } catch (IOException ioe) {
        throw new EncryptedFileReaderWriterException("Could not rename file " + newFiles.get(index).getAbsolutePath() + " to " + originalFiles.get(index).getAbsolutePath());
      }
    }

    // Secure delete the old files
    for (File fileToDelete : oldFiles) {
      try {
        if (fileToDelete.exists()) {
          SecureFiles.secureDelete(fileToDelete);
        }
      } catch (IOException ioe) {
        throw new EncryptedFileReaderWriterException("Could not delete file " + fileToDelete);
      }
    }
  }

  public static Protos.ScryptParameters makeScryptParameters(byte[] salt) {
    Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(salt));
    return scryptParametersBuilder.build();
  }


  /**
   * Encrypt a file and delete the original
   * @param fileToEncrypt the file to encrypt
   * @param encryptedFilename the encrypted filename
   * @param keyParameter the KeyParameter used to encrypt the file
   * @return the encrypted file - will be null if no encryption was done
   * @throws EncryptedFileReaderWriterException
   */
  private static synchronized File encryptAndDeleteOriginal(File fileToEncrypt, File encryptedFilename, KeyParameter keyParameter) throws EncryptedFileReaderWriterException {
    Preconditions.checkNotNull(encryptedFilename);
    Preconditions.checkNotNull(keyParameter);
    if (fileToEncrypt == null || !fileToEncrypt.exists()) {
      log.debug("Not encrypting file {} as it does not exist", fileToEncrypt == null ? "null" : fileToEncrypt.getAbsolutePath());
      // Nothing to do
      return null;
    }

    FileOutputStream encryptedWalletOutputStream = null;
    try {
      // Read in the file
      byte[] unencryptedBytes = Files.toByteArray(fileToEncrypt);
      byte[] encryptedBytes = encrypt(unencryptedBytes, keyParameter);

      // Save encrypted bytes
      ByteArrayInputStream encryptedWalletByteArrayInputStream = new ByteArrayInputStream(encryptedBytes);
      encryptedWalletOutputStream = new FileOutputStream(encryptedFilename);
      ByteStreams.copy(encryptedWalletByteArrayInputStream, encryptedWalletOutputStream);
      encryptedWalletOutputStream.flush();

      if (encryptedFilename.length() == encryptedBytes.length) {
        SecureFiles.secureDelete(fileToEncrypt);
      } else {
        // The saved file isn't the correct size - do not delete the original
        throw new EncryptedFileReaderWriterException("The saved file " + encryptedFilename + " is not the size of the encrypted bytes - not deleting the original file");
      }

      return encryptedFilename;

    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot make encrypted copy for file '" + fileToEncrypt.getAbsolutePath() + "'", e);
    } finally {
      if (encryptedWalletOutputStream != null) {
        try {
          encryptedWalletOutputStream.close();
          encryptedWalletOutputStream = null;
        } catch (IOException e) {
          log.error("Cannot close wallet output stream", e);
        }
      }
    }
  }

  /**
   * Encrypt a byte array, returning the encrypted byte array.
   * this method checks the encryption is reversible
   *
   * @param unencryptedBytes the unencrypted bytes you want to encrypt
   * @param keyParameter     the KeyParameter to use
   * @return encryptedBytes the encryptedBytes
   */
  private static byte[] encrypt(byte[] unencryptedBytes, KeyParameter keyParameter) {
    try {
      // Create an AES encoded version of the unencryptedBytes, using the credentials
      byte[] randomIvBytes = generateRandomIv();
      byte[] encryptedBytes = AESUtils.encrypt(unencryptedBytes,keyParameter,randomIvBytes);
      byte[] rebornBytes = AESUtils.decrypt(encryptedBytes,keyParameter,randomIvBytes);
      byte[] fileBytes = appendByteArrays(randomIvBytes,encryptedBytes);
      //filebytes = ivBytes + encryptedBytes
      if (Arrays.equals(unencryptedBytes, rebornBytes)) {
        return fileBytes;
      } else {
        throw new EncryptedFileReaderWriterException("The encryption was not reversible so aborting.");
      }
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot encryptAndWrite", e);
    }
  }

  /**
   * Encrypt a byte array, returning the encrypted byte array.
   * this method checks the encryption is reversible
   *
   * @param unencryptedBytes the unencrypted bytes you want to encrypt
   * @param password         the password to use
   * @return encryptedBytes the encryptedBytes
   */
  private static byte[] encrypt(byte[] unencryptedBytes, CharSequence password) {
    try {
      KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(makeScryptParameters(WalletManager.scryptSalt()));
      KeyParameter keyParameter = keyCrypterScrypt.deriveKey(password);

      return encrypt(unencryptedBytes, keyParameter);
    } catch (Exception e) {
      throw new EncryptedFileReaderWriterException("Cannot encryptAndWrite", e);
    }
  }
  private static byte[] generateRandomIv(){
    SecureRandom secureRandom = new SecureRandom();
    byte[] ivBytes = new byte[16];
    secureRandom.nextBytes(ivBytes);
    return ivBytes;
  }
  private static byte[] appendByteArrays(byte [] firstByteArray,byte [] secondByteArray){
    byte [] resultByteArray = new byte[firstByteArray.length+secondByteArray.length];
    System.arraycopy(firstByteArray, 0, resultByteArray, 0, firstByteArray.length);
    // copy encrypted bytes into end of destination (from pos iv.length,encryptedBytes.length)
    System.arraycopy(secondByteArray, 0, resultByteArray, firstByteArray.length, secondByteArray.length);
    return resultByteArray;
  }
}
