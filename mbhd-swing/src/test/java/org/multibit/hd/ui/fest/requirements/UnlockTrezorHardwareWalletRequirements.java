package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
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

    // Request the cipher key (triggers deterministic hierarchy)
    new TrezorRequestCipherKeyUseCase(window).execute(parameters);

    // Deterministic hierarchy
    HardwareWalletEventFixtures.fireNextEvent();

    // Unlock with a PIN
    new TrezorEnterPinUseCase(window).execute(parameters);
  }
}
