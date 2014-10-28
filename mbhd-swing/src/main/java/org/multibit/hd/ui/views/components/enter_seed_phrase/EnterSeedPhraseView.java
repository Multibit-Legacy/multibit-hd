package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>User entry of a seed phrase </li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterSeedPhraseView extends AbstractComponentView<EnterSeedPhraseModel> {

  private static final Logger log = LoggerFactory.getLogger(EnterSeedPhraseView.class);

  // View components
  private JTextArea seedPhraseTextArea;
  private JTextField seedTimestampText;
  private JCheckBox restoreAsTrezor;

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

  }

  @Override
  public JPanel newComponentPanel() {

    EnterSeedPhraseModel model = getModel().get();
    String panelName = model.getPanelName();

    panel = Panels.newPanel(new MigLayout(
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

    // Bind key and focus listeners to allow instant update of UI to mismatched seed phrase
    // TODO Convert this to DocumentListener
    seedPhraseTextArea.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModelFromView();
      }

    });
    seedPhraseTextArea.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        updateModelFromView();
      }
    });

    // Bind a key and focus listeners to allow instant update of UI to invalid date
    // TODO Convert this to DocumentListener
    seedTimestampText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModelFromView();
      }

    });
    seedTimestampText.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        updateModelFromView();
      }
    });

    restoreAsTrezor = new JCheckBox("Restore as Trezor soft wallet");
    final EnterSeedPhraseModel finalModel = model;
    restoreAsTrezor.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        finalModel.setRestoreAsTrezor(((JCheckBox)e.getSource()).isSelected());
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
      panel.add(seedTimestampText, "growx,wrap");
    }
    if (showSeedPhrase) {
      panel.add(Labels.newSeedPhrase());
      panel.add(seedPhraseTextArea, "growx,push");
      panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink,wrap");
    }

    if (!showTimestamp && showSeedPhrase) {
      panel.add(restoreAsTrezor, "span 3,wrap");
    }

    panel.add(verificationStatusLabel, "span 3,push,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    seedTimestampText.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {

    if (showSeedPhrase) {
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

    getModel().get().setRestoreAsTrezor(restoreAsTrezor.isSelected());

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

}
