package org.multibit.hd.ui.fest.requirements.keepkey;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.keepkey.*;
import org.multibit.hd.ui.fest.use_cases.standard.create_wallet.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.welcome_select.WelcomeSelectCreateHardwareWalletUseCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events before wallet unlock takes place</li>
 * </ul>
 *
 * @since 0.1.4
 */
public class CreateKeepKeyHardwareWalletWarmStartRequirements {

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Select create hardware wallet wallet
    new WelcomeSelectCreateHardwareWalletUseCase(window).execute(parameters);

    // Verify the KeepKey preparation
    new KeepKeyPreparationUseCase(window, hardwareWalletFixture).execute(parameters);

    // Select a backup location
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    // Enter wallet details
    new KeepKeyEnterWalletDetailsUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request create wallet (refer to mock client for "wipe device" ButtonRequest response)
    new KeepKeyRequestCreateWalletUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm wipe wallet
    new KeepKeyConfirmWipeUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Clicking Confirm (wipe)");

    hardwareWalletFixture.fireNextEvent("Enter new PIN");

    // Enter new PIN (refer to mock client for ButtonRequest response)
    new KeepKeyEnterNewPinUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm new PIN (refer to mock client for EntropyRequest response)
    new KeepKeyConfirmNewPinUseCase(window, hardwareWalletFixture).execute(parameters);

    // Confirm next words
    new KeepKeyEnterNextWordUseCase(window, hardwareWalletFixture).execute(parameters);

    // No check of new words with KeepKey

    // Verify report
    new KeepKeyCreateWalletReportUseCase(window, hardwareWalletFixture).execute(parameters);

    // Create is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    // Transitional
    new KeepKeyRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Allow time to gather the deterministic hierarchy
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Request the cipher key (refer to mock client for PIN entry responses)
    // Transitional
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
