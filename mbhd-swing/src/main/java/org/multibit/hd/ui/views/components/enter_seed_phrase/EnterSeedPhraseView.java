package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.views.components.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

  // View components
  private JTextArea seedPhraseTextArea;
  private JTextField seedTimestampText;

  private JLabel verificationStatusLabel;

  private final boolean showSeedPhrase;
  private final boolean showTimestamp;

  /**
   * @param model          The model backing this view
   * @param showTimestamp  True if the timestamp field should be visible
   * @param showSeedPhrase True if the seed phrase field should be visible
   */
  public EnterSeedPhraseView(EnterSeedPhraseModel model, boolean showTimestamp, boolean showSeedPhrase) {
    super(model);
    this.showTimestamp = showTimestamp;
    this.showSeedPhrase = showSeedPhrase;
  }

  @Override
  public JPanel newComponentPanel() {

    EnterSeedPhraseModel model = getModel().get();

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

    // Bind a key listener to allow instant update of UI to mismatched seed phrase
    seedPhraseTextArea.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModelFromView();
      }

    });

    // Bind a key listener to allow instant update of UI to invalid date
    seedTimestampText.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModelFromView();
      }

    });

    // Create a new verification status panel (initially invisible)
    verificationStatusLabel = Labels.newVerificationStatus(true);
    verificationStatusLabel.setVisible(false);

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    if (showTimestamp) {
      panel.add(Labels.newTimestamp());
      panel.add(seedTimestampText, "growx,wrap");
    }
    if (showSeedPhrase) {
      panel.add(seedPhraseTextArea, "span 2,growx,push");
      panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink,wrap");
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

  }

  @Subscribe
  public void onVerificationStatusChanged(final VerificationStatusChangedEvent event) {

    if (event.getPanelName().equals(getModel().get().getPanelName())) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (event != null) {
            verificationStatusLabel.setVisible(event.isOK());
          }
        }
      });


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
