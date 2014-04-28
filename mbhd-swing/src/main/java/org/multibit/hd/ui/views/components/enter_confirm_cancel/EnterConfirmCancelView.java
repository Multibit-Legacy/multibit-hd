package org.multibit.hd.ui.views.components.enter_confirm_cancel;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Simple Confirm/Cancel dialog</li>
 * </ul>
 * <p>This popover is normally in the context of "Do you wish to proceed?"
 * with Cancel meaning stop and take me back to safety with no changes.</p>
 *
 * @since 0.0.1
 *  
 */
public class EnterConfirmCancelView extends AbstractComponentView<EnterConfirmCancelModel> {

  private final AwesomeIcon confirmIcon;
  private final boolean isConfirmDangerous;

  private JButton panelCloseButton;

  /**
   * @param model              The model backing this view
   * @param isConfirmDangerous True if clicking confirm will discard or delete data without undo
   */
  public EnterConfirmCancelView(EnterConfirmCancelModel model, AwesomeIcon confirmIcon, boolean isConfirmDangerous) {
    super(model);

    this.confirmIcon = confirmIcon;
    this.isConfirmDangerous = isConfirmDangerous;

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel(new MigLayout(
      "fill,insets 10",
      "[][]",
      "[]"
    ));

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    panel.add(panelCloseButton, "span 2,align right,shrink,wrap");
    panel.add(Labels.newDataEnteredNote(), "grow,push,span 2,wrap");

    panel.add(Buttons.newCancelButton(getCancelAction()), "align left,push");
    panel.add(Buttons.newConfirmButton(getConfirmAction(), confirmIcon, isConfirmDangerous), "align right,push");

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
  private Action getConfirmAction() {

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
  private Action getCancelAction() {

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
