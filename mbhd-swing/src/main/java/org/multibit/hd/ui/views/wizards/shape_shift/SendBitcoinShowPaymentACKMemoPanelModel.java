package org.multibit.hd.ui.views.wizards.shape_shift;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "payment ack memo" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class SendBitcoinShowPaymentACKMemoPanelModel extends AbstractWizardPanelModel {

  private String paymentACKMemo;

  public SendBitcoinShowPaymentACKMemoPanelModel(String panelName) {
    super(panelName);
  }

  public String getPaymentACKMemo() {
    return paymentACKMemo;
  }

  public void setPaymentACKMemo(String paymentACKMemo) {
    this.paymentACKMemo = paymentACKMemo;
  }
}
