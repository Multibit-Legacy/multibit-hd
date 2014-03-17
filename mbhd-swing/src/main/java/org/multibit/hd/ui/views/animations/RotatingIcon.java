package org.multibit.hd.ui.views.animations;

import org.multibit.hd.ui.views.components.ImageDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

/**
 * <p>Icon to provide the following to UI:</p>
 * <ul>
 * <li>Self-contained rotating icon</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RotatingIcon implements Icon {

  private final Icon delegateIcon;

  private double theta = -Math.PI/4;
  private double delta = - theta / 4;
  private double thetaMax = 2 * Math.PI;

  private final int centerX;
  private final int centerY;

  private final int width;
  private final int height;

  private final Timer timer;

  /**
   * @param icon      The icon to rotate
   * @param component The containing component (e.g. label or button)
   */
  public RotatingIcon(Icon icon, final JComponent component) {

    delegateIcon = icon;

    width = delegateIcon.getIconWidth();
    height = delegateIcon.getIconHeight();

    centerX = width / 2;
    centerY = height / 2;

    timer = new Timer(50, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        theta += delta;

        if (theta >= thetaMax) {
          theta = 0;
        }

        component.repaint();

      }
    });

    // Run continuously
    timer.setRepeats(true);
    timer.start();

  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {

    // Prevent timer events during the paint
    timer.stop();

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    Rectangle rectangle = new Rectangle(x, y, delegateIcon.getIconWidth(), delegateIcon.getIconHeight());
    g2.setClip(rectangle);

    AffineTransform original = g2.getTransform();
    AffineTransform at = new AffineTransform();

    at.concatenate(original);
    at.rotate(theta, x + centerX, y + centerY);
    g2.setTransform(at);
    delegateIcon.paintIcon(c, g2, x, y);
    g2.setTransform(original);

    // Start the timer again
    timer.start();

  }

  @Override
  public int getIconWidth() {
    return delegateIcon.getIconWidth();
  }

  @Override
  public int getIconHeight() {
    return delegateIcon.getIconHeight();
  }
}