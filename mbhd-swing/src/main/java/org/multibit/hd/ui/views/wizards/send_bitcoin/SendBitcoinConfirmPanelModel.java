package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.wizards.AbstractPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "confirm" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinConfirmPanelModel extends AbstractPanelModel {

  private String notes;
  private final EnterPasswordModel passwordModel;

  public SendBitcoinConfirmPanelModel(String panelName, EnterPasswordModel passwordModel) {
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

  @Override
  protected void update(Optional componentModel) {

    // No need to update since we have the references

    // Inform the wizard model that a change has occurred
    ViewEvents.fireWizardPanelModelChangedEvent(panelName, Optional.of(this));

  }

}
