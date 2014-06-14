package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(PasswordWizardModel.class);

  /**
   * The "enter password" panel model
   */
  private PasswordEnterPasswordPanelModel enterPasswordPanelModel;

  /**
   * @param state The state object
   */
  public PasswordWizardModel(PasswordState state) {
    super(state);
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
