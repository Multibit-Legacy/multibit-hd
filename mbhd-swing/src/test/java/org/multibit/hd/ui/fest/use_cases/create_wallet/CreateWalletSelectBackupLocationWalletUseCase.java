package org.multibit.hd.ui.fest.use_cases.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

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
  public void execute(Map<String, Object> parameters) {

    // Verify that the title appears
    assertLabelText(MessageKey.SELECT_BACKUP_LOCATION_TITLE);

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

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

  }

}
