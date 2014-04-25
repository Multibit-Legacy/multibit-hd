package org.multibit.hd.core.crypto;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import org.bitcoinj.wallet.Protos;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Utility class to provide the following to BRIT API:</p>
 * <ul>
 * <li>Encryption and decryption using AES</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class AESUtils {

  private static final Logger log = LoggerFactory.getLogger(WalletId.class);

  // The salt used in derivation of the cloud backup AES key
  // This value is the 35,000,000th prime (http://primes.utm.edu/lists/small/millions/) which seemed like a nice number to use.
  public static final byte[] BACKUP_AES_KEY_SALT_USED_IN_SCRYPT = BigInteger.valueOf(573_259_433).toByteArray();

  /**
   * Utilities have private constructors
   */
  private AESUtils() {
  }

  /**
   * Generate 160 bits of entropy from the seed bytes.
   * This uses a number of trapdoor functions and is tweakable by specifying a custom salt value
   *
   * @param seed seed bytes to use as 'password'/ initial value
   * @param salt salt value used to customise trapdoor functions
   * @return entropy 20 bytes of entropy
   */
  public static byte[] generate160BitsOfEntropy(byte[] seed, byte[] salt) {
    Preconditions.checkNotNull(seed);
    Preconditions.checkState(seed.length == SeedPhraseGenerator.EXPECTED_SEED_LENGTH_IN_BYTES);
    Preconditions.checkNotNull(salt);

    log.debug("seed ='" + Utils.bytesToHexString(seed) + "'");

    BigInteger seedBigInteger = new BigInteger(1, seed);
    //log.debug("seedBigInteger ='" + seedBigInteger.toString() + "'");

    // Convert the seed to entropy using various trapdoor functions.

    // Scrypt - scrypt is run using the seedBigInteger.toString() as the 'password'.
    // This returns a byte array (normally used as an AES256 key but here passed on to more trapdoor functions).
    // The scrypt parameters used are the default, with a salt of '1'.
    Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(salt));
    Protos.ScryptParameters scryptParameters = scryptParametersBuilder.build();
    KeyCrypterScrypt keyCrypterScrypt = new KeyCrypterScrypt(scryptParameters);
    KeyParameter keyParameter = keyCrypterScrypt.deriveKey(seedBigInteger.toString());
    byte[] derivedKey = keyParameter.getKey();
    //log.debug("derivedKey ='" + Utils.bytesToHexString(derivedKey) +  "'");

    // Ensure that the seed is within the Bitcoin EC group.
    X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    BigInteger sizeOfGroup = params.getN();

    BigInteger derivedKeyBigInteger = new BigInteger(1, derivedKey);

    //log.debug("derivedKeyBigInteger (before) ='" + derivedKeyBigInteger +  "'");
    derivedKeyBigInteger = derivedKeyBigInteger.mod(sizeOfGroup);
    //log.debug("derivedKeyBigInteger (after) ='" + derivedKeyBigInteger +  "'");

    // EC curve generator function used to convert the key just derived (a 'private key') to a 'public key'
    ECPoint point = ECKey.CURVE.getG().multiply(derivedKeyBigInteger);
    // Note the public key is not compressed
    byte[] publicKey = point.getEncoded();
    //log.debug("publicKey ='" + Utils.bytesToHexString(publicKey) +  "'");

    // SHA256RIPE160 to generate final walletId bytes from the 'public key'
    byte[] entropy = Utils.sha256hash160(publicKey);

    log.debug("entropy ='" + Utils.bytesToHexString(entropy) + "'");

    return entropy;
  }

  /**
   * Create an AES 256 key given 20 bytes of entropy (e.g. a walletId) and a salt byte array
   * @param seed 20 bytes of entropy, typically a wallet id
   * @param salt bytes, used as salt
   * @return a KeyParameter suitable for AES encryption and decryption
   * @throws NoSuchAlgorithmException
   */
  public static KeyParameter createAESKey(byte[] seed, byte[] salt) throws NoSuchAlgorithmException {
    Preconditions.checkNotNull(seed);
    Preconditions.checkState(seed.length == SeedPhraseGenerator.EXPECTED_SEED_LENGTH_IN_BYTES);
    Preconditions.checkNotNull(salt);

    byte[] entropy = generate160BitsOfEntropy(seed, salt);

    // Stretch the 20 byte entropy to 32 bytes (256 bits) using SHA256
    byte[] stretchedEntropy = MessageDigest.getInstance("SHA-256").digest(entropy);
    log.debug("stretchedEntropy ='" + Utils.bytesToHexString(stretchedEntropy) + "'");

    return new KeyParameter(stretchedEntropy);
  }
}
