package org.multibit.hd.ui.fest.use_cases.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.fest.use_cases.security.CloseDebugSecurityPopoverUseCase;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select language" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WelcomeSelectLanguageUseCase extends AbstractFestUseCase {

  public WelcomeSelectLanguageUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    window
      .label(MessageKey.WELCOME_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.WELCOME_TITLE));

    // Verify that English (United States) is selected by default
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .requireSelection(LanguageKey.EN_US.ordinal());

    // Verify that the English welcome note is showing
    String welcomeNote1_en = window
      .label(MessageKey.WELCOME_NOTE_1.getKey())
      .text();

    assertThat(welcomeNote1_en).contains("MultiBit HD provides access to the Bitcoin network.");

    // Switch to Romanian
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .selectItem(LanguageKey.RO_RO.ordinal());

    pauseForViewReset();

    // Clear any security popover
    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    // Verify that the Romanian welcome note is showing
    String welcomeNote1_ro = window
      .label(MessageKey.WELCOME_NOTE_1.getKey())
      .text();

    assertThat(welcomeNote1_ro).contains("MultiBit HD oferă acces la rețeaua Bitcoin.");

    // Switch back to English (United States)
    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .selectItem(LanguageKey.EN_US.ordinal());

    pauseForViewReset();

    // Clear any security popover
    new CloseDebugSecurityPopoverUseCase(window).execute(parameters);

    // Verify that the Romanian welcome note is showing
    welcomeNote1_en = window
      .label(MessageKey.WELCOME_NOTE_1.getKey())
      .text();

    assertThat(welcomeNote1_en).contains("MultiBit HD provides access to the Bitcoin network.");

    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
