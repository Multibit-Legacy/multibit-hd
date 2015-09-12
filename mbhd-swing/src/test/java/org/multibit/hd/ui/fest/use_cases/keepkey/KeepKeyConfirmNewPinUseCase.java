package org.multibit.hd.ui.fest.use_cases.keepkey;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Keepkey create wallet confirm new PIN panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class KeepKeyConfirmNewPinUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public KeepKeyConfirmNewPinUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the Keepkey confirm new PIN panel view is showing
    window
      .label(MessageKey.CHANGE_PIN_CONFIRM_NEW_PIN_TITLE.getKey())
      .requireVisible();

    // Click some buttons
    window
      .button(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_1")
      .click();
    window
      .button(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_2")
      .click();
    window
      .button(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_3")
      .click();
    window
      .button(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".button_4")
      .click();

    // Check that the PIN entry has populated
    window
      .textBox(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_NEW_PIN.name()+".textbox")
      .requireText("****");

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();
  }
}
