package org.multibit.hd.ui.fest.requirements.keepkey;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorEnterPinFromCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestMasterPublicKeyUseCase;
import org.multibit.hd.ui.languages.MessageKey;

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
public class ReattachKeepKeyHardwareWalletRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    hardwareWalletFixture.fireNextEvent("Detach Trezor");

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Switch to password entry mode

    // Verify password entry screen is seen
    // Check that the Trezor enter new PIN panel view is showing
    window
      .label(MessageKey.PASSWORD_TITLE.getKey())
      .requireVisible();
    // Ensure Unlock is not available
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireDisabled();

    // Plug the Trezor back in
    hardwareWalletFixture.fireNextEvent("Attach Trezor");

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("User confirms unlock");

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);
  }
}
