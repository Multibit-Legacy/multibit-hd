package org.multibit.hd.ui.views.components.display_seed_phrase;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.utils.PrintingUtils;
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
      "insets 0", // Layout
      "[][][][][][][]", // Columns
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
    panel.add(seedTimestamp, "span 2,grow");
    panel.add(Labels.newSeedSize(), "span 2,grow");
    panel.add(seedSize, "shrink,wrap");
    panel.add(seedPhrase, "span 3,shrink");
    panel.add(Buttons.newHideButton(toggleDisplayAction), "shrink");
    panel.add(Buttons.newRefreshButton(refreshAction), "shrink");

    // Allowing printing of seed phrase is fraught with security hazards
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
   * <p>Handle the "change seed phrase size" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    DisplaySeedPhraseModel model = getModel().get();

    model.newSeedPhrase(SeedPhraseSize.fromOrdinal(source.getSelectedIndex()));
    seedPhrase.setText(model.displaySeedPhrase());

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

        DisplaySeedPhraseModel model = getModel().get();

        JButton button = (JButton) e.getSource();

        if (asClearText) {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE,
            button,
            true,
            MultiBitUI.NORMAL_ICON_SIZE
          );

        } else {
          AwesomeDecorator.applyIcon(
            AwesomeIcon.EYE_SLASH,
            button,
            true,
            MultiBitUI.NORMAL_ICON_SIZE
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

        DisplaySeedPhraseModel model = getModel().get();

        model.newSeedPhrase(model.getCurrentSeedSize());
        seedPhrase.setText(model.displaySeedPhrase());

      }
    };
  }

  /**
   * @return A new action for generating a new seed phrase
   */
  private Action getPrintAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        PrintingUtils.printSeedPhrase(
          getModel().get().getSeedPhrase(),
          getModel().get().getSeedTimestamp()
        );

      }
    };
  }
}