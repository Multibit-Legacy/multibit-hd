package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.messages.Features;
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
 */
public class ConnectThenEnterPINUseCase extends AbstractFestUseCase {

  public ConnectThenEnterPINUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    Features features = new Features();
    features.setLabel("Aardvark");

    // Simulate the insertion of a device
    HardwareWalletEvents.fireHardwareWalletEvent(
      HardwareWalletEventType.SHOW_DEVICE_READY,
      features
    );

    // Allow time for the view to react
    pauseForViewReset();

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", "Aardvark");

    // Check the 'Yes' button on the alert is present
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    // Click on the 'Yes' button
    window
      .button(MessageKey.YES.getKey())
      .click();

    // Verify the "Unlock screen" ("Enter PIN") appears by checking there is an exit button
    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
      .requireEnabled();

    // Allow time for the view to react
    pauseForViewReset();

    // Initially the 'Unlock' button should be disabled
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireDisabled();

    // Click on each pin button 0
    window.button("pin 0").click();

    // Unlock should be enabled after some PIN entry
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireEnabled();

    // Click on each pin button 1 to 8 in turn
    for (int i = 1; i < 8; i++) {
      window.button("pin " + i).click();
    }

    // Remove the buttons entered using the delete button
    for (int i = 0; i < 10; i++) {
      window.button(MessageKey.DELETE.getKey()).click();
    }

    // Enter a pin of 1234
    window.button("pin 1").click();
    window.button("pin 2").click();
    window.button("pin 3").click();
    window.button("pin 4").click();

    // Click on Unlock to perform check of PIN
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .click();
  }
}
