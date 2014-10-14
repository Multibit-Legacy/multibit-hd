package org.multibit.hd.ui.views.components;

import org.bitcoinj.core.Coin;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.renderers.AmountBTCTableHeaderRenderer;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.components.tables.StripedTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import java.util.List;

import static org.multibit.hd.ui.MultiBitUI.*;

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
   * @param contacts    The contacts to show
   * @param enterButton The button to be pressed on "Enter" or double click
   *
   * @return A new "contacts" striped table
   */
  public static StripedTable newContactsTable(List<Contact> contacts, JButton enterButton) {

    ContactTableModel model = new ContactTableModel(contacts);

    StripedTable table = new StripedTable(model);

    // Ensure it is accessible
    AccessibilityDecorator.apply(table, MessageKey.CONTACTS);

    // Decorate with standard screen theme
    TableDecorator.applyScreenTheme(table, enterButton);

    // Apply any exceptions
    table.setRowHeight(LARGE_ICON_SIZE + TABLE_SPACER);

    // Checkbox column
    TableColumn checkBoxTableColumn = table.getColumnModel().getColumn(ContactTableModel.CHECKBOX_COLUMN_INDEX);
    checkBoxTableColumn.setCellRenderer(Renderers.newCheckboxRenderer());
    resizeColumn(table, ContactTableModel.CHECKBOX_COLUMN_INDEX, NORMAL_ICON_SIZE + TABLE_SPACER);

    // Gravatar column
    TableColumn gravatarTableColumn = table.getColumnModel().getColumn(ContactTableModel.GRAVATAR_COLUMN_INDEX);
    gravatarTableColumn.setCellRenderer(Renderers.newImageIconRenderer());
    resizeColumn(table, ContactTableModel.GRAVATAR_COLUMN_INDEX, LARGE_ICON_SIZE + TABLE_SPACER);

    // Email column
    TableColumn emailTableColumn = table.getColumnModel().getColumn(ContactTableModel.EMAIL_COLUMN_INDEX);
    emailTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    // Address
    TableColumn addressTableColumn = table.getColumnModel().getColumn(ContactTableModel.ADDRESS_COLUMN_INDEX);
    addressTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    // Name
    TableColumn nameTableColumn = table.getColumnModel().getColumn(ContactTableModel.NAME_COLUMN_INDEX);
    nameTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    // Tags
    TableColumn tagTableColumn = table.getColumnModel().getColumn(ContactTableModel.TAG_COLUMN_INDEX);
    tagTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    justifyColumnHeaders(table);

    return table;
  }

  /**
   * @param paymentData The payments to show
   * @param enterButton The button to be pressed on "Enter" or double click
   *
   * @return A new "payments" striped table
   */
  public static StripedTable newPaymentsTable(List<PaymentData> paymentData, JButton enterButton) {

    PaymentTableModel model = new PaymentTableModel(paymentData);

    StripedTable table = new StripedTable(model);

    // Ensure it is accessible
    AccessibilityDecorator.apply(table, MessageKey.PAYMENTS);

    TableDecorator.applyScreenTheme(table, enterButton);

    // Date column
    TableColumn dateTableColumn = table.getColumnModel().getColumn(PaymentTableModel.DATE_COLUMN_INDEX);
    dateTableColumn.setCellRenderer(Renderers.newTrailingJustifiedDateRenderer());
    resizeColumn(table, PaymentTableModel.DATE_COLUMN_INDEX, 150, 200);

    // Status column
    TableColumn statusTableColumn = table.getColumnModel().getColumn(PaymentTableModel.STATUS_COLUMN_INDEX);
    statusTableColumn.setCellRenderer(Renderers.newRAGStatusRenderer(model));
    resizeColumn(table, PaymentTableModel.STATUS_COLUMN_INDEX, 60, 90);

    // Type column
    TableColumn typeTableColumn = table.getColumnModel().getColumn(PaymentTableModel.TYPE_COLUMN_INDEX);
    typeTableColumn.setCellRenderer(Renderers.newPaymentTypeRenderer());
    resizeColumn(table, PaymentTableModel.TYPE_COLUMN_INDEX, 100, 120);

    // Amount BTC column
    TableColumn column = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    column.setHeaderRenderer(new AmountBTCTableHeaderRenderer(
      table.getTableHeader().getDefaultRenderer(),
      new int[]{PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX}
    ));

    TableColumn amountBTCTableColumn = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    amountBTCTableColumn.setCellRenderer(Renderers.newTrailingJustifiedNumericRenderer());
    resizeColumn(table, PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX, 120, 180);

    // Description
    TableColumn descriptionTableColumn = table.getColumnModel().getColumn(PaymentTableModel.DESCRIPTION_COLUMN_INDEX);
    descriptionTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    // Amount Fiat column
    TableColumn amountFiatTableColumn = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_FIAT_COLUMN_INDEX);
    amountFiatTableColumn.setCellRenderer(Renderers.newTrailingJustifiedFiatRenderer());
    resizeColumn(table, PaymentTableModel.AMOUNT_FIAT_COLUMN_INDEX, 120, 180);

    // Row sorter for date
    TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
    table.setRowSorter(rowSorter);

    // Sort by date descending
    List<TableRowSorter.SortKey> sortKeys = Lists.newArrayList();
    sortKeys.add(new TableRowSorter.SortKey(PaymentTableModel.DATE_COLUMN_INDEX, SortOrder.DESCENDING));
    rowSorter.setSortKeys(sortKeys);

    // Comparator for date
    Comparator<DateTime> comparatorDate = newDateTimeComparator();
    rowSorter.setComparator(PaymentTableModel.DATE_COLUMN_INDEX, comparatorDate);

    // Comparator for status
    Comparator<PaymentStatus> comparatorStatus = newStatusComparator();
    rowSorter.setComparator(PaymentTableModel.STATUS_COLUMN_INDEX, comparatorStatus);

    // Comparator for payment type
    Comparator<PaymentType> comparatorPaymentType = newPaymentTypeComparator();
    rowSorter.setComparator(PaymentTableModel.TYPE_COLUMN_INDEX, comparatorPaymentType);

    // Comparator for amount BTC
    Comparator<Coin> comparatorCoin = newCoinComparator();
    rowSorter.setComparator(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX, comparatorCoin);

    // Comparator for amount fiat
    Comparator<FiatPayment> comparatorFiatPayment = newFiatPaymentComparator();
    rowSorter.setComparator(PaymentTableModel.AMOUNT_FIAT_COLUMN_INDEX, comparatorFiatPayment);

    justifyColumnHeaders(table);

    return table;
  }

  /**
   * @param historyEntries The history entries to show
   * @param enterButton The button to be pressed on "Enter" or double click
   *
   * @return A new "contacts" striped table
   */
  public static StripedTable newHistoryTable(List<HistoryEntry> historyEntries, JButton enterButton) {

    HistoryTableModel model = new HistoryTableModel(historyEntries);

    StripedTable table = new StripedTable(model);

    // Ensure it is accessible
    AccessibilityDecorator.apply(table, MessageKey.HISTORY);

    TableDecorator.applyScreenTheme(table, enterButton);

    // Checkbox column
    TableColumn checkBoxTableColumn = table.getColumnModel().getColumn(HistoryTableModel.CHECKBOX_COLUMN_INDEX);
    checkBoxTableColumn.setCellRenderer(Renderers.newCheckboxRenderer());
    resizeColumn(table, HistoryTableModel.CHECKBOX_COLUMN_INDEX, NORMAL_ICON_SIZE + TABLE_SPACER);

    // Date column
    TableColumn dateTableColumn = table.getColumnModel().getColumn(HistoryTableModel.CREATED_COLUMN_INDEX);
    dateTableColumn.setCellRenderer(Renderers.newTrailingJustifiedDateRenderer());
    resizeColumn(table, HistoryTableModel.CREATED_COLUMN_INDEX, 150, 200);

    // Description column
    TableColumn descriptionTableColumn = table.getColumnModel().getColumn(HistoryTableModel.DESCRIPTION_COLUMN_INDEX);
    descriptionTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    resizeColumn(table, HistoryTableModel.DESCRIPTION_COLUMN_INDEX, HUGE_ICON_SIZE + TABLE_SPACER);

    // Notes column
    TableColumn notesTableColumn = table.getColumnModel().getColumn(HistoryTableModel.NOTES_COLUMN_INDEX);
    notesTableColumn.setCellRenderer(Renderers.newLeadingJustifiedStringRenderer());

    // Row sorter for date
    TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
    table.setRowSorter(rowSorter);

    // Sort by date descending
    List<TableRowSorter.SortKey> sortKeys = Lists.newArrayList();
    sortKeys.add(new TableRowSorter.SortKey(HistoryTableModel.CREATED_COLUMN_INDEX, SortOrder.DESCENDING));
    rowSorter.setSortKeys(sortKeys);

    Comparator<DateTime> comparator = newDateTimeComparator();
    rowSorter.setComparator(HistoryTableModel.CREATED_COLUMN_INDEX, comparator);

    justifyColumnHeaders(table);

    return table;
  }

  /**
   * @return A new DateTime comparator for use with a TableRowSorter
   */
  private static Comparator<DateTime> newDateTimeComparator() {

    return new Comparator<DateTime>() {

      @Override
      public int compare(DateTime o1, DateTime o2) {

        if (o1 != null && o2 == null) {
          return 1;
        }

        return o1 != null ? o1.compareTo(o2) : 0;

      }
    };
  }

  /**
   * @return A new status comparator for use with a TableRowSorter
   */
  private static Comparator<PaymentStatus> newStatusComparator() {

    return new Comparator<PaymentStatus>() {


      @Override
      public int compare(PaymentStatus o1, PaymentStatus o2) {

        if (o1 != null && o2 == null) {
          return 1;
        }

        return o1 != null ? o1.compareToWithOrdinal(o2) : 0;

      }
    };
  }

  /**
   * @return A new Coin comparator for use with a TableRowSorter
   */
  private static Comparator<Coin> newCoinComparator() {

    return new Comparator<Coin>() {

      @Override
      public int compare(Coin o1, Coin o2) {

        if (o1 != null && o2 == null) {
          return 1;
        }

        return o1 != null ? o1.compareTo(o2) : 0;

      }
    };
  }

  /**
   * @return A new FiatPayment comparator for use with a TableRowSorter
   */
  private static Comparator<FiatPayment> newFiatPaymentComparator() {

    return new Comparator<FiatPayment>() {

      @Override
      public int compare(FiatPayment o1, FiatPayment o2) {

        if (o1 != null && o2 == null) {
          return 1;
        }

        return o1 != null ? o1.compareTo(o2) : 0;

      }
    };
  }

  /**
   * @return A new PaymentType comparator for use with a TableRowSorter
   */
  private static Comparator<PaymentType> newPaymentTypeComparator() {

    return new Comparator<PaymentType>() {

      @Override
      public int compare(PaymentType o1, PaymentType o2) {

        if (o1 != null && o2 == null) {
          return 1;
        }

        return o1 != null ? o1.compareTo(o2) : 0;

      }
    };
  }

  /**
   * <p>Center the column headers</p>
   *
   * @param table The table
   */
  private static void justifyColumnHeaders(JTable table) {

    TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
    JLabel label = (JLabel) renderer;
    label.setHorizontalAlignment(JLabel.CENTER);

  }

  /**
   * <p>Resize a column by setting its preferred with</p>
   *
   * @param table          The table
   * @param columnIndex    The column index
   * @param preferredWidth The preferred width
   */
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