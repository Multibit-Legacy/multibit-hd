package org.multibit.hd.ui.views.components.enter_yes_no;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Simple Yes/No dialog</li>
 * </ul>
 * <p>This popover is normally in the context of "Do you wish to proceed?" with No meaning stop
 * and take me back to safety with no changes.</p>
 *
 * @since 0.0.1
 *  
 */
public class EnterYesNoView extends AbstractComponentView<EnterYesNoModel> {

  private JButton panelCloseButton;

  /**
   * @param model The model backing this view
   */
  public EnterYesNoView(EnterYesNoModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel(new MigLayout(
      Panels.migXYLayout(),
      "[][]",
      "[]"
    ));

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    panel.add(panelCloseButton, "span 2,align right,shrink,wrap");
    panel.add(Labels.newDataEnteredNote(), "grow,push,span 2,wrap");

    panel.add(Buttons.newNoButton(getNoAction()), "align left,push");
    panel.add(Buttons.newYesButton(getYesAction()), "align right,push");

    // Set minimum size
    panel.setSize(MultiBitUI.POPOVER_MIN_WIDTH, MultiBitUI.POPOVER_MIN_HEIGHT);

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    panelCloseButton.requestFocusInWindow();
  }

  /**
   * @return A new action to indicate a Yes response
   */
  private Action getYesAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().setValue(true);

        Panels.hideLightBoxPopover();

        // Issue the wizard popover hide event
        ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(), false);

      }

    };
  }

  /**
   * @return A new action to indicate a No response
   */
  private Action getNoAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().setValue(false);

        Panels.hideLightBoxPopover();

        // Issue the wizard popover hide event
        ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(), true);

      }

    };
  }

  /**
   * @return A new action for closing the popover with a No response
   */
  private Action getClosePopoverAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        getModel().get().setValue(false);

        Panels.hideLightBoxPopover();

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
