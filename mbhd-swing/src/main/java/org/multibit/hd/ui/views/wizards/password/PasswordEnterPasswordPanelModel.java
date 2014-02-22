package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
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

  /**
   * @param panelName          The panel name
   * @param enterPasswordModel The "enter password" component model
   */
  public PasswordEnterPasswordPanelModel(
    String panelName,
    EnterPasswordModel enterPasswordModel
  ) {
    super(panelName);
    this.enterPasswordModel = enterPasswordModel;
  }

  /**
   * @return The "enter password" model
   */
  public EnterPasswordModel getEnterPasswordModel() {
    return enterPasswordModel;
  }

}
