package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "enter password" wizard:</p>
 * <ul>
 * <li>Storage of state for the "enter password" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordEnterPasswordPanelModel extends AbstractWizardPanelModel {

  private final EnterPasswordModel enterPasswordModel;
  private final SelectWalletModel selectWalletModel;

  /**
   * @param panelName          The panel name
   * @param enterPasswordModel The "enter password" component model
   */
  public PasswordEnterPasswordPanelModel(
    String panelName,
    EnterPasswordModel enterPasswordModel,
    SelectWalletModel selectWalletModel
  ) {
    super(panelName);
    this.enterPasswordModel = enterPasswordModel;
    this.selectWalletModel = selectWalletModel;
  }

  /**
   * @return The "enter password" model
   */
  public EnterPasswordModel getEnterPasswordModel() {
    return enterPasswordModel;
  }

  /**
   * @return the "select wallet" model
   */
  public SelectWalletModel getSelectWalletModel() {
    return selectWalletModel;
  }
}
