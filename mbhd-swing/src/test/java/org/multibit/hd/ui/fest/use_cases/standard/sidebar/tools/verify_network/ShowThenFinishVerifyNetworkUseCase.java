package org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.verify_network;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen verify network wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowThenFinishVerifyNetworkUseCase extends AbstractFestUseCase {

  public ShowThenFinishVerifyNetworkUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Verify network
    window
      .button(MessageKey.SHOW_VERIFY_NETWORK_WIZARD.getKey())
      .click();

    // Verify the "verify network" wizard appears
    assertLabelText(MessageKey.VERIFY_NETWORK_TITLE);

    // Verify Finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

   // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }
}
