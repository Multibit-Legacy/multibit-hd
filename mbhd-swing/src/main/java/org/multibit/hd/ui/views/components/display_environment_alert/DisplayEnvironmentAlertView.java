package org.multibit.hd.ui.views.components.display_environment_alert;

import org.multibit.hd.core.events.EnvironmentEvent;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of environment alert in eye-catching colours</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class DisplayEnvironmentAlertView extends AbstractComponentView<DisplayEnvironmentAlertModel> {

  private JButton panelCloseButton;

  /**
   * @param model The model backing this view
   */
  public DisplayEnvironmentAlertView(DisplayEnvironmentAlertModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel();

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());
    panelCloseButton.setName("environment_alert." + MessageKey.CLOSE.getKey());

    panel.add(panelCloseButton, "aligny top,alignx right,shrink,wrap");

    // Determine the title based on the alert type
    EnvironmentEvent event = getModel().get().getValue();
    switch (event.getSummary().getAlertType()) {

      case DEBUGGER_ATTACHED:
      case BACKUP_FAILED:
      case CERTIFICATE_FAILED:
      case UNSUPPORTED_FIRMWARE_ATTACHED:
        panel.add(Labels.newTitleLabel(MessageKey.SECURITY_TITLE), "aligny top, alignx center,shrink,wrap");
        break;
      case SYSTEM_TIME_DRIFT:
      case UNSUPPORTED_CONFIGURATION_ATTACHED:
      case DEPRECATED_FIRMWARE_ATTACHED:
        panel.add(Labels.newTitleLabel(MessageKey.INFO_TITLE), "aligny top, alignx center,shrink,wrap");
        break;
      default:

    }

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

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            // Issue the wizard popover hide event
            ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(), true);
          }
        });
      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}