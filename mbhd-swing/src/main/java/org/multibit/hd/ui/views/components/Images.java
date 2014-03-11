package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

  private static Font imageFont = new JLabel().getFont().deriveFont(Font.PLAIN, MultiBitUI.BALANCE_TRANSACTION_NORMAL_FONT_SIZE);

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
      BufferedImage qrCodePng = ImageDecorator.applyColor(
        ImageIO.read(is),
        Themes.currentTheme.buttonText()
      );

      return new ImageIcon(qrCodePng);

    } catch (IOException e) {
      throw new IllegalStateException("The QR code image is missing");
    }

  }

  /**
   * @param code The 2-letter language code (e.g. "EN") - will be uppercase
   *
   * @return A new "language" image icon suitable for use in combo boxes etc
   */
  public static ImageIcon newLanguageIcon(String code) {

    BufferedImage image = new BufferedImage(26, 20, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2 = image.createGraphics();

    // Fill the background
    g2.setColor(Themes.currentTheme.readOnlyComboBox());
    g2.fillRect(0,0,26,20);

    // Write the language code (looks better with white lowercase)
    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());
    g2.setColor(Color.WHITE);
    g2.setFont(imageFont);
    g2.drawString(code, 3, 16);

    g2.dispose();

    return new ImageIcon(image);

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
   * @param confirmationCount The confirmation count
   * @param isCoinbase        True if this transaction requires the coinbase rules (120 confirmations)
   * @param iconSize          THe size of the icon, typically MultiBitUI.SMALL_ICON_SIZE - pie icons are 50% bigger
   *
   * @return A new "confirmation" image icon suitable for use in tables
   */
  public static ImageIcon newConfirmationIcon(int confirmationCount, boolean isCoinbase, int iconSize) {

    if ((!isCoinbase && confirmationCount >= 6) || (isCoinbase && confirmationCount >= 120)) {
      return ImageDecorator.toImageIcon(AwesomeDecorator.createIcon(
              AwesomeIcon.CHECK,
              Themes.currentTheme.successAlertBackground(),
              iconSize));
    }
      
    BufferedImage background = new BufferedImage(MultiBitUI.NORMAL_ICON_SIZE, MultiBitUI.NORMAL_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2 = background.createGraphics();

    g2.setRenderingHints(ImageDecorator.smoothRenderingHints());

    final int angle;
    if (isCoinbase) {
      angle = confirmationCount * 3 >= 360 ? 360 : confirmationCount * 3;
    } else {
      angle = confirmationCount * 60 >= 360 ? 360 : confirmationCount * 60;

    }

    // Have an icon size 50% bigger for the pie pieces
    iconSize = (int) (iconSize * 1.5);
    g2.setColor(Themes.currentTheme.successAlertBackground());
    g2.fillArc(1, 1, iconSize - 2, iconSize - 2, 90, -angle);

    g2.setColor(Themes.currentTheme.successAlertBackground().darker());
    g2.drawArc(1, 1, iconSize - 2, iconSize - 2, 90, -angle);
    if (angle != 360) {
      int center = (int)( iconSize * 0.5);
      int diameter = center - 1;
      // vertical stroke
      g2.drawLine(center, center, center, 1);

      // angled stroke
      int xFinish = (int) (center + diameter * Math.cos(Math.toRadians(90 - angle)));
      int yFinish = (int) (center - diameter * Math.sin(Math.toRadians(90 - angle)));

      g2.drawLine(center, center, xFinish, yFinish);
    }

    g2.dispose();

    return ImageDecorator.toImageIcon(background);

  }

}