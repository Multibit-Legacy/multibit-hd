package org.multibit.hd.ui.views.components.display_message;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of the info message</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayInfoMessageView extends AbstractComponentView<DisplayInfoMessageModel> {

  private JLabel infoMessage;

  private JButton panelCloseButton;

  /**
   * @param model The model backing this view
   */
  public DisplayInfoMessageView(DisplayInfoMessageModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel();

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    panel.add(panelCloseButton, "aligny top, alignx right,shrink,wrap");
    panel.add(Panels.newLanguageChange(), "align center,grow,wrap");

    // Set size
    panel.setSize(MultiBitUI.POPOVER_PREF_WIDTH, MultiBitUI.POPOVER_PREF_HEIGHT);

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    panelCloseButton.requestFocusInWindow();
  }

  /**
   * @return A new action for closing the QR code popover
   */
  private Action getClosePopoverAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.hideLightBoxPopoverIfPresent();

        // Issue the wizard popover hide event
        ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(), true);


      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
