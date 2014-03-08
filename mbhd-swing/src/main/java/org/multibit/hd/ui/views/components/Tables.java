package org.multibit.hd.ui.views.components;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.renderers.AmountBTCTableHeaderRenderer;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;
import org.multibit.hd.ui.views.components.tables.PaymentTableModel;
import org.multibit.hd.ui.views.components.tables.StripedTable;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

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

  private static int SPACER = 10;

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

    table.setRowHeight(MultiBitUI.LARGE_ICON_SIZE + SPACER);
    table.setAutoCreateRowSorter(true);
    table.setRowSelectionAllowed(false);
    table.setCellSelectionEnabled(false);

    // Apply theme
    table.setForeground(Themes.currentTheme.text());

    // Orientation
    table.applyComponentOrientation(Languages.currentComponentOrientation());

    // Set preferred widths
    resizeColumn(table, ContactTableModel.CHECKBOX_COLUMN_INDEX, MultiBitUI.NORMAL_ICON_SIZE + SPACER);
    resizeColumn(table, ContactTableModel.GRAVATAR_COLUMN_INDEX, MultiBitUI.LARGE_ICON_SIZE + SPACER);


    justifyColumnHeaders(table);

    return table;
  }

  /**
   * @param paymentData The payments to show
   *
   * @return A new "payments" striped table
   */
  public static StripedTable newPaymentsTable(List<PaymentData> paymentData) {

    PaymentTableModel model = new PaymentTableModel(paymentData);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.NORMAL_PLUS_ICON_SIZE + SPACER);
    table.setAutoCreateRowSorter(true);
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);

    // Apply theme
    table.setForeground(Themes.currentTheme.text());

    // Orientation
    table.setComponentOrientation(Languages.currentComponentOrientation());

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
    resizeColumn(table, PaymentTableModel.TYPE_COLUMN_INDEX, 80, 100);

    // Amount BTC column
    TableColumn column = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    column.setHeaderRenderer(new AmountBTCTableHeaderRenderer(
      table.getTableHeader().getDefaultRenderer(),
      new int[] {PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX}
    ));

    TableColumn amountBTCTableColumn = table.getColumnModel().getColumn(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX);
    amountBTCTableColumn.setCellRenderer(Renderers.newTrailingJustifiedNumericRenderer());
    resizeColumn(table, PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX, 120, 180);

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
    // TODO not comparing depth
    Comparator<RAGStatus> comparatorStatus = newStatusComparator();
    rowSorter.setComparator(PaymentTableModel.STATUS_COLUMN_INDEX, comparatorStatus);

     // Comparator for payment type
    Comparator<PaymentType> comparatorPaymentType = newPaymentTypeComparator();
    rowSorter.setComparator(PaymentTableModel.TYPE_COLUMN_INDEX, comparatorPaymentType);

    // Comparator for amount BTC
    Comparator<BigInteger> comparatorBigInteger = newBigIntegerComparator();
    rowSorter.setComparator(PaymentTableModel.AMOUNT_BTC_COLUMN_INDEX, comparatorBigInteger);

    // Comparator for amount fiat
    Comparator<FiatPayment> comparatorFiatPayment = newFiatPaymentComparator();
    rowSorter.setComparator(PaymentTableModel.AMOUNT_FIAT_COLUMN_INDEX, comparatorFiatPayment);

     justifyColumnHeaders(table);

    return table;
  }

  /**
   * @param historyEntries The history entries to show
   *
   * @return A new "contacts" striped table
   */
  public static StripedTable newHistoryTable(List<HistoryEntry> historyEntries) {

    HistoryTableModel model = new HistoryTableModel(historyEntries);

    StripedTable table = new StripedTable(model);

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(MultiBitUI.NORMAL_PLUS_ICON_SIZE + SPACER);
    table.setAutoCreateRowSorter(true);
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);

    // Apply theme
    table.setForeground(Themes.currentTheme.text());

    // Orientation
    table.applyComponentOrientation(Languages.currentComponentOrientation());

    // Date column
    TableColumn dateTableColumn = table.getColumnModel().getColumn(HistoryTableModel.CREATED_COLUMN_INDEX);
    dateTableColumn.setCellRenderer(Renderers.newTrailingJustifiedDateRenderer());
    resizeColumn(table, HistoryTableModel.CREATED_COLUMN_INDEX, 150, 200);

    // Set preferred widths
    resizeColumn(table, HistoryTableModel.CHECKBOX_COLUMN_INDEX, MultiBitUI.NORMAL_ICON_SIZE + SPACER);
    resizeColumn(table, HistoryTableModel.DESCRIPTION_COLUMN_INDEX, MultiBitUI.HUGE_ICON_SIZE + SPACER);
    resizeColumn(table, HistoryTableModel.NOTES_COLUMN_INDEX, MultiBitUI.HUGE_ICON_SIZE + SPACER);

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
   private static Comparator<RAGStatus> newStatusComparator() {

     return new Comparator<RAGStatus>() {



       @Override
       public int compare(RAGStatus o1, RAGStatus o2) {

         if (o1 != null && o2 == null) {
           return 1;
         }

         return o1 != null ? o1.compareTo(o2) : 0;

       }
     };
   }

  /**
    * @return A new BigInteger comparator for use with a TableRowSorter
    */
   private static Comparator<BigInteger> newBigIntegerComparator() {

     return new Comparator<BigInteger>() {

       @Override
       public int compare(BigInteger o1, BigInteger o2) {

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


