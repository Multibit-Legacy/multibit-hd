package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentStatus;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Render a RAGStatus as an icon
 */
public class RAGStatusTableCellRenderer extends DefaultTableCellRenderer {

  private JLabel label = Labels.newBlankLabel();

  private PaymentTableModel paymentTableModel;

  public RAGStatusTableCellRenderer(PaymentTableModel paymentTableModel) {
    this.paymentTableModel = paymentTableModel;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setVerticalAlignment(SwingConstants.CENTER);
    label.setOpaque(true);

    // Get the RAG (which is in the model as a RAGStatus
    if (value instanceof PaymentStatus) {

      java.util.List<PaymentData> paymentDatas = paymentTableModel.getPaymentData();
      int modelRow = table.convertRowIndexToModel(row);
      PaymentData rowPaymentData = paymentDatas.get(modelRow);
      LabelDecorator.applyPaymentStatusIconAndColor(rowPaymentData.getStatus(), label, rowPaymentData.isCoinBase(), MultiBitUI.SMALL_ICON_SIZE);
    }

    if (isSelected) {
      label.setBackground(table.getSelectionBackground());
    } else {
      if (row % 2 != 0) {
        label.setBackground(Themes.currentTheme.tableRowAltBackground());
      } else {
        label.setBackground(Themes.currentTheme.tableRowBackground());
      }
    }

    return label;
  }
}
