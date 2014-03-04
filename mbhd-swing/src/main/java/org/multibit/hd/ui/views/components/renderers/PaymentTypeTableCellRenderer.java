package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentType;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Render the transaction type as localised text
 */
public class PaymentTypeTableCellRenderer extends DefaultTableCellRenderer {

  private JLabel label = Labels.newBlankLabel();

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    label.setHorizontalAlignment(SwingConstants.LEADING);
    label.setOpaque(true);

    if (value instanceof PaymentType) {
      PaymentType type = (PaymentType) value;

      switch (type) {
        case REQUESTED:
          label.setText(Languages.safeText(CoreMessageKey.PAYMENT_REQUESTED));
          break;
        case RECEIVING:
          label.setText(Languages.safeText(CoreMessageKey.PAYMENT_RECEIVING));
          break;
        case RECEIVED:
          label.setText(Languages.safeText(CoreMessageKey.PAYMENT_RECEIVED));
          break;
        case SENDING:
          label.setText(Languages.safeText(CoreMessageKey.PAYMENT_SENDING));
          break;
        case SENT:
          label.setText(Languages.safeText(CoreMessageKey.PAYMENT_SENT));
          break;
        default:
          // Something else
          label.setText(Languages.safeText(type.toString()));
      }
    }

    if (isSelected) {
      label.setBackground(table.getSelectionBackground());
      label.setForeground(table.getSelectionForeground());
    } else {
      label.setForeground(table.getForeground());
      if (row % 2 == 1) {
        label.setBackground(StripedTable.alternateColor);
      } else {
        label.setBackground(StripedTable.rowColor);
      }
    }

    return label;
  }
}
