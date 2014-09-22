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
public class ConnectThenEnterPINUseCase extends AbstractFestUseCase {

  public ConnectThenEnterPINUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Simulate the insertion of a device
    HardwareWalletEvents.fireSystemEvent(SystemMessageType.DEVICE_CONNECTED);

    // Allow time for the view to react
    pauseForViewReset();

    // Check that an alert message is present
    assertLabelContainsValue("alert_message_label", "Aardvark");

    // Check the 'Yes' button on the alert is present
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    // Click on the 'Yes' button and check the alert is dismissed
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

    // Click on each pin button 0 to 8 in turn
    window.button("pin 0").click();
    window.button("pin 1").click();
    window.button("pin 2").click();
    window.button("pin 3").click();
    window.button("pin 4").click();
    window.button("pin 5").click();
    window.button("pin 6").click();
    window.button("pin 7").click();
    window.button("pin 8").click();

    // Remove the buttons entered using the delete button
    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();

    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();

    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();
    window.button(MessageKey.DELETE.getKey()).click();

    window.button(MessageKey.DELETE.getKey()).click();

   // Click on Exit
    window
      .button(MessageKey.EXIT.getKey())
      .click();

  }

}
