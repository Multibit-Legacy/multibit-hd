package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.i18n.Languages;
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

  public static final String CREATE_WALLET_ACTION_NAME = "Create";
  public static final String RESTORE_WALLET_ACTION_NAME = "Restore";
  public static final String HARDWARE_WALLET_ACTION_NAME = "Hardware";
  public static final String SELECT_WALLET_ACTION_NAME = "Select";
  public static final String WELCOME_ACTION_NAME = "Welcome";
  public static final String CREATE_WALLET_PASSWORD_ACTION_NAME = "CreatePassword";

  /**
   * A global reference to the application frame
   */
  public static JFrame frame;

  private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();

  /**
   * @param layout The layout manager for the panel (typically MigLayout)
   *
   * @return A simple theme-aware detail panel with the given layout
   */
  public static JPanel newPanel(LayoutManager2 layout) {

    JPanel panel = new JPanel(layout);

    // Theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Ensure LTR and RTL is detected by the layout
    panel.applyComponentOrientation(Languages.currentComponentOrientation());

    return panel;

  }

  /**
   * @return A simple theme-aware panel with a single cell MigLayout
   */
  public static JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,fill,insets 0", // Layout
      "[]", // Columns
      "[]" // Rows
    ));

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
   * <p>A "broadcast status" panel provides a means of observing broadcast activity</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newBroadcastStatus() {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.BROADCAST_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, AwesomeDecorator.NORMAL_ICON_SIZE);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "relay status" panel provides a means of observing relay activity</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newRelayStatus() {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.RELAY_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, AwesomeDecorator.NORMAL_ICON_SIZE);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "confirmation count" panel provides a means of observing confirmations</p>
   *
   * @return A new wallet password panel
   */
  public static JPanel newConfirmationCount() {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.CONFIRMATION_STATUS_OK);
    AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, label, true, AwesomeDecorator.NORMAL_ICON_SIZE);

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

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.SELECT_LANGUAGE);
    AwesomeDecorator.applyIcon(AwesomeIcon.GLOBE, label, true, AwesomeDecorator.LARGE_ICON_SIZE);

    JComboBox<String> languages = ComboBoxes.newLanguagesComboBox(listener);

    panel.add(label);
    panel.add(languages, "wrap");

    return panel;
  }


  /**
   * <p>A "wallet selector" panel provides a means of choosing how a wallet is to be created</p>
   *
   * @param listener The action listener
   *
   * @return A new "wallet selector" panel
   */
  public static JPanel newWalletSelector(ActionListener listener) {

    JPanel panel = Panels.newPanel();

    JRadioButton radio1 = RadioButtons.newRadioButton(listener, MessageKey.CREATE_WALLET);
    radio1.setSelected(true);
    radio1.setActionCommand(Components.CREATE_WALLET_ACTION_NAME);

    JRadioButton radio2 = RadioButtons.newRadioButton(listener, MessageKey.RESTORE_WALLET);
    radio2.setActionCommand(Components.RESTORE_WALLET_ACTION_NAME);

    JRadioButton radio3 = RadioButtons.newRadioButton(listener, MessageKey.USE_HARDWARE_WALLET);
    radio3.setActionCommand(Components.HARDWARE_WALLET_ACTION_NAME);

    // Wallet selection is mutually exclusive
    ButtonGroup group = new ButtonGroup();
    group.add(radio1);
    group.add(radio2);
    group.add(radio3);

    // Add to the panel
    panel.add(radio1, "wrap");
    panel.add(radio2, "wrap");
    panel.add(radio3, "wrap");

    return panel;
  }


  /**
   * <p>A "seed phrase warning" panel displays the instructions to write down the seed phrase on a piece of paper</p>
   *
   * @return A new "seed phrase warning" panel
   */
  public static JPanel newSeedPhraseWarning() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,fill,insets 0", // Layout
      "[]", // Columns
      "[]" // Rows
    ));

    PanelDecorator.applyDangerFadedTheme(panel);

    // Add to the panel
    panel.add(Labels.newSeedWarningNote(),"shrink");

    return panel;
  }

  /**
   * <p>Invalidate a panel so that Swing will later redraw it properly with layout changes (normally as a result of a locale change)</p>
   *
   * @param panel The panel to invalidate
   */
  public static void invalidate(JPanel panel) {

    // Added new content so validate/repaint
    panel.validate();
    panel.repaint();

  }
}
