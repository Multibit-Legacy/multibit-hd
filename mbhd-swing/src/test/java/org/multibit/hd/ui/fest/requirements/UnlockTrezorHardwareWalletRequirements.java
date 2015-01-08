package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorConfirmUnlockUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorEnterPinUseCase;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.TrezorRequestCipherKeyUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of unlocking a Trezor wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class UnlockTrezorHardwareWalletRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Request the cipher key (refer to mock client for PublicKey responses)
    new TrezorRequestCipherKeyUseCase(window).execute(parameters);

    // Enter PIN
    new TrezorEnterPinUseCase(window).execute(parameters);

    // Unlock with cipher key
    new TrezorConfirmUnlockUseCase(window).execute(parameters);


  }
}
