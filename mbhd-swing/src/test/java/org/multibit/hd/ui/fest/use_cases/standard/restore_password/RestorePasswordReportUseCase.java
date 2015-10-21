package org.multibit.hd.ui.fest.use_cases.standard.restore_password;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.testing.WalletSummaryFixtures;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "restore credentials report" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class RestorePasswordReportUseCase extends AbstractFestUseCase {

  public RestorePasswordReportUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.RESTORE_PASSWORD_REPORT_TITLE);

    // Allow a short delay while password is recovered
    pauseForWalletPasswordChange();

    // Restoring the credentials should be instant
    assertLabelContainsValue(MessageKey.RESTORE_PASSWORD_REPORT_MESSAGE_SUCCESS, WalletSummaryFixtures.STANDARD_PASSWORD);

    // OK to proceed
    window
      .button(MessageKey.FINISH.getKey())
      .requireEnabled(timeout(2, TimeUnit.SECONDS))
      .click();

    // Wait for credentials wizard Exit button to appear
    pauseForViewReset();

    // Verify

    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
        // Allow a short time to overcome initialisation delays
      .requireEnabled(timeout(3, TimeUnit.SECONDS));

  }

}
