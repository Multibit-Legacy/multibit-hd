package org.multibit.hd.ui.fest.use_cases.welcome;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Unlock a wallet</li>
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
  public void execute() {

    window
      .label(MessageKey.WELCOME_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.WELCOME_TITLE));

    window
      .comboBox(MessageKey.SELECT_LANGUAGE.getKey())
      .requireSelection(7);

    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
