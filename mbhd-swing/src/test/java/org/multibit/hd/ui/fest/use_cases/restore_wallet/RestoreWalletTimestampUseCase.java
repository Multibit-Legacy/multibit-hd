package org.multibit.hd.ui.fest.use_cases.restore_wallet;

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
 * <li>Verify the welcome wizard "restore wallet timestamp and credentials" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class RestoreWalletTimestampUseCase extends AbstractFestUseCase {

  public RestoreWalletTimestampUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.RESTORE_WALLET_TIMESTAMP_TITLE);

    // Verify that the notes are present
    assertLabelText(MessageKey.RESTORE_TIMESTAMP_NOTE_1);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_TIMESTAMP.name(),
          "timestamp"
        )))
      .requireNotVisible();

    String timestamp = (String) parameters.get(MessageKey.TIMESTAMP.getKey());
    window
      .textBox(MessageKey.TIMESTAMP.getKey())
      .setText(timestamp)
        // Trigger the key release action
      .pressKey(KeyEvent.VK_SHIFT)
      .releaseKey(KeyEvent.VK_SHIFT);

    // Verify the credentials verification status is showing
    window
      .label(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_TIMESTAMP.name(),
          "timestamp"
        ))
      .requireVisible();

    // Verify we have a show button
    window
      .button(MessageKey.SHOW.getKey())
      .requireVisible();

    // Enter credentials
    window
      .textBox(MessageKey.ENTER_NEW_PASSWORD.getKey())
      .enterText(WalletFixtures.STANDARD_PASSWORD);

    // Verification status is not showing so requires a modified search
    window
      .label(newNotShowingJLabelFixture(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_TIMESTAMP.name(),
          "credentials"
        )))
      .requireNotVisible();

    // Retype credentials
    window
      .textBox(MessageKey.RETYPE_NEW_PASSWORD.getKey())
      .enterText(WalletFixtures.STANDARD_PASSWORD);

    // Verify the credentials verification status is showing
    window
      .label(
        getVerificationStatusName(
          WelcomeWizardState.RESTORE_WALLET_TIMESTAMP.name(),
          "credentials"
        ))
      .requireVisible();

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
