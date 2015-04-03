package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "payment memo" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendBitcoinEnterPaymentMemoPanelModel extends AbstractWizardPanelModel {

  private String paymentMemo;

  public SendBitcoinEnterPaymentMemoPanelModel(String panelName) {
    super(panelName);
  }

  public String getPaymentMemo() {
    return paymentMemo;
  }

  public void setPaymentMemo(String paymentMemo) {
    this.paymentMemo = paymentMemo;
  }
}
