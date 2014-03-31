package org.multibit.hd.brit.crypto;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

/**
 *  <p>Utility to provide the following to BRIT API:</p>
 *  <ul>
 *  <li>Access to PGP crypto functions in Bouncy Castle</li>
 *  </ul>
 * <p>Derived from <code>org.bouncycastle.openpgp.examples</code> by seamans</p>
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

    in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);

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
   *
   * @return The PGPPrivate key matching the keyID
   *
   * @throws IOException
   * @throws PGPException
   * @throws NoSuchProviderException
   */
  public static PGPPrivateKey findSecretKey(InputStream keyIn, long keyID, char[] pass)
    throws IOException, PGPException, NoSuchProviderException {

    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
      org.bouncycastle.openpgp.PGPUtil.getDecoderStream(keyIn));

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
   * @param password              The password
   */
  @SuppressWarnings("unchecked")
  public static void decryptFile(InputStream encryptedInputStream, OutputStream decryptedOutputStream, InputStream keyInputStream, char[] password)
    throws Exception {

    Security.addProvider(new BouncyCastleProvider());

    encryptedInputStream = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(encryptedInputStream);

    PGPObjectFactory pgpF = new PGPObjectFactory(encryptedInputStream);
    PGPEncryptedDataList enc;

    Object o = pgpF.nextObject();

    // The first object might be a PGP marker packet.
    if (o instanceof PGPEncryptedDataList) {
      enc = (PGPEncryptedDataList) o;
    } else {
      enc = (PGPEncryptedDataList) pgpF.nextObject();
    }

    // Find the secret key
    Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
    PGPPrivateKey sKey = null;
    PGPPublicKeyEncryptedData pbe = null;

    while (sKey == null && it.hasNext()) {
      pbe = it.next();

      sKey = findSecretKey(keyInputStream, pbe.getKeyID(), password);
    }

    if (sKey == null) {
      throw new IllegalArgumentException("Secret key for message not found.");
    }

    InputStream clear = pbe.getDataStream(sKey, "BC");

    PGPObjectFactory plainFact = new PGPObjectFactory(clear);

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
   * @param out       The output stream
   * @param inputFile The input file
   * @param encKey    The PGP public key for encrypting
   *
   * @throws IOException
   * @throws NoSuchProviderException
   * @throws PGPException
   */
  public static void encryptFile(OutputStream out,
                                 File inputFile,
                                 PGPPublicKey encKey)
    throws IOException, NoSuchProviderException, PGPException {

    Security.addProvider(new BouncyCastleProvider());

    // Armor output
    out = new ArmoredOutputStream(out);

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

    PGPUtil.writeFileToLiteralData(
      comData.open(bOut),
      PGPLiteralData.BINARY,
      inputFile
    );

    comData.close();

    // Always perform an integrity check
    PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
      PGPEncryptedData.CAST5,
      true,
      new SecureRandom(),
      "BC"
    );

    cPk.addMethod(encKey);

    byte[] bytes = bOut.toByteArray();

    OutputStream cOut = cPk.open(out, bytes.length);

    cOut.write(bytes);

    cOut.close();

    out.close();
  }


}