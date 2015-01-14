package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.credentials.QuickUnlockWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorEnterPinUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestMasterPublicKeyUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * plugging in and puling out a Trezor</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PlugInAndPullOutTrezorHardwareWalletRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinUseCase(window).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Pull the Trezor out
    HardwareWalletEventFixtures.fireNextEvent();

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Verify password entry screen is seen
    new QuickUnlockWalletUseCase(window).execute(null);

    // Plug the Trezor in
    HardwareWalletEventFixtures.fireNextEvent();

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinUseCase(window).execute(parameters);

    // Unlock with cipher key
    //new TrezorConfirmUnlockUseCase(window).execute(parameters);

    // User input "confirm unlock"
    //HardwareWalletEventFixtures.fireNextEvent();

    // Verify the wallet unlocked
    //new UnlockReportUseCase(window).execute(parameters);
  }
}
