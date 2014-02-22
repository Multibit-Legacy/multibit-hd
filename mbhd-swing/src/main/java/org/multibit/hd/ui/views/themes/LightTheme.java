package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;

import java.awt.*;

/**
 * <p>Strategy to provide the following to Themes API:</p>
 * <ul>
 * <li>A soothing light theme using an accented analogic colour wheel</li>
 * </ul>
 * <p>Use the <a href="http://colorschemedesigner.com/#2U62lw0w0w0w0">colour scheme</a> and Export to a file</p>
 * <p>The export file can be fitted into the array given below with minimal editing</p>
 *
 * @since 0.0.1
 *         
 */
public class LightTheme implements Theme {

  /**
   * Arranged in the same order as the Color Scheme Designer export
   */
  private static Color[][] colors = new Color[][]{
    {
      new Color(0x00BF32),
      new Color(0x248F40),
      new Color(0x003C21),
      new Color(0x38DF64),
      new Color(0x64DF85),
    },
    {
      new Color(0x1437AD),
      new Color(0x2C4081),
      new Color(0x061F70),
      new Color(0x4869D6),
      new Color(0x6E86D6),
    },
    {
      new Color(0xE9FB00),
      new Color(0xB2BC2F),
      new Color(0x97A300),
      new Color(0xEFFD3F),
      new Color(0xF3FD72),
    },
    {
      new Color(0xFF1E00),
      new Color(0xBF4030),
      new Color(0x761300),
      new Color(0xFF5640),
      new Color(0xFF8373),
    }
  };

  @Override
  public Color readOnlyBackground() {
    return colors[1][4];
  }

  @Override
  public Color readOnlyBorder() {
    return colors[1][2];
  }

  @Override
  public Color dataEntryBackground() {
    return colors[2][4];
  }

  @Override
  public Color dataEntryBorder() {
    return colors[2][2];
  }

  @Override
  public Color invalidDataEntryBackground() {
    return colors[3][1];
  }

  @Override
  public Color headerPanelBackground() {
    return new Color(0xe0e0e0);
  }

  @Override
  public Color detailPanelBackground() {
    return new Color(0xf0f0e0);
  }

  @Override
  public Color sidebarPanelBackground() {
    return new Color(0xf0f0f0);
  }

  @Override
  public Color text() {
    return new Color(6, 6, 6);
  }

  @Override
  public Color fadedText() {
    return new Color(0x808080);
  }

  @Override
  public Color inverseText() {
    return new Color(0xfafafa);
  }

  @Override
  public Color inverseFadedText() {
    return fadedText();
  }

  @Override
  public Color successAlertBackground() {
    return colors[0][1];
  }

  @Override
  public Color pinkBackground() {
    return Color.PINK;
  }

  @Override
  public Color successAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(colors[0][1], NamedButtonRegionPainter.BACKGROUND_DEFAULT);

    // Any of 28, 30, 31, 32
    return painter.color31;
  }


  @Override
  public Color successAlertBorder() {
    return colors[0][2];
  }

  @Override
  public Color successAlertText() {
    return inverseText();
  }

  @Override
  public Color infoAlertBackground() {
    return colors[1][4];
  }

  @Override
  public Color infoAlertBorder() {
    return colors[1][4];
  }

  @Override
  public Color infoAlertText() {
    return inverseText();
  }

  @Override
  public Color warningAlertBackground() {
    return colors[2][4];
  }

  @Override
  public Color warningAlertBorder() {
    return colors[2][2];
  }

  @Override
  public Color warningAlertText() {
    return text();
  }

  @Override
  public Color dangerAlertBackground() {
    return colors[3][1];
  }

  @Override
  public Color dangerAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(colors[3][1], NamedButtonRegionPainter.BACKGROUND_DEFAULT);

    // Any of 28, 30, 31, 32
    return painter.color31;
  }

  @Override
  public Color dangerAlertBorder() {
    return colors[3][2];
  }

  @Override
  public Color dangerAlertText() {
    return inverseText();
  }

}
