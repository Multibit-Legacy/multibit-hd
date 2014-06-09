package org.multibit.hd.ui.fest.use_cases.tools.verify_message;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen verify message wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowThenFinishVerifyMessageUseCase extends AbstractFestUseCase {

  public ShowThenFinishVerifyMessageUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Verify message
    window
      .button(MessageKey.SHOW_VERIFY_WIZARD.getKey())
      .click();

    // Verify the "verify message" wizard appears
    assertLabelText(MessageKey.VERIFY_MESSAGE_TITLE);

    // Verify finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // Create a message to sign
    window
      .textBox(MessageKey.MESSAGE.getKey())
      .setText("A message for Bob Cratchit");

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
