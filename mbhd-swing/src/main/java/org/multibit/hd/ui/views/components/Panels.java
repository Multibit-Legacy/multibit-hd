package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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

  private static final int STANDARD_ICON = 16;
  private static final int LARGE_ICON = 40;

  public static JFrame frame;

  private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();

  /**
   * @return A simple theme-aware panel with a single cell MigLayout
   */
  public static JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout
      "[]", // Columns
      "[]" // Rows
    ));

    return panel;

  }

  /**
   * @param layout The layout manager for the panel (typically MigLayout)
   *
   * @return A simple theme-aware detail panel with the given layout
   */
  public static JPanel newPanel(LayoutManager2 layout) {

    JPanel panel = new JPanel(layout);

    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    return panel;

  }

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
   * <p>A wallet detail panel provides a summary of the information contained within the wallet</p>
   *
   * @return A new wallet detail panel
   */
  public static JPanel newWalletDetailPanel() {

    MigLayout layout = new MigLayout(
      "fillx", // Layout
      "[]10[grow]", // Columns
      "[][][][]"  // Rows
    );

    JPanel panel = newPanel(layout);

    panel.add(new JLabel("Summary"), "wrap");
    panel.add(new JLabel("Location:"));
    panel.add(new JLabel("/Users/<someone>/Library/Application Support/MultiBitHD/mbhd-2412897490823174231947"), "push,wrap");
    panel.add(new JLabel("Contacts:"));
    panel.add(new JLabel("357"), "push,wrap");
    panel.add(new JLabel("Transactions:"));
    panel.add(new JLabel("165"), "push,wrap");

    return panel;
  }

  /**
   * <p>A contact search panel provides a means of finding a contact through their name or a Bitcoin address</p>
   *
   * @return A new recipient panel
   */
  public static JPanel newContactSearch() {

    JPanel panel = newPanel();
    panel.add(new JLabel("Recipient"));
    panel.add(TextBoxes.newRecipient());

    JLabel recipientIcon = new JLabel();
    AwesomeDecorator.applyIcon(AwesomeIcon.USER, recipientIcon, false, LARGE_ICON);
    panel.add(recipientIcon);

    return panel;
  }

  /**
   * <p>A Bitcoin amount panel provides a means of entering either a Bitcoin or fiat amount and seeing an immediate rate conversion</p>
   *
   * @return A new Bitcoin amount panel
   */
  public static JPanel newBitcoinAmount() {

    JPanel panel = Panels.newPanel();

    panel.add(new JLabel("Amount"));
    panel.add(new JLabel("BTC"));
    panel.add(TextBoxes.newCurrency("0.00"));
    panel.add(new JLabel("="));
    panel.add(new JLabel("$"));
    panel.add(TextBoxes.newCurrency("0.00"));
    panel.add(new JLabel("(MtGox)"));

    return panel;
  }

  /**
   * <p>A notes panel provides a means of entering some text data</p>
   *
   * @return A new notes panel
   */
  public static JPanel newNotes() {

    JPanel panel = newPanel();

    panel.add(new JLabel("Notes"));
    panel.add(TextBoxes.newNotes());

    return panel;
  }

  /**
   * <p>A wallet password panel provides a means of entering a user password</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newWalletPassword() {

    JPanel panel = newPanel();

    panel.add(new JLabel("Wallet password:"));
    panel.add(TextBoxes.newPassword());

    JLabel eyeIcon = new JLabel();
    AwesomeDecorator.applyIcon(AwesomeIcon.EYE, eyeIcon, false, STANDARD_ICON);
    panel.add(eyeIcon);

    return panel;
  }

  /**
   * <p>A "broadcast status" panel provides a means of observing broadcast activity</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newBroadcastStatus() {

    JPanel panel = newPanel();

    JLabel label = Labels.newLabel(MessageKey.BROADCAST_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, STANDARD_ICON);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "relay status" panel provides a means of observing relay activity</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newRelayStatus() {

    JPanel panel = newPanel();

    JLabel label = Labels.newLabel(MessageKey.RELAY_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, STANDARD_ICON);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "confirmation count" panel provides a means of observing confirmations</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newConfirmationCount() {

    JPanel panel = newPanel();

    JLabel label = Labels.newLabel(MessageKey.CONFIRMATION_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, STANDARD_ICON);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "language selector" panel provides a means of changing the display language</p>
   *
   * @param listener The action listener
   *
   * @return A new "language selector" panel
   */
  public static JPanel newLanguageSelector(ActionListener listener) {

    JPanel panel = newPanel();

    JLabel label = Labels.newLabel(MessageKey.SELECT_LANGUAGE);
    AwesomeDecorator.applyIcon(AwesomeIcon.GLOBE, label, true, LARGE_ICON);

    JComboBox<String> languages = ComboBoxes.newLanguagesComboBox(listener);

    panel.add(label);
    panel.add(languages);

    return panel;
  }
}
