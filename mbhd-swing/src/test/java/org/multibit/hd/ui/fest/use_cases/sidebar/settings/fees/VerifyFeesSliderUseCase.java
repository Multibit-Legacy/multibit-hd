package org.multibit.hd.ui.fest.use_cases.sidebar.settings.fees;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.brit.core.services.FeeService;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "fees slider" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *
 */
public class VerifyFeesSliderUseCase extends AbstractFestUseCase {

  public VerifyFeesSliderUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {
    // Test slide to minimum value

    // Click on "fees"
    window
      .button(MessageKey.FEES_SETTINGS_TITLE.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "fees" wizard appears
    assertLabelText(MessageKey.FEES_SETTINGS_TITLE);

    // Verify Apply is present
    window
      .button(MessageKey.APPLY.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify Cancel is present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Move the slider to the lowest position
    window
      .slider(MessageKey.ADJUST_TRANSACTION_FEE.getKey())
      .slideToMinimum();

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    pauseForViewReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Check the feePerKB in the configuration has been updated to the minimum value
    assertThat(Configurations.currentConfiguration.getWallet().getFeePerKB() == FeeService.MINIMUM_FEE_PER_KB.longValue()).isTrue();

    // Test slide to maximum value

    // Click on "fees"
    window
      .button(MessageKey.FEES_SETTINGS_TITLE.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "fees" wizard appears
    assertLabelText(MessageKey.FEES_SETTINGS_TITLE);

    // Move the slider to the highest position
    window
      .slider(MessageKey.ADJUST_TRANSACTION_FEE.getKey())
      .slideToMaximum();

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    pauseForViewReset();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

    // Check the feePerKB in the configuration has been updated to the minimum value
    assertThat(Configurations.currentConfiguration.getWallet().getFeePerKB() == FeeService.MAXIMUM_FEE_PER_KB.longValue()).isTrue();
  }
}
