package org.multibit.hd.ui.fest.use_cases.send_request.request;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "request" wizard with cancel</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowRequestThenCancelSendUseCase extends AbstractFestUseCase {

  public ShowRequestThenCancelSendUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Request allowing for network initialisation
    window
      .button(MessageKey.SHOW_REQUEST_WIZARD.getKey())
        // Allow time for the Bitcoin network to initialise
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.SHOW_REQUEST_WIZARD);

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Verify empty fields to start
    window
      .comboBox(MessageKey.RECIPIENT.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEditable()
      .requireNoSelection();

    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
