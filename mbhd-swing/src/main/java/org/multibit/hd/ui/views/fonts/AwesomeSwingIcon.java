package org.multibit.hd.ui.views.fonts;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * <p>Icon to provide the following to UI:</p>
 * <ul>
 * <li>Conversion of Font Awesome iconography to Swing Icon</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AwesomeSwingIcon implements Icon, PropertyChangeListener {

  private JComponent component;

  private String text;

  private Font font = AwesomeDecorator.AWESOME_FONT;

  private Color foreground;

  private int iconWidth;
  private int iconHeight;

  /**
   * <p>Create an "enabled" icon matching the component font</p>
   *
   * @param component The component to which the icon will be added
   * @param text      The text to be rendered on the Icon
   */
  public AwesomeSwingIcon(JComponent component, Character text) {

    this(component, text, component.getFont().getSize());

  }

  /**
   * <p>Create an "enabled" icon matching the component font with specified size</p>
   *
   * @param component The component to which the icon will be added
   * @param text      The text to be rendered on the Icon
   * @param enabled   True if the icon should be enabled, false for a more faded style
   */
  public AwesomeSwingIcon(JComponent component, Character text, boolean enabled) {

    this(component, text, component.getFont().getSize(), enabled);

  }

  /**
   * <p>Create an "enabled" icon matching the component font with specified size</p>
   *
   * @param component The component to which the icon will be added
   * @param text      The text to be rendered on the Icon
   * @param size      The font size to use
   */
  public AwesomeSwingIcon(JComponent component, Character text, int size) {

    this(component, text, size, true);

  }

  /**
   * <p>Create an icon matching the component font with specified size</p>
   *
   * @param component The component to which the icon will be added
   * @param text      The text to be rendered on the Icon
   * @param size      The font size to use
   * @param enabled   True if the icon should be enabled, false for a more faded style
   */
  public AwesomeSwingIcon(JComponent component, Character text, int size, boolean enabled) {

    this.component = component;

    font = font.deriveFont((float) size);
    setText(String.valueOf(text));

    if (!enabled) {
      setForeground(component.getForeground().brighter());
    }

    component.addPropertyChangeListener("font", this);

  }

  /**
   * Get the text String that will be rendered on the Icon
   *
   * @return the text of the Icon
   */
  public String getText() {
    return text;
  }

  /**
   * @param text the text to be rendered on the Icon
   */
  public void setText(String text) {
    this.text = text;

    calculateIconDimensions();
  }

  /**
   * @return The icon color (same as component text by default)
   */
  public Color getForeground() {
    if (foreground == null)
      return component.getForeground();
    else
      return foreground;
  }

  /**
   * @param foreground The icon color
   */
  public void setForeground(Color foreground) {
    this.foreground = foreground;
    component.repaint();
  }

  /**
   * <p>Work out the </p>
   * Calculate the size of the Icon using the FontMetrics of the Font.
   */
  private void calculateIconDimensions() {

    FontMetrics fm = component.getFontMetrics(font);

    iconWidth = fm.stringWidth(text) + 2;
    iconHeight = fm.getHeight();

    component.revalidate();
  }

  @Override
  public int getIconWidth() {
    return iconWidth;
  }

  @Override
  public int getIconHeight() {
    return iconHeight;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {

    Graphics2D iconGraphics = (Graphics2D) g.create();

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Map map = (Map) (toolkit.getDesktopProperty("awt.font.desktophints"));

    if (map != null) {
      iconGraphics.addRenderingHints(map);
    } else {
      iconGraphics.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    iconGraphics.setFont(font);
    iconGraphics.setColor(getForeground());

    FontMetrics fm = iconGraphics.getFontMetrics();

    iconGraphics.translate(x, y + fm.getAscent());
    iconGraphics.drawString(text, 2, 0);

    iconGraphics.dispose();
  }

  @Override
  public void propertyChange(PropertyChangeEvent e) {
    //  Handle font change when using the default font

    if (font == null)
      calculateIconDimensions();
  }
}

