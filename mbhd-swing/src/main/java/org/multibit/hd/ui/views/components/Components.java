package org.multibit.hd.ui.views.components;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.views.components.seed_phrase_display.SeedPhraseDisplayModel;
import org.multibit.hd.ui.views.components.seed_phrase_display.SeedPhraseDisplayView;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Creation of complex components requiring a model and view</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Components {

  public static final int STANDARD_ICON = 16;
  public static final int LARGE_ICON = 40;

  public static final String CREATE_WALLET_ACTION_NAME = "Create";
  public static final String RESTORE_WALLET_ACTION_NAME = "Restore";
  public static final String HARDWARE_WALLET_ACTION_NAME = "Hardware";
  public static final String SELECT_WALLET_ACTION_NAME = "Select";
  public static final String WELCOME_ACTION_NAME = "Welcome";

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

    JPanel panel = Panels.newPanel(layout);

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

    JPanel panel = Panels.newPanel();
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

    JPanel panel = Panels.newPanel();

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

    JPanel panel = Panels.newPanel();

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

    JPanel panel = Panels.newPanel();

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

    JPanel panel = Panels.newPanel();

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

    JPanel panel = Panels.newPanel();

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

    JPanel panel = Panels.newPanel();

    JLabel label = Labels.newLabel(MessageKey.SELECT_LANGUAGE);
    AwesomeDecorator.applyIcon(AwesomeIcon.GLOBE, label, true, LARGE_ICON);

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
   * <p>A "seed phrase display" panel shows the words used in a BIP0039 seed</p>
   *
   * @param generator The seed phrase generator
   *
   * @return A new "seed size selector" panel
   */
  public static ModelAndView<SeedPhraseDisplayModel, SeedPhraseDisplayView> newSeedPhraseDisplay(final SeedPhraseGenerator generator) {

    SeedPhraseDisplayModel model = new SeedPhraseDisplayModel(generator);
    SeedPhraseDisplayView view = new SeedPhraseDisplayView(model);

    return new ModelAndView<>(model ,view);

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

}
