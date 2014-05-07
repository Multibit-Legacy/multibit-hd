package org.multibit.hd.ui.views.themes;

import org.multibit.hd.ui.views.themes.painters.NamedButtonRegionPainter;

import java.awt.*;

/**
 * <p>Strategy to provide the following to Themes API:</p>
 * <ul>
 * <li>A Bootstrap theme</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BootstrapTheme extends BaseTheme {

  @Override
  public Color headerPanelBackground() {
    return new Color(35,35,35);
  }

  @Override
  public Color headerPanelText() {
    return inverseText();
  }

  @Override
  public Color footerPanelBackground() {
    return sidebarPanelBackground();
  }

  @Override
  public Color footerPanelText() {
    return text();
  }

  @Override
  public Color sidebarPanelBackground() {
    return new Color(250,250,250);
  }

  @Override
  public Color detailPanelBackground() {
    return new Color(230,230,230);
  }

  @Override
  public Color buttonBackground() {
    return new Color(228, 228, 228);
  }

  @Override
  public Color readOnlyBackground() {
    return new Color(217,237,247);
  }

  @Override
  public Color readOnlyBorder() {
    return new Color(103,126,146);
  }

  @Override
  public Color readOnlyComboBox() {
    return new Color(25,81,130);
  }

  @Override
  public Color focusBorder() {
    return new Color(6,6,6);
  }

  @Override
  public Color sidebarSelectedText() {
    return new Color(66,139,202);
  }

  @Override
  public Color dataEntryBackground() {
    return new Color(242,242,242);
  }

  @Override
  public Color dataEntryBorder() {
    return new Color(200, 200, 200);
  }

  @Override
  public Color invalidDataEntryBackground() {
    return new Color(252,91,79);
  }

  @Override
  public Color text() {
    return new Color(20,20,20);
  }

  @Override
  public Color fadedText() {
    return new Color(147,147,147);
  }

  @Override
  public Color inverseText() {
    return new Color(230,230,230);
  }

  @Override
  public Color inverseFadedText() {
    return fadedText();
  }

  @Override
  public Color buttonText() {
    return text();
  }

  @Override
  public Color successAlertText() {
    return text();
  }

  @Override
  public Color successAlertBackground() {
    return new Color(223,240,216);
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
    return new Color(70,136,71);
  }

  @Override
  public Color infoAlertBackground() {
    return new Color(107,163,199);
  }

  @Override
  public Color infoAlertBorder() {
    return new Color(64,97,118);
  }

  @Override
  public Color infoAlertText() {
    return inverseText();
  }

  @Override
  public Color warningAlertBackground() {
    return new Color(250,241,157);
  }

  @Override
  public Color warningAlertBorder() {
    return new Color(205,144,46);
  }

  @Override
  public Color warningAlertText() {
    return text();
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
    return painter.color31;
  }

  @Override
  public Color dangerAlertBorder() {
    return new Color(198, 54, 45);
  }

  @Override
  public Color dangerAlertText() {
    return text();
  }

  @Override
  public Color pendingAlertBackground() {
    return new Color(250,188,185);
  }

  @Override
  public Color pendingAlertFadedBackground() {
    NamedButtonRegionPainter painter = new NamedButtonRegionPainter(
      pendingAlertBackground(),
      NamedButtonRegionPainter.BACKGROUND_DEFAULT
    );

    // Any of 28, 30, 31, 32
    return painter.color31;
  }

  @Override
  public Color pendingAlertBorder() {
    return new Color(237,117,112);
  }

  @Override
  public Color pendingAlertText() {
    return text();
  }

}
