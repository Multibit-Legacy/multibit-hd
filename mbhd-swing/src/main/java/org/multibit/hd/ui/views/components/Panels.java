package org.multibit.hd.ui.views.components;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.panels.LightBoxPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.panels.RoundedPanel;
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

  /**
   * A global reference to the application frame
   */
  public static JFrame frame;

  private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();
  private static Optional<LightBoxPanel> lightBoxPopoverPanel = Optional.absent();

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
      "fill,insets 0", // Layout
      "[]", // Columns
      "[]" // Rows
    ));

    return panel;

  }

  /**
   * @return A simple theme-aware panel with rounded corners and a single cell MigLayout
   */
  public static JPanel newRoundedPanel() {

    JPanel panel = new RoundedPanel(new MigLayout(
      "fill,insets 0", // Layout
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

    lightBoxPanel = Optional.of(new LightBoxPanel(panel, JLayeredPane.MODAL_LAYER));

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
   * <p>Show a light box pop over</p>
   *
   * @param panel The panel to act as the focus of the popover
   */
  public synchronized static void showLightBoxPopover(JPanel panel) {

//    Preconditions.checkState(lightBoxPanel.isPresent(), "LightBoxPopover should not be called unless a light box is showing");
    Preconditions.checkState(!lightBoxPopoverPanel.isPresent(), "LightBoxPopover should never be called twice");

    lightBoxPopoverPanel = Optional.of(new LightBoxPanel(panel, JLayeredPane.DRAG_LAYER));

  }

  /**
   * <p>Hides the currently showing light box popover panel</p>
   */
  public synchronized static void hideLightBoxPopover() {

    if (lightBoxPopoverPanel.isPresent()) {
      lightBoxPopoverPanel.get().close();
    }

    lightBoxPopoverPanel = Optional.absent();

  }

  /**
   * <p>A "broadcast status" panel provides a means of observing broadcast activity</p>
   *
   * @return A new broadcast status panel
   */
  public static JPanel newBroadcastStatus() {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newStatusLabel(MessageKey.BROADCAST_STATUS, null, true);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "relay status" panel provides a means of observing relay activity</p>
   *
   * @return A new relay status panel
   */
  public static JPanel newRelayStatus() {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newStatusLabel(MessageKey.RELAY_STATUS, null, true);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "confirmation count" panel provides a means of observing confirmations</p>
   *
   * @return A new confirmation count status panel
   */
  public static JPanel newConfirmationCountStatus(String count, boolean status) {

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.CONFIRMATION_STATUS, new Object[]{count}, status);

    panel.add(label);

    return panel;
  }

  /**
   * <p>A "verification status" panel shows the user that they have entered their data correctly</p>
   *
   * @return A new "verification status" panel (not visible by default)
   */
  public static JPanel newVerificationStatus() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newVerificationStatus(true), "align center");

    // Typical use case requires this to be invisible
    panel.setVisible(false);

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
   * <p>A "wallet selector" panel provides a means of choosing how a wallet is to be created/accessed</p>
   *
   * @param listener        The action listener
   * @param createCommand   The create command name
   * @param restoreCommand  The restore command name
   * @param hardwareCommand The hardware command name
   * @param switchCommand   The switch command name
   *
   * @return A new "wallet selector" panel
   */
  public static JPanel newWalletSelector(
    ActionListener listener,
    String createCommand,
    String restoreCommand,
    String hardwareCommand,
    String switchCommand
  ) {

    JPanel panel = Panels.newPanel();

    JRadioButton radio1 = RadioButtons.newRadioButton(listener, MessageKey.CREATE_WALLET);
    radio1.setSelected(true);
    radio1.setActionCommand(createCommand);

    JRadioButton radio2 = RadioButtons.newRadioButton(listener, MessageKey.RESTORE_WALLET);
    radio2.setActionCommand(restoreCommand);

    JRadioButton radio3 = RadioButtons.newRadioButton(listener, MessageKey.USE_HARDWARE_WALLET);
    radio3.setActionCommand(hardwareCommand);
    radio3.setEnabled(false);
    radio3.setForeground(UIManager.getColor("RadioButton.disabledText"));

    JRadioButton radio4 = RadioButtons.newRadioButton(listener, MessageKey.SWITCH_WALLET);
    radio4.setActionCommand(switchCommand);

    // Wallet selection is mutually exclusive
    ButtonGroup group = new ButtonGroup();
    group.add(radio1);
    group.add(radio2);
    group.add(radio3);
    group.add(radio4);

    // Add to the panel
    panel.add(radio1, "wrap");
    panel.add(radio2, "wrap");
    panel.add(radio3, "wrap");
    panel.add(radio4, "wrap");

    return panel;
  }

  /**
   * <p>A "restore wallet selector" panel provides a means of choosing how a wallet is to be restored</p>
   *
   * @param listener          The action listener
   * @param backupCommand     The "from backup" command name
   * @param seedPhraseCommand The "from seed phrase" command name
   *
   * @return A new "restore wallet" panel
   */
  public static JPanel newRestoreWalletSelector(
    ActionListener listener,
    String backupCommand,
    String seedPhraseCommand
  ) {

    JPanel panel = Panels.newPanel();

    JRadioButton radio1 = RadioButtons.newRadioButton(listener, MessageKey.RESTORE_FROM_BACKUP);
    radio1.setSelected(true);
    radio1.setActionCommand(backupCommand);

    JRadioButton radio2 = RadioButtons.newRadioButton(listener, MessageKey.RESTORE_FROM_SEED_PHRASE);
    radio2.setActionCommand(seedPhraseCommand);

    // Wallet selection is mutually exclusive
    ButtonGroup group = new ButtonGroup();
    group.add(radio1);
    group.add(radio2);

    // Add to the panel
    panel.add(radio1, "wrap");
    panel.add(radio2, "wrap");

    return panel;
  }

  /**
   * <p>A "confirm seed phrase" panel displays the instructions to enter the seed phrase from a piece of paper</p>
   *
   * @return A new "confirm seed phrase" panel
   */
  public static JPanel newConfirmSeedPhrase() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newConfirmSeedPhraseNote(), "push");

    return panel;
  }

  /**
   * <p>A "seed phrase warning" panel displays the instructions to write down the seed phrase on a piece of paper</p>
   *
   * @return A new "seed phrase warning" panel
   */
  public static JPanel newSeedPhraseWarning() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    PanelDecorator.applyDangerFadedTheme(panel);

    // Add to the panel
    panel.add(Labels.newSeedWarningNote(), "push");

    return panel;
  }

  /**
   * <p>A "restore select method" panel displays the restore options</p>
   *
   * @return A new "restore select method" panel
   */
  public static JPanel newRestoreSelectMethod() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newRestoreSelectMethodNote(), "push");

    return panel;
  }

  /**
   * <p>A "restore from backup" panel displays the instructions to restore from a backup folder</p>
   *
   * @return A new "restore from backup" panel
   */
  public static JPanel newRestoreFromBackup() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newRestoreFromBackupNote(), "push");

    return panel;
  }

  /**
   * <p>A "restore from seed phrase" panel displays the instructions to restore from a seed phrase</p>
   *
   * @return A new "restore from seed phrase" panel
   */
  public static JPanel newRestoreFromSeedPhrase() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newRestoreFromSeedPhraseNote(), "push");

    return panel;
  }

  /**
   * <p>A "restore from timestamp" panel displays the instructions to restore from a seed phrase and timestamp</p>
   *
   * @return A new "restore from timestamp" panel
   */
  public static JPanel newRestoreFromTimestamp() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newRestoreFromTimestampNote(), "push");

    return panel;
  }

  /**
   * <p>A "select backup directory" panel displays the instructions to choose an appropriate backup directory</p>
   *
   * @return A new "select backup directory" panel
   */
  public static JPanel newSelectBackupDirectory() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newSelectBackupLocationNote(), "push");

    return panel;
  }

  /**
   * <p>A "select backup directory" panel displays the instructions to choose an appropriate backup directory</p>
   *
   * @return A new "select backup directory" panel
   */
  public static JPanel newContactDetail() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fillx,insets 0", // Layout
      "[grow]", // Columns
      "[]" // Rows
    ));

    // Add to the panel
    panel.add(Labels.newSelectBackupLocationNote(), "push");

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
