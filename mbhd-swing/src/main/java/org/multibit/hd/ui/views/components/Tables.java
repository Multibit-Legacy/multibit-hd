package org.multibit.hd.ui.views.components;

import org.joda.time.DateTime;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.tables.AmountBTCTableHeaderRenderer;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.*;

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

  private static TableRowSorter<TableModel> rowSorter;

  private static int SPACER = 10;

  /**
   * Utilities have no public constructor
   */
  private Tables() {

  }

  /**
   * @param contacts The contacts to show
   * @return A new "contacts" striped table
   */
  public static StripedTable newContactsTable(List<Contact> contacts) {

    ContactTableModel model = new ContactTableModel(contacts);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.LARGE_ICON_SIZE + SPACER);
    table.setAutoCreateRowSorter(true);
    table.setRowSelectionAllowed(false);
    table.setCellSelectionEnabled(false);

    // Set preferred widths
    resizeColumn(table, ContactTableModel.CHECKBOX_COLUMN_INDEX, MultiBitUI.NORMAL_ICON_SIZE + SPACER);
    resizeColumn(table, ContactTableModel.GRAVATAR_COLUMN_INDEX, MultiBitUI.LARGE_ICON_SIZE + SPACER);

    table.applyComponentOrientation(Languages.currentComponentOrientation());

    return table;
  }

  /**
   * @param paymentDatas The payments to show
   * @return A new "payments" striped table
   */
  public static StripedTable newPaymentsTable(Set<PaymentData> paymentDatas) {

    PaymentTableModel model = new PaymentTableModel(paymentDatas);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(40);
    table.setAutoCreateRowSorter(true);
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);

    // Status column
    TableColumn statusTableColumn = table.getColumnModel().getColumn(PaymentTableModel.STATUS_COLUMN_INDEX);
    statusTableColumn.setCellRenderer(Renderers.newRAGStatusRenderer());
    resizeColumn(table, PaymentTableModel.STATUS_COLUMN_INDEX, 60, 90);

    // Date column
    TableColumn dateTableColumn = table.getColumnModel().getColumn(PaymentTableModel.DATE_COLUMN_INDEX);
    dateTableColumn.setCellRenderer(Renderers.newTrailingJustifiedDateRenderer());
    resizeColumn(table, PaymentTableModel.DATE_COLUMN_INDEX, 150, 200);

    // Type column
    TableColumn typeTableColumn = table.getColumnModel().getColumn(PaymentTableModel.TYPE_COLUMN_INDEX);
    typeTableColumn.setCellRenderer(Renderers.newPaymentTypeRenderer());
    resizeColumn(table, PaymentTableModel.TYPE_COLUMN_INDEX, 80, 100);

    // Amount BTC column
    TableColumn column = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    column.setHeaderRenderer(new AmountBTCTableHeaderRenderer(table.getTableHeader().getDefaultRenderer()));

    TableColumn amountBTCTableColumn = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    amountBTCTableColumn.setCellRenderer(Renderers.newTrailingJustifiedNumericRenderer());
    resizeColumn(table, PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX, 120, 180);

    // Row sorter for date
    rowSorter = new TableRowSorter<TableModel>(table.getModel());
    table.setRowSorter(rowSorter);

    // Sort by date descending.
    List<TableRowSorter.SortKey> sortKeys = new ArrayList<TableRowSorter.SortKey>();
    sortKeys.add(new TableRowSorter.SortKey(1, SortOrder.DESCENDING));
    rowSorter.setSortKeys(sortKeys);
    Comparator<DateTime> comparator = new Comparator<DateTime>() {
      @Override
      public int compare(DateTime o1, DateTime o2) {
        if (o1 == null) {
          if (o2 == null) {
            return 0;
          } else {
            return 1;
          }
        } else {
          if (o2 == null) {
            return -1;
          }
        }
        long n1 = o1.getMillis();
        long n2 = o2.getMillis();
        if (n1 == 0) {
          // Object 1 has missing date.
          return 1;
        }
        if (n2 == 0) {
          // Object 2 has missing date.
          return -1;
        }
        if (n1 < n2) {
          return -1;
        } else if (n1 > n2) {
          return 1;
        } else {
          return 0;
        }
      }
    };
    rowSorter.setComparator(1, comparator);

    // TODO - also add to fiat column if it is present

    justifyColumnHeaders(table);
    return table;
  }

  private static void justifyColumnHeaders(JTable table) {
    TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
    JLabel label = (JLabel) renderer;
    label.setHorizontalAlignment(JLabel.CENTER);
  }

  private static void resizeColumn(StripedTable table, int columnIndex, int preferredWidth) {
    resizeColumn(table, columnIndex, preferredWidth, preferredWidth);
  }

  /**
   * <p>Resize a column by setting its preferred width</p>
   *
   * @param table          The table
   * @param columnIndex    The column index
   * @param preferredWidth The preferred width
   * @param maxWidth       The maximum width
   */
  private static void resizeColumn(StripedTable table, int columnIndex, int preferredWidth, int maxWidth) {
    String id = table.getColumnName(columnIndex);
    table.getColumn(id).setPreferredWidth(preferredWidth);
    table.getColumn(id).setMaxWidth(maxWidth);
  }
}


