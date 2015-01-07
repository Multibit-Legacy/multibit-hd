package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

/**
 * <p>Renderer to provide the following to tables:</p>
 * <ul>
 * <li>Renderer of numeric amount field</li>
 * </ul>
 *
 */
public class AmountFiatTableCellRenderer extends DefaultTableCellRenderer {
  JLabel label;

  private static final Logger log = LoggerFactory.getLogger(AmountFiatTableCellRenderer.class);

  public AmountFiatTableCellRenderer() {
    label = Labels.newBlankLabel();
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {
    label.setHorizontalAlignment(SwingConstants.TRAILING);
    label.setOpaque(true);
    label.setBorder(new EmptyBorder(new Insets(0, TrailingJustifiedDateTableCellRenderer.TABLE_BORDER, 1, TrailingJustifiedDateTableCellRenderer.TABLE_BORDER)));
    label.setFont(label.getFont().deriveFont(MultiBitUI.TABLE_TEXT_FONT_SIZE));

    if (value instanceof FiatPayment) {

      FiatPayment fiatPayment = (FiatPayment) value;

      if (!(fiatPayment.getAmount() == null) && fiatPayment.getAmount().isPresent() && fiatPayment.getCurrency().isPresent()) {
        BigDecimal amount = fiatPayment.getAmount().get();
        try {
          LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
          BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

          String balance = Formats.formatLocalAmount(amount, languageConfiguration.getLocale(), bitcoinConfiguration, true);
          if (!bitcoinConfiguration.getLocalCurrencyCode().equals(fiatPayment.getCurrency().get().getCurrencyCode())) {
            // Fiat payment is in a different currency to the main UI
            if (bitcoinConfiguration.isCurrencySymbolLeading()) {
              balance = fiatPayment.getCurrency().get().getSymbol() + "\u00a0" + balance;
            } else {
              balance = balance + "\u00a0" + fiatPayment.getCurrency().get().getSymbol();
            }
          }

          label.setText(balance + TrailingJustifiedDateTableCellRenderer.SPACER);

          if (amount.signum() == -1) {
            // Debit
            if (isSelected) {
              label.setForeground(table.getSelectionForeground());
            } else {
              label.setForeground(Themes.currentTheme.debitText());
            }
          } else {
            // Credit or zero
            if (isSelected) {
              label.setForeground(table.getSelectionForeground());
            } else {
              label.setForeground(Themes.currentTheme.creditText());
            }
          }
        } catch (NumberFormatException nfe) {
          // The fiat amount could not be understood as a number
          // show nothing
          log.error(nfe.getClass().getCanonicalName() + " " + nfe.getMessage());
        }
      } else {
        // No fiat payment information (could be no exchange)
        log.trace("Cannot render fiatPayment. {} {}",
          fiatPayment,
          Configurations.currentConfiguration.getBitcoin().getLocalCurrencyCode()
        );
        label.setText("");
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
