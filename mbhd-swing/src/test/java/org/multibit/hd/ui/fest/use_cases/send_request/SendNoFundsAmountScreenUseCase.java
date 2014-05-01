package org.multibit.hd.ui.fest.use_cases.send_request;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

  public SendNoFundsAmountScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Send allowing for network initialisation
    window
      .button(MessageKey.SHOW_SEND_WIZARD.getKey())
      // Allow time for the Bitcoin network to initialise
      .requireEnabled(Timeout.timeout(5, TimeUnit.SECONDS))
      .click();

    // Verify the wizard appears
    window
      .label(MessageKey.SEND_BITCOIN_TITLE.getKey());

    // Verify buttons
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Set the recipient editor text box to the MultiBit address
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
    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .enterText("100.0");

    // Change focus to trigger validation
    window
      .textBox(MessageKey.LOCAL_AMOUNT.getKey())
      .focus();

    // Verify the Next button is enabled
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled();

    // Leave amount screen showing

  }


}
