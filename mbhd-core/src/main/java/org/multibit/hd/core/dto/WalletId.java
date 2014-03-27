package org.multibit.hd.core.dto;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.crypto.KeyCrypterScrypt;
import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import org.bitcoinj.wallet.Protos;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.managers.WalletManager;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.math.ec.ECPoint;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *  <p>Data object to provide the following to wallet seed related classes<br>
 *  <ul>
 *  <li>Creation of wallet id from seed</li>
 *  <li>Creation of wallet directory name from wallet id</li>
 *  <li>Roundtripping of the above</li>
 *  </ul>
 *  </p>
 *  
 */
public class WalletId {

  //private static final Logger log = LoggerFactory.getLogger(WalletId.class);

  public static final int SEPARATOR_REPEAT_PERIOD = 4;
  public static final String SEPARATOR = "-";

  private static final byte SALT_USED_IN_SCRYPT = (byte) 1;

  private static final int NUMBER_OF_BYTES_IN_WALLET_ID = 20;
  public static final int LENGTH_OF_FORMATTED_WALLETID = 2 * NUMBER_OF_BYTES_IN_WALLET_ID + (NUMBER_OF_BYTES_IN_WALLET_ID / SEPARATOR_REPEAT_PERIOD) - 1;

  private final byte[] walletId;

  /**
   * Create a wallet id from a formatted wallet id
   *
   * @param formattedWalletId The formatted wallet id you want to use e.g. 66666666-77777777-88888888-99999999-aaaaaaaa
   */
  public WalletId(String formattedWalletId) {
    Preconditions.checkState(formattedWalletId.length() == LENGTH_OF_FORMATTED_WALLETID);

     // remove any embedded hyphens
    formattedWalletId = formattedWalletId.replaceAll("-","");

    walletId = Utils.parseAsHexOrBase58(formattedWalletId);
  }

  /**
   * Create a wallet id from the given seed.
   * This produces a wallet id from the seed using various trapdoor functions.
   * The seed is typically generated from the SeedPhraseGenerator#convertToSeed method.
   *
   * @param seed The seed to use in deriving the wallet id
   */
  public WalletId(byte[] seed) {
    Preconditions.checkNotNull(seed);
    Preconditions.checkState(seed.length == SeedPhraseGenerator.EXPECTED_SEED_LENGTH_IN_BYTES);

    //log.debug("seed ='" + Utils.bytesToHexString(seed) + "'");

    BigInteger seedBigInteger = new BigInteger(1, seed);
    //log.debug("seedBigInteger ='" + seedBigInteger.toString() + "'");


    // Convert the seed to a wallet id using various trapdoor functions.

    // Scrypt - scrypt is run using the seedBigInteger.toString() as the 'password'.
    // This returns a byte array (normally used as an AES256 key but here passed on to more trapdoor functions).
    // The scrypt parameters used are the default, with a salt of '1'.
    Protos.ScryptParameters.Builder scryptParametersBuilder  = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(new byte[] {SALT_USED_IN_SCRYPT}));
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
    walletId = Utils.sha256hash160(publicKey);

    //log.debug("walletId ='" + Utils.bytesToHexString(walletId) + "'");
  }

  /**
   * Create a WalletId from a wallet filename - the filename is parsed into a walletId byte array
   * The wallet filename should be the whole fire name e.g /herp/derp/mbhd-23bb865e-161bfefc-3020c418-66bf6f75-7fecdfcc/mbhd.wallet
   * @return WalletId
   */
  public static WalletId parseWalletFilename(String walletFilename) {
    File walletFile = new File(walletFilename);

    // Get the parent directory, in which the wallet id is embedded
    File walletRoot = walletFile.getParentFile();
    String walletRootName = walletRoot.getName();

    // Remove the prefix mbhd
    String prefix = WalletManager.WALLET_DIRECTORY_PREFIX + WalletManager.SEPARATOR;
    if (walletRootName.startsWith(prefix)) {
      walletRootName = walletRootName.replace(prefix, "");

      return new WalletId(walletRootName);

    } else {
      throw new IllegalStateException("Cannot parse '" + walletFilename + "' into a WalletId. Does not start with '" + prefix + "'");
    }
  }

  /**
   * @return the raw wallet id as a byte[]
   */
  public byte[] getBytes() {
    return walletId;
  }

  /**
   * @return the wallet id as a formatted string
   */
  public String toFormattedString() {

    StringBuilder buffer = new StringBuilder();

    for (int i=0; i< walletId.length; i++) {
      buffer.append(Utils.bytesToHexString(new byte[]{walletId[i]}));

      if (((i + 1) % SEPARATOR_REPEAT_PERIOD == 0) && !(i == walletId.length - 1)) {
        buffer.append(SEPARATOR);
      }
    }
    return buffer.toString();
  }

  @Override
  public String toString() {
    return toFormattedString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WalletId walletId1 = (WalletId) o;

    if (!Arrays.equals(walletId, walletId1.walletId)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(walletId);
  }

}
