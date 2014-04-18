package org.multibit.hd.ui.fest.use_cases.welcome;

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
 * <li>Verify the welcome wizard "create wallet create password" panel view</li>
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

    window
      .label(MessageKey.CREATE_WALLET_PASSWORD_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.CREATE_WALLET_PASSWORD_TITLE));

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
      .label(newNotShowingJLabelFixture(WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD+"."+MessageKey.VERIFICATION_STATUS.getKey()))
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

    // Matching password
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText("abc123")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText("abc123")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .label(WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD+"."+MessageKey.VERIFICATION_STATUS.getKey())
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
      .setText("abc123")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD+"."+MessageKey.VERIFICATION_STATUS.getKey()))
      .requireNotVisible();

    // Almost correct (long)
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText("abc123")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText("abc1234")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(WelcomeWizardState.CREATE_WALLET_CREATE_PASSWORD+"."+MessageKey.VERIFICATION_STATUS.getKey()))
      .requireNotVisible();

  }

  /**
   * This is purely a visual test since the password echo char cannot be detected by FEST
   */
  private void verifyPasswordShows() {

    String password1 = "abc123";

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

    // Show the password
    window
      .button(MessageKey.SHOW.getKey())
      .click();

    // Hide the password
    window
      .button(MessageKey.SHOW.getKey())
      .click();

  }

}
