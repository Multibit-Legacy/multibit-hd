package org.multibit.hd.ui.fest.use_cases.welcome_select;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select language" panel view</li>
 * <li>Selected language will be ro_RO</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeSelectLanguage_ro_RO_UseCase extends AbstractFestUseCase {

  public WelcomeSelectLanguage_ro_RO_UseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertLabelText(MessageKey.WELCOME_TITLE);

    // Verify that English (United States) is selected by default
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .requireSelection(LanguageKey.EN_US.ordinal());

    // Verify that the English welcome note is showing
    assertLabelText(MessageKey.WELCOME_NOTE_1);

    // Switch to Romanian
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .selectItem(LanguageKey.RO_RO.ordinal());

    pauseForViewReset();

    // Clear any security popover
    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    // Verify that the Romanian welcome note is showing
    assertLabelText(MessageKey.WELCOME_NOTE_1);

    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
