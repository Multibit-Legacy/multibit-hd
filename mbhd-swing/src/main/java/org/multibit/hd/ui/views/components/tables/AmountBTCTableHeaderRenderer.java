package org.multibit.hd.ui.views.components.tables;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */

import org.multibit.hd.ui.views.components.LabelDecorator;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AmountBTCTableHeaderRenderer extends JLabel implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  public AmountBTCTableHeaderRenderer(TableCellRenderer defaultRenderer) {
    this.defaultRenderer = defaultRenderer;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    if (comp instanceof JLabel) {
      JLabel label = (JLabel) comp;

      LabelDecorator.applyBitcoinSymbolLabel(label, label.getText());

      // TODO bitcoin icon lost on sort of column

    }

    return comp;
  }
}
