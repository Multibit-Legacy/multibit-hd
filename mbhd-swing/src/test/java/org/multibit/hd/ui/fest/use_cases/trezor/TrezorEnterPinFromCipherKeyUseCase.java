package org.multibit.hd.ui.fest.use_cases.trezor;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
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
public class TrezorEnterPinFromCipherKeyUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public TrezorEnterPinFromCipherKeyUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for UI to catch up with events
    pauseForViewReset();

    if (OSUtils.isDebuggerAttached()) {

      // Expect debug popover to be showing
      window
        .panel(CoreMessageKey.DEBUGGER_ATTACHED.getKey())
        .requireVisible();

      // Dismiss
      window
        .button("environment_alert." + MessageKey.CLOSE.getKey())
        .requireVisible()
        .requireEnabled()
        .click();
    }

    // Allow time for the popover to close
    pauseForViewReset();

    // Check that the Trezor enter new PIN panel view is showing
    window
      .label(MessageKey.PIN_TITLE.getKey())
      .requireVisible();

    // Click some buttons
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY.name() + ".button_1")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY.name() + ".button_2")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY.name() + ".button_3")
      .requireEnabled()
      .click();
    window
      .button(CredentialsState.CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY.name() + ".button_4")
      .requireEnabled()
      .click();

    // Check that the PIN entry has populated
    window
      .textBox(CredentialsState.CREDENTIALS_ENTER_PIN_FROM_CIPHER_KEY.name() + ".textbox")
      .requireText("****");

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.PASSWORD_UNLOCK.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
