package org.multibit.hd.ui.views.components.renderers;

import org.joda.time.DateTime;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  <p>Renderer to provide the following to tables:</p>
 *  <ul>
 *  <li>Renders dates</li>
 *  </ul>
 *  
 */
public class TrailingJustifiedDateTableCellRenderer extends DefaultTableCellRenderer {

  JLabel label;

  public static final int TABLE_BORDER = 3;

  public static final String SPACER = "   "; // 3 spaces

  public TrailingJustifiedDateTableCellRenderer() {

    label = Labels.newBlankLabel();

  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);

    String formattedDate;
    if (value != null && value instanceof DateTime) {
        DateTime date = (DateTime) value;
        formattedDate = LocalisedDateUtils.formatFriendlyDate(date);
    } else {
      formattedDate = "";
    }

    label.setText(formattedDate + SPACER);

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
