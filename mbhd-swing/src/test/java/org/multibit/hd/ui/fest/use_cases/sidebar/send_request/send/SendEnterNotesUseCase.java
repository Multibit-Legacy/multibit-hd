package org.multibit.hd.ui.fest.use_cases.sidebar.send_request.send;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "send/request" amount screen by entering some notes</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.8
 *
 */
public class SendEnterNotesUseCase extends AbstractFestUseCase {

  private static final Logger log = LoggerFactory.getLogger(SendEnterNotesUseCase.class);

  public SendEnterNotesUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Next to show notes screen
    window
      .button(MessageKey.NEXT.getKey())
      // Allow time for the Bitcoin network to initialise
      .requireEnabled(timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.CONFIRM_SEND_TITLE);

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled(); // This is due to unrestricted

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Set the recipient editor text box to the MultiBit address
    log.debug("Setting private notes");
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Payment for something");

    // Verify the Next button is enabled
    window
      .button(MessageKey.SEND.getKey())
      .requireVisible()
      .requireEnabled(timeout(1, TimeUnit.SECONDS));

    // Leave amount screen showing

  }


}
