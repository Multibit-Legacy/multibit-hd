package org.multibit.hd.ui.views.components.seed_phrase_display;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.views.View;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a seed phrase display</li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SeedPhraseDisplayView implements View<SeedPhraseDisplayModel> {

  private SeedPhraseDisplayModel model;

  public SeedPhraseDisplayView(SeedPhraseDisplayModel model) {
    this.model = model;
  }

  @Override
  public JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,insets 0", // Layout
      "[][][]", // Columns
      "[]" // Rows
    ));

    final JTextArea seedPhrase = TextBoxes.newSeedPhrase();

    seedPhrase.setText(model.displaySeedPhrase());

    // Configure the actions
    Action refreshAction = getRefreshAction(seedPhrase);
    Action toggleDisplayAction = getToggleDisplayAction(seedPhrase);

    // Add to the panel
    panel.add(seedPhrase, "shrink");
    panel.add(Buttons.newShowButton(toggleDisplayAction), "shrink");
    panel.add(Buttons.newRefreshButton(refreshAction), "shrink");

    return panel;


  }

  /**
   * @param seedPhrase The seed phrase text area
   *
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getToggleDisplayAction(final JTextArea seedPhrase) {
    // Show or hide the seed phrase
    return new AbstractAction() {

      private boolean asClearText = model.asClearText();

      @Override
      public void actionPerformed(ActionEvent e) {

        JButton button = (JButton) e.getSource();

        if (asClearText) {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE,
            button,
            true,
            Components.STANDARD_ICON
          );

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            Components.STANDARD_ICON
          );
        }
        asClearText = !asClearText;

        model.setAsClearText(asClearText);

        seedPhrase.setText(model.displaySeedPhrase());

      }

    };
  }

  /**
   * @param seedPhrase The seed phrase text area
   *
   * @return A new action for generating a new seed phrase
   */
  private Action getRefreshAction(final JTextArea seedPhrase) {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        model.newSeedPhrase();

        seedPhrase.setText(model.displaySeedPhrase());

      }
    };
  }

  @Override
  public void setModel(SeedPhraseDisplayModel model) {
    this.model = model;
  }
}
