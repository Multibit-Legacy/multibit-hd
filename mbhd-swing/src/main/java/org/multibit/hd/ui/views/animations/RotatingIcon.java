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
  private final int width;
  private final int height;

  /**
   * Required to correct for variation in the centroid of Font Awesome icons
   */
  private final int[][] rotationOffsets = new int[][]{

    // 0
    {0, 0},
    {0, 0},
    {0, 0},
    {0, 1},

    // 4
    {0, 1},
    {0, 1},
    {0, 1},
    {-1, 1},

    // 8
    {-1, 1},
    {-1, 1},
    {-1, 1},
    {-1, 0},

    // 12
    {-1, 0},
    {-1, 0},
    {-1, 0},
    {0, 0},

  };

  /**
   * The number of steps to make per rotation (power of 2)
   */
  private final static int maxStepCount = 16;

  /**
   * Starting position is "North"
   */
  private double theta = 0;

  /**
   * Delta is in radians is a complete circle split into max steps
   */
  private double delta = 2 * Math.PI / maxStepCount;

  /**
   * The current step count (start at the halfway point)
   */
  private int stepCount = 0;

  /**
   * Handles periodic increments of rotation
   */
  private final Timer timer;

  /**
   * @param icon      The icon to rotate
   * @param component The containing component (e.g. label or button)
   */
  public RotatingIcon(Icon icon, final JComponent component) {

    delegateIcon = icon;

    width = delegateIcon.getIconWidth();
    height = delegateIcon.getIconHeight();

    // Timer needs to be fairly fast to appear responsive
    timer = new Timer(75, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Guaranteed to be on the EDT
        incrementRotation(component);

      }
    });

    component.repaint();

    // Run continuously
    timer.setRepeats(true);
    timer.start();

  }

  /**
   * Increment the rotation
   *
   * @param component The containing component (e.g. label or button)
   */
  public void incrementRotation(final JComponent component) {

    // Increment theta
    theta += delta;

    // Rollover to start
    stepCount++;
    if (stepCount >= maxStepCount) {

      theta = 0;
      stepCount = 0;

    }

    component.repaint();

  }

  public void decrementRotation(JComponent component) {

    theta -= delta;

    // Rollover to end
    if (stepCount == 0) {

      theta = 2 * Math.PI - delta;
      stepCount = maxStepCount - 1;

    } else {

      stepCount--;

    }

    component.repaint();

  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {

    // Prevent timer events during the paint
    timer.stop();

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    // Required to center the image within the component
    int xCenteringOffset = 0;
    int yCenteringOffset = 0;

    // Required to compensate for centroid movements during placement within the component
    int xRotationOffset = rotationOffsets[stepCount][0];
    int yRotationOffset = rotationOffsets[stepCount][1];

    // Calculate the centroid
    double centerX = xRotationOffset + xCenteringOffset + x + (width / 2);
    double centerY = yRotationOffset + yCenteringOffset + y + (height / 2);

    // Create rotation transform
    AffineTransform tx = new AffineTransform();
    tx.rotate(theta, centerX, centerY);

    // Paint the icon onto the component
    g2.setTransform(tx);
    delegateIcon.paintIcon(c, g2, xRotationOffset + xCenteringOffset + x, yRotationOffset + yCenteringOffset + y);
    g2.dispose();

    // Start the timer again
    timer.start();

  }

  @Override
  public int getIconWidth() {
    return width;
  }

  @Override
  public int getIconHeight() {
    return height;
  }
}