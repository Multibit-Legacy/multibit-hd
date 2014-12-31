package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import com.google.common.util.concurrent.Uninterruptibles;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the user can initialise a wiped Trezor</li>
 * </ul>
 *
 * <p>Part of the overall "wiped Trezor attached at cold start" use case</p>
 *
 * @since 0.0.5
 */
public class InitialiseTrezorUseCase extends AbstractFestUseCase {

  public InitialiseTrezorUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Start the attach use case
    HardwareWalletEventFixtures.newInitialiseTrezorUseCase();

    // Allow time for the view to react
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Check that the Trezor preparation screen is showing
    window
      .label(MessageKey.CREATE_TREZOR_WALLET_PREPARATION_TITLE.getKey())
      .requireVisible();

    // Check the 'Next' button is present and click it
    window
      .button(MessageKey.NEXT.getKey())
      .requireEnabled(Timeout.timeout(3, TimeUnit.SECONDS))
      .click();

  }
}
