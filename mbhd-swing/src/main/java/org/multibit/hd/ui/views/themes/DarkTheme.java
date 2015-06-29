package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;

import java.awt.*;

/**
 * <p>Strategy to provide the following to Themes API:</p>
 * <ul>
 * <li>A low-light dark theme</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class DarkTheme extends BaseTheme {

  @Override
  public Color headerPanelBackground() {
    return new Color(72, 75, 78);
  }

  @Override
  public Color headerPanelText() {
    return text();
  }

  @Override
  public Color footerPanelBackground() {
    return headerPanelBackground();
  }

  @Override
  public Color footerPanelText() {
    return text();
  }

  @Override
  public Color sidebarPanelBackground() {
    return new Color(77, 80, 83);
  }

  @Override
  public Color detailPanelBackground() {
    return new Color(57, 57, 57);
  }

  @Override
  public Color buttonBackground() {
    return new Color(130, 130, 130);
  }

  @Override
  public Color buttonDefaultBackground() {
    return new Color(0,151,50);
  }

  @Override
  public Color readOnlyBackground() {
    return new Color(193, 226, 248);
  }

  @Override
  public Color readOnlyBorder() {
    return new Color(103, 126, 146);
  }

  @Override
  public Color readOnlyComboBox() {
    return new Color(25, 81, 130);
  }

  @Override
  public Color focusBorder() {
    return new Color(252,91,79);
  }

  @Override
  public Color sidebarSelectedText() {
    return new Color(66,139,202); // Must contrast with dataEntryBackground
  }

  @Override
  public Color dataEntryBackground() {
    return new Color(240, 211, 85);
  }

  @Override
  public Color dataEntryText() {
    return inverseText();
  }

  @Override
  public Color dataEntryBorder() {
    return new Color(240, 240, 128);
  }

  @Override
  public Color invalidDataEntryBackground() {
    return new Color(252, 91, 79);
  }

  @Override
  public Color text() {
    return new Color(207, 207, 207);
  }

  @Override
  public Color fadedText() {
    return new Color(187, 187, 187);
  }

  @Override
  public Color inverseText() {
    return new Color(47, 47, 47);
  }

  @Override
  public Color inverseFadedText() {
    return fadedText();
  }

  @Override
  public Color buttonText() {
    return inverseText();
  }

  @Override
  public Color buttonFadedText() {
    return new Color(210, 210, 210);
  }

  @Override
  public Color successAlertText() {
    return inverseText();
  }

  @Override
  public Color successAlertBackground() {
    return new Color(111, 176, 83);
  }

  @Override
  public Color successAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      successAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.alertFadedBackground;
  }


  @Override
  public Color successAlertBorder() {
    return new Color(64, 106, 45);
  }


  @Override
  public Color infoAlertBackground() {
    return new Color(107, 163, 199);
  }

  @Override
  public Color infoAlertBorder() {
    return new Color(64, 97, 118);
  }

  @Override
  public Color infoAlertText() {
    return inverseText();
  }

  @Override
  public Color warningAlertBackground() {
    return new Color(250, 241, 157);
  }

  @Override
  public Color warningAlertBorder() {
    return new Color(205, 144, 46);
  }

  @Override
  public Color warningAlertText() {
    return inverseText();
  }

  @Override
  public Color dangerAlertBackground() {
    return new Color(252, 101, 91);
  }

  @Override
  public Color dangerAlertFadedBackground() {

    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      dangerAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.alertFadedBackground;
  }

  @Override
  public Color dangerAlertBorder() {
    return new Color(198, 54, 45);
  }

  @Override
  public Color dangerAlertText() {
    return inverseText();
  }

  @Override
  public Color pendingAlertBackground() {
    return new Color(250, 188, 185);
  }

  @Override
  public Color pendingAlertFadedBackground() {
    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      pendingAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.alertFadedBackground;
  }

  @Override
  public Color pendingAlertBorder() {
    return new Color(237, 117, 112);
  }

  @Override
  public Color pendingAlertText() {
    return inverseText();
  }

  @Override
  public boolean isInvert() {
    return true;
  }
}
