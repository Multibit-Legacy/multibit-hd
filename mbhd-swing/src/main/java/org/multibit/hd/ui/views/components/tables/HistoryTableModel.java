package org.multibit.hd.ui.views.components.tables;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.HistoryEntry;

import javax.swing.table.AbstractTableModel;
import java.util.Collection;
import java.util.List;

/**
 * <p>TableModel to provide the following to contact JTable:</p>
 * <ul>
 * <li>Adapts a list of history entries into a table model</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class HistoryTableModel extends AbstractTableModel {

  public static final int CHECKBOX_COLUMN_INDEX = 0;
  public static final int CREATED_COLUMN_INDEX = 1;
  public static final int DESCRIPTION_COLUMN_INDEX = 2;
  public static final int NOTES_COLUMN_INDEX = 3;
  public static final int COLUMN_COUNT = 4;

  /**
   * The column names - note the use of spaces as identifiers for blank columns
   */
  private String[] columnNames = {
    " ", // Checkbox (wider than a star icon)
    "Date",
    "Description",
    "Notes",
  };

  private Object[][] data;
  private List<HistoryEntry> historyEntries = Lists.newArrayList();

  public HistoryTableModel(List<HistoryEntry> historyEntries) {

    Preconditions.checkNotNull(historyEntries, "'contacts' must be present");

    setHistoryEntries(historyEntries, false);

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
   * then the checkbox column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
    if (c == CHECKBOX_COLUMN_INDEX) {
       return Boolean.class;
     } else if (c == CREATED_COLUMN_INDEX) {
       return DateTime.class;
     } else {
       return String.class;
     }

  }

  /**
   * Handle changes to the data
   */
  public void setValueAt(Object value, int row, int col) {

    data[row][col] = value;

    // Keep repaints to a minimum
    fireTableCellUpdated(row, col);

  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return col == CHECKBOX_COLUMN_INDEX;
  }

  /**
   * @param checkSelectorIndex Represents the selection type (e.g. "all", "none" etc)
   */
  public void updateSelectionCheckboxes(int checkSelectorIndex) {

    switch (checkSelectorIndex) {
      case 0:
        // All
        for (int row = 0; row < getRowCount(); row++) {
          setSelectionCheckmark(row, true);
        }
        break;
      case 1:
        // None
        for (int row = 0; row < getRowCount(); row++) {
          setSelectionCheckmark(row, false);
        }
        break;
      default:
        throw new IllegalStateException("Unknown history selected index: " + checkSelectorIndex);
    }

  }

  /**
   * @param modelRow The model row index (after <code>convertRowIndexToModel</code> has been applied)
   * @param selected True if the checkbox column should be marked as selected
   */
  public void setSelectionCheckmark(int modelRow, boolean selected) {

    // If it is not starred then apply a check or remove the existing one
    setValueAt(selected, modelRow, CHECKBOX_COLUMN_INDEX);

  }

  /**
   * @param selected True if a selected checkbox indicates that the row should be included in the result set
   *
   * @return A list of contacts by selection
   */
  public List<HistoryEntry> getHistoryEntriesBySelection(boolean selected) {

    List<HistoryEntry> results = Lists.newArrayList();

    for (int row = 0; row < getRowCount(); row++) {

      if (getValueAt(row, CHECKBOX_COLUMN_INDEX).equals(selected)) {

        results.add(historyEntries.get(row));

      }

    }

    return results;
  }

  /**
   * <p>Populate the table data from the current history entries</p>
   *
   * @param entries              The history entries that will form the basis of the table model in the same order as presented
   * @param fireTableDataChanged True if the table data changed event should be fired
   */
  public void setHistoryEntries(Collection<HistoryEntry> entries, boolean fireTableDataChanged) {

    this.historyEntries = Lists.newArrayList(entries);

    data = new Object[entries.size()][];

    int row = 0;
    for (HistoryEntry historyEntry : entries) {

      // Build row manually to allow for flexible column index reporting
      final Object[] rowData = new Object[COLUMN_COUNT];
      rowData[CHECKBOX_COLUMN_INDEX] = false;
      rowData[CREATED_COLUMN_INDEX] = historyEntry.getCreated();
      rowData[DESCRIPTION_COLUMN_INDEX] = historyEntry.getDescription();
      rowData[NOTES_COLUMN_INDEX] = historyEntry.getNotes().or("");

      data[row] = rowData;

      row++;

    }

    if (fireTableDataChanged) {
      fireTableDataChanged();
    }

  }

}