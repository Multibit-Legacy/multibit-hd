package org.multibit.hd.ui.views.components;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

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
   * <p>Apply the scroll bar UI to the scroll pan</p>
   */
  public static void apply(JScrollPane scrollPane) {

    if (scrollPane.getVerticalScrollBar() != null) {
      scrollPane.getVerticalScrollBar().setUI(newScrollBarUI());
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