package org.multibit.hd.ui.views.components.enter_seed_phrase;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.View;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
public class EnterSeedPhraseView implements View<EnterSeedPhraseModel> {

  private EnterSeedPhraseModel model;

  private JTextArea seedPhrase;

  public EnterSeedPhraseView(EnterSeedPhraseModel model) {
    this.model = model;
  }

  @Override
  public JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,insets 0", // Layout
      "[][][]", // Columns
      "[]" // Rows
    ));

    seedPhrase = TextBoxes.newEnterSeedPhrase();

    // Fill the text area with
    seedPhrase.setText(model.displaySeedPhrase());

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    panel.add(seedPhrase, "shrink");
    panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink");

    return panel;

  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getToggleDisplayAction() {
    // Show or hide the seed phrase
    return new AbstractAction() {

      private boolean asClearText = model.asClearText();
      private char echoChar = TextBoxes.getPasswordEchoChar();

      @Override
      public void actionPerformed(ActionEvent e) {

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

        seedPhrase.setText(model.displaySeedPhrase());

        // Only permit editing in the clear text mode
        seedPhrase.setEnabled(asClearText);

      }

    };
  }

  @Override
  public void setModel(EnterSeedPhraseModel model) {
    this.model = model;
  }

  @Override
  public void updateModel() {
    model.setSeedPhrase(seedPhrase.getText());
  }

}
