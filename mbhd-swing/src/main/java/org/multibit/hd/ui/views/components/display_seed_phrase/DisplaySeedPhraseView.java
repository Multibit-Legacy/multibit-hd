package org.multibit.hd.ui.views.components.display_seed_phrase;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.*;

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
 */
public class DisplaySeedPhraseView extends AbstractComponentView<DisplaySeedPhraseModel> implements ActionListener {

  // View components
  private JTextArea seedPhrase;
  private JTextField seedTimestamp;

  /**
   * @param model The model backing this view
   */
  public DisplaySeedPhraseView(DisplaySeedPhraseModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(), // Layout
      "[][][][][]", // Columns
      "[][]" // Rows
    ));

    // Populate components
    final JComboBox<String> seedSize = ComboBoxes.newSeedSizeComboBox(this);
    seedPhrase = TextBoxes.newDisplaySeedPhrase();
    seedPhrase.setText(getModel().get().displaySeedPhrase());
    seedTimestamp = TextBoxes.newDisplaySeedTimestamp(getModel().get().getSeedTimestamp());

    // Configure the actions
    Action toggleDisplayAction = getToggleDisplayAction();
    Action refreshAction = getRefreshAction();
    //Action printAction = getPrintAction();

    // Add to the panel
    panel.add(Labels.newTimestamp());
    panel.add(seedTimestamp, MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    panel.add(Labels.newSeedPhrase());
    panel.add(seedPhrase, MultiBitUI.WIZARD_MAX_WIDTH_SEED_PHRASE_MIG);
    panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink");
    panel.add(Buttons.newRefreshButton(refreshAction), "shrink,wrap");

    // Advanced controls are placed at the end
    panel.add(Labels.newSeedSize(), "");
    panel.add(seedSize, "wrap");

    // Allowing printing of seed phrase is fraught with security hazards
    // Could use BIP38 encrypted QR code once webcam scanning is introduced
    //panel.add(Buttons.newPrintButton(printAction), "shrink,wrap");

    seedSize.requestFocusInWindow();

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    seedTimestamp.requestFocusInWindow();
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }

  /**
   * <p>Trigger a new seed phrase</p>
   *
   * @param size The size to use (provided by the model)
   */
  public void newSeedPhrase(SeedPhraseSize size) {

    final DisplaySeedPhraseModel model = getModel().get();

    model.newSeedPhrase(size);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        seedPhrase.setText(model.displaySeedPhrase());

      }
    });

  }

  /**
   * <p>Handle the "change seed phrase size" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    newSeedPhrase(SeedPhraseSize.fromOrdinal(source.getSelectedIndex()));

  }

  /**
   * @return A new action for generating a new seed phrase
   */
  private Action getRefreshAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        newSeedPhrase(getModel().get().getCurrentSeedSize());

      }
    };
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

        final DisplaySeedPhraseModel model = getModel().get();

        JButton button = (JButton) e.getSource();

        if (asClearText) {

          ButtonDecorator.applyShow(button);

        } else {

          ButtonDecorator.applyHide(button);

        }

        asClearText = !asClearText;

        model.setAsClearText(asClearText);

        seedPhrase.setText(model.displaySeedPhrase());

      }

    };
  }
}