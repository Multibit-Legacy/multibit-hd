package org.multibit.hd.ui.fest.use_cases.sidebar.settings.appearance;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.themes.BootstrapTheme;
import org.multibit.hd.ui.views.themes.LightTheme;
import org.multibit.hd.ui.views.themes.Themes;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "change theme" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class VerifyAppearanceThemeUseCase extends AbstractFestUseCase {

  public VerifyAppearanceThemeUseCase(FrameFixture window) {
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
    assertLabelText(MessageKey.APPEARANCE_SETTINGS_TITLE);

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

    // Verify theme header color is "Bootstrap"
    assertThat(Themes.currentTheme.headerPanelBackground()).isEqualTo(new BootstrapTheme().headerPanelBackground());

    // Verify "Bootstrap" is selected (2) then select "Light theme"
    window
      .comboBox(MessageKey.SELECT_THEME.getKey())
      .requireSelection(2)
      .selectItem(Languages.safeText(MessageKey.LIGHT_THEME));

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

    // Verify theme header color is "Light Theme"
    assertThat(Themes.currentTheme.headerPanelBackground()).isEqualTo(new LightTheme().headerPanelBackground());

    // Click on "appearance"
    window
      .button(MessageKey.SHOW_APPEARANCE_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify "Light Theme" is selected (0) then select "Light theme"
    window
      .comboBox(MessageKey.SELECT_THEME.getKey())
      .requireSelection(0)
      .selectItem(Languages.safeText(MessageKey.BOOTSTRAP_THEME));

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

    // Verify theme header color is "Bootstrap"
    assertThat(Themes.currentTheme.headerPanelBackground()).isEqualTo(new BootstrapTheme().headerPanelBackground());

  }

}
