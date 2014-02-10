package org.multibit.hd.ui.views.components.tables;

import org.multibit.hd.core.api.TransactionData;

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

  private String[] columnNames = {
          "Type",
          "Depth",
          "Date",
          "Amount (BTC)"
  };

  private Object[][] data;

  public TransactionTableModel(Set<TransactionData> transactions) {
    setTransactions(transactions, false);
  }

  /**
   * Set the transactions into the table
   * @param transactions
   */
  public void setTransactions(Set<TransactionData> transactions, boolean fireTableDataChanged) {
    data = new Object[transactions.size()][];

    int row = 0;
    for (TransactionData transaction : transactions) {

      Object[] rowData = new Object[]{
              transaction.getConfidenceType(),
              transaction.getDepth(),
              transaction.getUpdateTime(),
              transaction.getAmountBTC()
      };

      data[row] = rowData;

      row++;

      if (fireTableDataChanged) {
        this.fireTableDataChanged();
      }
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
    return data[row][col];
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
