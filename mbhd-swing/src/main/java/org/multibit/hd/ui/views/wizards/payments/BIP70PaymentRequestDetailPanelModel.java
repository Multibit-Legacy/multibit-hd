package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "payments" wizard:</p>
 * <ul>
 * <li>Storage of state for the "payment request details" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BIP70PaymentRequestDetailPanelModel extends AbstractWizardPanelModel {

  private Optional<PaymentRequestData> paymentRequestDataOptional = Optional.absent();

  public BIP70PaymentRequestDetailPanelModel(String panelName) {
    super(panelName);
  }

  public Optional<PaymentRequestData> getPaymentRequestDataOptional() {
    return paymentRequestDataOptional;
  }

  public void setPaymentRequestDataOptional(Optional<PaymentRequestData> paymentRequestDataOptional) {
    this.paymentRequestDataOptional = paymentRequestDataOptional;
  }
}
