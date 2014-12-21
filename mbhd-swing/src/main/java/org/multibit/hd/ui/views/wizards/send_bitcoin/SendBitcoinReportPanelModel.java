package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin report" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin report " panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendBitcoinReportPanelModel extends AbstractWizardPanelModel {

  // The id of the transaction that this model is describing.
  // May be null
  private String transactionId;

  /**
   * @param panelName The panel name
   */
  public SendBitcoinReportPanelModel(
          String panelName
  ) {
    super(panelName);

    // At construction time the model is completely blank.
    transactionId = null;

  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }
}
