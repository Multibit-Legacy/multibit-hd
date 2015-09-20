package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import org.multibit.hd.hardware.core.HardwareWalletService;
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
   * Determine a wallet mode from a hardware event's source
   *
   * @param event The hardware wallet event
   *
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

  /**
   * Determine a wallet mode from a hardware wallet service
   *
   * @param hardwareWalletService The optional hardware wallet service
   *
   * @return A matching wallet mode (STANDARD for an absent hardware wallet service)
   */
  public static WalletMode of(Optional<HardwareWalletService> hardwareWalletService) {

    if (hardwareWalletService.isPresent()) {
      String name = hardwareWalletService.get().getContext().getClient().name();
      try {
        return WalletMode.valueOf(name.toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        return UNKNOWN;
      }

    }

    // No hardware wallet service so use standard
    return STANDARD;

  }
}
