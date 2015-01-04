package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor create wallet report panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorCreateWalletReportUseCase extends AbstractFestUseCase {

  public TrezorCreateWalletReportUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for any hardware wallet events to propagate
    pauseForViewReset();

    // Check that the Trezor enter next word view is showing
    window
      .label(MessageKey.USE_TREZOR_REPORT_TITLE.getKey())
      .requireVisible();

    window
      .label(MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP_NOTE_2.getKey())
      .requireVisible();

    // Check the 'Finish' button is present and click it
    window
      .button(MessageKey.FINISH.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
