package org.multibit.hd.ui.fest.use_cases.send_request;

import org.fest.swing.fixture.FrameFixture;
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
public class SendNoFundsConfirmScreenUseCase extends AbstractFestUseCase {

  public SendNoFundsConfirmScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click Next
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Verify the confirm panel is showing
    window
      .label(MessageKey.CONFIRM_SEND_TITLE.getKey());

    // Verify buttons
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify recipient summary
    window
      .label(MessageKey.RECIPIENT_SUMMARY.getKey())
      .requireVisible()
      .requireText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // Verify amount panels are present
    window
      .panel("transaction")
      .requireVisible();

    window
      .panel("transaction_fee")
      .requireVisible();

    window
      .panel("client_fee")
      .requireVisible();

    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Some notes");

    // Have not entered password yet
    window
      .button(MessageKey.SEND.getKey())
      .requireVisible()
      .requireDisabled();

    // Enter password
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .enterText("abc123");

    // Send is enabled
    window
      .button(MessageKey.SEND.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Verify the send progress panel is showing
    window
      .label(MessageKey.SEND_PROGRESS_TITLE.getKey());

    window
      .label(MessageKey.TRANSACTION_CONSTRUCTION_STATUS_SUMMARY.getKey());

    window
      .label(MessageKey.TRANSACTION_CONSTRUCTION_STATUS_DETAIL.getKey());

    window
      .label(MessageKey.TRANSACTION_BROADCAST_STATUS_SUMMARY.getKey());

    window
      .label(MessageKey.TRANSACTION_BROADCAST_STATUS_DETAIL.getKey());

    window
      .label(MessageKey.TRANSACTION_CONFIRMATION_STATUS.getKey());

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

  }


}
