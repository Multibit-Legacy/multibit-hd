package org.multibit.hd.ui.fest.use_cases.sidebar.settings.fees;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "settings" screen fee wizard shows</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class ShowThenCancelFeesUseCase extends AbstractFestUseCase {

  public ShowThenCancelFeesUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "appearance"
    window
      .button(MessageKey.FEES_SETTINGS_TITLE.getKey())
      .click();

    // Verify the "fees" wizard appears
    assertLabelText(MessageKey.FEES_SETTINGS_TITLE);

    // Verify cancel is present
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
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
