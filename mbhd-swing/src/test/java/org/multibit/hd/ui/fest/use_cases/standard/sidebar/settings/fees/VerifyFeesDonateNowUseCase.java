package org.multibit.hd.ui.fest.use_cases.standard.sidebar.settings.fees;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "donate now" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class VerifyFeesDonateNowUseCase extends AbstractFestUseCase {

  public VerifyFeesDonateNowUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {
    // Test slide to minimum value

    // Click on "fees"
    window
      .button(MessageKey.FEES_SETTINGS_TITLE.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "fees" wizard appears
    assertLabelText(MessageKey.FEES_SETTINGS_TITLE);

    // Verify Apply is present
    window
      .button(MessageKey.APPLY.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify Cancel is present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Click on the donate now button
    window
      .button(MessageKey.DONATE_NOW.getKey())
      .click();

    pauseForViewReset();

    // Verify the send screen is shown
    assertLabelText(MessageKey.SEND_BITCOIN_TITLE);

    // Click on the cancel button on the send screen
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Check that the Settings screen is shown
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }
}
