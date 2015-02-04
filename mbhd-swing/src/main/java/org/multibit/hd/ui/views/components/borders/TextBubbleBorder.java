package org.multibit.hd.ui.views.components.borders;

import org.multibit.hd.ui.views.components.ImageDecorator;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * <p>Border to provide the following to panels:</p>
 * <ul>
 * <li>Rounded, colored border with variable thickness</li>
 * </ul>
 * <p>Adapted from <a href="http://stackoverflow.com/a/16909994/396747">this Stack Overflow answer</a></p>
 *
 * @since 0.0.1
 *
 */
public class TextBubbleBorder extends AbstractBorder {

  /**
   * The border color
   */
  private final Color color;

  /**
   * The border thickness
   */
  private final int thickness;

  /**
   * The border radii
   */
  private final int radii;

  /**
   * The "speech pointer"
   */
  private final int pointerSize;

  /**
   * The positioning of the "speech pointer"
   */
  private final boolean pointerLeft;

  private final Insets insets;

  private final BasicStroke stroke;

  private final int strokePad;

  private final RenderingHints hints;

  /**
   * <p>A default rounded panel with no pointer</p>
   *
   * @param color The color
   */
  public TextBubbleBorder(Color color) {

    this(color, 1, 10, 0, true);

  }

  /**
   * <p>A customised text bubble with no pointer</p>
   *
   * @param color     The color
   * @param thickness The thickness (in px)
   * @param radii     The radius to use for each corner (default 10)
   */
  public TextBubbleBorder(Color color, int thickness, int radii) {

    this(color, thickness, radii, 0, true);

  }

  /**
   * <p>A customised text bubble with pointer defined</p>
   *
   * @param color       The color
   * @param thickness   The thickness (in px)
   * @param radii       The radius to use for each corner (default 10)
   * @param pointerSize The pointer size
   * @param pointerLeft True if the pointer is on the left
   */
  public TextBubbleBorder(Color color, int thickness, int radii, int pointerSize, boolean pointerLeft) {

    this.pointerLeft = pointerLeft;

    this.thickness = thickness;
    this.radii = radii;
    this.pointerSize = pointerSize;
    this.color = color;

    this.stroke = new BasicStroke(thickness);
    this.strokePad = thickness / 2;

    this.hints = new RenderingHints(ImageDecorator.smoothRenderingHints());

    // Top and bottom padding must adjust to allow for single line panels (e.g. alerts)
    // and also compromise for text fields and text areas. A good value is 6px.
    int topPad = 6;
    int bottomPad = Math.max(topPad, pointerSize + strokePad);

    insets = new Insets(topPad, 8, bottomPad, 8);

  }

  @Override
  public Insets getBorderInsets(Component c) {
    return insets;
  }

  @Override
  public Insets getBorderInsets(Component c, Insets insets) {
    return getBorderInsets(c);
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

    // Work out the lowest inside line of the bubble
    int bottomLineY = height - thickness - pointerSize - 1;

    // Draw the rounded bubble border with a few tweaks for text fields and areas
    RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
      strokePad + 2,
      strokePad + 2,
      width - thickness - strokePad - 3,
      bottomLineY - 2,
      radii,
      radii);

    Area area = new Area(bubble);

    // Should the "speech pointer" polygon be included?
    if (pointerSize > 0) {
      Polygon pointer = new Polygon();
      int pointerPad = 4;

      // Place on left
      if (pointerLeft) {
        // Left point
        pointer.addPoint(strokePad + radii + pointerPad, bottomLineY);
        // Right point
        pointer.addPoint(strokePad + radii + pointerPad + pointerSize, bottomLineY);
        // Bottom point
        pointer.addPoint(strokePad + radii + pointerPad + (pointerSize / 2), height - strokePad);
      } else {
        // Left point
        pointer.addPoint(width - (strokePad + radii + pointerPad), bottomLineY);
        // Right point
        pointer.addPoint(width - (strokePad + radii + pointerPad + pointerSize), bottomLineY);
        // Bottom point
        pointer.addPoint(width - (strokePad + radii + pointerPad + (pointerSize / 2)), height - strokePad);
      }
      area.add(new Area(pointer));
    }

    // Get the 2D graphics context
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHints(hints);

    // Paint the background color of the parent everywhere
    // outside the clip of the text bubble
    Component parent = c.getParent();
    if (parent != null) {

      Color bg = parent.getBackground();
      Rectangle rect = new Rectangle(0, 0, width, height);

      Area borderRegion = new Area(rect);
      borderRegion.subtract(area);

      g2.setClip(borderRegion);
      g2.setColor(bg);
      g2.fillRect(0, 0, width, height);
      g2.setClip(null);

    }

    // Set the border color
    g2.setColor(color);
    g2.setStroke(stroke);
    g2.draw(area);

  }
}