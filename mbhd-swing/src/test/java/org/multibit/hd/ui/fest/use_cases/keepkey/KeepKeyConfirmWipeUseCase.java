package org.multibit.hd.ui.fest.use_cases.keepkey;

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
 * <li>Verify the Keepkey "press confirm wipe device" screen</li>
 * </ul>
 *
 * <p>Require the panel view to be showing (event triggered earlier)</p>
 *
 * @since 0.0.5
 */
public class KeepKeyConfirmWipeUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public KeepKeyConfirmWipeUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for hardware events to propagate
    pauseForHardwareEvent();

    // Check that the Keepkey press confirm to wipe panel view is showing
    window
      .label(MessageKey.HARDWARE_PRESS_CONFIRM_TITLE.getKey())
      .requireVisible();

    window
      .textBox(WelcomeWizardState.HARDWARE_CREATE_WALLET_CONFIRM_CREATE_WALLET.name() + ".keepkey_display")
      .requireText(Languages.safeText(MessageKey.KEEP_KEY_WIPE_CONFIRM_DISPLAY))
      .requireVisible();

  }
}
