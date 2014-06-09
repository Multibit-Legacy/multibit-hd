package org.multibit.hd.ui.fest.use_cases.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "create wallet confirm seed phrase" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletConfirmSeedPhraseUseCase extends AbstractFestUseCase {

  public CreateWalletConfirmSeedPhraseUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.CONFIRM_WALLET_SEED_PHRASE_TITLE);

    window
      .label(MessageKey.CONFIRM_SEED_PHRASE_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.TIMESTAMP.getKey())
      .requireVisible();

    window
      .textBox(MessageKey.TIMESTAMP.getKey())
      .requireVisible()
      .requireEmpty();

    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .requireVisible()
      .requireEmpty();

    window
      .button(MessageKey.HIDE.getKey())
      .requireVisible();

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(MessageKey.VERIFICATION_STATUS.getKey()))
      .requireNotVisible();

    // Verify interactions
    verifySeedPhrase(parameters);
    verifySeedPhraseHides(parameters);

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

  private void verifySeedPhrase(Map<String, Object> parameters) {

    String seedPhrase1 = (String) parameters.get(MessageKey.SEED_PHRASE.getKey());
    String seedPhrase2 = ((String) parameters.get(MessageKey.SEED_PHRASE.getKey())).substring(1);
    String seedPhrase3 = parameters.get(MessageKey.SEED_PHRASE.getKey()) + "x";

    String timestamp = (String) parameters.get(MessageKey.TIMESTAMP.getKey());

    window
      .textBox(MessageKey.TIMESTAMP.getKey())
      .setText(timestamp)
       // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Correct seed phrase
    log.debug("Sending correct seed phrase {}", seedPhrase1);
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase1)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .label(MessageKey.VERIFICATION_STATUS.getKey())
      .requireVisible();

    // Almost correct seed phrase (short)
    log.debug("Sending short seed phrase");
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase2)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(MessageKey.VERIFICATION_STATUS.getKey()))
      .requireNotVisible();

    // Almost correct seed phrase (long)
    log.debug("Sending long seed phrase");
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase3)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(MessageKey.VERIFICATION_STATUS.getKey()))
      .requireNotVisible();

  }

  private void verifySeedPhraseHides(Map<String, Object> parameters) {

    String seedPhrase1 = (String) parameters.get(MessageKey.SEED_PHRASE.getKey());

    log.debug("Sending correct seed phrase {}", seedPhrase1);

    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase1);

    // Hide the phrase
    window
      .button(MessageKey.HIDE.getKey())
      .click();

    log.debug("Sending correct seed phrase (hidden)");
    String seedPhrase2 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase2).isNotEqualTo(seedPhrase1);

    // Show the phrase
    window
      .button(MessageKey.SHOW.getKey())
      .click();

    log.debug("Sending correct seed phrase (showing)");
    String seedPhrase3 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase3).isEqualTo(seedPhrase1);

  }

}
