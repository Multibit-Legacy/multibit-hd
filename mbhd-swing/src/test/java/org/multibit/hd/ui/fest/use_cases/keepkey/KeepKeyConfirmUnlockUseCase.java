package org.multibit.hd.ui.fest.use_cases.keepkey;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsState;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Keepkey "press confirm unlock device" screen</li>
 * </ul>
 *
 * <p>Require the panel view to be showing (event triggered earlier)</p>
 *
 * @since 0.0.5
 */
public class KeepKeyConfirmUnlockUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public KeepKeyConfirmUnlockUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for UI to catch up with events
    pauseForViewReset();

    // Check that the Keepkey press confirm to unlock panel view is showing
    window
      .label(MessageKey.HARDWARE_PRESS_CONFIRM_TITLE.getKey())
      .requireVisible();

    window
      .textBox(CredentialsState.CREDENTIALS_PRESS_CONFIRM_FOR_UNLOCK.name() + ".keepkey_display")
      .requireText(Languages.safeText(MessageKey.KEEP_KEY_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY))
      .requireVisible();

  }
}
