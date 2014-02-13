package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  <p>Renderer to provide the following to tables:<br>
 *  <ul>
 *  <li>Renders dates</li>
 *  </ul>
 *  
 */
public class TrailingJustifiedDateRenderer extends DefaultTableCellRenderer {

  SimpleDateFormat dateFormatter;

  JLabel label;

  public static final int TABLE_BORDER = 3;

  public static final String SPACER = "   "; // 3 spaces

  private int selectedRow;

  public TrailingJustifiedDateRenderer() {
    label = new JLabel();
    dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Languages.currentLocale());
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {
    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);
    label.setBorder(new EmptyBorder(new Insets(0, TABLE_BORDER, 1, TABLE_BORDER)));

    String formattedDate = "";
    if (value != null) {
      if (value instanceof Date) {
        if (((Date) value).getTime() != 0) {
          try {
            formattedDate = dateFormatter.format(value);
          } catch (IllegalArgumentException iae) {
            // ok
          }
        }
      } else {
        formattedDate = value.toString();
      }
    }

    label.setText(formattedDate + SPACER);

    if (isSelected) {
      selectedRow = row;
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
