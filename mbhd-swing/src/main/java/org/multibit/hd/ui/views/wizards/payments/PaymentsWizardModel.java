package org.multibit.hd.ui.views.wizards.payments;

import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitHD;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
   * Whether the prev button should be shown on the payment request screen
   */
  boolean showPrevOnPaymentRequest;

  /**
   * @param state The state object
   */
  public PaymentsWizardModel(PaymentsState state, PaymentData paymentData) {
    super(state);
    this.paymentData = paymentData;
    CoreServices.uiEventBus.register(this);

  }

  @Override
  public void showNext() {

    switch (state) {
      case TRANSACTION_OVERVIEW:
        state = PaymentsState.TRANSACTION_DETAIL;
        break;
      case TRANSACTION_DETAIL:
        // If there is one payment request being paid bitcoin go directly to the payment request details
        // If there are more one payment request go to the payment request chooser
        if (paymentData instanceof TransactionData) {
          TransactionData transactionData = (TransactionData)paymentData;
          Collection<PaymentRequestData> relatedPaymentRequestDataCollection = MultiBitHD.getWalletService().findPaymentRequestsThisTransactionFunds(transactionData);
          if (relatedPaymentRequestDataCollection != null && relatedPaymentRequestDataCollection.size() > 0) {
            if (relatedPaymentRequestDataCollection.size() <= 1) {
              state = PaymentsState.PAYMENT_REQUEST_DETAILS;
            } else {
              state = PaymentsState.CHOOSE_PAYMENT_REQUEST;
            }
          } else {
            // No payment requests - the single payment request screen deals with that
            state = PaymentsState.PAYMENT_REQUEST_DETAILS;
          }
        }
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

  public PaymentData getPaymentData() {
    return paymentData;
  }

  public boolean isShowPrevOnPaymentRequest() {
    return showPrevOnPaymentRequest;
  }

  public void setShowPrevOnPaymentRequest(boolean showPrevOnPaymentRequest) {
    this.showPrevOnPaymentRequest = showPrevOnPaymentRequest;
  }
}
