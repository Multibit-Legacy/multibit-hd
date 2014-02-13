package org.multibit.hd.ui.views.components.text_fields;

import org.multibit.hd.ui.views.components.ImageDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * <p>Text field to provide the following to UI:</p>
 * <ul>
 * <li>Text field with rounded corners</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RoundedTextField extends JTextField {

  private Shape shape;

  public RoundedTextField(int size) {
    super(size);
    setOpaque(false); // As suggested by @AVD in comment.
  }

  protected void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    g2.setColor(getBackground());
    g2.fillRoundRect(
      0, 0,
      getWidth(), getHeight(),
      15, 15
    );
    super.paintComponent(g2);

  }

  protected void paintBorder(Graphics g) {

    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    g2.setColor(getForeground());
    g2.drawRoundRect(
      0, 0,
      getWidth(), getHeight(),
      15, 15
    );

  }

  public boolean contains(int x, int y) {

    if (shape == null || !shape.getBounds().equals(getBounds())) {
      shape = new RoundRectangle2D.Float(
        0, 0,
        getWidth(), getHeight(),
        15, 15
      );
    }

    return shape.contains(x, y);
  }
}