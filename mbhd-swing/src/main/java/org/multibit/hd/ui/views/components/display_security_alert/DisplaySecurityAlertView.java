package org.multibit.hd.ui.views.components.display_security_alert;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of Bitcoin address</li>
 * <li>Support for clipboard copy operation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplaySecurityAlertView extends AbstractComponentView<DisplaySecurityAlertModel> {

  private JLabel securityAlertMessage;

  private JButton panelCloseButton;

  /**
   * @param model The model backing this view
   */
  public DisplaySecurityAlertView(DisplaySecurityAlertModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel();

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    panel.add(panelCloseButton, "aligny top, alignx right,shrink,wrap");
    panel.add(Panels.newDebuggerWarning(), "align center,wrap");

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

        Panels.hideLightBoxPopover();

      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
