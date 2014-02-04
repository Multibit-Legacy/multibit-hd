package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.wizards.AbstractPanelModel;

/**
 * <p>Panel model to provide the following to "welcome" wizard:</p>
 * <ul>
 * <li>Storage of state for the "restore wallet from backup" panel</li>
 * </ul>
 * <p>Restore from backup requires a backup location and the seed phrase for decryption.</p>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletSelectBackupLocationPanelModel extends AbstractPanelModel {

  private final SelectFileModel selectFileModel;
  private final EnterSeedPhraseModel enterSeedPhraseModel;

  /**
   * @param panelName            The panel name
   * @param selectFileModel      The "select file" component model
   * @param enterSeedPhraseModel The "enter seed phrase" component model
   */
  public RestoreWalletSelectBackupLocationPanelModel(
    String panelName,
    SelectFileModel selectFileModel,
    EnterSeedPhraseModel enterSeedPhraseModel
  ) {
    super(panelName);
    this.selectFileModel = selectFileModel;
    this.enterSeedPhraseModel = enterSeedPhraseModel;
  }

  /**
   * @return The backup folder location model
   */
  public SelectFileModel getSelectFileModel() {
    return selectFileModel;
  }

  /**
   * @return The seed phrase model
   */
  public EnterSeedPhraseModel getEnterSeedPhraseModel() {
    return enterSeedPhraseModel;
  }

}
