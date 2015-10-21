package org.multibit.hd.ui.fest.use_cases.standard.sidebar.manage_wallet.edit_wallet;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "manage wallet" screen edit wallet wizard shows</li>
 * </ul>
 * <p>Requires the "manage wallet" screen to be showing</p>
 *
 * @since 0.0.1
 */
public class ShowThenCancelEditWalletUseCase extends AbstractFestUseCase {

  public ShowThenCancelEditWalletUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "edit wallet"
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .click();

    // Verify the "edit wallet" wizard appears
    assertLabelText(MessageKey.EDIT_WALLET_TITLE);

    // Verify the "name" field is present
    assertLabelText(MessageKey.NAME);

    // Verify "notes" field is present
    window
      .textBox(MessageKey.NOTES.getKey())
      .requireVisible()
      .requireEnabled();

    // We should have a known FEST cloud backup directory
    String festCloudBackups = InstallationManager.getOrCreateApplicationDataDirectory().getAbsolutePath() + "/fest-cloud-backups";

    // Verify "cloud backups" field is present
    window
      .textBox(MessageKey.SELECT_FILE.getKey())
      .requireVisible()
      .requireEnabled()
      .requireText(festCloudBackups);

    // Verify Apply is present
    window
      .button(MessageKey.APPLY.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_EDIT_WALLET_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
