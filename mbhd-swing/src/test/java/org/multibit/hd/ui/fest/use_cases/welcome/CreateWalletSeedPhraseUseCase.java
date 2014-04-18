package org.multibit.hd.ui.fest.use_cases.welcome;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "create wallet seed phrase" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletSeedPhraseUseCase extends AbstractFestUseCase {

  public CreateWalletSeedPhraseUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute() {

    window
      .label(MessageKey.CREATE_WALLET_SEED_PHRASE_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.CREATE_WALLET_SEED_PHRASE_TITLE));

    window
      .label(MessageKey.SEED_WARNING_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP.getKey())
      .requireVisible();

    window
      .textBox(MessageKey.TIMESTAMP.getKey())
      .requireVisible();

    window
      .label(MessageKey.SEED_SIZE.getKey())
      .requireVisible();

    window
      .comboBox(MessageKey.SEED_SIZE.getKey())
      .requireEnabled()
      .requireVisible()
      .requireSelection(0)
      .click();

    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .requireVisible();

    window
      .button(MessageKey.HIDE.getKey())
      .requireVisible();

    window
      .button(MessageKey.REFRESH.getKey())
      .requireVisible();

    // Verify interactions
    //verifySelectWalletEnabled();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
