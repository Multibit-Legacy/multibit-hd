package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor preparation screen</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorConfirmWipeUseCase extends AbstractFestUseCase {

  public TrezorConfirmWipeUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Wait for the display to complete
    HardwareWalletEventFixtures.fireNextEvent();

    // Allow time for the hardware wallet event to be processed
    pauseForViewReset();

    // Check that the Trezor confirm wipe panel view is showing
    window
      .label(MessageKey.TREZOR_WIPE_CONFIRM_DISPLAY.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_2.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_3.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_4.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_5.getKey())
      .requireVisible();

    window
      .label(MessageKey.TREZOR_PREPARATION_NOTE_6.getKey())
      .requireVisible();

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
