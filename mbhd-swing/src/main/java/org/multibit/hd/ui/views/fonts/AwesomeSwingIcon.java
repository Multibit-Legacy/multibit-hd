package org.multibit.hd.ui.views.fonts;

import org.multibit.hd.ui.views.components.ImageDecorator;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
      setForeground(Themes.currentTheme.buttonFadedText());
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

    iconWidth = fm.stringWidth(text);
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

    Graphics2D g2 = (Graphics2D) g.create();

    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    g2.setFont(font);
    g2.setColor(getForeground());

    // Align the icon vertically
    FontMetrics fm = g2.getFontMetrics();
    g2.translate(x, y + fm.getAscent());

    // Draw the Font Awesome character without any offset to allow rotation if required
    g2.drawString(text, 0, 0);

    g2.dispose();
  }

  @Override
  public void propertyChange(PropertyChangeEvent e) {

    //  Handle font change when using the default font
    if (font == null) {
      calculateIconDimensions();
    }

  }
}

