package org.multibit.hd.ui.views.components.enter_recipient;

import org.multibit.hd.core.api.Recipient;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Storage of state for the recipient information</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterRecipientModel implements Model<Recipient> {

  private Recipient recipient;
  private final String panelName;

  /**
   * @param panelName The panel name to identify the "verification status" and "next" buttons
   */
  public EnterRecipientModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  @Override
  public Recipient getValue() {
    return recipient;
  }

  @Override
  public void setValue(Recipient value) {
    this.recipient = value;
  }

  /**
   * @return The recipient
   */
  public Recipient getRecipient() {
    return recipient;
  }
}
