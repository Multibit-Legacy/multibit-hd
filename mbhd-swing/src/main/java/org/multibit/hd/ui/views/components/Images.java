package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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

    try (InputStream is = Images.class.getResourceAsStream("/assets/images/qrcode.png")) {

      // Transform the mask color into the current themed text
      BufferedImage qrCodePng = colorImage(
        ImageIO.read(is),
        Themes.currentTheme.buttonText()
      );

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
   * <p>Applies a single alpha-blended color over all pixels</p>
   * @param image    The source image
   * @param newColor The color to use as the replacement to non-transparent pixels
   *
   * @return The new image with color applied
   */
  public static BufferedImage colorImage(BufferedImage image, Color newColor) {

    int width = image.getWidth();
    int height = image.getHeight();

    WritableRaster raster = image.getRaster();

    int newColorRed = newColor.getRed();
    int newColorGreen = newColor.getGreen();
    int newColorBlue = newColor.getBlue();

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int[] pixels = raster.getPixel(x, y, (int[]) null);

        pixels[0] = newColorRed;
        pixels[1] = newColorGreen;
        pixels[2] = newColorBlue;

        raster.setPixel(x, y, pixels);
      }
    }

    return image;
  }

}