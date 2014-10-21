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
  MBHD_SOFT_WALLET("mbhd", CoreMessageKey.WALLET_CAPABILITY_MBHD_SOFT),

  /**
   * Trezor wallet with no keys
   * Wallet follows BIP44 account 0 and requires a Trezor to do the signing
   * m/44'/0'/0'/0/0 is first receiving key
   */
  TREZOR_HARD_WALLET("trezor", CoreMessageKey.WALLET_CAPABILITY_TREZOR_HARD),

  /**
   * Trezor soft wallet
   * Wallet has BIP44 account 0 keys only
   * m/44'/0'/0'/0/0 is first receiving key
   */
  TREZOR_SOFT_WALLET("trezor-soft", CoreMessageKey.WALLET_CAPABILITY_TREZOR_SOFT)

  // End of enum
  ;

  private final String prefix;
  private final CoreMessageKey key;

  /**
   * @param prefix The wallet prefix identifying this wallet under the application directory
   * @param key    The core message key describing the wallet type in user friendly localised terms
   */
  WalletType(String prefix, CoreMessageKey key) {
    this.prefix = prefix;
    this.key = key;
  }

  public String getPrefix() {
    return prefix;
  }

  /**
   * @return The message key providing the localised description
   */
  public CoreMessageKey getKey() {
    return key;
  }
}
