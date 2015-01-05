package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor create wallet confirm new PIN panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorConfirmNewPinUseCase extends AbstractFestUseCase {

  public TrezorConfirmNewPinUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the Trezor confirm new PIN panel view is showing
    window
      .label(MessageKey.CHANGE_PIN_CONFIRM_NEW_PIN_TITLE.getKey())
      .requireVisible();

    // Click some buttons
    window
      .button(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_1")
      .click();
    window
      .button(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_2")
      .click();
    window
      .button(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_3")
      .click();
    window
      .button(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_4")
      .click();

    // Check that the PIN entry has populated
    window
      .textBox(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".textbox")
      .requireText("****");

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();
  }
}
