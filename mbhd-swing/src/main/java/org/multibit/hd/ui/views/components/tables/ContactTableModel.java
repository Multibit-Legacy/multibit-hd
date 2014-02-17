package org.multibit.hd.ui.views.components.tables;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.views.components.ImageDecorator;
import org.multibit.hd.ui.views.components.Images;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

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

  public static final int CHECKBOX_COLUMN_INDEX = 0;
  public static final int GRAVATAR_COLUMN_INDEX = 1;
  public static final int NAME_COLUMN_INDEX = 2;
  public static final int EMAIL_COLUMN_INDEX = 3;
  public static final int ADDRESS_COLUMN_INDEX = 4;
  public static final int TAG_COLUMN_INDEX = 5;
  public static final int COLUMN_COUNT = 6;

  /**
   * The column names - note the use of spaces as identifiers for blank columns
   */
  private String[] columnNames = {
    " ", // Checkbox (wider than a star icon)
    "  ", // Gravatar
    "Name",
    "Email",
    "Address",
    "Tags",
  };

  private Object[][] data;
  private final List<Contact> contacts;

  public ContactTableModel(List<Contact> contacts) {

    Preconditions.checkNotNull(contacts, "'contacts' must be present");

    this.contacts = contacts;
    data = new Object[contacts.size()][];

    int row = 0;
    for (Contact contact : contacts) {

      // Build row manually to allow for flexible column index reporting
      final Object[] rowData = new Object[COLUMN_COUNT];
      rowData[CHECKBOX_COLUMN_INDEX] = false;
      rowData[NAME_COLUMN_INDEX] = contact.getName();
      rowData[EMAIL_COLUMN_INDEX] = contact.getEmail().or("");
      rowData[ADDRESS_COLUMN_INDEX] = contact.getBitcoinAddress().or("");
      rowData[TAG_COLUMN_INDEX] = Joiner.on(" ").join(contact.getTags());

      // Ensure we download the Gravatar asynchronously
      final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(contact.getEmail().or("nobody@example.org"));
      Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {

        // we want this handler to run immediately after we push the big red button!
        public void onSuccess(Optional<BufferedImage> image) {
          if (image.isPresent()) {
            final ImageIcon gravatar = ImageDecorator.toImageIcon(
              ImageDecorator.applyRoundedCorners(image.get(), MultiBitUI.IMAGE_CORNER_RADIUS)
            );
            rowData[GRAVATAR_COLUMN_INDEX] = gravatar;
          } else {
            rowData[GRAVATAR_COLUMN_INDEX] = Images.newUserIcon();
          }
        }

        public void onFailure(Throwable thrown) {
          rowData[GRAVATAR_COLUMN_INDEX] = Images.newUserIcon();
        }
      });

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
   * then the checkbox column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
    if (c == CHECKBOX_COLUMN_INDEX) {
      return Boolean.class;
    } else if (c == GRAVATAR_COLUMN_INDEX) {
      return ImageIcon.class;
    } else {
      return String.class;
    }
    //if (getValueAt(0, c) != null) {
    //  return getValueAt(0, c).getClass();
    //} else {
    //  return String.class;
    //}
  }

  /**
   * Handle changes to the data
   */
  public void setValueAt(Object value, int row, int col) {

    data[row][col] = value;
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
        throw new IllegalStateException("Unknown contact selected index: " + checkSelectorIndex);
    }

  }

  /**
   * @param row     The row index
   * @param checked True if the checkbox column should be checked
   */
  public void setSelectionCheckmark(int row, boolean checked) {

    // If it is not starred then apply a check or remove the existing one
    setValueAt(checked, row, CHECKBOX_COLUMN_INDEX);

  }

}