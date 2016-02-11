package org.multibit.hd.ui.fest.requirements.keepkey.unlock_wallet;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyRequestMasterPublicKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.environment.CloseUnsupportedFirmwareEnvironmentPopoverUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a KeepKey wallet with unsupported firmware</li>
 * </ul>
 *
 * @since 0.1.4
 */
public class UnlockKeepKeyHardwareWalletUnsupportedFirmwareRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new KeepKeyRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new KeepKeyRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Expect "unsupported firmware" popover to be showing
    new CloseUnsupportedFirmwareEnvironmentPopoverUseCase(window).execute(null);

    // Unlock the wallet
    new QuickUnlockWalletUseCase(window).execute(null);

    // Verify the report screen is working
    new UnlockReportUseCase(window).execute(null);
  }
}
