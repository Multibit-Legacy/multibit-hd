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
 * <li>Verify the "send/request" amount screen with no funds</li>
 * </ul>
 * <p>Requires the "send/request" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class SendNoFundsAmountScreenUseCase extends AbstractFestUseCase {

  private static final Logger log = LoggerFactory.getLogger(SendNoFundsAmountScreenUseCase.class);

  public SendNoFundsAmountScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Send allowing for network initialisation
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      // Allow time for the Bitcoin network to initialise
      .requireEnabled(timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    assertLabelText(MessageKey.SEND_BITCOIN_TITLE);

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
    log.debug("Setting recipient");
    window
      .textBox(MessageKey.RECIPIENT.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // Change focus to trigger validation
    window
      .button(MessageKey.PASTE.getKey())
      .focus();

    // Verify the Next button is disabled (no amount)
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Set a nominal amount for sending (the wallet is empty)
    log.debug("Setting amount");
    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .setText("")
      .enterText("100.0");

    // Change focus to trigger validation
    window
      .button(MessageKey.PASTE.getKey())
      .focus();

    // Verify the Next button is enabled
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled(timeout(1, TimeUnit.SECONDS));

    // Leave amount screen showing

  }


}
