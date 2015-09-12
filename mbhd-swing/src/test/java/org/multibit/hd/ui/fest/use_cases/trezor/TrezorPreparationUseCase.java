package org.multibit.hd.ui.fest.use_cases.trezor;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
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
public class TrezorPreparationUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public TrezorPreparationUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Wait for the display to complete
    pauseForPreparationDisplay();

    // Check that the Trezor preparation screen is showing
    window
      .label(MessageKey.CREATE_TREZOR_WALLET_PREPARATION_TITLE.getKey())
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
