package org.multibit.hd.ui.fest.use_cases.sidebar.settings.appearance;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "show balance" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class VerifyAppearanceShowBalanceUseCase extends AbstractFestUseCase {

  public VerifyAppearanceShowBalanceUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "appearance"
    window
      .button(MessageKey.SHOW_APPEARANCE_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "appearance" wizard appears
    assertLabelText(MessageKey.SHOW_APPEARANCE_WIZARD);

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

    // Verify balance header is visible (no exchange)
    // TODO depends on sync status (which shows and hides balance
    // assertDisplayAmount("header", "balance", true, false);

    // Verify "Yes" is selected (0) then select "No"
    window
      .comboBox(MessageKey.SHOW_BALANCE.getKey())
      .requireSelection(0)
      .selectItem(Languages.safeText(MessageKey.NO));

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    pauseForViewReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify balance header is not visible (no exchange)
    // TODO depends on sync status (which shows and hides balance
    //assertDisplayAmount("header", "balance", false, false);

    // Click on "appearance"
    window
      .button(MessageKey.SHOW_APPEARANCE_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify "No" is selected (1) then select "Yes"
    window
      .comboBox(MessageKey.SHOW_BALANCE.getKey())
      .requireSelection(1)
      .selectItem(Languages.safeText(MessageKey.YES));

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    pauseForViewReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify balance header is visible (no exchange)
    // TODO depends on sync status (which shows and hides balance
    //assertDisplayAmount("header", "balance", true, false);
  }
}
