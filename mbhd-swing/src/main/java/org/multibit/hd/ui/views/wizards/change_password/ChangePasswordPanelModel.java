package org.multibit.hd.ui.views.wizards.change_password;

import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "change password" wizard:</p>
 * <ul>
 * <li>Storage of state for the "change password" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ChangePasswordPanelModel extends AbstractWizardPanelModel {

  private final EnterPasswordModel enterPasswordModel;
  private final ConfirmPasswordModel confirmPasswordModel;

  /**
   * @param panelName          The panel name
   * @param enterPasswordModel The "enter password" component model
   */
  public ChangePasswordPanelModel(
    String panelName,
    EnterPasswordModel enterPasswordModel,
    ConfirmPasswordModel confirmPasswordModel
  ) {
    super(panelName);
    this.enterPasswordModel = enterPasswordModel;
    this.confirmPasswordModel = confirmPasswordModel;
  }

  /**
   * @return The "enter password" model
   */
  public EnterPasswordModel getEnterPasswordModel() {
    return enterPasswordModel;
  }

  /**
   * @return The "confirm password" model
   */
  public ConfirmPasswordModel getConfirmPasswordModel() {
    return confirmPasswordModel;
  }

}
