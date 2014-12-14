package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.SecuritySummary;
import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.enter_pin.EnterPinModel;
import org.multibit.hd.ui.views.components.enter_pin.EnterPinView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Credentials: Enter pin</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CredentialsEnterPinPanelView extends AbstractWizardPanelView<CredentialsWizardModel, CredentialsEnterPinPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(CredentialsEnterPinPanelView.class);

  // Panel specific components
  private ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> displaySecurityPopoverMaV;
  private ModelAndView<EnterPinModel, EnterPinView> enterPinMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public CredentialsEnterPinPanelView(AbstractWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PIN_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    displaySecurityPopoverMaV = Popovers.newDisplaySecurityPopoverMaV(getPanelName());
    enterPinMaV = Components.newEnterPinMaV(getPanelName());

    // Configure the panel model
    final CredentialsEnterPinPanelModel panelModel = new CredentialsEnterPinPanelModel(
      getPanelName(),
      enterPinMaV.getModel()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPinPanelView(this);

    // Register components
    registerComponents(displaySecurityPopoverMaV, enterPinMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXLayout(),
        "[120][][][40]", // Column constraints
        "[]12[][][30]" // Row constraints
      ));

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(Labels.newPinIntroductionNote(), "align left,span 2,wrap");

    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(enterPinMaV.getView().newComponentPanel(), "align left,span 2, wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    PanelDecorator.addExitCancelRestoreUnlock(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Initialise with "Unlock" disabled to force users to enter a credentials
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      false
    );

    // Initialise with "Restore" disabled to prevent PIN problems
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.RESTORE,
      false
    );

  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          enterPinMaV.getView().requestInitialFocus();

          // Check for any security alerts
          Optional<SecurityEvent> securityEvent = CoreServices.getApplicationEventService().getLatestSecurityEvent();
          if (securityEvent.isPresent() && securityEvent.get().is(SecuritySummary.AlertType.DEBUGGER_ATTACHED)) {

            displaySecurityPopoverMaV.getModel().setValue(securityEvent.get());

            // Show the security alert as a popover
            Panels.showLightBoxPopover(displaySecurityPopoverMaV.getView().newComponentPanel());

          }

        }
      });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // Ensure the view disables components
          getFinishButton().setEnabled(false);
          getExitButton().setEnabled(false);
          getRestoreButton().setEnabled(false);

        }
      });

    // Use the wizard model to handle the traffic to the Trezor
    getWizardModel().requestPinCheck(enterPinMaV.getModel().getValue());

    // Defer the hide operation
    return false;
  }


  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      isFinishEnabled()
    );

  }

  /**
   * @param status  True if successful (check mark), false for failure (cross)
   * @param visible True if the PIN status should be visible
   */
  public void setPinStatus(final boolean status, final boolean visible) {
    // Start the spinner (we are deferring the hide)
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          enterPinMaV.getView().setPinStatus(status, visible);

          // The "unlock" button mirrors the status
          getFinishButton().setEnabled(status);

          // Ensure the view enables the escape components
          getExitButton().setEnabled(true);
          getRestoreButton().setEnabled(true);
        }
      });
  }

  /**
   * @return True if the "finish" button should be enabled
   */
  private boolean isFinishEnabled() {

    return !Strings.isNullOrEmpty(
      getPanelModel().get()
        .getEnterPinModel()
        .getValue()
    );

  }

  /**
   * Show the PIN entry as incorrect
   */
  public void incorrectPin() {

    setPinStatus(false, true);
  }
}