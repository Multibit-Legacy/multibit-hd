package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.create_wallet.CreateWalletSelectBackupLocationWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.*;
import org.multibit.hd.ui.fest.use_cases.welcome_select.AcceptLicenceUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectCreateTrezorWalletUseCase;
import org.multibit.hd.ui.fest.use_cases.welcome_select.WelcomeSelectLanguage_en_US_UseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events before wallet unlock takes place</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class HardwareWalletBeforeUnlockRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Work through the licence and language panels
    new AcceptLicenceUseCase(window).execute(parameters);
    new WelcomeSelectLanguage_en_US_UseCase(window).execute(parameters);

    // Select create Trezor wallet
    new WelcomeSelectCreateTrezorWalletUseCase(window).execute(parameters);

    // Verify the Trezor preparation
    new TrezorPreparationUseCase(window).execute(parameters);

    // Select a backup location
    new CreateWalletSelectBackupLocationWalletUseCase(window).execute(parameters);

    // Enter wallet details
    new TrezorEnterWalletDetailsUseCase(window).execute(parameters);

    // Confirm device wipe
    HardwareWalletEventFixtures.fireNextEvent();
    new TrezorConfirmWipeUseCase(window).execute(parameters);

    // Enter new PIN
    HardwareWalletEventFixtures.fireNextEvent();
    new TrezorEnterNewPinUseCase(window).execute(parameters);

    // Confirm new PIN
    HardwareWalletEventFixtures.fireNextEvent();
    new TrezorConfirmNewPinUseCase(window).execute(parameters);

    // Enter next words
    new TrezorEnterNextWordUseCase(window).execute(parameters);

    // Confirm words
    new TrezorConfirmNextWordUseCase(window).execute(parameters);

    // Verify report
    new TrezorCreateWalletReportUseCase(window).execute(parameters);

  }
}
