package org.multibit.hd.ui.views.themes;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.hd.ui.languages.LanguageKey;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * <p>utility to provide the following to Views:</p>
 * <ul>
 * <li>Provision of a UI theme</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class Themes {

  // Always start with the Bootstrap theme and switch during startup if required
  @SuppressFBWarnings({"MS_CANNOT_BE_FINAL"})
  public static Theme currentTheme = new BootstrapTheme();

  /**
   * Utilities do not have public constructors
   */
  private Themes() {
  }

  public static synchronized void switchTheme(Theme newTheme) {

    Preconditions.checkNotNull(newTheme, "'newTheme' must be present");

    currentTheme = newTheme;

    // Gets used in combo box borders and provides the basis for a "default button"
    UIManager.put("nimbusBase", currentTheme.readOnlyComboBox());

    // Provides basis for text
    UIManager.put("nimbusBlueGrey", currentTheme.fadedText());

    UIManager.put("nimbusBorder", currentTheme.text());
    UIManager.put("nimbusDisabledText", currentTheme.fadedText());

    UIManager.put("nimbusLightBackground", currentTheme.sidebarPanelBackground());

    UIManager.put("nimbusFocus", currentTheme.infoAlertBorder());

    UIManager.put("nimbusSelectedText", currentTheme.infoAlertText());
    UIManager.put("nimbusSelection", currentTheme.infoAlertBackground());
    UIManager.put("nimbusSelectionBackground", currentTheme.infoAlertBackground());

    // Scrollbar UI
    Color scrollBarThumb = currentTheme.infoAlertBackground();
    UIManager.put("ScrollBar.thumb", scrollBarThumb);
    UIManager.put("ScrollBar.thumbDarkShadow", scrollBarThumb);
    UIManager.put("ScrollBar.thumbShadow", scrollBarThumb.darker());
    UIManager.put("ScrollBar.thumbHighlight", scrollBarThumb.darker());

    Color scrollBarTrack = currentTheme.detailPanelBackground();
    UIManager.put("ScrollBar.background", new ColorUIResource(Color.RED));
    UIManager.put("ScrollBar.foreground", new ColorUIResource(Color.BLUE));
    UIManager.put("ScrollBar.track", scrollBarTrack);
    UIManager.put("ScrollBar.trackHighlight", new ColorUIResource(Color.YELLOW));

    // Ensure the language icons match the colour scheme
    LanguageKey.resetIcons();

    // Adjust the font size
    initializeFontSize();

  }

  /**
   * @param color The color
   *
   * @return A hex representation of the RGB values (e.g. "abcdef")
   */
  public static String toHexString(Color color) {
    return Integer.toHexString(color.getRGB());
  }

  /**
   * <p>Apply a default font size modifier</p>
   */
  public static void initializeFontSize() {

    // TODO Introduce a theme-aware font factory for components to use

    // TODO Add this to the configuration in appearance for visually impaired users
    float multiplier = 1.0f;
    UIDefaults defaults = UIManager.getDefaults();

    for (Enumeration e = defaults.keys(); e.hasMoreElements(); ) {

      Object key = e.nextElement();
      Object value = defaults.get(key);

      if (value instanceof Font) {

        Font font = (Font) value;
        int newSize = Math.round(font.getSize() * multiplier);

        if (value instanceof FontUIResource) {
          defaults.put(key, new FontUIResource(font.getName(), font.getStyle(), newSize));
        } else {
          defaults.put(key, new Font(font.getName(), font.getStyle(), newSize));
        }

      }

    }

  }
}
