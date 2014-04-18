package org.multibit.hd.ui.fest.use_cases.welcome;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select backup location" panel view</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletSelectBackupLocationWalletUseCase extends AbstractFestUseCase {

  public CreateWalletSelectBackupLocationWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute() {

    window
      .label(MessageKey.SELECT_BACKUP_LOCATION_TITLE.getKey())
      .requireText(Languages.safeText(MessageKey.SELECT_BACKUP_LOCATION_TITLE));

    window
      .textBox()
      .requireEnabled()
      .requireVisible();

    window
      .button(MessageKey.SELECT_FOLDER.getKey())
      .requireEnabled()
      .requireVisible()
      .click();

    window
      .fileChooser()
      .requireVisible()
      .cancel();

  }

}
