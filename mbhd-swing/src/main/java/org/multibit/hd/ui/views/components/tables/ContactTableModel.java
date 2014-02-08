package org.multibit.hd.ui.views.components.tables;

import com.google.common.base.Joiner;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.views.components.ImageDecorator;
import org.multibit.hd.ui.views.components.Images;

import javax.swing.*;
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
public class ContactTableModel extends AbstractTableModel {

  private String[] columnNames = {
    "",
    "",
    "Name",
    "Email",
    "Address",
    "Tags",
    ""
  };

  private Object[][] data;

  public ContactTableModel(Set<Contact> contacts) {

    data = new Object[contacts.size()][];

    int row = 0;
    for (Contact contact : contacts) {

      final ImageIcon imageIcon =  ImageDecorator.toImageIcon(
        ImageDecorator.applyRoundedCorners(
          Gravatars.retrieveGravatar(contact.getEmail().or("nobody@example.org")).get(), 20)
      );

      final ImageIcon starIcon = Images.newStarIcon(contact.getStarStyle());

      Object[] rowData = new Object[]{
        false,
        starIcon,
        contact.getName(),
        contact.getEmail().or(""),
        contact.getBitcoinAddress().or(""),
        Joiner.on(" ").join(contact.getTags()),
        imageIcon
      };

      data[row] = rowData;

      row++;

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

    data[row][col] = value;
    fireTableCellUpdated(row, col);

  }

}
