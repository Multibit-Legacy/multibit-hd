package org.multibit.hd.ui.fest.use_cases.standard.welcome_select;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.fest.use_cases.standard.environment.CloseDebugEnvironmentPopoverUseCase;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select language" panel view</li>
 * <li>Selected language will be ru_RU</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WelcomeSelectLanguage_ru_RU_UseCase extends AbstractFestUseCase {

  public WelcomeSelectLanguage_ru_RU_UseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertLabelText(MessageKey.SELECT_LANGUAGE_TITLE);

    // Verify that English (United States) is selected by default
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .requireSelection(LanguageKey.EN_US.ordinal());

    // Switch to Russian in Russia
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .selectItem(LanguageKey.RU_RU.ordinal());

    pauseForViewReset();

    // Clear any environment popover
    new CloseDebugEnvironmentPopoverUseCase(window).execute(parameters);

    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
