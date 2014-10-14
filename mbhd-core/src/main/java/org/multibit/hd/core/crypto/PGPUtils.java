package org.multibit.hd.core.crypto;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

/**
 * <p>Utility to provide the following to CORE API:</p>
 * <ul>
 * <li>Generation of PGP private and public keys from a WalletId</li>
 * </ul>
 * <p>Derived from <code>org.bouncycastle.openpgp.examples</code> by seamans</p>
 *
 * @since 0.0.1
 */
public class PGPUtils {

  public static final int PGP_KEY_SIZE = 4096;

  /**
   * Utilities have private constructors
   */
  private PGPUtils() {
  }

  /**
   * Load a PGP public key from a public keyring or ASCII armored text file
   *
   * @return key the first PGP public key in the found keyring/ ASCII armored text file
   */
  @SuppressWarnings("unchecked")
  public static PGPPublicKey readPublicKey(InputStream in) throws IOException, PGPException {

    in = PGPUtil.getDecoderStream(in);

    PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

    // Loop through the collection until we find a key suitable for encryption
    // (in the real world you would probably want to be a bit smarter about this)
    PGPPublicKey key = null;

    // Iterate through the key rings
    Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

    while (key == null && rIt.hasNext()) {

      PGPPublicKeyRing kRing = rIt.next();
      Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
      while (key == null && kIt.hasNext()) {
        PGPPublicKey k = kIt.next();

        if (k.isEncryptionKey()) {
          key = k;
        }
      }

    }

    if (key == null) {
      throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }

    return key;
  }

  /**
   * Load a secret key ring collection from keyIn and find the secret key corresponding to
   * keyID if it exists.
   *
   * @param keyIn input stream representing a key ring collection.
   * @param keyID keyID we want.
   * @param pass  passphrase to decrypt secret key with.
   * @return The PGPPrivate key matching the keyID
   * @throws java.io.IOException
   * @throws org.bouncycastle.openpgp.PGPException
   * @throws java.security.NoSuchProviderException
   */
  public static PGPPrivateKey findPrivateKey(InputStream keyIn, long keyID, char[] pass)
          throws IOException, PGPException, NoSuchProviderException {

    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
            PGPUtil.getDecoderStream(keyIn));

    PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

    if (pgpSecKey == null) {
      return null;
    }

    return pgpSecKey.extractPrivateKey(pass, "BC");
  }

  /**
   * Decrypt the passed in message stream
   *
   * @param encryptedInputStream  The input stream
   * @param decryptedOutputStream The output stream
   * @param keyInputStream        The key input stream
   * @param password              The credentials
   * @throws Exception TODO This is too general (many exceptions wrapped up into one)
   */
  @SuppressWarnings("unchecked")
  public static void decryptFile(InputStream encryptedInputStream, OutputStream decryptedOutputStream, InputStream keyInputStream, char[] password)
          throws Exception {

    Security.addProvider(new BouncyCastleProvider());

    encryptedInputStream = PGPUtil.getDecoderStream(encryptedInputStream);

    final PGPObjectFactory pgpFactory = new PGPObjectFactory(encryptedInputStream);

    final PGPEncryptedDataList enc;

    final Object o = pgpFactory.nextObject();

    // The first object might be a PGP marker packet.
    if (o instanceof PGPEncryptedDataList) {
      enc = (PGPEncryptedDataList) o;
    } else {
      enc = (PGPEncryptedDataList) pgpFactory.nextObject();
    }

    // Find the private key matching the public key in the secret key ring
    final Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
    PGPPrivateKey privateKey = null;
    PGPPublicKeyEncryptedData pbe = null;

    while (privateKey == null && it.hasNext()) {
      pbe = it.next();

      privateKey = findPrivateKey(keyInputStream, pbe.getKeyID(), password);
    }

    if (privateKey == null) {
      throw new IllegalArgumentException("Secret key for message not found.");
    }

    final InputStream clear = pbe.getDataStream(privateKey, "BC");

    final PGPObjectFactory plainFact = new PGPObjectFactory(clear);

    Object message = plainFact.nextObject();

    if (message instanceof PGPCompressedData) {
      PGPCompressedData cData = (PGPCompressedData) message;
      PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream());

      message = pgpFact.nextObject();
    }

    if (message instanceof PGPLiteralData) {
      PGPLiteralData ld = (PGPLiteralData) message;

      InputStream unc = ld.getInputStream();
      int ch;

      while ((ch = unc.read()) >= 0) {
        decryptedOutputStream.write(ch);
      }
    } else if (message instanceof PGPOnePassSignatureList) {
      throw new PGPException("Encrypted message contains a signed message - not literal data.");
    } else {
      throw new PGPException("Message is not a simple encrypted file - type unknown.");
    }

    if (pbe.isIntegrityProtected()) {
      if (!pbe.verify()) {
        throw new PGPException("Message failed integrity check");
      }
    }
  }

  /**
   * <p>Encrypt a file</p>
   *
   * @param armoredOut The output stream
   * @param inputFile  The input file
   * @param encKey     The PGP public key for encrypting
   * @throws java.io.IOException
   * @throws java.security.NoSuchProviderException
   * @throws org.bouncycastle.openpgp.PGPException
   */
  public static void encryptFile(OutputStream armoredOut,
                                 File inputFile,
                                 PGPPublicKey encKey)
          throws IOException, NoSuchProviderException, PGPException {

    Security.addProvider(new BouncyCastleProvider());

    // Armored output
    armoredOut = new ArmoredOutputStream(armoredOut);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    final PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

    PGPUtil.writeFileToLiteralData(
            comData.open(baos),
            PGPLiteralData.BINARY,
            inputFile
    );

    comData.close();

    final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
            PGPEncryptedData.CAST5,
            // Always perform an integrity check
            true,
            new SecureRandom(),
            "BC"
    );

    encryptedDataGenerator.addMethod(encKey);

    byte[] bytes = baos.toByteArray();

    OutputStream os = encryptedDataGenerator.open(armoredOut, bytes.length);

    os.write(bytes);

    os.close();

    armoredOut.close();
  }

  /**
   * @param email    email to include in PGP key eg. herp@derp.com
   * @param password credentials to use in generating the private key
   * @return PGPSecretKeyRing containing a PGP key and subkey.
   */

  public static PGPSecretKeyRing createKey(String email, char[] password) throws PGPException, NoSuchProviderException, NoSuchAlgorithmException {
//    Security.addProvider(new BouncyCastleProvider());
//
//    PGPKeyPair masterSigningKeyPair;
//    PGPKeyPair encryptionSubkeyPair;
//    PGPKeyPair secretKey;
//    PGPSecretKeyRing secretKeyRing;
//
//    //KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
//    //keyPairGenerator.initialize(PGP_KEY_SIZE);
//
//      // This object generates individual key-pairs.
//    RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
//
//    // Boilerplate RSA parameters, no need to change anything
//    // except for the RSA key-size (2048). You can use whatever
//    // key-size makes sense for you -- 4096, etc.
//    keyPairGenerator.init
//            (new RSAKeyGenerationParameters
//                    (BigInteger.valueOf(0x10001),
//                            new SecureRandom(), PGP_KEY_SIZE, 12));
//
//
//    masterSigningKeyPair = keyPairGenerator.generateKeyPair();
//    encryptionSubkeyPair = keyPairGenerator.generateKeyPair();
//
//    // Add a self-signature on the id
//    PGPSignatureSubpacketGenerator signHashGenerator =
//            new PGPSignatureSubpacketGenerator();
//
//    // Add signed metadata on the signature.
//    // 1) Declare its purpose
//    signHashGenerator.setKeyFlags
//            (false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
//    //signHashGenerator.setKeyFlags(true, KeyFlags.CERTIFY_OTHER | KeyFlags.SIGN_DATA
//    //        | KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
//
//    // 2) Set preferences for secondary crypto algorithms to use
//    //    when sending messages to this key.
//    signHashGenerator.setPreferredSymmetricAlgorithms
//            (false, new int[]{
//                    SymmetricKeyAlgorithmTags.AES_256,
//                    SymmetricKeyAlgorithmTags.AES_192,
//                    SymmetricKeyAlgorithmTags.AES_128
//            });
//    signHashGenerator.setPreferredHashAlgorithms
//            (false, new int[]{
//                    HashAlgorithmTags.SHA256,
//                    HashAlgorithmTags.SHA1,
//                    HashAlgorithmTags.SHA384,
//                    HashAlgorithmTags.SHA512,
//                    HashAlgorithmTags.SHA224,
//            });
//
//    secretKey = new PGPKeyPair(
//            PGPPublicKey.RSA_SIGN,
//            (java.security.KeyPair)masterSigningKeyPair,
//            new Date());
//    PGPKeyPair secretKey2 = new PGPKeyPair(
//            PGPPublicKey.RSA_ENCRYPT,
//            encryptionSubkeyPair,
//            new Date());
//
//    PGPKeyRingGenerator keyRingGen = new
//            PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, secretKey,
//            email + "<" + email + ">", PGPEncryptedData.AES_256, credentials, true,
//            signHashGenerator.generate(), null, new SecureRandom(), "BC");
//
//    keyRingGen.addSubKey(secretKey2);
//    secretKeyRing = keyRingGen.generateSecretKeyRing();
//
//    return secretKeyRing;

    return null;
  }

//  public static void main(String args[])
//          throws Exception {
//    char pass[] = {'h', 'e', 'l', 'l', 'o'};
//    PGPKeyRingGenerator krgen = generateKeyRingGenerator
//            ("alice@example.com", pass);
//
//    // Generate public key ring, dump to file.
//    PGPPublicKeyRing pkr = krgen.generatePublicKeyRing();
//    BufferedOutputStream pubout = new BufferedOutputStream
//            (new FileOutputStream("dummy.pkr"));
//    pkr.encode(pubout);
//    pubout.close();
//
//    // Generate private key, dump to file.
//    PGPSecretKeyRing skr = krgen.generateSecretKeyRing();
//    BufferedOutputStream secout = new BufferedOutputStream
//            (new FileOutputStream("dummy.skr"));
//    skr.encode(secout);
//    secout.close();
//  }

//  public static PGPKeyRingGenerator generateKeyRingGenerator
//          (String id, char[] pass)
//          throws Exception {
//    return generateKeyRingGenerator(id, pass, 0xc0);
//  }

  // Note: s2kcount is a number between 0 and 0xff that controls the
  // number of times to iterate the credentials hash before use. More
  // iterations are useful against offline attacks, as it takes more
  // time to check each credentials. The actual number of iterations is
  // rather complex, and also depends on the hash function in use.
  // Refer to Section 3.7.1.3 in rfc4880.txt. Bigger numbers give
  // you more iterations.  As a rough rule of thumb, when using
  // SHA256 as the hashing function, 0x10 gives you about 64
  // iterations, 0x20 about 128, 0x30 about 256 and so on till 0xf0,
  // or about 1 million iterations. The maximum you can go to is
  // 0xff, or about 2 million iterations.  I'll use 0xc0 as a
  // default -- about 130,000 iterations.

//  public static PGPKeyRingGenerator generateKeyRingGenerator
//          (String id, char[] pass, int s2kcount)
//          throws Exception {
//    // This object generates individual key-pairs.
//    RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
//
//    // Boilerplate RSA parameters, no need to change anything
//    // except for the RSA key-size (2048). You can use whatever
//    // key-size makes sense for you -- 4096, etc.
//    keyPairGenerator.init
//            (new RSAKeyGenerationParameters
//                    (BigInteger.valueOf(0x10001),
//                            new SecureRandom(), PGP_KEY_SIZE, 12));
//
//    // First create the master (signing) key with the generator.
//    PGPKeyPair rsakp_sign =
//            new BcPGPKeyPair
//                    (PGPPublicKey.RSA_SIGN, keyPairGenerator.generateKeyPair(), new Date());
//    // Then an encryption subkey.
//    PGPKeyPair rsakp_enc =
//            new BcPGPKeyPair
//                    (PGPPublicKey.RSA_ENCRYPT, keyPairGenerator.generateKeyPair(), new Date());
//
//    // Add a self-signature on the id
//    PGPSignatureSubpacketGenerator signhashgen =
//            new PGPSignatureSubpacketGenerator();
//
//    // Add signed metadata on the signature.
//    // 1) Declare its purpose
//    signhashgen.setKeyFlags
//            (false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
//    // 2) Set preferences for secondary crypto algorithms to use
//    //    when sending messages to this key.
//    signhashgen.setPreferredSymmetricAlgorithms
//            (false, new int[]{
//                    SymmetricKeyAlgorithmTags.AES_256,
//                    SymmetricKeyAlgorithmTags.AES_192,
//                    SymmetricKeyAlgorithmTags.AES_128
//            });
//    signhashgen.setPreferredHashAlgorithms
//            (false, new int[]{
//                    HashAlgorithmTags.SHA256,
//                    HashAlgorithmTags.SHA1,
//                    HashAlgorithmTags.SHA384,
//                    HashAlgorithmTags.SHA512,
//                    HashAlgorithmTags.SHA224,
//            });
//    // 3) Request senders add additional checksums to the
//    //    message (useful when verifying unsigned messages.)
//    //signhashgen.setFeature
//    //        (false, Features.FEATURE_MODIFICATION_DETECTION);
//
//    // Create a signature on the encryption subkey.
//    PGPSignatureSubpacketGenerator enchashgen =
//            new PGPSignatureSubpacketGenerator();
//    // Add metadata to declare its purpose
//    enchashgen.setKeyFlags
//            (false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
//
//    // Objects used to encrypt the secret key.
//    PGPDigestCalculator sha1Calc =
//            new BcPGPDigestCalculatorProvider()
//                    .get(HashAlgorithmTags.SHA1);
//    PGPDigestCalculator sha256Calc =
//            new BcPGPDigestCalculatorProvider()
//                    .get(HashAlgorithmTags.SHA256);
//
//    // bcpg 1.48 exposes this API that includes s2kcount. Earlier
//    // versions use a default of 0x60.
//    PBESecretKeyEncryptor pske =
//            (new BcPBESecretKeyEncryptorBuilder
//                    (PGPEncryptedData.AES_256, sha256Calc, s2kcount))
//                    .build(pass);
//
//    // Finally, create the keyring itself. The constructor
//    // takes parameters that allow it to generate the self
//    // signature.
//    PGPKeyRingGenerator keyRingGen =
//            new PGPKeyRingGenerator
//                    (PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
//                            id, sha1Calc, signhashgen.generate(), null,
//                            new BcPGPContentSignerBuilder
//                                    (rsakp_sign.getPublicKey().getAlgorithm(),
//                                            HashAlgorithmTags.SHA1),
//                            pske
//                    );
//
//    // Add our encryption subkey, together with its signature.
//    keyRingGen.addSubKey
//            (rsakp_enc, enchashgen.generate(), null);
//    return keyRingGen;
//  }
}