package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.*;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AttachHardwareWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_en_US_UseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a Trezor wallet under cold start</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class UnlockTrezorHardwareWalletColdStartRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the licence and language panels
    new AcceptLicenceUseCase(window).execute(parameters);
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    new AttachHardwareWalletUseCase(window).execute(parameters);

    // Welcome is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry (refer to mock client for PIN entry responses)
    new TrezorEnterPinFromMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (no PIN usually)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Confirm unlock");

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);

  }
}
