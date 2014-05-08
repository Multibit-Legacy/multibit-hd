package org.multibit.hd.ui.fest.use_cases.tools;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen sign message wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class ShowThenFinishSignMessageUseCase extends AbstractFestUseCase {

  public ShowThenFinishSignMessageUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Sign message
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .click();

    // Verify the sign message wizard appears
    window
      .label(MessageKey.SIGN_MESSAGE_TITLE.getKey());

    // Verify buttons
    window
      .button(MessageKey.SIGN_MESSAGE.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.CLEAR_ALL.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify text boxes
    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .requireEnabled()
      .requireEditable();

    window
      .textBox(MessageKey.MESSAGE.getKey())
      .requireEnabled()
      .requireEditable();

    window
      .textBox(MessageKey.SIGNATURE.getKey())
      .requireEnabled()
      .requireNotEditable();

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
