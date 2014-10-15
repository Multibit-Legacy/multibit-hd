package org.multibit.hd.ui.fest.use_cases.sidebar.settings.labs;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "toggle Trezor" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class VerifyLabsToggleTrezorUseCase extends AbstractFestUseCase {

  public VerifyLabsToggleTrezorUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "labs"
    window
      .button(MessageKey.SHOW_LABS_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "labs" wizard appears
    assertLabelText(MessageKey.LABS_SETTINGS_TITLE);

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

    // Verify Trezor "Yes" is selected (0) then select "No"
    window
      .comboBox(MessageKey.SELECT_TREZOR.getKey())
      .requireSelection(0)
      .selectItem(Languages.safeText(MessageKey.NO));

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    pauseForComponentReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify configuration has changed
    assertThat(Configurations.currentConfiguration.isTrezor()).isFalse();

    // Click on "labs"
    window
      .button(MessageKey.SHOW_LABS_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify "No" is selected (1) then select "Yes"
    window
      .comboBox(MessageKey.SELECT_TREZOR.getKey())
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

    // Verify configuration has changed
    assertThat(Configurations.currentConfiguration.isTrezor()).isTrue();

  }

}
