package org.multibit.hd.ui.views.wizards.empty_wallet;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "empty wallet" wizard:</p>
 * <ul>
 * <li>Storage of state for the "confirm" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletConfirmPanelModel extends AbstractWizardPanelModel {

  private String notes;
  private final EnterPasswordModel passwordModel;

  public EmptyWalletConfirmPanelModel(String panelName, EnterPasswordModel passwordModel) {
    super(panelName);

    this.passwordModel = passwordModel;

  }

  /**
   * @return The notes associated with the transaction
   */
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * @return The "enter password" model
   */
  public EnterPasswordModel getPasswordModel() {
    return passwordModel;
  }

}
