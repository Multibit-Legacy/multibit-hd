package org.multibit.hd.ui.fest.use_cases.send_request;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add Alice contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
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

    // Click on Send
    window
      .button(MessageKey.SEND.getKey())
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

    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    window
      .textBox(MessageKey.BITCOIN_AMOUNT.getKey())
      .setText("10.0"); // 1 mBTC

    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

  }


}
