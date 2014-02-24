package org.multibit.hd.ui.views.components.combo_boxes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Combo box to provide the following to UI:</p>
 * <ul>
 * <li>Theme-aware colors for the background and button in Nimbus</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ThemeAwareComboBox<E> extends JComboBox<E> {

  private boolean ignoreRepaint;

  public ThemeAwareComboBox(E[] items) {
    super(items);
  }

  @Override
  public void paintComponent(Graphics g) {

    ignoreRepaint = true;

    try {

      // Work out the size of the text area
      Rectangle arrowButtonBounds = getComponent(0).getBounds();
      g.setClip(
        0, 0,
        getWidth() - arrowButtonBounds.width, getHeight()
      );
//      setBackground(Themes.currentTheme.dataEntryBackground());
      setBackground(Color.YELLOW);

      super.paintComponent(g);

      // Work out the size of the arrow button
      g.setClip(
        arrowButtonBounds.x, arrowButtonBounds.y,
        arrowButtonBounds.width, arrowButtonBounds.height
      );
//      setBackground(Themes.currentTheme.buttonBackground());
      setBackground(Color.RED);

      super.paintComponent(g);

    } finally {

      ignoreRepaint = false;

    }
  }

  @Override
  public void repaint() {

    if (!ignoreRepaint)
      super.repaint();
  }

}
