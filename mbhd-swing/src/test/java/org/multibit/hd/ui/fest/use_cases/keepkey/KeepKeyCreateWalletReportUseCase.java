package org.multibit.hd.ui.fest.use_cases.keepkey;

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
 * <li>Verify the Keepkey create wallet report panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class KeepKeyCreateWalletReportUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public KeepKeyCreateWalletReportUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the Keepkey enter next word view is showing
    window
      .label(MessageKey.USE_HARDWARE_REPORT_TITLE.getKey())
      .requireVisible();

    window
      .label(MessageKey.USE_HARDWARE_REPORT_MESSAGE_SUCCESS.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP_NOTE_1.getKey())
      .requireVisible();

    // Check the 'Finish' button is present and click it
    window
      .button(MessageKey.FINISH.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
