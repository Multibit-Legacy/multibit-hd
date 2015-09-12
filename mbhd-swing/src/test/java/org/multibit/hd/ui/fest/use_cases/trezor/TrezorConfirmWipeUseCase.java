package org.multibit.hd.ui.fest.use_cases.trezor;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor "press confirm wipe device" screen</li>
 * </ul>
 *
 * <p>Require the panel view to be showing (event triggered earlier)</p>
 *
 * @since 0.0.5
 */
public class TrezorConfirmWipeUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public TrezorConfirmWipeUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for hardware events to propagate
    pauseForHardwareEvent();

    // Check that the Trezor press confirm to wipe panel view is showing
    window
      .label(MessageKey.TREZOR_PRESS_CONFIRM_TITLE.getKey())
      .requireVisible();

    window
      .textBox(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET.name() + ".trezor_display")
      .requireText(Languages.safeText(MessageKey.TREZOR_WIPE_CONFIRM_DISPLAY))
      .requireVisible();

  }
}
