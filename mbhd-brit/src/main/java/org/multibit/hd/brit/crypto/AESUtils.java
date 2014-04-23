package org.multibit.hd.brit.crypto;

import com.google.bitcoin.crypto.KeyCrypterException;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * <p>Utility class to provide the following to BRIT API:</p>
 * <ul>
 * <li>Encryption and decryption using AES</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class AESUtils {

  /**
   * Key length in bytes.
   */
  public static final int KEY_LENGTH = 32; // = 256 bits.

  /**
   * The size of an AES block in bytes.
   * This is also the length of the initialisation vector.
   */
  public static final int BLOCK_LENGTH = 16;  // = 128 bits.

  /**
   * Utilities have private constructors
   */
  private AESUtils() {
  }

  /**
   * Password based encryption using AES - CBC 256 bits.
   *
   * @param plainBytes           The unencrypted bytes for encryption
   * @param aesKey               The AES key to use for encryption
   * @param initialisationVector The initialisationVector to use whilst encrypting
   *
   * @return The encrypted bytes
   */
  public static byte[] encrypt(byte[] plainBytes, KeyParameter aesKey, byte[] initialisationVector) throws KeyCrypterException {

    checkNotNull(plainBytes);
    checkNotNull(aesKey);
    checkNotNull(initialisationVector);
    checkState(initialisationVector.length == BLOCK_LENGTH, "The initialisationVector must be " + BLOCK_LENGTH + " bytes long.");

    try {
      ParametersWithIV keyWithIv = new ParametersWithIV(aesKey, initialisationVector);

      // Encrypt using AES
      BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
      cipher.init(true, keyWithIv);
      byte[] encryptedBytes = new byte[cipher.getOutputSize(plainBytes.length)];
      final int processLength = cipher.processBytes(plainBytes, 0, plainBytes.length, encryptedBytes, 0);
      final int doFinalLength = cipher.doFinal(encryptedBytes, processLength);

      return Arrays.copyOf(encryptedBytes, processLength + doFinalLength);
    } catch (Exception e) {
      throw new KeyCrypterException("Could not encrypt bytes.", e);
    }

  }

  /**
   * Decrypt bytes previously encrypted with this class.
   *
   * @param encryptedBytes       The encrypted bytes required to decrypt
   * @param aesKey               The AES key to use for decryption
   * @param initialisationVector The initialisationVector to use whilst decrypting
   *
   * @return The decrypted bytes
   *
   * @throws KeyCrypterException if bytes could not be decoded to a valid key
   */

  public static byte[] decrypt(byte[] encryptedBytes, KeyParameter aesKey, byte[] initialisationVector) throws KeyCrypterException {

    checkNotNull(encryptedBytes);
    checkNotNull(aesKey);
    checkNotNull(initialisationVector);

    try {
      ParametersWithIV keyWithIv = new ParametersWithIV(new KeyParameter(aesKey.getKey()), initialisationVector);

      // Decrypt the message.
      BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
      cipher.init(false, keyWithIv);

      int minimumSize = cipher.getOutputSize(encryptedBytes.length);
      byte[] outputBuffer = new byte[minimumSize];
      int length1 = cipher.processBytes(encryptedBytes, 0, encryptedBytes.length, outputBuffer, 0);
      int length2 = cipher.doFinal(outputBuffer, length1);
      int actualLength = length1 + length2;

      byte[] decryptedBytes = new byte[actualLength];
      System.arraycopy(outputBuffer, 0, decryptedBytes, 0, actualLength);

      return decryptedBytes;
    } catch (Exception e) {
      throw new KeyCrypterException("Could not decrypt bytes", e);
    }
  }

}
