package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.create_wallet.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.*;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectCreateTrezorWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_en_US_UseCase;

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
public class CreateTrezorHardwareWalletColdStartRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the licence and language panels
    new AcceptLicenceUseCase(window).execute(parameters);
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    // Select create Trezor wallet
    // TODO Remove this
    new WelcomeSelectCreateTrezorWalletUseCase(window).execute(parameters);

    // Verify the Trezor preparation
    new TrezorPreparationUseCase(window).execute(parameters);

    // Select a backup location
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    // Enter wallet details
    new TrezorEnterWalletDetailsUseCase(window).execute(parameters);

    // Request create wallet (refer to mock client for "wipe device" ButtonRequest response)
    //new TrezorRequestCreateWalletUseCase(window).execute(parameters);

    // Confirm wipe wallet
    new TrezorConfirmWipeUseCase(window).execute(parameters);

    // User input ("New PIN (first)" - implied confirmation of "wipe device"")
    HardwareWalletEventFixtures.fireNextEvent();

    // Enter new PIN (refer to mock client for ButtonRequest response)
    new TrezorEnterNewPinUseCase(window).execute(parameters);

    // Confirm new PIN (refer to mock client for EntropyRequest response)
    new TrezorConfirmNewPinUseCase(window).execute(parameters);

    // Confirm next words
    new TrezorEnterNextWordUseCase(window).execute(parameters);

    // Confirm words
    new TrezorConfirmNextWordUseCase(window).execute(parameters);

    // Verify report
    new TrezorCreateWalletReportUseCase(window).execute(parameters);

    // Create is complete - hand over to credentials
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinUseCase(window).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window).execute(parameters);

    // User input "confirm unlock"
    HardwareWalletEventFixtures.fireNextEvent();

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);

  }
}
