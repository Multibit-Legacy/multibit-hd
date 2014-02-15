package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.dto.StarStyle;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
   * @return A new "qr code" image icon that's nicer than the Font Awesome version
   */
  public static Icon newQRCodeIcon() {

    final Icon icon;

    try (InputStream is = Images.class.getResourceAsStream("/assets/images/qrcode.png")) {

      BufferedImage qrCodePng = ImageIO.read(is);
      return new ImageIcon(qrCodePng);

    } catch (IOException e) {
      throw new IllegalStateException("The QR code image is missing");
    }

  }

  /**
   * @return A new "user" image icon suitable for use in tables
   */
  public static ImageIcon newUserIcon() {

    final Icon icon;

    icon = AwesomeDecorator.createIcon(
      AwesomeIcon.USER,
      Themes.currentTheme.text(),
      MultiBitUI.LARGE_ICON_SIZE
    );

    return ImageDecorator.toImageIcon(icon);

  }

  /**
   * @param style The star style
   *
   * @return A new "star" image icon suitable for use in tables
   */
  public static ImageIcon newStarIcon(StarStyle style) {

    final Icon icon;

    switch (style) {
      case UNKNOWN:
        icon = AwesomeDecorator.createIcon(
          AwesomeIcon.BAN,
          Themes.currentTheme.text(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      case EMPTY:
        icon = AwesomeDecorator.createIcon(
          AwesomeIcon.STAR_ALT,
          Themes.currentTheme.text(),
          MultiBitUI.SMALL_ICON_SIZE
        );
        break;
      case FILL_1:
        icon = AwesomeDecorator.createIcon(
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