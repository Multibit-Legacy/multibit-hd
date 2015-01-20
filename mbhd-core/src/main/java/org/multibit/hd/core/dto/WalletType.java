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
   * MultiBit HD soft wallet
   * Wallet has BIP32 account 0 keys only
   * m/0'/0/0 is first receiving key
   */
  MBHD_SOFT_WALLET(CoreMessageKey.WALLET_CAPABILITY_MBHD_SOFT),

  /**
   * Trezor wallet with no keys
   * Wallet follows BIP44 account 0 and requires a Trezor to do the signing
   * m/44'/0'/0'/0/0 is first receiving key
   */
  TREZOR_HARD_WALLET(CoreMessageKey.WALLET_CAPABILITY_TREZOR_HARD),

  /**
   * Trezor soft wallet
   * Wallet has BIP44 account 0 keys only
   * m/44'/0'/0'/0/0 is first receiving key
   */
  TREZOR_SOFT_WALLET(CoreMessageKey.WALLET_CAPABILITY_TREZOR_SOFT),

  /**
   * A wallet with unknown capabilities (most likely from the future)
   */
  UNKNOWN(CoreMessageKey.WALLET_CAPABILITY_UNKNOWN)

  // End of enum
  ;

  private final CoreMessageKey key;

  /**
   * @param key    The core message key describing the wallet type in user friendly localised terms
   */
  WalletType(CoreMessageKey key) {
    this.key = key;
  }

  /**
   * @return The message key providing the localised description
   */
  public CoreMessageKey getKey() {
    return key;
  }
}
