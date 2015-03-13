package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
   * The matching payment requests for a transactionData
   * (may be empty
   */
  private List<MBHDPaymentRequestData> matchingPaymentRequestList = Lists.newArrayList();

  /**
   * The MBHD payment request to show in the payment request details screen
   */
  private MBHDPaymentRequestData MBHDPaymentRequestData;

  /**
    * The BIP70 payment request to show in the payment request details screen
    */
  private PaymentRequestData paymentRequestData;

   /**
   * Whether to show the prev button on the payment request detail screen
   */
  boolean showPrevOnPaymentRequestDetailScreen = false;


  /**
   * @param state The state object
   */
  public PaymentsWizardModel(PaymentsState state, PaymentData paymentData) {
    super(state);
    this.paymentData = paymentData;
  }

  @Override
  public void showNext() {

    switch (state) {
      case TRANSACTION_OVERVIEW:
        state = PaymentsState.TRANSACTION_AMOUNT;
        break;
      case TRANSACTION_AMOUNT:
        state = PaymentsState.TRANSACTION_DETAIL;
        break;
      case TRANSACTION_DETAIL:
        state = PaymentsState.CHOOSE_PAYMENT_REQUEST;
        break;
      case CHOOSE_PAYMENT_REQUEST:
        state = PaymentsState.PAYMENT_REQUEST_DETAILS;
        break;
      case PAYMENT_REQUEST_DETAILS:
      case BIP70_PAYMENT_REQUEST_DETAILS:
      default:
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
      case TRANSACTION_AMOUNT:
        state = PaymentsState.TRANSACTION_OVERVIEW;
        break;
      case TRANSACTION_DETAIL:
        state = PaymentsState.TRANSACTION_AMOUNT;
        break;
      case CHOOSE_PAYMENT_REQUEST:
        state = PaymentsState.TRANSACTION_DETAIL;
        break;
      case PAYMENT_REQUEST_DETAILS:
        state = PaymentsState.CHOOSE_PAYMENT_REQUEST;
        break;
      default:
    }
  }

  @Override
  public String getPanelName() {
    return state.name();
  }

  public PaymentData getPaymentData() {
    return paymentData;
  }

  public List<MBHDPaymentRequestData> getMatchingPaymentRequestList() {
    return matchingPaymentRequestList;
  }

  public void setMatchingPaymentRequestList(List<MBHDPaymentRequestData> matchingPaymentRequestList) {
    this.matchingPaymentRequestList = matchingPaymentRequestList;
  }

  public MBHDPaymentRequestData getMBHDPaymentRequestData() {
    return MBHDPaymentRequestData;
  }

  public void setMBHDPaymentRequestData(MBHDPaymentRequestData MBHDPaymentRequestData) {
    this.MBHDPaymentRequestData = MBHDPaymentRequestData;
  }

  public PaymentRequestData getPaymentRequestData() {
    return paymentRequestData;
  }

  public void setPaymentRequestData(PaymentRequestData paymentRequestData) {
    this.paymentRequestData = paymentRequestData;
  }

  public boolean isShowPrevOnPaymentRequestDetailScreen() {
    return showPrevOnPaymentRequestDetailScreen;
  }

  public void setShowPrevOnPaymentRequestDetailScreen(boolean showPrevOnPaymentRequestDetailScreen) {
    this.showPrevOnPaymentRequestDetailScreen = showPrevOnPaymentRequestDetailScreen;
  }
}
