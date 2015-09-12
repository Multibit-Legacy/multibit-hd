package org.multibit.hd.ui.fest.use_cases.standard.restore_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "restore wallet enter seed phrase" panel view with a Beta7 wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class RestoreBeta7WalletEnterSeedPhraseUseCase extends AbstractFestUseCase {

  public RestoreBeta7WalletEnterSeedPhraseUseCase(FrameFixture window) {
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
    verifyWalletType(parameters);

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


  private void verifyWalletType(Map<String, Object> parameters) {
    String seedPhrase1 = (String) parameters.get(MessageKey.SEED_PHRASE.getKey());

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

    // Verify "MultiBit HD BIP32" wallet type is selected (0) then select "Trezor" (should be 2 as MultiBit HD Beta 7 option is present)
    window
      .comboBox(MessageKey.SELECT_WALLET_TYPE.getKey())
      .requireSelection(0)
      .selectItem(Languages.safeText(MessageKey.SELECT_WALLET_TYPE_BIP44));

    pauseForViewReset();

    // Verify "Trezor" wallet type is selected (2) then select "MultiBit HD Beta 7"
    window
      .comboBox(MessageKey.SELECT_WALLET_TYPE.getKey())
      .requireSelection(2)
      .selectItem(Languages.safeText(MessageKey.SELECT_WALLET_TYPE_BETA7));

    pauseForViewReset();

    // Verify "Beta 7" wallet type is selected (1) then select "MultiBit HD BIP32"
    window
      .comboBox(MessageKey.SELECT_WALLET_TYPE.getKey())
      .requireSelection(1)
      .selectItem(Languages.safeText(MessageKey.SELECT_WALLET_TYPE_BIP32));

    pauseForViewReset();

    // Verify "MultiBit HD BIP32" wallet type is selected (0)
    window
       .comboBox(MessageKey.SELECT_WALLET_TYPE.getKey())
       .requireSelection(0);

    pauseForViewReset();
  }
}
