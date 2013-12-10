package org.multibit.hd.ui.views.components;

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
  private Color color;

  /**
   * The border thickness
   */
  private int thickness = 2;

  /**
   * The border radii
   */
  private int radii = 10;

  /**
   * The "speech pointer"
   */
  private int pointerSize = 0;

  /**
   * The positioning of the "speech pointer"
   */
  private boolean left = true;

  private Insets insets = null;

  private BasicStroke stroke = null;

  private int strokePad;

  private RenderingHints hints;

  /**
   * <p>A default rounded panel with no pointer</p>
   *
   * @param color The color
   */
  public TextBubbleBorder(Color color) {

    this(color, 1, 5, 1);

  }

  /**
   * <p>A customised text bubble with its pointer on the left</p>
   *
   * @param color       The color
   * @param thickness   The thickness (in px)
   * @param radii       The radius to use for each corner (default 10)
   * @param pointerSize The pointer size
   */
  public TextBubbleBorder(Color color, int thickness, int radii, int pointerSize) {

    this(color, thickness, radii, pointerSize, true);

  }

  /**
   * <p>A fully-customised text bubble</p>
   *
   * @param color       The color
   * @param thickness   The thickness (in px)
   * @param radii       The radius to use for each corner (default 10)
   * @param pointerSize The pointer size
   * @param left        True if the pointer is on the left
   */
  public TextBubbleBorder(Color color, int thickness, int radii, int pointerSize, boolean left) {

    this.left = left;

    this.thickness = thickness;
    this.radii = radii;
    this.pointerSize = pointerSize;
    this.color = color;

    stroke = new BasicStroke(thickness);
    strokePad = thickness / 2;

    hints = new RenderingHints(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);

    int pad = radii + strokePad;
    int bottomPad = pad + pointerSize + strokePad;

    insets = new Insets(pad, pad, bottomPad, pad);

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
  public void paintBorder(
    Component c,
    Graphics g,
    int x, int y,
    int width, int height) {

    // Work out the lowest line
    int bottomLineY = height - thickness - pointerSize;

    // Draw the "speech pointer" polygon
    Polygon pointer = new Polygon();
    int pointerPad = 4;
    if (left) {
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

    // Draw the bubble
    RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
      strokePad,
      strokePad,
      width - thickness,
      bottomLineY,
      radii,
      radii);
    Area area = new Area(bubble);
    area.add(new Area(pointer));

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

    g2.setColor(color);
    g2.setStroke(stroke);
    g2.draw(area);

  }
}