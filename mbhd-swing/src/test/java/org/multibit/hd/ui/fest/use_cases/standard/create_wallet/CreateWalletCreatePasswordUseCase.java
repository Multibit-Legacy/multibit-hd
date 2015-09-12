package org.multibit.hd.ui.fest.use_cases.standard.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "create wallet create credentials" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class CreateWalletCreatePasswordUseCase extends AbstractFestUseCase {

  public CreateWalletCreatePasswordUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.CREATE_WALLET_PASSWORD_TITLE);

    window
      .label(MessageKey.WALLET_PASSWORD_NOTE_1.getKey())
      .requireVisible();

    window
      .label(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .requireVisible();

    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .requireVisible()
      .requireEmpty();

    window
      .label(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .requireVisible();

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .requireVisible()
      .requireEmpty();

    window
      .button(MessageKey.SHOW.getKey())
      .requireVisible();

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD.name(),
          "credentials"
        )))
      .requireNotVisible();

    // Verify interactions
    verifyPassword();
    verifyPasswordShows();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

  private void verifyPassword() {

    // Matching credentials
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.STANDARD_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.STANDARD_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .label(
        getVerificationStatusName(
          WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD.name(),
          "credentials"
        ))
      .requireVisible();

    // Almost correct (short)
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText("abc12")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.STANDARD_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD.name(),
          "credentials"
        )))
      .requireNotVisible();

    // Almost correct (long)
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.STANDARD_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.STANDARD_PASSWORD+"4")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD.name(),
          "credentials"
        )))
      .requireNotVisible();

  }

  /**
   * This is purely a visual test since the credentials echo char cannot be detected by FEST
   */
  private void verifyPasswordShows() {

    String password1 = WalletFixtures.STANDARD_PASSWORD;

    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText(password1)
      // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(password1)
      // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Show the credentials
    window
      .button(MessageKey.SHOW.getKey())
      .click();

    // Hide the credentials
    window
      .button(MessageKey.HIDE.getKey())
      .click();

  }

}
