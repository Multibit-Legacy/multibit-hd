package org.multibit.hd.ui.views.components.display_payments;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.ui.models.Model;

import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayPaymentsModel implements Model<List<PaymentData>> {

  private String panelName;

  private List<PaymentData> paymentDataList = Lists.newArrayList();

  public DisplayPaymentsModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The payment data list
   */
  public List<PaymentData> getPaymentDataList() {
    return paymentDataList;
  }


  @Override
  public List<PaymentData> getValue() {
    return getPaymentDataList();
  }

  @Override
  public void setValue(List<PaymentData> value) {
    this.paymentDataList = value;
  }
}
