package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.components.display_payment_request.DisplayPaymentRequestModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" payment request panel</li>
 * </ul>
 *
 * @since 0.0.8
 */
public class SendBitcoinDisplayPaymentRequestPanelModel extends AbstractWizardPanelModel {

  private final DisplayPaymentRequestModel displayPaymentRequestModel;

  /**
   * @param panelName                  The panel name
   * @param displayPaymentRequestModel The "display payment request" component model
   */
  public SendBitcoinDisplayPaymentRequestPanelModel(
    String panelName,
    DisplayPaymentRequestModel displayPaymentRequestModel
  ) {
    super(panelName);
    this.displayPaymentRequestModel = displayPaymentRequestModel;
  }

  /**
   * @return The display payment request model
   */
  public DisplayPaymentRequestModel getDisplayPaymentRequestModel() {
    return displayPaymentRequestModel;
  }

}
