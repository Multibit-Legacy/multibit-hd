package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.SwitchToHardwareWalletUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the responses to hardware wallet events in the context of an unlocked wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class HardwareWalletAfterUnlockRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Verify the "device connected" alert triggers a switch wallet
    new SwitchToHardwareWalletUseCase(window).execute(parameters);

    // TODO Exercise the UseTrezor wizard

  }
}
