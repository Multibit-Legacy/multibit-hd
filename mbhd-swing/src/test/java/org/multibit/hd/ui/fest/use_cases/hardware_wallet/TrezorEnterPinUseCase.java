package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsState;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor create wallet enter PIN panel (unlock)</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorEnterPinUseCase extends AbstractFestUseCase {

  public TrezorEnterPinUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for UI to catch up with events
    pauseForViewReset();

    // Check that the Trezor enter new PIN panel view is showing
    window
      .label(MessageKey.PIN_TITLE.getKey())
      .requireVisible();

    // Click some buttons
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN.name() + ".button_1")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN.name() + ".button_2")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN.name() + ".button_3")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN.name() + ".button_4")
      .requireEnabled()
      .click();

    // Check that the PIN entry has populated
    window
      .textBox(CredentialsState.CREDENTIALS_ENTER_PIN.name() + ".textbox")
      .requireText("****");

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
