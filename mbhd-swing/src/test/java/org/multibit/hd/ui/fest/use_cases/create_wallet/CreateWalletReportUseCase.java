package org.multibit.hd.ui.fest.use_cases.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

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

    window
      .label(MessageKey.CREATE_WALLET_REPORT_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.CREATE_WALLET_REPORT_TITLE));

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

    // OK to proceed
    window
      .button(MessageKey.FINISH.getKey())
      .click();

  }

}
