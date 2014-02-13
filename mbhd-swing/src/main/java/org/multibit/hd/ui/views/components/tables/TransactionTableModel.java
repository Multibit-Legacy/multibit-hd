package org.multibit.hd.ui.views.components.tables;

import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.Set;

/**
 * <p>TableModel to provide the following to contact JTable:</p>
 * <ul>
 * <li>Adapts a list of contacts into a table model</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TransactionTableModel extends AbstractTableModel {

  public static final int STATUS_COLUMN_INDEX = 0;
  public static final int DATE_COLUMN_INDEX = 1;
  public static final int TYPE_COLUMN_INDEX = 2;
  public static final int DESCRIPTION_COLUMN_INDEX = 3;
  public static final int AMOUNT_BTC_COLUMN_INDEX = 4;

  private static final Logger log = LoggerFactory.getLogger(TransactionTableModel.class);

  private String[] columnNames = {
          "Status",
          "Date",
          "Type",
          "Description",
          "Amount (" + BitcoinSymbol.current().getSymbol() + ")"
  };

  private Object[][] data;

  public TransactionTableModel(Set<TransactionData> transactions) {
    setTransactions(transactions, false);
  }

  /**
   * Set the transactions into the table
   *
   * @param transactions the transactions to show in the table
   */
  public void setTransactions(Set<TransactionData> transactions, boolean fireTableDataChanged) {
    data = new Object[transactions.size()][];

    int row = 0;
    for (TransactionData transaction : transactions) {

      Object[] rowData = new Object[]{
              transaction.getStatus(),
              transaction.getUpdateTime(),
              transaction.getType(),
              transaction.getDescription(),
              transaction.getAmountBTC()
      };

      data[row] = rowData;

      row++;
    }
    if (fireTableDataChanged) {
      fireTableDataChanged();
    }
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    return data.length;
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    try {
      return data[row][col];
    } catch (NullPointerException npe) {
      log.error("NullPointerException reading row = " + row + ", column = " + col);
      return "";
    }
  }

  /**
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  /**
   * Handle changes to the data
   */
  public void setValueAt(Object value, int row, int col) {
    // No table updates allowed
  }
}
