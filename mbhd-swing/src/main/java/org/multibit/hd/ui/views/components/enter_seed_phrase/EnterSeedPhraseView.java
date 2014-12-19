package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of a seed phrase </li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class EnterSeedPhraseView extends AbstractComponentView<EnterSeedPhraseModel> {

  private static final Logger log = LoggerFactory.getLogger(EnterSeedPhraseView.class);

  // View components
  private JTextArea seedPhraseTextArea;
  private JTextField seedTimestampText;
  private JComboBox<String> restoreAsTrezor;

  private JLabel verificationStatusLabel;

  private final boolean showSeedPhrase;
  private final boolean showTimestamp;
  private final String componentName;

  /**
   * @param model          The model backing this view
   * @param showTimestamp  True if the timestamp field should be visible
   * @param showSeedPhrase True if the seed phrase field should be visible
   */
  public EnterSeedPhraseView(EnterSeedPhraseModel model, boolean showTimestamp, boolean showSeedPhrase) {
    super(model);

    this.showTimestamp = showTimestamp;
    this.showSeedPhrase = showSeedPhrase;

    // Determine the component name
    if (showTimestamp && !showSeedPhrase) {
      // Timestamp only
      componentName = ".timestamp";
    } else {
      // Seed phrase only or seed phrase and timestamp
      componentName = ".seedphrase";
    }

    // Initialise to BIP32
    getModel().get().setRestoreAsTrezor(false);

  }

  @Override
  public JPanel newComponentPanel() {

    EnterSeedPhraseModel model = getModel().get();
    String panelName = model.getPanelName();

    panel = Panels.newPanel(
      new MigLayout(
        "insets 0", // Layout
        "[][][]", // Columns
        "[][]" // Rows
      ));

    // Create view components
    seedPhraseTextArea = TextBoxes.newEnterSeedPhrase();
    seedPhraseTextArea.setVisible(showSeedPhrase);

    seedTimestampText = TextBoxes.newEnterSeedTimestamp();
    seedTimestampText.setVisible(showTimestamp);

    // Fill the text area with appropriate content
    seedPhraseTextArea.setText(model.displaySeedPhrase());

    // Bind document listener to allow instant update of UI to mismatched seed phrase
    seedPhraseTextArea.getDocument().addDocumentListener(
      new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          updateModelFromView();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          updateModelFromView();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          updateModelFromView();
        }
      });

    // Bind document listener to allow instant update of UI to invalid date
    seedTimestampText.getDocument().addDocumentListener(
      new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          updateModelFromView();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          updateModelFromView();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          updateModelFromView();
        }
      });

    // Create a new verification status panel (initially invisible)
    verificationStatusLabel = Labels.newVerificationStatus(panelName + componentName, true);
    verificationStatusLabel.setVisible(false);

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    if (showTimestamp) {
      panel.add(Labels.newTimestamp());
      panel.add(seedTimestampText, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    }
    if (showSeedPhrase) {
      panel.add(Labels.newSeedPhrase());
      panel.add(seedPhraseTextArea, MultiBitUI.WIZARD_MAX_WIDTH_SEED_PHRASE_MIG);
      panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink,wrap");
    }

    // Wallet type selector
    restoreAsTrezor = ComboBoxes.newRestoreWalletTypeComboBox(getSelectWalletTypeAction());
    if (!showTimestamp && showSeedPhrase) {
      panel.add(Labels.newValueLabel(Languages.safeText(MessageKey.SELECT_WALLET_TYPE)), "wmax 150");
      panel.add(restoreAsTrezor, "span 2,wrap");
    }

    panel.add(verificationStatusLabel, "span 3,push,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {

    if (showTimestamp) {
      seedTimestampText.requestFocusInWindow();
    } else if (showSeedPhrase) {
      seedPhraseTextArea.requestFocusInWindow();
    }
  }

  @Override
  public void updateModelFromView() {

    boolean asClearText = getModel().get().asClearText();

    if (showSeedPhrase && asClearText) {
      getModel().get().setSeedPhrase(seedPhraseTextArea.getText());
    }

    if (showTimestamp) {
      try {

        // Need to parse at any length
        getModel().get().setSeedTimestamp(seedTimestampText.getText());

      } catch (IllegalArgumentException e) {

        // Ignore the input - don't give feedback because it is confusing at the start of data entry

      }
    }

  }

  @Subscribe
  public void onVerificationStatusChanged(final VerificationStatusChangedEvent event) {


    if (event.getPanelName().equals(getModel().get().getPanelName() + componentName) && verificationStatusLabel != null) {

      // Determine if the component is initialised
      Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on the EDT");

      verificationStatusLabel.setVisible(event.isOK());

    }
  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getToggleDisplayAction() {

    // Show or hide the seed phrase
    return new AbstractAction() {

      private boolean asClearText = getModel().get().asClearText();

      @Override
      public void actionPerformed(ActionEvent e) {

        EnterSeedPhraseModel model = getModel().get();

        JButton button = (JButton) e.getSource();

        if (asClearText) {

          ButtonDecorator.applyShow(button);

          // Ensure the model matches the clear contents
          updateModelFromView();

        } else {

          ButtonDecorator.applyHide(button);

          // Do not update the model with the hidden contents (they are meaningless)

        }

        asClearText = !asClearText;

        model.setAsClearText(asClearText);

        seedPhraseTextArea.setText(model.displaySeedPhrase());

        // Only permit editing in the clear text mode
        seedPhraseTextArea.setEnabled(asClearText);

      }

    };
  }

  /**
   * @return A new action for selecting the wallet type for the seed phrase (BIP32, BIP44 etc)
   */
  private ActionListener getSelectWalletTypeAction() {

    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().setRestoreAsTrezor(((JComboBox) e.getSource()).getSelectedIndex() == 1);
      }
    };

  }

}
