package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.renderers.TagPillListCellRenderer;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
   * @param listModel The list model populated with tags
   *
   * @return A new JList with custom cell renderer for tag pill handling
   */
  public static JList<String> newTagPillList(DefaultListModel<String> listModel) {

    final JList<String> list = new JList<>(listModel);

    // Ensure it is accessible
    AccessibilityDecorator.apply(list, MessageKey.TAGS);

    list.setCellRenderer(new TagPillListCellRenderer());
    list.setLayoutOrientation(JList.VERTICAL_WRAP);

    list.setBorder(BorderFactory.createEmptyBorder());
    list.setOpaque(false);

    // Ensure the wrapping is intuitive
    list.setVisibleRowCount(-1);

    // Only a single selection (and nothing selected at the start)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Add listeners
    list.addMouseMotionListener(getHandCursorMouseMotionListener(list));

    // Ensure users can see when the list gets focus for the first time
    list.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (list.getSelectedIndex() < 0) {
          list.setSelectedIndex(0);
        }
      }
    });

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
