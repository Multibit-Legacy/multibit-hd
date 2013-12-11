package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

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

    JPanel panel = new JPanel();
    panel.add(new JLabel("Recipient"));
    panel.add(new JTextField("Name or Bitcoin address"));
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

}
