package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.components.renderers.TagPillListCellRenderer;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised list boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ListBoxes {

  /**
   * Utilities have no public constructor
   */
  private ListBoxes() {
  }

  /**
   * @param listModel         The list model populated with tags
   *
   * @return A new JList with custom cell renderer for tag pill handling
   */
  public static JList<String> newTagPillList(DefaultListModel<String> listModel) {

    final JList<String> list = new JList<>(listModel);

    list.setCellRenderer(new TagPillListCellRenderer());
    list.setLayoutOrientation(JList.VERTICAL_WRAP);

    list.setBorder(BorderFactory.createEmptyBorder());
    list.setOpaque(false);

    // The maximum is 2 for good behaviour here
    list.setVisibleRowCount(2);

    // Only a single selection (and nothing selected at the start)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectedIndex(0);

    // Add listeners
    list.addMouseMotionListener(getHandCursorMouseMotionListener(list));

    // Apply the theme
    list.setBackground(Themes.currentTheme.detailPanelBackground());

    // Ensure we use the correct component orientation
    list.applyComponentOrientation(Languages.currentComponentOrientation());

    return list;
  }

  /**
   * <p>Ensure each element in a list has a mouse listener that effects a cursor change</p>
   *
   * @param list The list
   *
   * @return A mouse motion listener to trigger the cursor change
   */
  private static MouseMotionListener getHandCursorMouseMotionListener(final JList<String> list) {

    return new MouseMotionListener() {

      public void mouseDragged(MouseEvent e) {
        updateCursor(e);
      }

      public void mouseMoved(MouseEvent e) {
        updateCursor(e);
      }

      private void updateCursor(MouseEvent e) {
        list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
    };

  }

}
