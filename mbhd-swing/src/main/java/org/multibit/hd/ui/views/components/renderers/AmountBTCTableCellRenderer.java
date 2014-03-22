package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.tables.StripedTable;
import org.multibit.hd.ui.views.themes.Themes;

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
public class AmountBTCTableCellRenderer extends DefaultTableCellRenderer {
  JLabel label;

  public AmountBTCTableCellRenderer() {
    label = Labels.newBlankLabel();
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {
    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);
    label.setBorder(new EmptyBorder(new Insets(0, TrailingJustifiedDateTableCellRenderer.TABLE_BORDER, 1, TrailingJustifiedDateTableCellRenderer.TABLE_BORDER)));

    if (value instanceof BigInteger) {

      // Do the Bitcoin processing

      BigInteger valueBigInteger = (BigInteger) value;
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguageConfiguration();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();

      String[] balanceArray = Formats.formatSatoshisAsSymbolic(valueBigInteger, languageConfiguration, bitcoinConfiguration, true);
      String balanceString = balanceArray[0] + balanceArray[1];

      label.setText(balanceString + TrailingJustifiedDateTableCellRenderer.SPACER);

      if ((valueBigInteger.compareTo(BigInteger.ZERO) < 0)) {
        // Debit
        if (isSelected) {
          label.setForeground(table.getSelectionForeground());
        } else {
          label.setForeground(Themes.currentTheme.debitText());
        }
      } else {
        // Credit
        if (isSelected) {
          label.setForeground(table.getSelectionForeground());
        } else {
          label.setForeground(Themes.currentTheme.creditText());
        }
      }
      if (isSelected) {
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
