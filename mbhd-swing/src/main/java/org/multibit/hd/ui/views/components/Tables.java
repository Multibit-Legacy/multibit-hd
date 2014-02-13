package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.StripedTable;
import org.multibit.hd.ui.views.components.tables.TransactionTableModel;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Set;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised tables with themed rendering</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Tables {

  /**
   * Utilities have no public constructor
   */
  private Tables() {
  }

  /**
   * @param contacts The contacts to show
   * @return A new "contacts" striped table
   */
  public static StripedTable newContactsTable(Set<Contact> contacts) {

    ContactTableModel model = new ContactTableModel(contacts);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.LARGE_ICON_SIZE + 10);
    table.setAutoCreateRowSorter(true);

    return table;
  }

  /**
   * @param transactions The transactions to show
   * @return A new "transactions" striped table
   */
  public static StripedTable newTransactionsTable(Set<TransactionData> transactions) {

    TransactionTableModel model = new TransactionTableModel(transactions);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.LARGE_ICON_SIZE + 10);
    table.setAutoCreateRowSorter(true);

    // Status column
    TableColumn statusTableColumn = table.getColumnModel().getColumn(0);
    statusTableColumn.setPreferredWidth(60); // TODO work out width from FontMetrics
    statusTableColumn.setMaxWidth(90); // TODO work out width from FontMetrics
    statusTableColumn.setCellRenderer(new RAGStatusRenderer());

    justifyColumnHeaders(table);
    return table;
  }

  private static void justifyColumnHeaders(JTable table) {
      TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
      JLabel label = (JLabel) renderer;
      label.setHorizontalAlignment(JLabel.CENTER);
  }
}



/**
 * Render a RAGStatus as an icon
 */
class RAGStatusRenderer extends DefaultTableCellRenderer {

  JLabel statusLabel = new JLabel();

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    // Prepare the primary icon (used always), and an extra icon and containing panel for use as required.
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statusLabel.setVerticalAlignment(SwingConstants.CENTER);
    statusLabel.setOpaque(true);

    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, statusLabel, false, MultiBitUI.SMALL_ICON_SIZE);

    // Get the RAG (which is in the model as a RAGStatus
    if (value instanceof RAGStatus) {
      RAGStatus status = (RAGStatus) value;

      switch (status) {
        case RED:
          statusLabel.setForeground(Themes.currentTheme.dangerAlertBackground());
          break;
        case AMBER:
          statusLabel.setForeground(Themes.currentTheme.warningAlertBackground());
          break;
        case GREEN:
          statusLabel.setForeground(Themes.currentTheme.successAlertBackground());
          break;
        default:
          // Unknown status
          throw new IllegalStateException("Unknown status " + status);
      }
    }
    return statusLabel;
  }
}
