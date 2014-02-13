package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigInteger;

/**
 *  <p>Renderer to provide the following to tables:<br>
 *  <ul>
 *  <li>Renderer of numeric amount field</li>
 *  </ul>
 *  
 */
public class TrailingJustifiedNumericRenderer extends DefaultTableCellRenderer {
  JLabel label;

  public static Color CREDIT_FOREGROUND_COLOR = Color.GREEN.darker().darker();
  public static Color DEBIT_FOREGROUND_COLOR = Color.RED.darker();

  private int selectedRow;

  public TrailingJustifiedNumericRenderer() {
    label = new JLabel("");
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {
    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);
    label.setBorder(new EmptyBorder(new Insets(0, TrailingJustifiedDateRenderer.TABLE_BORDER, 1, TrailingJustifiedDateRenderer.TABLE_BORDER)));

    label.setText(value.toString() + TrailingJustifiedDateRenderer.SPACER);

    if (value instanceof BigInteger) {
      BigInteger valueBigInteger = (BigInteger)value;
    if ((valueBigInteger.compareTo(BigInteger.ZERO) < 0)) {
      // Debit.
      if (isSelected) {
        label.setForeground(table.getSelectionForeground());
      } else {
        label.setForeground(DEBIT_FOREGROUND_COLOR);
      }
    } else {
      // Credit.
      if (isSelected) {
        label.setForeground(table.getSelectionForeground());
      } else {
        label.setForeground(CREDIT_FOREGROUND_COLOR);
      }
    }
    if (isSelected) {
      selectedRow = row;
      label.setBackground(table.getSelectionBackground());
      label.setForeground(table.getSelectionForeground());
    } else {
      if (row % 2 == 1) {
        label.setBackground(StripedTable.alternateColor);
      } else {
        label.setBackground(StripedTable.rowColor);
      }
    }
    }

    return label;
  }
}
