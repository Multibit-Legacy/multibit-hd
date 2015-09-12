package org.multibit.hd.ui.fest.use_cases.standard.restore_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "restore wallet report" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class RestoreWalletReportUseCase extends AbstractFestUseCase {

  public RestoreWalletReportUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.RESTORE_WALLET_REPORT_TITLE);

    // Require a few seconds to allow wallet creation to complete
    // due to extensive crypto operations and CA cert lookups
    pauseForWalletRestore();

    window
      .label(MessageKey.BACKUP_LOCATION_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.WALLET_CREATED_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.CACERTS_INSTALLED_STATUS.getKey())
      .requireVisible();

    // By the time the messages have appeared the Finish button
    // should be ready
    window
      .button(MessageKey.FINISH.getKey())
      .requireEnabled(timeout(2, TimeUnit.SECONDS))
      .click();

    // Wait for credentials wizard Exit button to appear
    pauseForViewReset();

    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
        // Allow a short time to overcome initialisation delays
      .requireEnabled(timeout(3, TimeUnit.SECONDS));

    // Examine the list
    window
      .comboBox(MessageKey.SELECT_WALLET.getKey())
      .requireEnabled()
      .requireSelection(0)
      .requireNotEditable();

    String description = window
      .textBox(MessageKey.DESCRIPTION.getKey())
      .text();

    assertThat(description).startsWith("Wallet created");

  }

}
