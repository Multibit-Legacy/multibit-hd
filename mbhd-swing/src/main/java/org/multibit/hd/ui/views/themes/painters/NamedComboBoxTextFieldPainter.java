package org.multibit.hd.ui.views.themes.painters;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * <p>Region painter to provide the following to Nimbus LAF:</p>
 * <ul>
 * <li>Paint a combo box with the given foreground color in the Nimbus style</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class NamedComboBoxTextFieldPainter extends AbstractNamedRegionPainter {

  /**
   * Painter states
   */
  public static final int BACKGROUND_DISABLED = 1;
  public static final int BACKGROUND_ENABLED = 2;
  public static final int BACKGROUND_SELECTED = 3;

  // All Colors used for painting
  private Color color1 = adjustForegroundColor(-0.6111111f, -0.110526316f, -0.74509805f, -237);
  private Color color2 = adjustForegroundColor(-0.006944418f, -0.07187897f, 0.06666666f, 0);
  private Color color3 = adjustForegroundColor(0.007936537f, -0.07703349f, 0.0745098f, 0);
  private Color color4 = adjustForegroundColor(0.007936537f, -0.07968931f, 0.14509803f, 0);
  private Color color5 = adjustForegroundColor(0.007936537f, -0.07856284f, 0.11372548f, 0);
  private Color color6 = adjustBackgroundColor(0.040395975f, -0.60315615f, 0.29411763f, 0);
  private Color color7 = adjustBackgroundColor(0.016586483f, -0.6051466f, 0.3490196f, 0);
  private Color color8 = adjustForegroundColor(-0.027777791f, -0.0965403f, -0.18431371f, 0);
  private Color color9 = adjustForegroundColor(0.055555582f, -0.1048766f, -0.05098039f, 0);
  private Color color10 = adjustBackgroundColor(0.6666667f, 0.004901961f, -0.19999999f, 0);
  private Color color11 = adjustBackgroundColor(0.0f, 0.0f, 0.0f, 0);
  private Color color12 = adjustForegroundColor(0.055555582f, -0.105344966f, 0.011764705f, 0);

  // Array of current component colors, updated in each paint call
  private Object[] componentColors;

  /**
   * @param color The color to use as the basis for the painter
   * @param state The state of the button to which this painter will apply
   */
  public NamedComboBoxTextFieldPainter(Color color, int state) {

//    super(color, Themes.currentTheme.buttonBackground(), state);

    super(Color.RED, Color.BLUE, state);

    Insets insets = new Insets(8, 1, 8, 8);
    this.ctx = new PaintContext(insets, new Dimension(20, 24), false);

  }

  @Override
  protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
    //populate componentColors array with colors calculated in getExtendedCacheKeys call
    componentColors = extendedCacheKeys;
    //generate this entire method. Each state/bg/fg/border combo that has
    //been painted gets its own KEY and paint method.
    switch (state) {
      case BACKGROUND_DISABLED:
        paintBackgroundDisabled(g);
        break;
      case BACKGROUND_ENABLED:
        paintBackgroundEnabled(g);
        break;
      case BACKGROUND_SELECTED:
        paintBackgroundSelected(g);
        break;

    }
  }

  private void paintBackgroundDisabled(Graphics2D g) {
    rect = decodeRect1();
    g.setPaint(color1);
    g.fill(rect);
    rect = decodeRect2();
    g.setPaint(decodeGradient1(rect));
    g.fill(rect);
    rect = decodeRect3();
    g.setPaint(decodeGradient2(rect));
    g.fill(rect);
    rect = decodeRect4();
    g.setPaint(color6);
    g.fill(rect);
    rect = decodeRect5();
    g.setPaint(color7);
    g.fill(rect);

  }

  private void paintBackgroundEnabled(Graphics2D g) {
    rect = decodeRect1();
    g.setPaint(color1);
    g.fill(rect);
    rect = decodeRect2();
    g.setPaint(decodeGradient3(rect));
    g.fill(rect);
    rect = decodeRect3();
    g.setPaint(decodeGradient4(rect));
    g.fill(rect);
    rect = decodeRect4();
    g.setPaint(color12);
    g.fill(rect);
    rect = decodeRect5();
    g.setPaint(color11);
    g.fill(rect);

  }

  private void paintBackgroundSelected(Graphics2D g) {
    rect = decodeRect1();
    g.setPaint(color1);
    g.fill(rect);
    rect = decodeRect2();
    g.setPaint(decodeGradient3(rect));
    g.fill(rect);
    rect = decodeRect3();
    g.setPaint(decodeGradient4(rect));
    g.fill(rect);
    rect = decodeRect4();
    g.setPaint(color12);
    g.fill(rect);
    rect = decodeRect5();
    g.setPaint(color11);
    g.fill(rect);

  }


  private Rectangle2D decodeRect1() {
    rect.setRect(decodeX(0.6666667f), //x
      decodeY(2.3333333f), //y
      decodeX(3.0f) - decodeX(0.6666667f), //width
      decodeY(2.6666667f) - decodeY(2.3333333f)); //height
    return rect;
  }

  private Rectangle2D decodeRect2() {
    rect.setRect(decodeX(0.6666667f), //x
      decodeY(0.4f), //y
      decodeX(3.0f) - decodeX(0.6666667f), //width
      decodeY(1.0f) - decodeY(0.4f)); //height
    return rect;
  }

  private Rectangle2D decodeRect3() {
    rect.setRect(decodeX(1.0f), //x
      decodeY(0.6f), //y
      decodeX(3.0f) - decodeX(1.0f), //width
      decodeY(1.0f) - decodeY(0.6f)); //height
    return rect;
  }

  private Rectangle2D decodeRect4() {
    rect.setRect(decodeX(0.6666667f), //x
      decodeY(1.0f), //y
      decodeX(3.0f) - decodeX(0.6666667f), //width
      decodeY(2.3333333f) - decodeY(1.0f)); //height
    return rect;
  }

  private Rectangle2D decodeRect5() {
    rect.setRect(decodeX(1.0f), //x
      decodeY(1.0f), //y
      decodeX(3.0f) - decodeX(1.0f), //width
      decodeY(2.0f) - decodeY(1.0f)); //height
    return rect;
  }


  private Paint decodeGradient1(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.0f, 0.5f, 1.0f},
      new Color[]{color2,
        decodeColor(color2, color3, 0.5f),
        color3});
  }

  private Paint decodeGradient2(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (1.0f * h) + y, (0.5f * w) + x, (0.0f * h) + y,
      new float[]{0.0f, 0.5f, 1.0f},
      new Color[]{color4,
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
      new float[]{0.0f, 0.49573863f, 0.99147725f},
      new Color[]{color8,
        decodeColor(color8, color9, 0.5f),
        color9});
  }

  private Paint decodeGradient4(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.1f, 0.49999997f, 0.9f},
      new Color[]{color10,
        decodeColor(color10, color11, 0.5f),
        color11});
  }


}