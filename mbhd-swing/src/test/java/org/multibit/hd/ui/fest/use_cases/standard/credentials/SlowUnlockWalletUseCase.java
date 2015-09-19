package org.multibit.hd.ui.fest.use_cases.standard.credentials;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.testing.WalletSummaryFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.themes.Themes;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Unlock a wallet with a failed credentials</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SlowUnlockWalletUseCase extends AbstractFestUseCase {

  public SlowUnlockWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Wait for Exit button to appear
    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
      // Allow a short time to overcome initialisation delays
      .requireEnabled(timeout(1, TimeUnit.SECONDS));

    // Ensure Unlock is not enabled
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireDisabled();

    // Enter incorrect credentials text
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .enterText(WalletSummaryFixtures.ALTERNATIVE_PASSWORD);

    // Verify show and hide
    window
      .button(MessageKey.SHOW.getKey())
      .click();
    window
      .button(MessageKey.HIDE.getKey())
      .click();

    // Click on unlock (expect failure)
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Fixed time to fail to unlock
    pauseForFailedWalletUnlock();

    // Verify report screen is showing in failure mode
    window
      .label(MessageKey.LOAD_WALLET_REPORT_TITLE.getKey())
      .requireVisible();

    // Test for failed wallet load
    assertLabelContainsValue(
      CoreMessageKey.WALLET_LOADING.getKey(),
      Languages.safeText(CoreMessageKey.WALLET_BAD_PASSWORD));

    // Verify buttons
    window
      .button(MessageKey.FINISH.getKey())
      .requireDisabled();
    window
      .button(MessageKey.PREVIOUS.getKey())
      .click();

    // Enter new credentials
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .deleteText()
      .enterText(WalletSummaryFixtures.STANDARD_PASSWORD);

    // Verify new attempt colouring
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .background()
      .requireEqualTo(Themes.currentTheme.dataEntryBackground());

    // Verify show and hide
    window
      .button(MessageKey.SHOW.getKey())
      .click();
    window
      .button(MessageKey.HIDE.getKey())
      .click();

    // Click on unlock
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Hand over to "unlock report use case"

  }

}
