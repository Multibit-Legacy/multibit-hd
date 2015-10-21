package org.multibit.hd.ui.fest.use_cases.standard.credentials;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.WalletSummaryFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Quickly unlock a wallet as part of a larger test</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class QuickUnlockWalletUseCase extends AbstractFestUseCase {

  public QuickUnlockWalletUseCase(FrameFixture window) {
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

    // Verify there is a wallet selected
    // If this fails check the standard wallet fixture YAML
    window
      .comboBox(MessageKey.SELECT_WALLET.getKey())
      .requireEnabled()
      .requireNotEditable()
      .requireItemCount(1);

    // Enter credentials text
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .enterText(WalletSummaryFixtures.STANDARD_PASSWORD);

    // Click on unlock
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

  }

}
