package org.multibit.hd.ui.fest.requirements.keepkey.restore_wallet;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyEnterPinFromCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.keepkey.KeepKeyRequestMasterPublicKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.RestoreButtonHardwareWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * restoring a KeepKey</li>
 * </ul>
 *
 * @since 0.1.4
 */
public class RestoreKeepKeyRestoreWithLocalBackupRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Verify wallet unlocked and start the restore process
    new RestoreButtonHardwareWalletUseCase(window).execute(parameters);

    // Restore is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new KeepKeyRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new KeepKeyRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry
    new KeepKeyEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new KeepKeyConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Confirm unlock");

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);
  }
}
