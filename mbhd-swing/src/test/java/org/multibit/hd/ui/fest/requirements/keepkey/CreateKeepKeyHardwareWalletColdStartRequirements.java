package org.multibit.hd.ui.fest.requirements.keepkey;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.standard.create_wallet.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.AttachHardwareWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectLanguage_en_US_UseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events before wallet unlock takes place</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CreateKeepKeyHardwareWalletColdStartRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the licence and language panels
    new AcceptLicenceUseCase(window).execute(parameters);
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    new AttachHardwareWalletUseCase(window).execute(parameters);

    // Verify the Trezor preparation
    new TrezorPreparationUseCase(window, hardwareWalletFixture).execute(parameters);

    // Select a backup location
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    // Enter wallet details
    new TrezorEnterWalletDetailsUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request create wallet (refer to mock client for "wipe device" ButtonRequest response)
    new TrezorRequestCreateWalletUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm wipe wallet
    new TrezorConfirmWipeUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Clicking Confirm (wipe)");

    hardwareWalletFixture.fireNextEvent("Enter new PIN");

    // Enter new PIN (refer to mock client for ButtonRequest response)
    new TrezorEnterNewPinUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm new PIN (refer to mock client for EntropyRequest response)
    new TrezorConfirmNewPinUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm next words
    new TrezorEnterNextWordUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm words
    new TrezorConfirmNextWordUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify report
    new TrezorCreateWalletReportUseCase(window, hardwareWalletFixture).execute(parameters);

    // Create is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    // Transitional
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Allow time to gather the deterministic hierarchy
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Request the cipher key (refer to mock client for PIN entry responses)
    // Transitional
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
