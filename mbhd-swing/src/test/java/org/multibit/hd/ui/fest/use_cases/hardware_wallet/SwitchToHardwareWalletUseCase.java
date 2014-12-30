package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify an alert is shown when a hardware wallet is connected</li>
 * <li>Verify selecting Yes will switch to the "credentials" wizard</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class SwitchToHardwareWalletUseCase extends AbstractFestUseCase {

  public SwitchToHardwareWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Start the attach use case
    HardwareWalletEventFixtures.newAttachUseCase();

    // Allow time for the view to react
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", HardwareWalletEventFixtures.STANDARD_LABEL);

    // Check the 'Yes' button on the alert is present and click it
    window
      .button(MessageKey.YES.getKey())
      .click();

    // Allow time for the switch to take place
    pauseForWalletSwitch();

    // Verify the "credentials" wizard appears after a switch in Trezor mode

    window
      .label(MessageKey.TREZOR_UNLOCK_TITLE.getKey())
      .requireVisible();

  }
}
