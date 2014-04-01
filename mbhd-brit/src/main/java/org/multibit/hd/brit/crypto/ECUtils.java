package org.multibit.hd.brit.crypto;

import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;

import java.math.BigInteger;

/**
 * <p>Utility class to provide the following to BRIT API:</p>
 * <ul>
 * <li>Encryption and decryption using AES</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ECUtils {


  /**
   * Utilities have private constructors
   */
  private ECUtils() {
  }

  /**
   * Ensure that the seed is within the range of the bitcoin EC group
   *
   * @param seedAsBigInteger the seed - converted to a BigInteger
   *
   * @return the seed, guaranteed to be within the Bitcoin EC group range
   */
  public static BigInteger moduloSeedByECGroupSize(BigInteger seedAsBigInteger) {

    X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    BigInteger sizeOfGroup = params.getN();

    return seedAsBigInteger.mod(sizeOfGroup);
  }
}
