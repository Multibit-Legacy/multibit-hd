package org.multibit.hd.ui.fest.requirements;

import com.google.common.collect.Maps;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.hardware_wallet.ShowThenCancelUseHardwareWalletUseCase;

import java.util.Map;

/**
 * <p>FEST Swing UI test to provide:</p>
 * <ul>
 * <li>Exercise the "hardware wallet system events" to verify its wizards and alerts show correctly</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class HardwareWalletEventRequirements {

  public static void verifyUsing(FrameFixture window) {

    Map<String, Object> parameters = Maps.newHashMap();

    // Verify the "device connected" alert
    new ShowThenCancelUseHardwareWalletUseCase(window).execute(parameters);

    // Require mocking capability on HardwareWalletService (perhaps use emulator)
    // Verify the PIN matrix
    // new ShowPINMatrixUseCase(window).execute(parameters);
  }
}
