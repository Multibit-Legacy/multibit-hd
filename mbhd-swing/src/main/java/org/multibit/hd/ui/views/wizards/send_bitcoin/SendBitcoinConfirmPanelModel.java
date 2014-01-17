package org.multibit.hd.ui.views.wizards.send_bitcoin;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "confirm" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinConfirmPanelModel {

  private String notes;
  private String password;

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
}
