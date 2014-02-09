package org.multibit.hd.ui.views.components.tables;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * <p>Table to provide the following to views:</p>
 * <ul>
 * <li>A table with a theme-aware striped row appearance</li>
 * </ul>
 * <p>Striping continues outside of the rows to fill the entire viewport. This gives
 * a consistent appearance to tables that fill the entire screen but are not fully
 * populated.</p>
 *
 * @since 0.0.1
 *  
 */
public class StripedTable extends JTable {

  private Color evenColor  = Themes.currentTheme.fadedText();
  private Color oddColor = Themes.currentTheme.inverseFadedText();

  public StripedTable(AbstractTableModel model) {
    super(model);
  }

  /**
   * @return The color of the second stripe
   */
  public Color getEvenColor() {
    return evenColor;
  }

  public void setEvenColor(Color evenColor) {
    this.evenColor = evenColor;
  }

  /**
   * @return The color of the first stripe
   */
  public Color getOddColor() {
    return oddColor;
  }

  public void setOddColor(Color oddColor) {
    this.oddColor = oddColor;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Stripe the rest of the viewport

    Graphics newGraphics = g.create();

    // Start with the odd color
    newGraphics.setColor(oddColor);

    // Get the rectangle for the last row
    Rectangle rectOfLastRow = getCellRect(getRowCount() - 1, 0, true);

    // The top Y-coordinate of the first empty row
    int firstNonExistentRowY = rectOfLastRow.y;

    // Only paint the grid if empty space is visible
    if (getVisibleRect().height > firstNonExistentRowY) {
      // Fill the rows alternating and paint the row-lines

      // Use minus 1 otherwise the first empty row is one pixel too high
      int rowYToDraw = (firstNonExistentRowY - 1) + getRowHeight();

      // Continue the stripes from the area with table data
      int actualRow = 0;

      while (rowYToDraw < getHeight()) {
        if (actualRow % 2 == 0) {
          // Even row
          newGraphics.setColor(evenColor);

          // Draw the rectangle
          newGraphics.fillRect(0, rowYToDraw, getWidth(), getRowHeight());

          // Switch back to odd for the next one
          newGraphics.setColor(oddColor);
        }

        if (showHorizontalLines) {
          // Draw the horizontal line
          newGraphics.drawLine(0, rowYToDraw, getWidth(), rowYToDraw);
        }

        rowYToDraw += getRowHeight();
        actualRow++;
      }

      if (showVerticalLines) {
        // Draw the vertical line
        int x = 0;
        for (int i = 0; i < getColumnCount(); i++) {
          TableColumn column = getColumnModel().getColumn(i);

          // Add the column width to the x-coordinate
          x += column.getWidth();

          newGraphics.drawLine(x - 1, firstNonExistentRowY, x - 1, getHeight());
        }
      }

      newGraphics.dispose();

    }

  }

  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

    Component c = super.prepareRenderer(renderer, row, column);

    if (!isRowSelected(row)) {
      // Provide a different color depending on the row count
      c.setBackground(row % 2 == 0 ? getBackground() : evenColor);
    }

    return c;
  }

}
