package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.select_file.SelectFileModel;
import org.multibit.hd.ui.views.wizards.AbstractPanelModel;

/**
 * <p>Panel model to provide the following to "welcome" wizard:</p>
 * <ul>
 * <li>Storage of state for the "restore wallet from backup" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletBackupPanelModel extends AbstractPanelModel {

  private final SelectFileModel selectFileModel;
  private final EnterPasswordModel enterPasswordModel;

  /**
   * @param panelName          The panel name
   * @param selectFileModel    The "select file" component model
   * @param enterPasswordModel The "enter password" component model
   */
  public RestoreWalletBackupPanelModel(
    String panelName,
    SelectFileModel selectFileModel,
    EnterPasswordModel enterPasswordModel
  ) {
    super(panelName);
    this.selectFileModel = selectFileModel;
    this.enterPasswordModel = enterPasswordModel;
  }

  /**
   * @return The backup folder location model
   */
  public SelectFileModel getSelectFileModel() {
    return selectFileModel;
  }

  /**
   * @return The password model
   */
  public EnterPasswordModel getEnterPasswordModel() {
    return enterPasswordModel;
  }

}
