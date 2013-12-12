package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of panels</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Panels {

  public static JFrame frame;

  private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();

  /**
   * <p>Show a light box</p>
   *
   * @param panel The panel to act as the focus of the light box
   */
  public synchronized static void showLightBox(JPanel panel) {

    Preconditions.checkState(!lightBoxPanel.isPresent(), "Light box should never be called twice");

    lightBoxPanel = Optional.of(new LightBoxPanel(panel));

  }

  /**
   * <p>Hides the currently showing light box panel</p>
   */
  public synchronized static void hideLightBox() {

    if (lightBoxPanel.isPresent()) {
      lightBoxPanel.get().close();
    }

    lightBoxPanel = Optional.absent();

  }

  /**
   * <p>A contact search panel provides a means of finding a contact through their name or a Bitcoin address</p>
   *
   * @return A new recipient panel
   */
  public static JPanel newContactSearch() {

    JComboBox<String> recipient = new JComboBox<>(new String[]{
      "19siB8yMyB1yt8KKwNMWK3dwe5VWNE3a84 (Ester)",
      "1Hten6Nzz8UFwtX8b5MUEosuhYRSxuQ3xQ (Jordi)",
      "123MtBH8uWuPRV1ZPAANZ45WqUs3TUzGv9 (Jordina)",
      "19FBfiMKECgxMSxtSZcLL5YqoMnLkrrsqd (Sergi)",
      "175Qrvh9kEPw5YEziK4QSNUw9mpWDMmBmN",
      "1EJD7f5myxrRSNAHK19zTcdVf8fu1hHNNW",
      "1LNVqN9Zbxa8AiLjKfc3e3jUpud8zLNH5Q",
      "17XJpq7NW9ovtuyKfECUrFFjddGWinwR4U",
      "1Q735J518JU9GyEeGf5B9dt5n5hLjZWm4E",
      "1J2Zems2DJQkHC84ybQYceSk54tcPinDAZ",
      "1DuRsLdMdxMnSHJni9mHxX9YPnW98DjkSs",
      "1JUtwDGram1WTf4CJ7mmi5cftso2wqLmDM",
      "1P3vBYPwdbxz4EKWKR6FyK3Vvg1vLtFao9",
      "14gzTg4qmsAWQyEfLjvhoUuz7Lo5vjhheb",
      "1Q5m8YUdEwhfXfxPNLoGpdMVFxMV9xi2R4",
      "13Y9Acs7mpJT5nsXERKHLjmFcuGHUiRYXB",
      "17uqqD7LuznoENBnkgGbTcc6HucxYGQGi8",
      "1ELZYpgoZkKXVxPWFpnF2QXVDGyYSybuun",
      "1Aga3oj2iDcSgkuCTE7FrQXtGitA8itXP9",
      "1MhN7RTt1NwfhB3cypBBuUG3RXAxdvJbHg"
    });
    recipient.setEditable(true);
    AutoCompleteDecorator.decorate(recipient);

    JPanel panel = new JPanel();
    panel.add(new JLabel("Recipient"));
    panel.add(recipient);
    panel.add(new JLabel("Image"));

    return panel;
  }

  /**
   * <p>A Bitcoin amount panel provides a means of entering either a Bitcoin or fiat amount and seeing an immediate rate conversion</p>
   *
   * @return A new Bitcoin amount panel
   */
  public static JPanel newBitcoinAmount() {

    JPanel panel = new JPanel();

    panel.add(new JLabel("Amount"));
    panel.add(new JLabel("BTC"));
    panel.add(new JTextField("0.00"));
    panel.add(new JLabel("="));
    panel.add(new JLabel("$"));
    panel.add(new JTextField("0.00"));
    panel.add(new JLabel("(MtGox)"));

    return panel;
  }

  /**
   * <p>A notes panel provides a means of entering some text data</p>
   *
   * @return A new notes panel
   */
  public static JPanel newNotes() {

    JPanel panel = new JPanel();

    panel.add(new JLabel("Notes"));
    panel.add(new JTextArea("0.00"));

    return panel;
  }

  /**
   * <p>A wallet password panel provides a means of entering a user password</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newWalletPassword() {

    JPanel panel = new JPanel();

    panel.add(new JLabel("Wallet password:"));
    panel.add(new JPasswordField());
    panel.add(new JLabel("Reveal"));

    return panel;
  }

  /**
   * <p>A wallet password panel provides a means of entering a user password</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newBroadcastStatus() {

    JPanel panel = new JPanel();

    panel.add(AwesomeDecorator.createIconLabel(
      AwesomeIcon.CHECK,
      "Broadcast OK",
      true
    ));

    return panel;
  }

  /**
   * <p>A wallet password panel provides a means of entering a user password</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newRelayStatus() {

    JPanel panel = new JPanel();

    panel.add(AwesomeDecorator.createIconLabel(
      AwesomeIcon.CHECK,
      "Relayed OK",
      true
    ));

    return panel;
  }

  /**
   * <p>A wallet password panel provides a means of entering a user password</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newConfirmationCount() {

    JPanel panel = new JPanel();

    panel.add(AwesomeDecorator.createIconLabel(
      AwesomeIcon.CHECK,
      "Confirmations 6+",
      true
    ));

    return panel;
  }

}
