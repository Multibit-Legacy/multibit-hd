package org.multibit.hd.ui.views.themes.painters;

import javax.swing.*;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * <p>Region painter to provide the following to Nimbus LAF:</p>
 * <ul>
 * <li>Paint a button with the given foreground color in the Nimbus style</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public final class NamedTabbedPaneTabAreaPainter extends AbstractNamedRegionPainter {

  /**
   * Painter states
   */
  public static final int BACKGROUND_ENABLED = 1;
  public static final int BACKGROUND_DISABLED = 2;
  public static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
  public static final int BACKGROUND_ENABLED_PRESSED = 4;

  // All Colors used for painting
  private Color color1 = new Color(255, 200, 0, 255);
  private Color color2 = decodeColor("nimbusOrange", 0.08801502f, 0.3642857f, -0.4784314f, 0);
  private Color color3 = decodeColor("nimbusOrange", 5.1498413E-4f, -0.45471883f, 0.31764704f, 0);
  private Color color4 = decodeColor("nimbusOrange", 5.1498413E-4f, -0.4633005f, 0.3607843f, 0);
  private Color color5 = decodeColor("nimbusOrange", 0.05468172f, -0.58308274f, 0.19607842f, 0);
  private Color color6 = decodeColor("nimbusOrange", -0.57865167f, -0.6357143f, -0.54901963f, 0);
  private Color color7 = decodeColor("nimbusOrange", 5.1498413E-4f, -0.4690476f, 0.39215684f, 0);
  private Color color8 = decodeColor("nimbusOrange", 5.1498413E-4f, -0.47635174f, 0.4352941f, 0);
  private Color color9 = decodeColor("nimbusOrange", 0.0f, -0.05401492f, 0.05098039f, 0);
  private Color color10 = decodeColor("nimbusOrange", 0.0f, -0.09303135f, 0.09411764f, 0);


  /**
   * @param color The color to use as the basis for the painter
   * @param state The state of the button to which this painter will apply
   */
  public NamedTabbedPaneTabAreaPainter(Color color, int state) {
    super(color, state);

    this.ctx = new AbstractRegionPainter.PaintContext(new Insets(0, 5, 6, 5), new Dimension(5, 24), false);

  }

  @Override
  protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {

    // Generate this entire method. Each state/bg/fg/border combo that has been painted gets its own KEY and paint method.
    switch (state) {
      case BACKGROUND_ENABLED:
        paintBackgroundEnabled(g);
        break;
      case BACKGROUND_DISABLED:
        paintBackgroundDisabled(g);
        break;
      case BACKGROUND_ENABLED_MOUSEOVER:
        paintBackgroundEnabledAndMouseOver(g);
        break;
      case BACKGROUND_ENABLED_PRESSED:
        paintBackgroundEnabledAndPressed(g);
        break;
    }
  }

  private void paintBackgroundEnabled(Graphics2D g) {
    rect = decodeRect1();
    g.setPaint(color1);
    g.fill(rect);
    rect = decodeRect2();
    g.setPaint(decodeGradient1(rect));
    g.fill(rect);

  }

  private void paintBackgroundDisabled(Graphics2D g) {
    rect = decodeRect2();
    g.setPaint(decodeGradient2(rect));
    g.fill(rect);

  }

  private void paintBackgroundEnabledAndMouseOver(Graphics2D g) {
    rect = decodeRect2();
    g.setPaint(decodeGradient3(rect));
    g.fill(rect);

  }

  private void paintBackgroundEnabledAndPressed(Graphics2D g) {
    rect = decodeRect2();
    g.setPaint(decodeGradient4(rect));
    g.fill(rect);

  }


  private Rectangle2D decodeRect1() {
    rect.setRect(decodeX(0.0f), //x
      decodeY(1.0f), //y
      decodeX(0.0f) - decodeX(0.0f), //width
      decodeY(1.0f) - decodeY(1.0f)); //height
    return rect;
  }

  private Rectangle2D decodeRect2() {
    rect.setRect(decodeX(0.0f), //x
      decodeY(2.1666667f), //y
      decodeX(3.0f) - decodeX(0.0f), //width
      decodeY(3.0f) - decodeY(2.1666667f)); //height
    return rect;
  }


  private Paint decodeGradient1(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f},
      new Color[]{color2,
        decodeColor(color2, color3, 0.5f),
        color3,
        decodeColor(color3, color4, 0.5f),
        color4,
        decodeColor(color4, color2, 0.5f),
        color2});
  }

  private Paint decodeGradient2(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f},
      new Color[]{color5,
        decodeColor(color5, color3, 0.5f),
        color3,
        decodeColor(color3, color4, 0.5f),
        color4,
        decodeColor(color4, color5, 0.5f),
        color5});
  }

  private Paint decodeGradient3(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f},
      new Color[]{color6,
        decodeColor(color6, color7, 0.5f),
        color7,
        decodeColor(color7, color8, 0.5f),
        color8,
        decodeColor(color8, color2, 0.5f),
        color2});
  }

  private Paint decodeGradient4(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f},
      new Color[]{color2,
        decodeColor(color2, color9, 0.5f),
        color9,
        decodeColor(color9, color10, 0.5f),
        color10,
        decodeColor(color10, color2, 0.5f),
        color2});
  }

}