package org.multibit.hd.brit.crypto;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.*;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.bc.BcPGPObjectFactory;
import org.spongycastle.openpgp.bc.BcPGPPublicKeyRingCollection;
import org.spongycastle.openpgp.bc.BcPGPSecretKeyRingCollection;
import org.spongycastle.openpgp.operator.*;
import org.spongycastle.openpgp.operator.bc.*;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Iterator;

/**
 * <p>Utility to provide the following to BRIT API:</p>
 * <ul>
 * <li>Access to PGP crypto functions in Bouncy Castle</li>
 * </ul>
 * <p>Derived from <code>org.bouncycastle.openpgp.examples</code> by seamans</p>
 *
 * @since 0.0.1
 */
public class PGPUtils {

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

    // Use BC public key ring collection for backwards compatibility
    PGPPublicKeyRingCollection pgpPub = new BcPGPPublicKeyRingCollection(in);

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
   *
   * @return The PGPPrivate key matching the keyID
   *
   * @throws IOException If the input stream has a problem
   * @throws PGPException If the data format is incorrect
   * @throws NoSuchProviderException If the digest provider is not available
   */
  public static PGPPrivateKey findPrivateKey(InputStream keyIn, long keyID, char[] pass)
    throws IOException, PGPException, NoSuchProviderException {

    // Open the PGP secret key ring
    PGPSecretKeyRingCollection pgpSec = new BcPGPSecretKeyRingCollection(
      PGPUtil.getDecoderStream(keyIn)
    );

    // Load the required PGP secret key
    PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

    // Use the BC digest calculator for backwards compatibility
    PGPDigestCalculatorProvider calcProvider = new BcPGPDigestCalculatorProvider();

    // Use the BC secret key decryptor for backwards compatibility
    PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(calcProvider)
      .build(pass);

    return pgpSecKey.extractPrivateKey(decryptor);

  }

  /**
   * Decrypt the passed in message stream
   *
   * @param encryptedInputStream  The input stream
   * @param decryptedOutputStream The output stream
   * @param keyInputStream        The key input stream
   * @param password              The credentials
   *
   * @throws IOException If the input stream has a problem
   * @throws PGPException If the data format is incorrect
   * @throws NoSuchProviderException If the digest provider is not available
   */
  @SuppressWarnings("unchecked")
  public static void decryptFile(
    InputStream encryptedInputStream,
    OutputStream decryptedOutputStream,
    InputStream keyInputStream,
    char[] password
  ) throws IOException, PGPException, NoSuchProviderException {

    Security.addProvider(new BouncyCastleProvider());

    encryptedInputStream = PGPUtil.getDecoderStream(encryptedInputStream);

    // Use the BC PGP Object factory for backwards compatibility
    final PGPObjectFactory pgpFactory = new BcPGPObjectFactory(encryptedInputStream);

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

    // Use a BC public key data decryptor factory for backwards compatibility
    PublicKeyDataDecryptorFactory dataDecryptorFactory = new BcPublicKeyDataDecryptorFactory(privateKey);
    final InputStream clear = pbe.getDataStream(dataDecryptorFactory);

    final PGPObjectFactory plainFactory = new BcPGPObjectFactory(clear);

    Object message = plainFactory.nextObject();

    if (message instanceof PGPCompressedData) {
      PGPCompressedData cData = (PGPCompressedData) message;
      PGPObjectFactory pgpFact = new BcPGPObjectFactory(cData.getDataStream());

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
   *
   * @throws IOException
   * @throws NoSuchProviderException
   * @throws PGPException
   */

  public static void encryptFile(
    OutputStream armoredOut,
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

    PGPDataEncryptorBuilder builder = new BcPGPDataEncryptorBuilder(PGPEncryptedData.CAST5);
    final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(builder);


    // Add method
    PGPKeyEncryptionMethodGenerator method = new BcPublicKeyKeyEncryptionMethodGenerator(encKey);
    encryptedDataGenerator.addMethod(method);

    byte[] bytes = baos.toByteArray();

    OutputStream os = encryptedDataGenerator.open(armoredOut, bytes.length);

    os.write(bytes);

    os.close();

    armoredOut.close();
  }


}