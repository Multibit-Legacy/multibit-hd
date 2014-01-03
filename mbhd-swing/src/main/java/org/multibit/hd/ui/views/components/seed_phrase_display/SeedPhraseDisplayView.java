package org.multibit.hd.ui.views.components.seed_phrase_display;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.views.View;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class SeedPhraseDisplayView implements View<SeedPhraseDisplayModel>, ActionListener {

  private SeedPhraseDisplayModel model;

  private JTextArea seedPhrase;

  public SeedPhraseDisplayView(SeedPhraseDisplayModel model) {
    this.model = model;
  }

  @Override
  public JPanel newPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "debug,insets 0", // Layout
      "[][][]", // Columns
      "[][]" // Rows
    ));

    final JComboBox<String> seedSize = ComboBoxes.newSeedSizeComboBox(this);
    seedPhrase = TextBoxes.newSeedPhrase();

    seedPhrase.setText(model.displaySeedPhrase());

    // Configure the actions
    Action refreshAction = getRefreshAction();
    Action toggleDisplayAction = getToggleDisplayAction();

    // Add to the panel
    panel.add(Labels.newSeedSize(),"split 2");
    panel.add(seedSize,"wrap");
    panel.add(seedPhrase, "shrink");
    panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink");
    panel.add(Buttons.newRefreshButton(refreshAction), "shrink");

    return panel;


  }

  /**
   * @return A new action for toggling the display of the seed phrase
   */
  private Action getToggleDisplayAction() {
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
            AwesomeDecorator.NORMAL_ICON_SIZE
          );

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            AwesomeDecorator.NORMAL_ICON_SIZE
          );
        }

        asClearText = !asClearText;

        model.setAsClearText(asClearText);

        seedPhrase.setText(model.displaySeedPhrase());

      }

    };
  }

  /**
   * @return A new action for generating a new seed phrase
   */
  private Action getRefreshAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        model.newSeedPhrase(model.getCurrentSeedSize());
        seedPhrase.setText(model.displaySeedPhrase());

      }
    };
  }

  @Override
  public void setModel(SeedPhraseDisplayModel model) {
    this.model = model;
  }

  /**
   * <p>Handle the "change seed phrase size" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    model.newSeedPhrase(SeedPhraseSize.fromOrdinal(source.getSelectedIndex()));
    seedPhrase.setText(model.displaySeedPhrase());

  }

}
