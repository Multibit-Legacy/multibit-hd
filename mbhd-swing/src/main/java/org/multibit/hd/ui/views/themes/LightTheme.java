package org.multibit.hd.ui.views.themes;

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
  public Color dataEntryBackground() {
    return colors[2][4];
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
    return new Color(0x606060);
  }

  @Override
  public Color inverseText() {
    return new Color(0xffffff);
  }

  @Override
  public Color inverseFadedText() {
    return fadedText();
  }

  @Override
  public Color successBackground() {
    return colors[0][1];
  }

  @Override
  public Color successBorder() {
    return colors[0][2];
  }

  @Override
  public Color successText() {
    return inverseText();
  }

  @Override
  public Color infoBackground() {
    return colors[1][4];
  }

  @Override
  public Color infoBorder() {
    return colors[1][4];
  }

  @Override
  public Color infoText() {
    return inverseText();
  }

  @Override
  public Color warningBackground() {
    return colors[2][4];
  }

  @Override
  public Color warningBorder() {
    return colors[2][2];
  }

  @Override
  public Color warningText() {
    return text();
  }

  @Override
  public Color dangerBackground() {
    return colors[3][1];
  }

  @Override
  public Color dangerBorder() {
    return colors[3][2];
  }

  @Override
  public Color dangerText() {
    return inverseText();
  }

}
