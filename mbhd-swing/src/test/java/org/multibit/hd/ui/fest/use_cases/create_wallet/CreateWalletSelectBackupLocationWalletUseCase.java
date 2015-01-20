package org.multibit.hd.ui.fest.use_cases.create_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.io.File;
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

    // This may be populated with the current cloud backup depending on the test
    // so just leave it unspecified for now
    window
      .textBox(MessageKey.SELECT_FILE.getKey())
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

    // Create a backup location off the InstallationManager
    File festCloudBackupsDirectory = SecureFiles.verifyOrCreateDirectory(InstallationManager.getOrCreateApplicationDataDirectory(), "fest-cloud-backups");

    // Enter the directory path
    window
      .textBox(MessageKey.SELECT_FILE.getKey())
      .setText(festCloudBackupsDirectory.getAbsolutePath());

    // OK to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

  }

}
