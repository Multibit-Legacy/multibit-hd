package org.multibit.hd.testing.hardware_wallet_fixtures;

import org.multibit.hd.core.dto.WalletMode;
import org.multibit.hd.testing.hardware_wallet_fixtures.keepkey.*;
import org.multibit.hd.testing.hardware_wallet_fixtures.trezor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>High level mocked hardware wallet fixtures to provide the following to FEST tests:</p>
 * <ul>
 * <li>Predictable mocked responses to various use cases</li>
 * </ul>
 *
 * @since 0.1.4
 *  
 */
public class HardwareWalletFixtures {

  public static final Logger log = LoggerFactory.getLogger(HardwareWalletFixtures.class);

  /**
   * @param walletMode The wallet mode
   *
   * @return A "wiped" hardware wallet fixture
   */
  public static HardwareWalletFixture newWipedFixture(WalletMode walletMode) {

    switch (walletMode) {
      case TREZOR:
        return new TrezorWipedFixture();
      case KEEP_KEY:
        return new KeepKeyWipedFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }

  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the unlock use case
   */
  public static HardwareWalletFixture newInitialisedUnlockFixture(WalletMode walletMode) {

    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedUnlockFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedUnlockFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }

  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the re-attach use case
   */
  public static HardwareWalletFixture newInitialisedReattachedFixture(WalletMode walletMode) {
    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedReattachedFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedReattachedFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }
  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the restore use case
   */
  public static HardwareWalletFixture newInitialisedRestoreFixture(WalletMode walletMode) {
    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedRestoreFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedRestoreFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }
  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the unsupported firmware use case
   */
  public static HardwareWalletFixture newInitialisedUnsupportedFirmwareFixture(WalletMode walletMode) {
    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedUnsupportedFirmwareFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedUnsupportedFirmwareFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }
  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the deprecated firmware use case
   */
  public static HardwareWalletFixture newInitialisedDeprecatedFirmwareFixture(WalletMode walletMode) {
    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedDeprecatedFirmwareFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedDeprecatedFirmwareFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }
  }

  /**
   * @param walletMode The wallet mode
   *
   * @return An "initialised" hardware wallet fixture for the unsupported configuration passphrase use case
   */
  public static HardwareWalletFixture newInitialisedUnsupportedConfigurationPassphraseFixture(WalletMode walletMode) {
    switch (walletMode) {
      case TREZOR:
        return new TrezorInitialisedUnsupportedConfigurationPassphraseFixture();
      case KEEP_KEY:
        return new KeepKeyInitialisedUnsupportedConfigurationPassphraseFixture();
      default:
        throw new IllegalStateException("Unsupported wallet mode: " + walletMode.name());
    }
  }
}
