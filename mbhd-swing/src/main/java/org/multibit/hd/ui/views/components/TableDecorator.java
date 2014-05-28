package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.event.*;

import static org.multibit.hd.ui.MultiBitUI.NORMAL_PLUS_ICON_SIZE;
import static org.multibit.hd.ui.MultiBitUI.TABLE_SPACER;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Application of standard table look and feel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TableDecorator {

  /**
   * Utilities have no public constructor
   */
  private TableDecorator() {
  }

  /**
   * @param table       The table to decorate using the standard screen behaviour
   * @param enterButton The button that will respond to the "Enter" key or a double click on a row
   */
  public static void applyScreenTheme(final JTable table, final JButton enterButton) {

    table.setFillsViewportHeight(true);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);

    table.setRowHeight(NORMAL_PLUS_ICON_SIZE + TABLE_SPACER);
    table.setAutoCreateRowSorter(true);

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);

    // Apply theme
    table.setForeground(Themes.currentTheme.text());

    // Orientation
    table.applyComponentOrientation(Languages.currentComponentOrientation());

    // Key bindings

    // Override the Swing input map to avoid default behaviour
    table
      .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
      .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
    table.getActionMap().put("Enter", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        enterButton.doClick();

      }
    });

  }

}