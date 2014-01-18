package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
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
public class SendBitcoinConfirmPanelModel  extends AbstractPanelModel  {

  private String notes;
  private String password;

  protected SendBitcoinConfirmPanelModel(String panelName) {
    super(panelName);
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
   * @return The password the user entered to authorise the transaction
   */
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  protected void update(Optional componentModel) {

    // No need to update since we have the references

    // Inform the wizard model that a change has occurred
    ViewEvents.fireWizardPanelModelChangedEvent(panelName, Optional.of(this));

  }

}
