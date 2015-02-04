package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.bitcoinj.core.Utils;
import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.hardware.core.events.HardwareWalletEventType;
import org.multibit.hd.hardware.core.events.HardwareWalletEvents;
import org.multibit.hd.hardware.core.events.MessageEventType;
import org.multibit.hd.hardware.core.events.MessageEvents;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.hardware.core.messages.MessageSignature;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify a hardware wallet PIN matrix appears</li>
 * </ul>
 *
 * <p>Requires the "use device" screen to be showing</p>
 *
 * @since 0.0.1
 */
public class ShowPINMatrixUseCase extends AbstractHardwareWalletFestUseCase {


  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public ShowPINMatrixUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
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

    // Check the 'Yes' button on the alert is present and click it
    window
      .button(MessageKey.YES.getKey())
      .click();

    // Verify the "use hardware wallet" wizard appears

    // Verify that the title appears
    assertLabelText(MessageKey.USE_TREZOR_TITLE);

    // Click Next (use is the default)
    window
      .button(MessageKey.NEXT.getKey())
      .click();

    // Allow time for the view to react
    pauseForViewReset();

    // Request cipher key
    window
      .button(MessageKey.PIN_TITLE.getKey())
      .requireVisible()
      .requireDisabled();

    // Simulate a deterministic response to the request
    MessageEvents.fireMessageEvent(MessageEventType.BUTTON_REQUEST);

    // Allow time for the "user" to react to the button request
    pauseForUserInput();

    // Simulate the response after the button was pressed
    MessageSignature messageSignature = new MessageSignature(
      "1KqYyzL53R8oA1LdYvyv7m6JUryFfGJDpa",
      Utils.HEX.decode("be3c43189407284bb3fd1ac0040db1e0")
    );
    HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED, messageSignature);

    // Allow time for the UI to react
    pauseForComponentReset();

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
