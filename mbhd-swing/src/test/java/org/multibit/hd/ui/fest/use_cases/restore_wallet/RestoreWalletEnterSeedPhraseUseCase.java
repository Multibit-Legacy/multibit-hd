package org.multibit.hd.ui.fest.use_cases.restore_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "restore wallet enter seed phrase" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletEnterSeedPhraseUseCase extends AbstractFestUseCase {

  public RestoreWalletEnterSeedPhraseUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.RESTORE_WALLET_SEED_PHRASE_TITLE);

    assertLabelText(MessageKey.RESTORE_FROM_SEED_PHRASE_NOTE_1);

    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .requireVisible()
      .requireEmpty();

    window
      .button(MessageKey.HIDE.getKey())
      .requireVisible();

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_SEED_PHRASE.name(),
          "seedphrase"
        )))
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

    // Correct seed phrase
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase1)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is showing
    window
      .label(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_SEED_PHRASE.name(),
          "seedphrase"
        ))
      .requireVisible();

    // Almost correct seed phrase (short)
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase2)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_SEED_PHRASE.name(),
          "seedphrase"
        )))
      .requireNotVisible();

    // Almost correct seed phrase (long)
    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase3)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_SEED_PHRASE.name(),
          "seedphrase"
        )))
      .requireNotVisible();

  }

  private void verifySeedPhraseHides(Map<String, Object> parameters) {

    String seedPhrase1 = (String) parameters.get(MessageKey.SEED_PHRASE.getKey());

    window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .setText(seedPhrase1);

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
      .button(MessageKey.SHOW.getKey())
      .click();

    String seedPhrase3 = window
      .textBox(MessageKey.SEED_PHRASE.getKey())
      .text();

    assertThat(seedPhrase3).isEqualTo(seedPhrase1);

  }

}
