package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify an alert is shown when a hardware wallet is connected</li>
 * <li>Verify selecting Yes will show the "hardware wallet" wizard</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ShowThenCancelUseHardwareWalletUseCase extends AbstractFestUseCase {

  public ShowThenCancelUseHardwareWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    Features features = new Features();
    features.setLabel("Aardvark");

    // Start the attach use case
    HardwareWalletEventFixtures.newAttachUseCase(features);

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

    window
      .radioButton(MessageKey.USE_TREZOR_WALLET.getKey())
      .requireSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.BUY_TREZOR.getKey())
      .requireNotSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.VERIFY_DEVICE.getKey())
      .requireNotSelected()
      .requireEnabled()
      .requireVisible();

    window
      .radioButton(MessageKey.WIPE_DEVICE.getKey())
      .requireNotSelected()
      .requireEnabled()
      .requireVisible();

    // Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

  }
}
