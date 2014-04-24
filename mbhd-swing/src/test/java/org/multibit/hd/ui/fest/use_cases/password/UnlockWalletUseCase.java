package org.multibit.hd.ui.fest.use_cases.password;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Unlock a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UnlockWalletUseCase extends AbstractFestUseCase {

  public UnlockWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Ensure Unlock is not enabled
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireDisabled();

    // Enter password text
    window
      .textBox(MessageKey.ENTER_PASSWORD.getKey())
      .enterText("abc123");

    // Click on unlock
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Fixed time to unlock
    Pause.pause(2, TimeUnit.SECONDS);

  }

}
