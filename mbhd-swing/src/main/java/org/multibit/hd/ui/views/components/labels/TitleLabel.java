package org.multibit.hd.ui.views.components.labels;

import javax.swing.*;
import java.awt.*;

/**
 * Provides an anti-aliased JLabel that corrects font rendering problems on Windows
 */
public class TitleLabel extends JLabel {

  public TitleLabel(String htmlText) {
    super(htmlText);
  }

  @Override
  protected void paintComponent(Graphics g) {

    // Prepare the rendering hints
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    super.paintComponent(g);
  }
}
