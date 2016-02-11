package org.multibit.hd.ui.fest.requirements.trezor.unlock_wallet;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.environment.CloseUnsupportedConfigurationEnvironmentPopoverUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestMasterPublicKeyUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a Trezor wallet with unsupported configuration "passphrase"</li>
 * </ul>
 *
 * @since 0.0.8
 */
public class UnlockTrezorHardwareWalletUnsupportedConfigurationPassphraseRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Expect "unsupported configuration" popover to be showing
    new CloseUnsupportedConfigurationEnvironmentPopoverUseCase(window).execute(null);

    // Unlock the wallet
    new QuickUnlockWalletUseCase(window).execute(null);

    // Verify the report screen is working
    new UnlockReportUseCase(window).execute(null);
  }
}
