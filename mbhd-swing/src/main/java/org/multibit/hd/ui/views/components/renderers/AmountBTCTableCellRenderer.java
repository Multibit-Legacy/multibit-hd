package org.multibit.hd.ui.views.components.renderers;

import com.google.common.base.Optional;
import org.bitcoinj.core.Coin;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * <p>Renderer to provide the following to tables:</p>
 * <ul>
 * <li>Renderer of numeric amount field</li>
 * </ul>
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
    label.setFont(label.getFont().deriveFont(MultiBitUI.TABLE_TEXT_FONT_SIZE));

    if (value instanceof Optional) {

      // Do the Bitcoin processing

      Optional<Coin> valueCoin = (Optional<Coin>) value;
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

      String[] balanceArray = Formats.formatCoinAsSymbolic(valueCoin.or(Coin.ZERO), languageConfiguration, bitcoinConfiguration, true);
      String balanceString = balanceArray[0] + balanceArray[1];

      label.setText(balanceString + TrailingJustifiedDateTableCellRenderer.SPACER);

      if ((valueCoin.or(Coin.ZERO).compareTo(Coin.ZERO) < 0)) {
        // Debit
        if (isSelected) {
          label.setForeground(Themes.currentTheme.inverseText());
        } else {
          label.setForeground(Themes.currentTheme.debitText());
        }
      } else {
        // Credit
        if (isSelected) {
          label.setForeground(Themes.currentTheme.inverseText());
        } else {
          label.setForeground(Themes.currentTheme.creditText());
        }
      }
      if (isSelected) {
        label.setBackground(Themes.currentTheme.tableRowSelectedBackground());
        label.setForeground(Themes.currentTheme.inverseText());
      } else {
        if (row % 2 != 0) {
          label.setBackground(Themes.currentTheme.tableRowAltBackground());
        } else {
          label.setBackground(Themes.currentTheme.tableRowBackground());
        }
      }
    }

    return label;
  }
}
