package org.multibit.hd.core.dto;

/**
 * <p>Enum to provide the following to various UI models:</p>
 * <ul>
 * <li>High level wallet type selection (standard, Trezor, KeepKey etc)</li>
 * </ul>
 *
 * <p>This reduces code complexity in factory methods when deciding how to build supporting objects</p>
 *
 * @since 0.0.1
 *
 */
public enum WalletMode {

  /**
   * Target a standard soft wallet (BIP 32 or BIP 44)
   */
  STANDARD,

  /**
   * Target a Trezor wallet (BIP 44 only)
   */
  TREZOR,

  /**
   * Target a KeepKey wallet (BIP 44 only)
   */
  KEEP_KEY,

  // End of enum
  ;

}
