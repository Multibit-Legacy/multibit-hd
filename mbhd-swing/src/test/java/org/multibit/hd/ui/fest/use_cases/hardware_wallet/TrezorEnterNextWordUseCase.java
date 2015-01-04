package org.multibit.hd.ui.fest.use_cases.hardware_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.HardwareWalletEventFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Trezor create wallet enter next word panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class TrezorEnterNextWordUseCase extends AbstractFestUseCase {

  public TrezorEnterNextWordUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Allow time for any hardware wallet events to propagate
    pauseForViewReset();

    // Check that the Trezor enter next word view is showing
    window
      .label(MessageKey.TREZOR_PRESS_NEXT_TITLE.getKey())
      .requireVisible();

    for (int i=1; i<13; i++) {

      // Click some buttons
      window
        .textBox(WelcomeWizardState.TREZOR_CREATE_WALLET_CONFIRM_WORD.name() + ".trezor_display")
        .requireText(Pattern.compile(".*"+i+"*"))
        .click();

      // Simulate a Trezor button click
      HardwareWalletEventFixtures.fireNextEvent();

    }

  }
}
