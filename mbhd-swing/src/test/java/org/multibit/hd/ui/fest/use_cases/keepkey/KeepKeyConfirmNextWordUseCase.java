package org.multibit.hd.ui.fest.use_cases.keepkey;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.hardware_wallet_fixtures.HardwareWalletFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractHardwareWalletFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the Keepkey create wallet confirm next word panel</li>
 * </ul>
 *
 * @since 0.0.5
 */
public class KeepKeyConfirmNextWordUseCase extends AbstractHardwareWalletFestUseCase {

  /**
   * @param window                The FEST window frame fixture
   * @param hardwareWalletFixture The hardware wallet fixture
   */
  public KeepKeyConfirmNextWordUseCase(FrameFixture window, HardwareWalletFixture hardwareWalletFixture) {
    super(window, hardwareWalletFixture);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Check that the Keepkey enter next word view is showing
    window
      .label(MessageKey.KEEPKEY_PRESS_NEXT_TITLE.getKey())
      .requireVisible();

    for (int i = 1; i < 13; i++) {

      // Get the display text
      String displayText = window
        .textBox(WelcomeWizardState.KEEPKEY_CREATE_WALLET_CONFIRM_WORD.name() + ".keepkey_display")
        .text();

      assertThat(displayText.contains("" + i)).isTrue();

      if (i < 12) {
        hardwareWalletFixture.fireNextEvent("Click Next");
      } else {
        hardwareWalletFixture.fireNextEvent("Success");
      }

    }

  }

}
