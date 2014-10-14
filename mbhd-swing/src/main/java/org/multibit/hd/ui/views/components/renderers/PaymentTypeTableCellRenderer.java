package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.PaymentType;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.themes.Themes;

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
    label.setFont(label.getFont().deriveFont(MultiBitUI.TABLE_TEXT_FONT_SIZE));


    if (value instanceof PaymentType) {
      PaymentType type = (PaymentType) value;

      label.setText(Languages.safeText(type.getLocalisationKey()));
    }


    if (isSelected) {
      label.setBackground(table.getSelectionBackground());
      label.setForeground(table.getSelectionForeground());
    } else {
      label.setForeground(table.getForeground());
      if (row % 2 == 1) {
        label.setBackground(Themes.currentTheme.tableRowAltBackground());
      } else {
        label.setBackground(Themes.currentTheme.tableRowBackground());
      }
    }

    return label;
  }
}
