package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.request;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "request" wizard can handle a Bitcoin amount entry</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowRequestThenFinishUseCase extends AbstractFestUseCase {

  public ShowRequestThenFinishUseCase(FrameFixture window) {
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
    assertLabelText(MessageKey.REQUEST_BITCOIN_TITLE);

    // Verify buttons
    window
      .button(MessageKey.COPY.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.QR_CODE.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify empty fields to start
    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty();

    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .requireVisible()
      .requireEnabled()
      .requireNotEditable();

    window
      .textBox(MessageKey.QR_CODE_LABEL.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty();

    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .requireVisible()
      .requireEnabled()
      .requireEmpty();

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
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
