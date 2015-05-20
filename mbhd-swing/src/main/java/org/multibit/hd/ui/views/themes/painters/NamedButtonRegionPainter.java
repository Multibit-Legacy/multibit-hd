package org.multibit.hd.ui.views.themes.painters;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * <p>Region painter to provide the following to Nimbus LAF:</p>
 * <ul>
 * <li>Paint a button with the given foreground color in the Nimbus style</li>
 * </ul>
 *
 * @since 0.0.1
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
  public final Color foreground = adjustForegroundColor(-0.0f, -0.0f, -0.0f, 0);
  public final Color background = adjustBackgroundColor(-0.0f, -0.0f, -0.0f, 0);

  public final Color lowerBorder = adjustForegroundColor(-0.027f, -0.068f, -0.368f, -190);
  public final Color focusBorder = decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);

  private Color innerFill = adjustForegroundColor(-0.00f, -0.00f, -0.00f, 0);
  private Color innerFillDisabled = adjustForegroundColor(0.05f, -0.05f, -0.05f, 0);

  private Color innerFillPressed = adjustForegroundColor(-0.06f, -0.06f, -0.06f, 0);
  private Color innerFillFocused = adjustForegroundColor(-0.0f, -0.0f, -0.0f, 0);
  private Color innerFillPressedFocused = adjustForegroundColor(-0.06f, -0.06f, -0.06f, 0);
  private Color innerFillMouseOver = adjustForegroundColor(-0.06f, -0.06f, -0.06f, 0);

  private Color innerBorder = adjustForegroundColor(-0.05f, -0.05f, -0.05f, 0);
  private Color innerBorderDisabled = adjustForegroundColor(-0.05f, -0.05f, -0.05f, 0);
  private Color innerBorderPressed = adjustForegroundColor(-0.05f, -0.05f, -0.05f, 0);
  private Color innerBorderMouseOver = adjustForegroundColor(-0.05f, -0.05f, -0.05f, 0);

  public final Color outerBorderDisabled = adjustForegroundColor(-0.05f, -0.05f, -0.05f, 0);

  public final Color alertFadedBackground = adjustForegroundColor(0.0f, -0.07052632f, 0.1372549f, 0);

  /**
   * @param foregroundBasisColor The color to use as the basis for the painter
   * @param state                The state of the button to which this painter will apply
   */
  public NamedButtonRegionPainter(Color foregroundBasisColor, int state) {
    super(foregroundBasisColor, Themes.currentTheme.detailPanelBackground(), state);

    Insets insets = new Insets(7, 7, 7, 7);
    this.ctx = new AbstractRegionPainter.PaintContext(insets, new Dimension(10, 20), false);

  }

  @Override
  protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {

    // Generate this entire method. Each state/bg/fg/border combo that has
    // been painted gets its own KEY and paint method.
    switch (state) {
      case BACKGROUND_DEFAULT:
        paintBackgroundDefaultEnabled(g);
        break;
      case BACKGROUND_DEFAULT_FOCUSED:
        paintBackgroundDefaultAndFocused(g);
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT:
        paintBackgroundDefaultAndMouseOver(g);
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        paintBackgroundDefaultAndMouseOverAndFocused(g);
        break;
      case BACKGROUND_PRESSED_DEFAULT:
        paintBackgroundDefaultAndPressed(g);
        break;
      case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
        paintBackgroundDefaultAndPressedAndFocused(g);
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
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0)
        };
        break;
      case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_PRESSED_DEFAULT:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_DISABLED:
        // Do nothing
        break;
      case BACKGROUND_ENABLED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_MOUSEOVER:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_MOUSEOVER_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_PRESSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      case BACKGROUND_PRESSED_FOCUSED:
        extendedCacheKeys = new Object[]{
          getComponentColor(c, "background", foreground, 0.0f, 0.0f, 0),
        };
        break;
      default:
        throw new IllegalStateException("Unknown state:" + state);
    }
    return extendedCacheKeys;
  }

  private void paintBackgroundDefaultEnabled(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(lowerBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFill);
    g.fill(roundRect);
  }

  private void paintBackgroundDefaultAndFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillFocused);
    g.fill(roundRect);

  }

  private void paintBackgroundDefaultAndMouseOver(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(lowerBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderMouseOver);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillMouseOver);
    g.fill(roundRect);

  }

  private void paintBackgroundDefaultAndMouseOverAndFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderMouseOver);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillPressed);
    g.fill(roundRect);

  }

  private void paintBackgroundDefaultAndPressed(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderPressed);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillPressed);
    g.fill(roundRect);

  }

  private void paintBackgroundDefaultAndPressedAndFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillPressed);
    g.fill(roundRect);

  }

  private void paintBackgroundDisabled(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(outerBorderDisabled);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderDisabled);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillDisabled);
    g.fill(roundRect);

  }

  private void paintBackgroundEnabled(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(lowerBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFill);
    g.fill(roundRect);

  }

  private void paintBackgroundFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillFocused);
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOver(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(lowerBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillMouseOver);
    g.fill(roundRect);

  }

  private void paintBackgroundMouseOverAndFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorder);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillMouseOver);
    g.fill(roundRect);

  }

  private void paintBackgroundPressed(Graphics2D g) {
    roundRect = decodeRoundRectOutermost();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderPressed);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillPressed);
    g.fill(roundRect);

  }

  private void paintBackgroundPressedAndFocused(Graphics2D g) {
    roundRect = decodeRoundRectFocus();
    g.setPaint(focusBorder);
    g.fill(roundRect);
    roundRect = innerBorderRect();
    g.setPaint(innerBorderPressed);
    g.fill(roundRect);
    roundRect = innerFillRect();
    g.setPaint(innerFillPressedFocused);
    g.fill(roundRect);

  }

  private RoundRectangle2D decodeRoundRectOutermost() {
    roundRect.setRoundRect(
      decodeX(0.2857143f), //x
      decodeY(0.42857143f), //y
      decodeX(2.7142859f) - decodeX(0.2857143f), //width
      decodeY(2.857143f) - decodeY(0.42857143f), //height
      12.0f, 12.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D innerBorderRect() {
    roundRect.setRoundRect(
      decodeX(0.2857143f), //x
      decodeY(0.2857143f), //y
      decodeX(2.7142859f) - decodeX(0.2857143f), //width
      decodeY(2.7142859f) - decodeY(0.2857143f), //height
      9.0f, 9.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D innerFillRect() {
    roundRect.setRoundRect(
      decodeX(0.42857143f), //x
      decodeY(0.42857143f), //y
      decodeX(2.5714285f) - decodeX(0.42857143f), //width
      decodeY(2.5714285f) - decodeY(0.42857143f), //height
      7.0f, 7.0f); //rounding
    return roundRect;
  }

  private RoundRectangle2D decodeRoundRectFocus() {
    roundRect.setRoundRect(
      decodeX(0.08571429f), //x
      decodeY(0.08571429f), //y
      decodeX(2.914286f) - decodeX(0.08571429f), //width
      decodeY(2.914286f) - decodeY(0.08571429f), //height
      11.0f, 11.0f); //rounding
    return roundRect;
  }

}