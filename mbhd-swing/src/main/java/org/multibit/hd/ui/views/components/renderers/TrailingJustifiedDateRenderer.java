package org.multibit.hd.ui.views.components.renderers;

import org.joda.time.DateTime;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 *  <p>Renderer to provide the following to tables:<br>
 *  <ul>
 *  <li>Renders dates</li>
 *  </ul>
 *  
 */
public class TrailingJustifiedDateRenderer extends DefaultTableCellRenderer {

  SimpleDateFormat longDateFormatter;
  SimpleDateFormat shortDateFormatter;

  JLabel label;

  public static final int TABLE_BORDER = 3;

  public static final String SPACER = "   "; // 3 spaces

  public TrailingJustifiedDateRenderer() {
    label = Labels.newBlankLabel();
    longDateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Languages.currentLocale());
    shortDateFormatter = new SimpleDateFormat("HH:mm", Languages.currentLocale());
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {
    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);

    String formattedDate = "";
    if (value != null) {
      if (value instanceof DateTime) {
        DateTime date = (DateTime) value;
        if (date.getMillis() != 0) {
          try {
            // TODO Localise
            if (date.toDateMidnight().equals((DateTime.now().toDateMidnight()))) {
              formattedDate = "Today " + shortDateFormatter.format(date.toDate());
            } else {
              if (date.toDateMidnight().equals((DateTime.now().minusDays(1).toDateMidnight()))) {
                formattedDate = "Yesterday " + shortDateFormatter.format(date.toDate());
              } else {
                formattedDate = longDateFormatter.format(date.toDate());
              }
            }
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
