package org.multibit.hd.core.dto;

import org.multibit.hd.hardware.core.events.HardwareWalletEvent;

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

  /**
   * Target an unknown (possibly future) hardware wallet (BIP 44 only)
   */
  UNKNOWN("Unknown"),

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

  /**
   *
   * @param event The hardware wallet event
   * @return A matching wallet mode
   */
  public static WalletMode of(HardwareWalletEvent event) {

    for (WalletMode walletMode : WalletMode.values()) {
      if (walletMode.name().equalsIgnoreCase(event.getSource())) {
        return walletMode;
      }
    }

    return WalletMode.UNKNOWN;

  }
}
