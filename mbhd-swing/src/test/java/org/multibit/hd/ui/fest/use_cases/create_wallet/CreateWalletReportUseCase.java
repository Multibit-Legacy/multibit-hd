package org.multibit.hd.ui.fest.use_cases.create_wallet;

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
 * <li>Verify the welcome wizard "create wallet report" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletReportUseCase extends AbstractFestUseCase {

  public CreateWalletReportUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    // Verify that the title appears
    assertLabelText(MessageKey.CREATE_WALLET_REPORT_TITLE);

    // Building the wallet can take time
    pauseForWalletCreation();

    window
      .label(MessageKey.SEED_PHRASE_CREATED_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.WALLET_PASSWORD_CREATED_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.WALLET_CREATED_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.BACKUP_LOCATION_STATUS.getKey())
      .requireVisible();

    window
      .label(MessageKey.CACERTS_INSTALLED_STATUS.getKey())
      .requireVisible();

    // OK to proceed
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

    String description =window
      .label(MessageKey.DESCRIPTION.getKey())
      .text();

    assertThat(description).startsWith("Wallet created");


  }

}
