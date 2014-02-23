package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;

import java.awt.*;

/**
 * <p>Strategy to provide the following to Themes API:</p>
 * <ul>
 * <li>A low-light dark theme using an accented analogic colour wheel</li>
 * </ul>
 * <p>See <a href="http://colorschemedesigner.com/#2U62lw0w0w0w0">colour scheme</a> and Export to a file as a starting point</p>
 *
 * @since 0.0.1
 *         
 */
public class DarkTheme implements Theme {

  @Override
  public Color readOnlyBackground() {
    return new Color(0x2c4081);
  }

  @Override
  public Color readOnlyBorder() {
    return new Color(0x4869d6);
  }

  @Override
  public Color dataEntryBackground() {
    return new Color(0xe9fb00);
  }

  @Override
  public Color dataEntryBorder() {
    return new Color(0xeffd3f);
  }

  @Override
  public Color invalidDataEntryBackground() {
    return new Color(0xbf4030);
  }

  @Override
  public Color headerPanelBackground() {
    return new Color(0x404040);
  }

  @Override
  public Color detailPanelBackground() {
    return new Color(0x303030);
  }

  @Override
  public Color buttonBackground() {
    return new Color(0x506040);
  }

  @Override
  public Color sidebarPanelBackground() {
    return new Color(0x303030);
  }

  @Override
  public Color text() {
    return new Color(0xbababa);
  }

  @Override
  public Color fadedText() {
    return new Color(0x9a9a9a);
  }

  @Override
  public Color inverseText() {
    return new Color(0x060606);
  }

  @Override
  public Color inverseFadedText() {
    return fadedText();
  }

  @Override
  public Color successAlertBackground() {
    return new Color(0x248f40);
  }

  @Override
  public Color pendingAlertBackground() {
    return new Color(0xffafaf);
  }

  @Override
  public Color pendingAlertFadedBackground() {
    return new Color(0xffcfcf);
  }

  @Override
  public Color pendingAlertBorder() {
    return new Color(0xe05f5f);
  }

  @Override
  public Color pendingAlertText() {
    return text();
  }

  @Override
  public Color successAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      successAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.color31;
  }


  @Override
  public Color successAlertBorder() {
    return new Color(0x003c21);
  }

  @Override
  public Color successAlertText() {
    return inverseText();
  }

  @Override
  public Color infoAlertBackground() {
    return new Color(0x6e86d6);
  }

  @Override
  public Color infoAlertBorder() {
    return new Color(0x2c4081);
  }

  @Override
  public Color infoAlertText() {
    return inverseText();
  }

  @Override
  public Color warningAlertBackground() {
    return new Color(0xf3fd72);
  }

  @Override
  public Color warningAlertBorder() {
    return new Color(0x97a300);
  }

  @Override
  public Color warningAlertText() {
    return text();
  }

  @Override
  public Color dangerAlertBackground() {
    return new Color(0xbf4030);
  }

  @Override
  public Color dangerAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      dangerAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.color31;
  }

  @Override
  public Color dangerAlertBorder() {
    return new Color(0x761300);
  }

  @Override
  public Color dangerAlertText() {
    return inverseText();
  }

}
