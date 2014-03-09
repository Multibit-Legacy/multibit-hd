package org.multibit.hd.ui.views.wizards.payments;

import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Model object to provide the following to "payments wizard":</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PaymentsWizardModel extends AbstractWizardModel<PaymentsState> {

  private static final Logger log = LoggerFactory.getLogger(PaymentsWizardModel.class);

  /**
   * The payment being shown by the payment wizard
   */
  private PaymentData paymentData;

  /**
   * @param state The state object
   */
  public PaymentsWizardModel(PaymentsState state) {
    super(state);
    CoreServices.uiEventBus.register(this);
  }

  @Override
  public void showNext() {

    switch (state) {
      case TRANSACTION_OVERVIEW:
        state = PaymentsState.TRANSACTION_DETAIL;
        break;
      case TRANSACTION_DETAIL:
        state = PaymentsState.CHOOSE_PAYMENT_REQUEST;
        break;
      case CHOOSE_PAYMENT_REQUEST:
        state = PaymentsState.PAYMENT_REQUEST_DETAILS;
        break;
      case PAYMENT_REQUEST_DETAILS:
        // Finished

        break;
    }
  }

  @Override
  public void showPrevious() {

    switch (state) {
      case TRANSACTION_OVERVIEW:
        // Start - previous should not be enabled
        state = PaymentsState.TRANSACTION_OVERVIEW;
        break;
      case TRANSACTION_DETAIL:
        state = PaymentsState.TRANSACTION_OVERVIEW;
        break;
      case CHOOSE_PAYMENT_REQUEST:
        state = PaymentsState.TRANSACTION_DETAIL;
        break;
      case PAYMENT_REQUEST_DETAILS:
        state = PaymentsState.CHOOSE_PAYMENT_REQUEST;
        break;
    }
  }

  @Override
  public String getPanelName() {
    return state.name();
  }


  /**
   * <p>Reduced visibility for panel models only</p>
   *
   * @param enterAmountPanelModel The "enter amount" panel model
   */
  //void setEnterAmountPanelModel(SendBitcoinEnterAmountPanelModel enterAmountPanelModel) {
  //  this.enterAmountPanelModel = enterAmountPanelModel;
  //}

  public PaymentData getPaymentData() {
    return paymentData;
  }

  public void setPaymentData(PaymentData paymentData) {
    this.paymentData = paymentData;
  }
}
