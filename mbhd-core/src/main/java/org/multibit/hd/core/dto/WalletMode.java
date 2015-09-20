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
 */
public enum WalletMode {

  /**
   * Target a standard soft wallet (BIP 32 or BIP 44)
   */
  STANDARD("MultiBit"),

  /**
   * Target a Trezor wallet (BIP 44 only)
   */
  TREZOR("Trezor"),

  /**
   * Target a KeepKey wallet (BIP 44 only)
   */
  KEEP_KEY("KeepKey"),

  // End of enum
  ;

  private final String brand;

  WalletMode(String brand) {
    this.brand = brand;
  }

  /**
   * @return The brand name for use with localisation
   */
  public String brand() {
    return brand;
  }
}
