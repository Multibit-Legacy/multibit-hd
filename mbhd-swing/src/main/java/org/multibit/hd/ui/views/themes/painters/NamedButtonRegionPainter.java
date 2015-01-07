package org.multibit.hd.ui.views.themes.painters;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * <p>Region painter to provide the following to Nimbus LAF:</p>
 * <ul>
 * <li>Paint a button with the given foreground color in the Nimbus style</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class NamedButtonRegionPainter extends AbstractNamedRegionPainter {

  /**
   * Painter states
   */
  public static final int BACKGROUND_DEFAULT = 1;
  public static final int BACKGROUND_DEFAULT_FOCUSED = 2;
  public static final int BACKGROUND_MOUSEOVER_DEFAULT = 3;
  public static final int BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED = 4;
  public static final int BACKGROUND_PRESSED_DEFAULT = 5;
  public static final int BACKGROUND_PRESSED_DEFAULT_FOCUSED = 6;
  public static final int BACKGROUND_DISABLED = 7;
  public static final int BACKGROUND_ENABLED = 8;
  public static final int BACKGROUND_FOCUSED = 9;
  public static final int BACKGROUND_MOUSEOVER = 10;
  public static final int BACKGROUND_MOUSEOVER_FOCUSED = 11;
  public static final int BACKGROUND_PRESSED = 12;
  public static final int BACKGROUND_PRESSED_FOCUSED = 13;

  // All Colors used for painting
  public final Color color1 = adjustForegroundColor(-0.027777791f, -0.06885965f, -0.36862746f, -190);
  public final Color color2 = adjustBackgroundColor( 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
  public final Color color3 = adjustBackgroundColor( 5.1498413E-4f, -0.095173776f, -0.25882354f, 0);
  public final Color color4 = adjustBackgroundColor( 0.004681647f, -0.6197143f, 0.43137252f, 0);
  public final Color color5 = adjustBackgroundColor( 0.004681647f, -0.5766426f, 0.38039213f, 0);
  public final Color color6 = adjustBackgroundColor( 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
  public final Color color7 = adjustBackgroundColor( 5.1498413E-4f, -0.46404046f, 0.36470586f, 0);
  public final Color color8 = adjustBackgroundColor( 5.1498413E-4f, -0.47761154f, 0.44313723f, 0);
  public final Color color9 = decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
  public final Color color10 = adjustBackgroundColor( 0.0013483167f, -0.1769987f, -0.12156865f, 0);
  public final Color color11 = adjustBackgroundColor( 0.059279382f, 0.3642857f, -0.43529415f, 0);
  public final Color color12 = adjustBackgroundColor( 0.004681647f, -0.6198413f, 0.43921566f, 0);
  public final Color color13 = adjustBackgroundColor( -0.0017285943f, -0.5822163f, 0.40392154f, 0);
  public final Color color14 = adjustBackgroundColor( 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
  public final Color color15 = adjustBackgroundColor( 5.1498413E-4f, -0.47698414f, 0.43921566f, 0);
  public final Color color16 = adjustBackgroundColor( -0.06415892f, -0.5455182f, 0.45098037f, 0);
  public final Color color17 = adjustForegroundColor(0.0f, -0.110526316f, 0.25490195f, -95);
  public final Color color18 = adjustBackgroundColor( -0.57865167f, -0.6357143f, -0.54901963f, 0);
  public final Color color19 = adjustBackgroundColor( -3.528595E-5f, 0.018606722f, -0.23137257f, 0);
  public final Color color20 = adjustBackgroundColor( -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
  public final Color color21 = adjustBackgroundColor( 0.001903832f, -0.29863563f, 0.1490196f, 0);
  public final Color color22 = adjustBackgroundColor( 0.0f, 0.0f, 0.0f, 0);
  public final Color color23 = adjustBackgroundColor( 0.0018727183f, -0.14126986f, 0.15686274f, 0);
  public final Color color24 = adjustBackgroundColor( 8.9377165E-4f, -0.20852983f, 0.2588235f, 0);
  public final Color color25 = adjustForegroundColor(-0.027777791f, -0.06885965f, -0.36862746f, -232);
  public final Color color26 = adjustForegroundColor(0.0f, -0.06766917f, 0.07843137f, 0);
  public final Color color27 = adjustForegroundColor(0.0f, -0.06484103f, 0.027450979f, 0);
  public final Color color28 = adjustForegroundColor(0.0f, -0.08477524f, 0.16862744f, 0);
  public final Color color29 = adjustForegroundColor(-0.015872955f, -0.080091536f, 0.15686274f, 0);
  public final Color color30 = adjustForegroundColor(0.0f, -0.07016757f, 0.12941176f, 0);
  public final Color color31 = adjustForegroundColor(0.0f, -0.07052632f, 0.1372549f, 0);
  public final Color color32 = adjustForegroundColor(0.0f, -0.070878744f, 0.14509803f, 0);
  public final Color color33 = adjustForegroundColor(-0.055555522f, -0.05356429f, -0.12549019f, 0);
  public final Color color34 = adjustForegroundColor(0.0f, -0.0147816315f, -0.3764706f, 0);
  public final Color color35 = adjustForegroundColor(0.055555582f, -0.10655806f, 0.24313724f, 0);
  public final Color color36 = adjustForegroundColor(0.0f, -0.09823123f, 0.2117647f, 0);
  public final Color color37 = adjustForegroundColor(0.0f, -0.0749532f, 0.24705881f, 0);
  public final Color color38 = adjustForegroundColor(0.0f, -0.110526316f, 0.25490195f, 0);
  public final Color color39 = adjustForegroundColor(0.0f, -0.020974077f, -0.21960783f, 0);
  public final Color color40 = adjustForegroundColor(0.0f, 0.11169591f, -0.53333336f, 0);
  public final Color color41 = adjustForegroundColor(0.055555582f, -0.10658931f, 0.25098038f, 0);
  public final Color color42 = adjustForegroundColor(0.0f, -0.098526314f, 0.2352941f, 0);
  public final Color color43 = adjustForegroundColor(0.0f, -0.07333623f, 0.20392156f, 0);
  public final Color color44 = new Color(245, 250, 255, 160);
  public final Color color45 = adjustForegroundColor(0.055555582f, 0.8894737f, -0.7176471f, 0);
  public final Color color46 = adjustForegroundColor(0.0f, 5.847961E-4f, -0.32156864f, 0);
  public final Color color47 = adjustForegroundColor(-0.00505054f, -0.05960039f, 0.10196078f, 0);
  public final Color color48 = adjustForegroundColor(-0.008547008f, -0.04772438f, 0.06666666f, 0);
  public final Color color49 = adjustForegroundColor(-0.0027777553f, -0.0018306673f, -0.02352941f, 0);
  public final Color color50 = adjustForegroundColor(-0.0027777553f, -0.0212406f, 0.13333333f, 0);
  public final Color color51 = adjustForegroundColor(0.0055555105f, -0.030845039f, 0.23921567f, 0);

  // Array of current component colors, updated in each paint call
  private Object[] componentColors;

  /**
   * @param color The color to use as the basis for the painter
   * @param state The state of the button to which this painter will apply
   */
  public NamedButtonRegionPainter(Color color, int state) {
    super(color, Themes.currentTheme.detailPanelBackground(), state);

    Insets insets = new Insets(7, 7, 7, 7);
    this.ctx = new AbstractRegionPainter.PaintContext(insets, new Dimension(10, 20), false);

  }

  @Override
  protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
    // Populate componentColors array with colors calculated in getExtendedCacheKeys call
    componentColors = extendedCacheKeys;

    // Generate this entire method. Each state/bg/fg/border combo that has
    // been painted gets its own KEY and paint method.
    switch (state) {
      case BACKGROUND_DEFAULT:
        paintBackgroundDefault(g);
        break;
      case BACKGROUND_DEFAULT_FOCUSED:
        paintBackgroundDefaultAndFocused(g);
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT:
        paintBackgroundMouseOverAndDefault(g);
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        paintBackgroundMouseOverAndDefaultAndFocused(g);
        break;
      case BACKGROUND_PRESSED_DEFAULT:
        paintBackgroundPressedAndDefault(g);
        break;
      case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
        paintBackgroundPressedAndDefaultAndFocused(g);
        break;
      case BACKGROUND_DISABLED:
        paintBackgroundDisabled(g);
        break;
      case BACKGROUND_ENABLED:
        paintBackgroundEnabled(g);
        break;
      case BACKGROUND_FOCUSED:
        paintBackgroundFocused(g);
        break;
      case BACKGROUND_MOUSEOVER:
        paintBackgroundMouseOver(g);
        break;
      case BACKGROUND_MOUSEOVER_FOCUSED:
        paintBackgroundMouseOverAndFocused(g);
        break;
      case BACKGROUND_PRESSED:
        paintBackgroundPressed(g);
        break;
      case BACKGROUND_PRESSED_FOCUSED:
        paintBackgroundPressedAndFocused(g);
        break;
      default:
        throw new IllegalStateException("Unknown state:" + state);
    }
  }

  protected Object[] getExtendedCacheKeys(JComponent c) {
    Object[] extendedCacheKeys = null;
    switch (state) {
      case BACKGROUND_DEFAULT:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color4, -0.6197143f, 0.43137252f, 0),
          getComponentColor(c, "background", color5, -0.5766426f, 0.38039213f, 0),
          getComponentColor(c, "background", color6, -0.43866998f, 0.24705881f, 0),
          getComponentColor(c, "background", color7, -0.46404046f, 0.36470586f, 0),
          getComponentColor(c, "background", color8, -0.47761154f, 0.44313723f, 0)};
        break;
      case BACKGROUND_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color4, -0.6197143f, 0.43137252f, 0),
          getComponentColor(c, "background", color5, -0.5766426f, 0.38039213f, 0),
          getComponentColor(c, "background", color6, -0.43866998f, 0.24705881f, 0),
          getComponentColor(c, "background", color7, -0.46404046f, 0.36470586f, 0),
          getComponentColor(c, "background", color8, -0.47761154f, 0.44313723f, 0)};
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color12, -0.6198413f, 0.43921566f, 0),
          getComponentColor(c, "background", color13, -0.5822163f, 0.40392154f, 0),
          getComponentColor(c, "background", color14, -0.4555341f, 0.3215686f, 0),
          getComponentColor(c, "background", color15, -0.47698414f, 0.43921566f, 0),
          getComponentColor(c, "background", color16, -0.5455182f, 0.45098037f, 0)};
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color12, -0.6198413f, 0.43921566f, 0),
          getComponentColor(c, "background", color13, -0.5822163f, 0.40392154f, 0),
          getComponentColor(c, "background", color14, -0.4555341f, 0.3215686f, 0),
          getComponentColor(c, "background", color15, -0.47698414f, 0.43921566f, 0),
          getComponentColor(c, "background", color16, -0.5455182f, 0.45098037f, 0)};
        break;
      case BACKGROUND_PRESSED_DEFAULT:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color20, -0.38050595f, 0.20392156f, 0),
          getComponentColor(c, "background", color21, -0.29863563f, 0.1490196f, 0),
          getComponentColor(c, "background", color22, 0.0f, 0.0f, 0),
          getComponentColor(c, "background", color23, -0.14126986f, 0.15686274f, 0),
          getComponentColor(c, "background", color24, -0.20852983f, 0.2588235f, 0)};
        break;
      case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color20, -0.38050595f, 0.20392156f, 0),
          getComponentColor(c, "background", color21, -0.29863563f, 0.1490196f, 0),
          getComponentColor(c, "background", color22, 0.0f, 0.0f, 0),
          getComponentColor(c, "background", color23, -0.14126986f, 0.15686274f, 0),
          getComponentColor(c, "background", color24, -0.20852983f, 0.2588235f, 0)};
        break;
      case BACKGROUND_ENABLED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color35, -0.10655806f, 0.24313724f, 0),
          getComponentColor(c, "background", color36, -0.09823123f, 0.2117647f, 0),
          getComponentColor(c, "background", color30, -0.07016757f, 0.12941176f, 0),
          getComponentColor(c, "background", color37, -0.0749532f, 0.24705881f, 0),
          getComponentColor(c, "background", color38, -0.110526316f, 0.25490195f, 0)};
        break;
      case BACKGROUND_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color35, -0.10655806f, 0.24313724f, 0),
          getComponentColor(c, "background", color36, -0.09823123f, 0.2117647f, 0),
          getComponentColor(c, "background", color30, -0.07016757f, 0.12941176f, 0),
          getComponentColor(c, "background", color37, -0.0749532f, 0.24705881f, 0),
          getComponentColor(c, "background", color38, -0.110526316f, 0.25490195f, 0)};
        break;
      case BACKGROUND_MOUSEOVER:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color41, -0.10658931f, 0.25098038f, 0),
          getComponentColor(c, "background", color42, -0.098526314f, 0.2352941f, 0),
          getComponentColor(c, "background", color43, -0.07333623f, 0.20392156f, 0),
          getComponentColor(c, "background", color38, -0.110526316f, 0.25490195f, 0)};
        break;
      case BACKGROUND_MOUSEOVER_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color41, -0.10658931f, 0.25098038f, 0),
          getComponentColor(c, "background", color42, -0.098526314f, 0.2352941f, 0),
          getComponentColor(c, "background", color43, -0.07333623f, 0.20392156f, 0),
          getComponentColor(c, "background", color38, -0.110526316f, 0.25490195f, 0)};
        break;
      case BACKGROUND_PRESSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color47, -0.05960039f, 0.10196078f, 0),
          getComponentColor(c, "background", color48, -0.04772438f, 0.06666666f, 0),
          getComponentColor(c, "background", color49, -0.0018306673f, -0.02352941f, 0),
          getComponentColor(c, "background", color50, -0.0212406f, 0.13333333f, 0),
          getComponentColor(c, "background", color51, -0.030845039f, 0.23921567f, 0)};
        break;
      case BACKGROUND_PRESSED_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", color47, -0.05960039f, 0.10196078f, 0),
          getComponentColor(c, "background", color48, -0.04772438f, 0.06666666f, 0),
          getComponentColor(c, "background", color49, -0.0018306673f, -0.02352941f, 0),
          getComponentColor(c, "background", color50, -0.0212406f, 0.13333333f, 0),
          getComponentColor(c, "background", color51, -0.030845039f, 0.23921567f, 0)};
        break;
      default:
        throw new IllegalStateException("Unknown state:" + state);
    }
    return extendedCacheKeys;
  }

  private void paintBackgroundDefault(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color1);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient1(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundDefaultAndFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient1(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOverAndDefault(Graphics2D g) {
    roundRect = decodeRoundRect5();
    g.setPaint(color1);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient3(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOverAndDefaultAndFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient3(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundPressedAndDefault(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color17);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient4(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundPressedAndDefaultAndFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient4(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundDisabled(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color25);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient5(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient6(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundEnabled(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color1);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient7(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient7(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient8(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOver(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color1);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient9(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient10(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOverAndFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient9(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient10(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundPressed(Graphics2D g) {
    roundRect = decodeRoundRect1();
    g.setPaint(color44);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient11(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }

  private void paintBackgroundPressedAndFocused(Graphics2D g) {
    roundRect = decodeRoundRect4();
    g.setPaint(color9);
    g.fill(roundRect);
    roundRect = decodeRoundRect2();
    g.setPaint(decodeGradient11(roundRect));
    g.fill(roundRect);
    roundRect = decodeRoundRect3();
    g.setPaint(decodeGradient2(roundRect));
    g.fill(roundRect);

  }


  private RoundRectangle2D decodeRoundRect1() {
    roundRect.setRoundRect(decodeX(0.2857143f), //x
      decodeY(0.42857143f), //y
      decodeX(2.7142859f) - decodeX(0.2857143f), //width
      decodeY(2.857143f) - decodeY(0.42857143f), //height
      12.0f, 12.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D decodeRoundRect2() {
    roundRect.setRoundRect(decodeX(0.2857143f), //x
      decodeY(0.2857143f), //y
      decodeX(2.7142859f) - decodeX(0.2857143f), //width
      decodeY(2.7142859f) - decodeY(0.2857143f), //height
      9.0f, 9.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D decodeRoundRect3() {
    roundRect.setRoundRect(decodeX(0.42857143f), //x
      decodeY(0.42857143f), //y
      decodeX(2.5714285f) - decodeX(0.42857143f), //width
      decodeY(2.5714285f) - decodeY(0.42857143f), //height
      7.0f, 7.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D decodeRoundRect4() {
    roundRect.setRoundRect(decodeX(0.08571429f), //x
      decodeY(0.08571429f), //y
      decodeX(2.914286f) - decodeX(0.08571429f), //width
      decodeY(2.914286f) - decodeY(0.08571429f), //height
      11.0f, 11.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D decodeRoundRect5() {
    roundRect.setRoundRect(decodeX(0.2857143f), //x
      decodeY(0.42857143f), //y
      decodeX(2.7142859f) - decodeX(0.2857143f), //width
      decodeY(2.857143f) - decodeY(0.42857143f), //height
      9.0f, 9.0f); //rounding
    return roundRect;
  }

  private Paint decodeGradient1(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.05f, 0.5f, 0.95f},
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
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98399997f, 1.0f},
      new Color[]{(Color) componentColors[0],
        decodeColor((Color) componentColors[0], (Color) componentColors[1], 0.5f),
        (Color) componentColors[1],
        decodeColor((Color) componentColors[1], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[3], 0.5f),
        (Color) componentColors[3],
        decodeColor((Color) componentColors[3], (Color) componentColors[4], 0.5f),
        (Color) componentColors[4]});
  }

  private Paint decodeGradient3(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.05f, 0.5f, 0.95f},
      new Color[]{color10,
        decodeColor(color10, color11, 0.5f),
        color11});
  }

  private Paint decodeGradient4(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.05f, 0.5f, 0.95f},
      new Color[]{color18,
        decodeColor(color18, color19, 0.5f),
        color19});
  }

  private Paint decodeGradient5(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.09f, 0.52f, 0.95f},
      new Color[]{color26,
        decodeColor(color26, color27, 0.5f),
        color27});
  }

  private Paint decodeGradient6(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f},
      new Color[]{color28,
        decodeColor(color28, color29, 0.5f),
        color29,
        decodeColor(color29, color30, 0.5f),
        color30,
        decodeColor(color30, color30, 0.5f),
        color30,
        decodeColor(color30, color31, 0.5f),
        color31,
        decodeColor(color31, color32, 0.5f),
        color32});
  }

  private Paint decodeGradient7(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.09f, 0.52f, 0.95f},
      new Color[]{color33,
        decodeColor(color33, color34, 0.5f),
        color34});
  }

  private Paint decodeGradient8(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f},
      new Color[]{(Color) componentColors[0],
        decodeColor((Color) componentColors[0], (Color) componentColors[1], 0.5f),
        (Color) componentColors[1],
        decodeColor((Color) componentColors[1], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[3], 0.5f),
        (Color) componentColors[3],
        decodeColor((Color) componentColors[3], (Color) componentColors[4], 0.5f),
        (Color) componentColors[4]});
  }

  private Paint decodeGradient9(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.09f, 0.52f, 0.95f},
      new Color[]{color39,
        decodeColor(color39, color40, 0.5f),
        color40});
  }

  private Paint decodeGradient10(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98f, 1.0f},
      new Color[]{(Color) componentColors[0],
        decodeColor((Color) componentColors[0], (Color) componentColors[1], 0.5f),
        (Color) componentColors[1],
        decodeColor((Color) componentColors[1], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[2], 0.5f),
        (Color) componentColors[2],
        decodeColor((Color) componentColors[2], (Color) componentColors[3], 0.5f),
        (Color) componentColors[3],
        decodeColor((Color) componentColors[3], (Color) componentColors[3], 0.5f),
        (Color) componentColors[3]});
  }

  private Paint decodeGradient11(Shape s) {
    Rectangle2D bounds = s.getBounds2D();
    float x = (float) bounds.getX();
    float y = (float) bounds.getY();
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
      new float[]{0.05f, 0.5f, 0.95f},
      new Color[]{color45,
        decodeColor(color45, color46, 0.5f),
        color46});
  }

}