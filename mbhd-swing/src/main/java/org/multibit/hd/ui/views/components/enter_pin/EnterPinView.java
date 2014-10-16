package org.multibit.hd.ui.views.components.enter_pin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of pin entry</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterPinView extends AbstractComponentView<EnterPinModel> {

  /**
   * @param model The model backing this view
   */
  public EnterPinView(EnterPinModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(), // Layout
      "[]12[]12[]", // Columns
      "[][][]" // Rows
    ));

    // Create an array of buttons - the pin matrix buttons
    int NUMBER_OF_PIN_BUTTONS = 9;
    JButton[] pinButtons = new JButton[NUMBER_OF_PIN_BUTTONS];

    final EnterPinModel finalModel = getModel().get();

    for (int i = 0; i < NUMBER_OF_PIN_BUTTONS; i++) {
      final int finalButtonNumber = i;

      Action buttonAction = new AbstractAction() {
        final int buttonNumber = finalButtonNumber;

        @Override
        public void actionPerformed(ActionEvent e) {
          finalModel.addButtonPressed(buttonNumber);
        }
      };

      pinButtons[i] = Buttons.newButton(buttonAction);
      pinButtons[i].setText("?");

      // Ensure it is accessible
      pinButtons[i].setName("pin " + i);
      pinButtons[i].getAccessibleContext().setAccessibleName("pin " + i);
    }

    panel.add(pinButtons[0], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[1], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[2], MultiBitUI.MEDIUM_BUTTON_MIG + ", wrap");
    panel.add(pinButtons[3], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[4], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[5], MultiBitUI.MEDIUM_BUTTON_MIG + ", wrap");
    panel.add(pinButtons[6], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[7], MultiBitUI.MEDIUM_BUTTON_MIG);
    panel.add(pinButtons[8], MultiBitUI.MEDIUM_BUTTON_MIG + ", wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
  }

  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
