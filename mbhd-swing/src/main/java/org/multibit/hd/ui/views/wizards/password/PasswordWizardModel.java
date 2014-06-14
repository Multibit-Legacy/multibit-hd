package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "password wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordWizardModel extends AbstractWizardModel<PasswordState> {

  /**
   * The "enter password" panel model
   */
  private PasswordEnterPasswordPanelModel enterPasswordPanelModel;

  public PasswordWizardModel() {
    super(PasswordState.PASSWORD_ENTER_PASSWORD);
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  /**
   * @return The password the user entered
   */
  public String getPassword() {
    return enterPasswordPanelModel.getEnterPasswordModel().getValue();
  }

  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterPasswordPanelModel The "enter password" panel model
   */
  void setEnterPasswordPanelModel(PasswordEnterPasswordPanelModel enterPasswordPanelModel) {
    this.enterPasswordPanelModel = enterPasswordPanelModel;
  }

}
