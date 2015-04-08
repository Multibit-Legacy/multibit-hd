package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.environment.CloseDeprecatedFirmwareEnvironmentPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorEnterPinFromCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestMasterPublicKeyUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a Trezor wallet with deprecated firmware</li>
 * </ul>
 *
 * @since 0.0.8
 */
public class UnlockTrezorHardwareWalletDeprecatedFirmwareRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Expect "deprecated firmware" popover to be showing
    new CloseDeprecatedFirmwareEnvironmentPopoverUseCase(window).execute(null);

    // Enter the PIN
    new TrezorEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Confirm unlock");

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);

  }
}
