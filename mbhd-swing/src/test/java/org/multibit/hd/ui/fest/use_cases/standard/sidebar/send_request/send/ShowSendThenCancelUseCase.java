package org.multibit.hd.ui.fest.use_cases.standard.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send" wizard with cancel</li>
 * </ul>
 * <p>Requires the "send" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowSendThenCancelUseCase extends AbstractFestUseCase {

  public ShowSendThenCancelUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Send allowing for network initialisation
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
        // Allow time for the Bitcoin network to initialise
        // This should occur quickly since we're in test mode
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.SEND_BITCOIN_TITLE);

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(); // This is due to unrestricted

    // Verify empty fields to start
    window
      .comboBox(MessageKey.RECIPIENT.getKey())
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .requireEditable()
      .requireNoSelection();

    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .requireEmpty();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      .requireVisible();

  }

}
