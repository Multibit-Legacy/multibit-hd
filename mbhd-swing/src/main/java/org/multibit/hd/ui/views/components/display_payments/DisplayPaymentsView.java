package org.multibit.hd.ui.views.components.display_payments;

import com.google.common.collect.Lists;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.util.List;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a list of payments</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayPaymentsView extends AbstractComponentView<DisplayPaymentsModel>{

  // View components
  private List<JLabel> paymentLabelList;

  /**
   * @param model The model backing this view
   */
  public DisplayPaymentsView(DisplayPaymentsModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][][]", // Columns
      "[][]" // Rows
    ));

    // Populate components
    paymentLabelList = Lists.newArrayList();

    // TODO order by reverse age
    if (getModel().isPresent()) {
      List<PaymentData> paymentDataList = getModel().get().getPaymentDataList();
      for (PaymentData paymentData : paymentDataList) {
        JLabel paymentDataLabel = new JLabel();
        paymentDataLabel.setText(Languages.safeText(paymentData.getStatus().getStatusKey(), paymentData.getStatus().getStatusData()));
        LabelDecorator.applyStatusIconAndColor(paymentData.getStatus(), paymentDataLabel, paymentData.isCoinBase(), MultiBitUI.NORMAL_ICON_SIZE);
        panel.add(paymentDataLabel, "growx, wrap");
      }
    }

    return panel;

  }

  @Override
  public void requestInitialFocus() {
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }
}