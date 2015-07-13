package org.multibit.hd.ui.views.components.labels;

import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.views.fonts.TitleFontDecorator;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import java.awt.*;

/**
 * Provides an anti-aliased JLabel that corrects font rendering problems on Windows
 */
public class TitleLabel extends JLabel {

  private final boolean useAATextProperty;

  public TitleLabel(String htmlText) {
    super(htmlText);

    // Determine if anti-aliasing can be used
    Font titleFont  = LanguageKey.fromLocale(Configurations.currentConfiguration.getLocale()).getTitleFont();
    useAATextProperty = titleFont.equals(TitleFontDecorator.CORBEN_REGULAR)
    || titleFont.equals(TitleFontDecorator.OPENSANS_SEMIBOLD);

  }

  @Override
  protected void paintComponent(Graphics g) {

    // Fixes font antialias problem on Windows
    // Note: Must be this exact reference or it doesn't work
    // e.g. using new StringBuffer("AATextInfoPropertyKey") fails
    // See StackOverflow: http://stackoverflow.com/questions/2266199/how-to-make-jtextpane-paint-anti-aliased-font
    if (useAATextProperty) {
      this.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, null);
    }
    super.paintComponent(g);
  }
}
