package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.StripedTable;
import org.multibit.hd.ui.views.components.tables.TransactionTableModel;

import javax.swing.table.TableColumn;
import java.util.List;
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
   *
   * @return A new "contacts" striped table
   */
  public static StripedTable newContactsTable(List<Contact> contacts) {

    ContactTableModel model = new ContactTableModel(contacts);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.LARGE_ICON_SIZE + 10);
    table.setAutoCreateRowSorter(true);
    table.setRowSelectionAllowed(false);
    table.setCellSelectionEnabled(false);

    // Set preferred widths
    resizeColumn(table, ContactTableModel.STAR_COLUMN_INDEX, MultiBitUI.NORMAL_ICON_SIZE);
    resizeColumn(table, ContactTableModel.CHECKBOX_COLUMN_INDEX, MultiBitUI.NORMAL_ICON_SIZE);
    resizeColumn(table, ContactTableModel.GRAVATAR_COLUMN_INDEX, MultiBitUI.LARGE_ICON_SIZE);

    return table;
  }

  /**
   * @param transactions The transactions to show
   *
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

    return table;
  }

  /**
   * <p>Remove a column from the table view</p>
   *
   * @param table       The table
   * @param columnIndex The column index
   */
  private static void removeColumn(StripedTable table, int columnIndex) {

    String id = table.getColumnName(columnIndex);
    TableColumn column = table.getColumn(id);
    table.removeColumn(column);

  }

  /**
   * <p>Resize a column by setting its preferred width</p>
   *
   * @param table          The table
   * @param columnIndex    The column index
   * @param preferredWidth The preferred width
   */
  private static void resizeColumn(StripedTable table, int columnIndex, int preferredWidth) {

    String id = table.getColumnName(columnIndex);
    table.getColumn(id).setMaxWidth(preferredWidth);

  }

}
