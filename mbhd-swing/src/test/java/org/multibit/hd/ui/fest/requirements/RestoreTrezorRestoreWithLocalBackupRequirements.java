package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.RestoreButtonTrezorUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorEnterPinFromCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestMasterPublicKeyUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * restoring a hard Trezor</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RestoreTrezorRestoreWithLocalBackupRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Verify wallet unlocked and start the restore process
    new RestoreButtonTrezorUseCase(window).execute(parameters);

    // Restore is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Confirm unlock");

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);
  }
}
