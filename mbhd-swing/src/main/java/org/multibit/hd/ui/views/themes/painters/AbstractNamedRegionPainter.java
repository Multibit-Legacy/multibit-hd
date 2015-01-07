package org.multibit.hd.ui.views.themes.painters;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * <p>Abstract base class to provide the following to Nimbus LAF:</p>
 * <ul>
 * <li>Common methods to support an interior color (not directly supported through Component or LAF APIs)</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractNamedRegionPainter extends AbstractRegionPainter {

  /**
   * The painter state
   */
  protected int state;

  protected PaintContext ctx;

  // The following 4 variables are reused during the painting code of the layers
  protected Path2D path = new Path2D.Float();
  protected Rectangle2D rect = new Rectangle2D.Float(0, 0, 0, 0);
  protected RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);

  /**
   * The color corresponding to the "foregroundBasisColor" reference
   */
  private final Color foregroundBasisColor;

  /**
   * The color corresponding to the "backgroundBasisColor" reference
   */
  private final Color backgroundBasisColor;

  /**
   * @param foregroundBasisColor The color to use as the basis for the foreground
   * @param backgroundBasisColor The color to use as the basis for the background
   * @param state           The state of the button to which this painter will apply
   */
  public AbstractNamedRegionPainter(Color foregroundBasisColor, Color backgroundBasisColor, int state) {

    this.state = state;
    this.foregroundBasisColor = foregroundBasisColor;
    this.backgroundBasisColor = backgroundBasisColor;

  }

  @Override
  protected final PaintContext getPaintContext() {
    return ctx;
  }

  /**
   * <p>Decodes and returns a color based on the background</p>
   *
   * @param hOffset The hue offset used for derivation.
   * @param sOffset The saturation offset used for derivation.
   * @param bOffset The brightness offset used for derivation.
   * @param aOffset The alpha offset used for derivation. Between 0...255
   *
   * @return The derived color, who's color value will change if the parent uiDefault color changes.
   */
  public Color adjustBackgroundColor(float hOffset, float sOffset, float bOffset, int aOffset) {


    float[] tmp = Color.RGBtoHSB(backgroundBasisColor.getRed(), backgroundBasisColor.getGreen(), backgroundBasisColor.getBlue(), null);
    // apply offsets
    tmp[0] = clamp(tmp[0] + hOffset);
    tmp[1] = clamp(tmp[1] + sOffset);
    tmp[2] = clamp(tmp[2] + bOffset);
    int alpha = clamp(backgroundBasisColor.getAlpha() + aOffset);
    int argbValue = (Color.HSBtoRGB(tmp[0], tmp[1], tmp[2]) & 0xFFFFFF) | (alpha << 24);
    return new Color(argbValue, true);
  }

  /**
   * <p>Decodes and returns a color based on the foreground</p>
   *
   * @param hOffset The hue offset used for derivation.
   * @param sOffset The saturation offset used for derivation.
   * @param bOffset The brightness offset used for derivation.
   * @param aOffset The alpha offset used for derivation. Between 0...255
   *
   * @return The derived color, who's color value will change if the parent uiDefault color changes.
   */
  public Color adjustForegroundColor(float hOffset, float sOffset, float bOffset, int aOffset) {


    float[] tmp = Color.RGBtoHSB(foregroundBasisColor.getRed(), foregroundBasisColor.getGreen(), foregroundBasisColor.getBlue(), null);
    // apply offsets
    tmp[0] = clamp(tmp[0] + hOffset);
    tmp[1] = clamp(tmp[1] + sOffset);
    tmp[2] = clamp(tmp[2] + bOffset);
    int alpha = clamp(foregroundBasisColor.getAlpha() + aOffset);
    int argbValue = (Color.HSBtoRGB(tmp[0], tmp[1], tmp[2]) & 0xFFFFFF) | (alpha << 24);
    return new Color(argbValue, true);
  }

  /**
   * @param value The value
   *
   * @return The value between a limit of 0 to 1
   */
  private float clamp(float value) {
    if (value < 0) {
      value = 0;
    } else if (value > 1) {
      value = 1;
    }
    return value;
  }

  /**
   * @param value The value
   *
   * @return The value between a limit of 0 to 255
   */
  private int clamp(int value) {
    if (value < 0) {
      value = 0;
    } else if (value > 255) {
      value = 255;
    }
    return value;
  }

}
