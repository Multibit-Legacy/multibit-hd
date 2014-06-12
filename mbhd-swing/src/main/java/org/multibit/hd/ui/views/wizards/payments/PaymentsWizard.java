package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "Payments":</p>
 * <ol>
 * <li>Show transaction overview</li>
 * <li>Show transaction details</li>
 * <li>Choose payment payment</li>
 * <li>Show payment request details</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class PaymentsWizard extends AbstractWizard<PaymentsWizardModel> {

  public PaymentsWizard(PaymentsWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {
    wizardViewMap.put(
      PaymentsState.TRANSACTION_OVERVIEW.name(),
      new TransactionOverviewPanelView(this, PaymentsState.TRANSACTION_OVERVIEW.name()));
    wizardViewMap.put(
      PaymentsState.TRANSACTION_DETAIL.name(),
      new TransactionDetailPanelView(this, PaymentsState.TRANSACTION_DETAIL.name()));
    wizardViewMap.put(
      PaymentsState.CHOOSE_PAYMENT_REQUEST.name(),
      new ChoosePaymentRequestPanelView(this, PaymentsState.CHOOSE_PAYMENT_REQUEST.name()));
    wizardViewMap.put(
      PaymentsState.PAYMENT_REQUEST_DETAILS.name(),
      new PaymentRequestDetailPanelView(this, PaymentsState.PAYMENT_REQUEST_DETAILS.name()));
  }
}
