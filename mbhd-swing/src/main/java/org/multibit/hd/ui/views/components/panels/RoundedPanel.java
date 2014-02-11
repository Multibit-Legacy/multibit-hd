package org.multibit.hd.ui.views.components.panels;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.ImageDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Panel to provide the following to UI:</p>
 * <ul>
 * <li>Rounded corners for use with wizards/light boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class RoundedPanel extends JPanel {

  private final Color shadowColor;

  private final int shadowAlpha;

  private final int cornerRadius;

  /**
   * @param layout The layout manager
   */
  public RoundedPanel(LayoutManager2 layout) {
    super(layout);

    setOpaque(false);

    this.shadowColor = Color.BLACK;
    this.shadowAlpha = 127;
    this.cornerRadius = MultiBitUI.CORNER_RADIUS;

  }

  /**
   * @param layout       The layout manager
   * @param shadowColor  The shadow color (usually black but see LightBoxPanel)
   * @param shadowAlpha  The transparency value of the shadow (usually 127 representing 0.5f but see LightBoxPanel)
   * @param cornerRadius The corner radius in pixels (keep this under 20 to avoid obvious rendering artifacts)
   */
  public RoundedPanel(LayoutManager2 layout, Color shadowColor, int shadowAlpha, int cornerRadius) {
    super(layout);

    setOpaque(false);

    this.shadowColor = shadowColor;
    this.shadowAlpha = shadowAlpha;
    this.cornerRadius = cornerRadius;

  }

  @Override
  protected void paintComponent(Graphics g) {

    super.paintComponent(g);

    // Get the dimensions
    int width = getWidth();
    int height = getHeight();

    // Convert the shadow color into an alpha composite
    Color shadowColorA = new Color(
      shadowColor.getRed(),
      shadowColor.getGreen(),
      shadowColor.getBlue(),
      shadowAlpha
    );

    // Ensure we render with smooth outcome
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    // Fill in a solid block of the shadow background
    g2.setColor(shadowColorA);
    g2.fillRect(0, 0, width, height);

    // Fill in a solid rounded block of the panel
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

    // Draw the panel foreground over the shadow with rounded corners to give a subtle border effect
    Stroke original = g2.getStroke();
    g2.setColor(getForeground());
    g2.setStroke(new BasicStroke(0));
    g2.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
    g2.setStroke(original);

  }
}
