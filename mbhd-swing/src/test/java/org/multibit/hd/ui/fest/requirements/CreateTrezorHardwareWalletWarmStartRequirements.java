package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.MessageEventFixtures;
import org.multibit.hd.ui.fest.use_cases.create_wallet.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.credentials.UnlockReportUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.*;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectCreateTrezorWalletUseCase;

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
public class CreateTrezorHardwareWalletWarmStartRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

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
    new TrezorRequestCreateWalletUseCase(window).execute(parameters);

    // Confirm wipe wallet
    new TrezorConfirmWipeUseCase(window).execute(parameters);

    // User input ("New PIN (first)" - implied confirmation of "wipe device"")
    MessageEventFixtures.fireNextEvent();

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
    MessageEventFixtures.fireNextEvent();

    // Verify the wallet unlocked
    new UnlockReportUseCase(window).execute(parameters);

  }
}
