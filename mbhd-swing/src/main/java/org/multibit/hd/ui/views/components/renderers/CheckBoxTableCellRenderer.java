package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Render the background of a check box correctly
 */
public class CheckBoxTableCellRenderer extends DefaultTableCellRenderer {

  JCheckBox checkBox = new JCheckBox("");


  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    checkBox.setHorizontalAlignment(SwingConstants.CENTER);

    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    }
    else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    checkBox.setSelected((value != null && (Boolean) value));

    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    } else {
      setBorder(noFocusBorder);
    }

    if (isSelected) {
      checkBox.setBackground(table.getSelectionBackground());
    } else {
      if (row % 2 != 0) {
        checkBox.setBackground(Themes.currentTheme.tableRowAltBackground());
      } else {
        checkBox.setBackground(Themes.currentTheme.tableRowBackground());
      }
    }

    return checkBox;
  }
}
