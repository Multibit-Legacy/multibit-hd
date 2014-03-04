package org.multibit.hd.ui.views.components.renderers;

/**
 * <p>Table header renderer to provide the following to tables:<br>
 * <ul>
 * <li>Customised iconography for Bitcoin symbol</li>
 * </ul>
 *  
 */

import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AmountBTCTableHeaderRenderer extends JLabel implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  private final Icon descBitcoinIcon;
  private final Icon ascBitcoinIcon;
  private final Icon descIcon;
  private final Icon ascIcon;

  private final int[] bitcoinColumns;

  private final BitcoinConfiguration bitcoinConfiguration;

  public AmountBTCTableHeaderRenderer(TableCellRenderer defaultRenderer, int[] bitcoinColumns) {

    this.defaultRenderer = defaultRenderer;

    // Prepare some icons
    descIcon = AwesomeDecorator.createIcon(
      AwesomeIcon.SORT_NUMERIC_DESC,
      Themes.currentTheme.text(),
      MultiBitUI.SMALL_ICON_SIZE
    );

    ascIcon = AwesomeDecorator.createIcon(
      AwesomeIcon.SORT_NUMERIC_ASC,
      Themes.currentTheme.text(),
      MultiBitUI.SMALL_ICON_SIZE
    );

    descBitcoinIcon = AwesomeDecorator.createIcon(
      AwesomeIcon.SORT_NUMERIC_DESC,
      Themes.currentTheme.text(),
      MultiBitUI.SMALL_ICON_SIZE
    );

    ascBitcoinIcon = AwesomeDecorator.createIcon(
      AwesomeIcon.SORT_NUMERIC_ASC,
      Themes.currentTheme.text(),
      MultiBitUI.SMALL_ICON_SIZE
    );

    this.bitcoinColumns = bitcoinColumns;

    this.bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration().deepCopy();
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    // TODO Integrate with int[] bitcoinColumns

    if (comp instanceof JLabel) {
      JLabel label = (JLabel) comp;

      // Bitcoin column
      LabelDecorator.applyBitcoinSymbolLabel(
        label,
        bitcoinConfiguration,
        label.getText());

      // TODO bitcoin icon lost on sort of column (use ImageDecorator.overlay and something like the code below)
      /*
      for (RowSorter.SortKey sortKey : table.getRowSorter().getSortKeys()) {

        if (sortKey.getColumn() == column) {
          switch (sortKey.getSortOrder()) {
            case ASCENDING:
              return ascIcon;
            case DESCENDING:
              return (UIManager.getIcon("Table.descendingSortIcon"));
          }
        }

      }
      */

    }

    return comp;
  }

}
