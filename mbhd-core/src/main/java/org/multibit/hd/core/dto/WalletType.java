package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to Core API:</p>
 * <ul>
 * <li>Types of wallet e.g. MultiBit HD soft wallet, Trezor hardwallet, Trezor soft wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum WalletType {

  /**
   * MultiBit HD soft wallet i.e. wallet has BIP32 purpose=0 keys only
   */
  MBHD_SOFT_WALLET("mbhd"),

  /**
    * A Trezor wallet i.e BIP44 purpose-44 keys, where there is a Trezor to do the signing
    */
  TREZOR_HARD_WALLET("trezor"),

  /**
   * A clone of a Trezor wallet i.e. BIP44 purpose-44 keys but where the seed has been used to create a wallet.
   * There is no Trezor wallet to do te signing of spends
   */
  TREZOR_SOFT_WALLET("trezor")

  // End of enum
  ;

  private String prefix;

  WalletType(String prefix) {
    this.prefix = prefix;
  }

  public String toString() {
    return prefix;
  }
}
