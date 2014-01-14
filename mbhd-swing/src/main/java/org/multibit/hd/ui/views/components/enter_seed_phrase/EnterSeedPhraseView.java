package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.VerificationStatusChangedEvent;
import org.multibit.hd.ui.views.AbstractView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

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
public class EnterSeedPhraseView extends AbstractView<EnterSeedPhraseModel> {

  // View components
  private JTextArea seedPhraseTextArea;
  private JPanel verificationStatusPanel;

  /**
   * @param model The model backing this view
   */
  public EnterSeedPhraseView(EnterSeedPhraseModel model) {
    super(model);
  }

  @Override
  public JPanel newPanel() {

    EnterSeedPhraseModel model = getModel().get();

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][]", // Columns
      "[][]" // Rows
    ));

    seedPhraseTextArea = TextBoxes.newEnterSeedPhrase();

    // Fill the text area with appropriate content
    seedPhraseTextArea.setText(model.displaySeedPhrase());

    // Bind a key listener to allow instant update of UI to mismatched seed phrase
    seedPhraseTextArea.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        updateModel();
      }

    });

    // Create a new verification status panel
    verificationStatusPanel = Panels.newVerificationStatus();

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    panel.add(seedPhraseTextArea,"grow,push");
    panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink,wrap");
    panel.add(verificationStatusPanel, "span 2,push,wrap");

    return panel;

  }

  @Override
  public void updateModel() {
    getModel().get().setSeedPhrase(seedPhraseTextArea.getText());
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
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE,
            button,
            true,
            AwesomeDecorator.NORMAL_ICON_SIZE
          );

          // Ensure the model matches the clear contents
          updateModel();

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            AwesomeDecorator.NORMAL_ICON_SIZE
          );

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

  @Subscribe
  public void onVerificationStatusChanged(VerificationStatusChangedEvent event) {

    if (event.getPanelName().equals(getModel().get().getPanelName())) {

      verificationStatusPanel.setVisible(event.isOK());

    }
  }

}
