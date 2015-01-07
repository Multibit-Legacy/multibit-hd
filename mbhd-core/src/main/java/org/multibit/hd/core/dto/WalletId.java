package org.multibit.hd.core.dto;

import org.bitcoinj.core.Utils;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.crypto.AESUtils;
import org.multibit.hd.core.managers.WalletManager;

import java.io.File;
import java.util.Arrays;

/**
 * <p>Data object to provide the following to wallet seed related classes:</p>
 * <ul>
 * <li>Creation of wallet id from seed</li>
 * <li>Creation of wallet directory name from wallet id</li>
 * <li>Round-tripping of the above</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class WalletId {

  public static final int SEPARATOR_REPEAT_PERIOD = 4;
  public static final String WALLET_ID_SEPARATOR = "-";

  /**
   * The salt used in converting seed bytes to a wallet id for MBHD wallets
   */
  private static final byte[] WALLET_ID_SALT_USED_IN_SCRYPT_FOR_MBHD_WALLETS = new byte[]{(byte) 1};

  /**
    * The salt used in converting seed bytes to a wallet id for Trezor wallets
    */
  private static final byte[] WALLET_ID_SALT_USED_IN_SCRYPT_FOR_TREZOR_SOFT_WALLETS = new byte[]{(byte) 2};

  private static final int NUMBER_OF_BYTES_IN_WALLET_ID = 20;
  public static final int LENGTH_OF_FORMATTED_WALLET_ID = 2 * NUMBER_OF_BYTES_IN_WALLET_ID + (NUMBER_OF_BYTES_IN_WALLET_ID / SEPARATOR_REPEAT_PERIOD) - 1;

  private final byte[] walletId;

  /**
   * @return A copy of the wallet ID salt used in Scrypt for MBHD wallets
   */
  public static byte[] getWalletIdSaltUsedInScryptForMbhdWallets() {
    return Arrays.copyOf(WALLET_ID_SALT_USED_IN_SCRYPT_FOR_MBHD_WALLETS, WALLET_ID_SALT_USED_IN_SCRYPT_FOR_MBHD_WALLETS.length);
  }

  /**
   * @return A copy of the wallet ID salt used in Scrypt for Trezor soft wallets
   */
  public static byte[] getWalletIdSaltUsedInScryptForTrezorSoftWallets() {
    return Arrays.copyOf(WALLET_ID_SALT_USED_IN_SCRYPT_FOR_TREZOR_SOFT_WALLETS, WALLET_ID_SALT_USED_IN_SCRYPT_FOR_TREZOR_SOFT_WALLETS.length);
  }

  /**
   * Create a wallet id from a formatted wallet id
   *
   * @param formattedWalletId The formatted wallet id you want to use (e.g. "66666666-77777777-88888888-99999999-aaaaaaaa")
   */
  public WalletId(String formattedWalletId) {

    Preconditions.checkState(formattedWalletId.length() == LENGTH_OF_FORMATTED_WALLET_ID);

    // remove any embedded hyphens
    formattedWalletId = formattedWalletId.replaceAll("-", "");

    walletId = Utils.parseAsHexOrBase58(formattedWalletId);
  }

  /**
   * Create an MBHD wallet id from the given seed.
   * This produces a wallet id from the seed using various trapdoor functions.
   * The seed is typically generated from the SeedPhraseGenerator#convertToSeed method.
   *
   * @param seed The seed to use in deriving the wallet id
   */
  public WalletId(byte[] seed) {
    walletId = AESUtils.generate160BitsOfEntropy(seed, WALLET_ID_SALT_USED_IN_SCRYPT_FOR_MBHD_WALLETS);
  }


  /**
   * Create an wallet id from the given seed and salt
   *
   * You can use this to generate a wallet id for a Trezor soft wallet passing in WALLET_ID_SALT_USED_IN_SCRYPT_FOR_TREZOR_SOFT_WALLETS
   * This produces a wallet id from the seed using various trapdoor functions.
   * The seed is typically generated from the SeedPhraseGenerator#convertToSeed method.
   *
   * @param seed The seed to use in deriving the wallet id
   */
  public WalletId(byte[] seed, byte[] salt) {
    walletId = AESUtils.generate160BitsOfEntropy(seed, salt);
  }

  /**
   * Create a WalletId from a wallet filename - the filename is parsed into a walletId byte array
   * The wallet filename should be the whole file name e.g /herp/derp/mbhd-23bb865e-161bfefc-3020c418-66bf6f75-7fecdfcc/mbhd.wallet
   *
   * @return The wallet ID
   */
  public static WalletId parseWalletFilename(String walletFilename) {

    File walletFile = new File(walletFilename);

    // Get the parent directory, in which the wallet id is embedded
    File walletRoot = walletFile.getParentFile();
    String walletRootName = walletRoot.getName();

    // Remove the prefix mbhd
    String prefix = WalletManager.WALLET_DIRECTORY_PREFIX + WALLET_ID_SEPARATOR;
    if (walletRootName.startsWith(prefix)) {
      walletRootName = walletRootName.replace(prefix, "");

      return new WalletId(walletRootName);

    } else {
      throw new IllegalStateException("Cannot parse '" + walletFilename + "' into a WalletId. Does not start with '" + prefix + "'");
    }
  }

  /**
   * @return The raw wallet id as a byte[]
   */
  public byte[] getBytes() {
    return Arrays.copyOf(walletId, walletId.length);
  }

  /**
   * @return The wallet id as a formatted string (e.g. "66666666-77777777-88888888-99999999-aaaaaaaa")
   */
  public String toFormattedString() {

    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < walletId.length; i++) {
      buffer.append(Utils.HEX.encode(new byte[]{walletId[i]}));

      if (((i + 1) % SEPARATOR_REPEAT_PERIOD == 0) && !(i == walletId.length - 1)) {
        buffer.append(WALLET_ID_SEPARATOR);
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

    WalletId that = (WalletId) o;

    return Arrays.equals(this.walletId, that.walletId);

  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(walletId);
  }

}
