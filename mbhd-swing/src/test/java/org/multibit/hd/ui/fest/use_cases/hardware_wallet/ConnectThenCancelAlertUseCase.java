package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.SystemMessageType;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify an alert is shown when a hardware wallet is connected</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ConnectThenCancelAlertUseCase extends AbstractFestUseCase {

  public ConnectThenCancelAlertUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Simulate the insertion of a device
    HardwareWalletEvents.fireSystemEvent(SystemMessageType.DEVICE_CONNECTED);

    // Allow time for the view to react
    pauseForViewReset();

    // Click on Change password
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify the "Trezor inserted" wizard appears
    assertLabelContainsValue("alert_message_label", "Aardvark");

   // Click Close
    window
      .button(MessageKey.CLOSE.getKey())
      .click();

  }

}
