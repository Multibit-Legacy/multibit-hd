package org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send/request"  confirm screen</li>
 * </ul>
 * <p>Requires the "send/request" amount screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class SendNoFundsReportScreenUseCase extends AbstractFestUseCase {

  public SendNoFundsReportScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // No network so cannot test
    if (!CoreServices.getOrCreateBitcoinNetworkService().isStartedOk()) {
      return;
    }

    // Click Next
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Allow a moment for processing
    pauseForComponentReset();

    // Verify the report panel is showing
    assertLabelText(MessageKey.SEND_PROGRESS_TITLE);

    // Verify failure message
    window
      .label(MessageKey.TRANSACTION_CONSTRUCTION_STATUS_SUMMARY.getKey())
      .requireVisible();

    // Verify buttons
    window
      .button(MessageKey.FINISH.getKey())
      .click();

  }

}
