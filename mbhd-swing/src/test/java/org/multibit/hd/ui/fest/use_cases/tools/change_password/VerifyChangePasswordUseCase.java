package org.multibit.hd.ui.fest.use_cases.tools.change_password;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.testing.WalletFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.change_password.ChangePasswordState;

import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "tools" screen change password wizard works</li>
 * </ul>
 * <p>Requires the "tools" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class VerifyChangePasswordUseCase extends AbstractFestUseCase {

  public VerifyChangePasswordUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on Change password
    window
      .button(MessageKey.SHOW_CHANGE_PASSWORD_WIZARD.getKey())
      .click();

    // Verify the "change password" wizard appears
    assertLabelText(MessageKey.CHANGE_PASSWORD_TITLE);

    // Verify buttons are present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled();

    // Fill in the original password
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .enterText(WalletFixtures.STANDARD_PASSWORD);

    // Exercise the confirm password component
    // finishing with a valid password
    verifyPassword();

    // Click next
    window
      .button(MessageKey.NEXT.getKey())
      .click();

    assertLabelText(MessageKey.CHANGE_PASSWORD_TITLE);

    // Allow time for the password change to occur
    pauseForWalletPasswordChange();

    // Verify the status message is OK
    window
      .label(MessageKey.PASSWORD_CHANGED_STATUS.getKey())
      .requireText(Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_SUCCESS));

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();


    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();
  }

  private void verifyPassword() {

    // Almost correct (short)
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText("def45")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.ALTERNATIVE_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name(),
          "password"
        )))
      .requireNotVisible();

    // Almost correct (long)
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.ALTERNATIVE_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.ALTERNATIVE_PASSWORD+"7")
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name(),
          "password"
        )))
      .requireNotVisible();

    // Matching password
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.ALTERNATIVE_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .setText(WalletFixtures.ALTERNATIVE_PASSWORD)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    window
      .label(
        getVerificationStatusName(
          ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD.name(),
          "password"
        ))
      .requireVisible();

  }

}
