package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.dto.StarStyle;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of images</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Images {

  /**
   * Utilities have no public constructor
   */
  private Images() {
  }


  /**
   *
   * @param style The star style
   *
   * @return A new "star" image icon
   */
  public static ImageIcon newStarIcon(StarStyle style) {

    final Icon icon;

    switch (style) {
      case UNKNOWN:
        // Fall through to empty
      case EMPTY:
        icon = AwesomeDecorator.createIcon(
          AwesomeIcon.STAR_ALT,
          Themes.currentTheme.text(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      case FILL_1:
        icon =AwesomeDecorator.createIcon(
          AwesomeIcon.STAR,
          Themes.currentTheme.warningAlertBackground(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      case FILL_2:
        icon = AwesomeDecorator.createIcon(
          AwesomeIcon.STAR,
          Themes.currentTheme.dangerAlertBackground(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      case FILL_3:
        icon = AwesomeDecorator.createIcon(
          AwesomeIcon.STAR,
          Themes.currentTheme.successAlertBackground(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      default:
        throw new IllegalStateException("Unknown star style: " + style.name());
    }

    return ImageDecorator.toImageIcon(icon);

  }

}