package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Apply a custom ScrollBarUI to the given components</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ScrollBarUIDecorator {

  /**
   * Utilities have no public constructor
   */
  private ScrollBarUIDecorator() {
  }

  /**
   * <p>Apply the scroll bar UI to the scroll pane</p>
   *
   * @param scrollPane      The scroll pane
   * @param addVerticalLine True if the scroll pane wraps a flat component (not rounded)
   */
  public static void apply(final JScrollPane scrollPane, boolean addVerticalLine) {

    if (scrollPane.getVerticalScrollBar() != null) {
      scrollPane.getVerticalScrollBar().setUI(newScrollBarUI());
      if (addVerticalLine) {
        // Add a vertical line to the left of scroll bar track for better visual effect
        scrollPane.getVerticalScrollBar().setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Themes.currentTheme.text()));
      }
    }
    if (scrollPane.getHorizontalScrollBar() != null) {
      scrollPane.getHorizontalScrollBar().setUI(newScrollBarUI());
    }

  }

  /**
   * <p>Apply the scroll bar UI to a scroll pane wrapping a table</p>
   *
   * @param scrollPane The scroll pane
   * @param table      The table contained within the scroll pane
   */
  public static void apply(final JScrollPane scrollPane, final JTable table) {

    if (scrollPane.getVerticalScrollBar() != null) {
      scrollPane.getVerticalScrollBar().setUI(newScrollBarUI());
      scrollPane.getVerticalScrollBar().addHierarchyListener(new HierarchyListener() {
        @Override
        public void hierarchyChanged(HierarchyEvent e) {

          if (HierarchyEvent.HIERARCHY_CHANGED == e.getID()
            && (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
            // The scroll bar has changed state
            if (e.getComponent().isShowing()) {
              // Draw a border to the right of the table
              table.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Themes.currentTheme.text()));
            } else {
              // Remove the table border to avoid a thick black line on the right
              table.setBorder(BorderFactory.createEmptyBorder());
            }
          }

        }
      });
    }
    if (scrollPane.getHorizontalScrollBar() != null) {
      scrollPane.getHorizontalScrollBar().setUI(newScrollBarUI());
    }

  }

  /**
   * @return The ScrollBarUI to use (see Themes for colouring)
   */
  private static ScrollBarUI newScrollBarUI() {

    return new BasicScrollBarUI() {

      @Override
      protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
      }

      @Override
      protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
      }

      private JButton createZeroButton() {
        JButton jbutton = new JButton();
        jbutton.setPreferredSize(new Dimension(0, 0));
        jbutton.setMinimumSize(new Dimension(0, 0));
        jbutton.setMaximumSize(new Dimension(0, 0));
        return jbutton;
      }

    };

  }
}