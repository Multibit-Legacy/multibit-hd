package org.multibit.hd.ui.fest.requirements.keepkey;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorEnterPinFromCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestCipherKeyUseCase;
import org.multibit.hd.ui.fest.use_cases.trezor.TrezorRequestMasterPublicKeyUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of
 * unlocking a Trezor wallet under warm start followed by a restore</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RestoreKeepKeyWarmStartRequirements {

  private static final Logger log = LoggerFactory.getLogger(RestoreKeepKeyWarmStartRequirements.class);

  public static void verifyUsing(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the master public key (refer to mock client for PublicKey responses)
    new TrezorRequestMasterPublicKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Request the cipher key (refer to mock client for PIN entry responses)
    new TrezorRequestCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Verify PIN entry
    new TrezorEnterPinFromCipherKeyUseCase(window, hardwareWalletFixture).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window, hardwareWalletFixture).execute(parameters);

    hardwareWalletFixture.fireNextEvent("Confirm unlock");

    log.debug("Entropy 0 = {}", CoreServices.getOrCreateHardwareWalletService().get().getContext().getEntropy());

  }
}
