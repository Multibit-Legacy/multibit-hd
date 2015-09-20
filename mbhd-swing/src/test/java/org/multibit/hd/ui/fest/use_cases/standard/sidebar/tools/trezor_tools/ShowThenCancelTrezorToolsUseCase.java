package org.multibit.hd.ui.fest.use_cases.standard.sidebar.tools.trezor_tools;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen Trezor wizard shows</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowThenCancelTrezorToolsUseCase extends AbstractFestUseCase {

  public ShowThenCancelTrezorToolsUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "Trezor"
    window
      .button(MessageKey.SHOW_HARDWARE_TOOLS_WIZARD.getKey())
      .click();

    // Verify the "Trezor tools" wizard appears
    assertLabelText(MessageKey.USE_HARDWARE_TITLE);

    // Verify Cancel is present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_SIGN_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
