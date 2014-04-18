package org.multibit.hd.ui.fest.use_cases.welcome;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import static org.fest.assertions.Assertions.assertThat;

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
      .requireSelection(0);

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
    verifySeedPhraseHides();
    verifySeedPhraseRefreshes();
    verifySeedPhraseSizeChanges();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

  private void verifySeedPhraseHides() {

    String seedPhrase1 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    // Hide the phrase
    window
      .button(MessageKey.HIDE.getKey())
      .click();

    String seedPhrase2 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase2).isNotEqualTo(seedPhrase1);

    // Show the phrase
    window
      .button(MessageKey.HIDE.getKey())
      .click();

    String seedPhrase3 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase3).isEqualTo(seedPhrase1);

  }

  private void verifySeedPhraseRefreshes() {

    String seedPhrase1 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    window
      .button(MessageKey.REFRESH.getKey())
      .click();

    String seedPhrase2 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase2).isNotEqualTo(seedPhrase1);

  }

  private void verifySeedPhraseSizeChanges() {

    String seedPhrase1 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    window
      .comboBox(MessageKey.SEED_SIZE.getKey())
      .selectItem(1);

    String seedPhrase2 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    // Expect at least 24 extra characters (6 words * 4 minimum length)
    assertThat(seedPhrase2.length()).isGreaterThan(seedPhrase1.length()+24);

    window
      .comboBox(MessageKey.SEED_SIZE.getKey())
      .selectItem(2);

    String seedPhrase3 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    // Expect at least 24 extra characters (6 words * 4 minimum length)
    assertThat(seedPhrase3.length()).isGreaterThan(seedPhrase2.length()+24);

  }

}
