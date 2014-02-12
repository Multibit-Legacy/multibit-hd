package org.multibit.hd.ui.views.components.borders;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.ImageDecorator;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * <p>Border to provide the following to UI:</p>
 * <ul>
 * <li>Rounded corners for consistent look and feel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RoundedBorder extends AbstractBorder {

  private final Color borderColor;

  private int cornerRadius;

  private Insets insets = new Insets(5, 10, 5, 10);

  /**
   * <p>Default rounded border with Nimbus color and standard corner radius</p>
   */
  public RoundedBorder() {

    this(UIManager.getColor("nimbusBorder"));

  }

  /**
   * <p>Default rounded border with chosen color and standard corner radius</p>
   *
   * @param borderColor The border color
   */
  public RoundedBorder(Color borderColor) {

    this(borderColor, MultiBitUI.COMPONENT_CORNER_RADIUS);

  }

  /**
   * <p>Default rounded border with chosen color and curve radius</p>
   *
   * @param borderColor The border color
   * @param cornerRadius The curve radius
   */
  public RoundedBorder(Color borderColor, int cornerRadius) {

    this.borderColor = borderColor;
    this.cornerRadius = cornerRadius;

  }

  @Override
  public Insets getBorderInsets(Component c, Insets insets) {

    // Use this insets to initialise the provided
    insets.top = this.insets.top;
    insets.left = this.insets.left;
    insets.bottom = this.insets.bottom;
    insets.right = this.insets.right;

    return insets;
  }

  public void paintBorder(Component c, Graphics g, int x, int y,
                          int width, int height) {

    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    g2.translate(x, y);

    Stroke original = g2.getStroke();
    g2.setColor(borderColor);
    g2.setStroke(new BasicStroke(1));
    g2.drawRoundRect(1, 1, width -2 , height -2, cornerRadius, cornerRadius);
    g2.setStroke(original);

    g2.translate(-x, -y);

  }

  public boolean isBorderOpaque() {

    return true;

  }
}